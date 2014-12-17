package com.mobiletheatertech.plot;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.ArrayList;
//import java.awt.geom.Line2D;

/**
 * Represents a theatrical lighting instrument and includes various information about where it is
 * located and how it is connected.
 * <p/>
 * XML tag is 'luminaire'.
 * <p/>
 * Required attributes are:<dl> <dt>type</dt><dd>references the name of a
 * 'luminaire-definition'</dd> <dt>on</dt><dd>references the name of a pipe, truss, or other object
 * on which can support a luminaire</dd> <dt>location</dt><dd>specifies where on the 'on' item, this
 * is placed</dd> </dl>
 * <p/>
 * Optional attributes are:<dl> <dt>circuit</dt><dd>wiring circuit which powers this</dd>
 * <dt>dimmer</dt><dd>dimmer which powers this circuit</dd> <dt>channel</dt><dd>board channel which
 * controls this dimmer</dd> <dt>color</dt><dd>gel number</dd> <dt>unit</dt><dd>marker to identify
 * this within the set of luminaires mounted on a given support item</dd> </dl>
 *
 * @author dhs
 * @since 0.0.7
 */
public class Luminaire extends MinderDom {

    private static ArrayList<Luminaire> LUMINAIRELIST = new ArrayList<>();
    /**
     * Name of {@code Layer} of {@code Luminaoire}s.
     */
    public static final String LAYERNAME = "Luminaires";

    /**
     * Name of {@code Layer} of {@code Luminare}s.
     */
    public static final String LAYERTAG = "luminaire";


    private String type;
    private String on;
    private String location;
    private String circuit;
    private String dimmer;
    private String channel;
    private String color;
    private String unit;
    private String target;
    private String info;
    private String address;
    private Double rotation = 0.0;
    private Point point;
    private Place place;
    private Point origin;
    private Double pipeRotation;
    private String transform;
    private Mountable mount = null;

    static final String COLOR = "black";


    /**
     * Construct a {@code Luminaire} from an XML element.
     *
     * @param element DOM Element defining a luminaire
     * @throws AttributeMissingException if any attribute is missing
     * @throws InvalidXMLException       if element is null
     */
    public Luminaire(Element element)
            throws AttributeMissingException, DataException,
            InvalidXMLException
    {
        super(element);

        type = getStringAttribute(element, "type");
        on = getStringAttribute(element, "on");
        location = getStringAttribute(element, "location");
        unit = getStringAttribute(element, "unit");
        circuit = getOptionalStringAttribute(element, "circuit");
        dimmer = getOptionalStringAttribute(element, "dimmer");
        channel = getOptionalStringAttribute(element, "channel");
        color = getOptionalStringAttribute(element, "color");
        target = getOptionalStringAttribute(element, "target");
        info = getOptionalStringAttribute( element, "info" );
        address = getOptionalStringAttribute( element, "address" );
        String rotate = getOptionalStringAttribute(element, "rotation");
        if (null != rotate && ! rotate.isEmpty() ) {
            rotation = new Double(rotate);
        }

        id = on + ":" + unit;

        if( null != Select( id )){
            throw new InvalidXMLException(
                    this.getClass().getSimpleName()+" id '"+id+"' is not unique.");
        }


        new Layer( LAYERTAG, LAYERNAME, COLOR );
        new LuminaireInformation( element, this );

        GearList.Add(type);

        LUMINAIRELIST.add( this );
    }

    public static Luminaire Select( String identifier ) {
        for (Luminaire selection : LUMINAIRELIST) {
            if (selection.id.equals( identifier )) {
                return selection;
            }
        }
        return null;
    }

    /**
     * Provide the drawing location of this {@code Luminaire}.
     *
     * @return drawing location
     * @throws MountingException if the {@code Pipe} that this is supposed to be on does not exist
     */
    protected Place location() throws InvalidXMLException, MountingException, ReferenceException {
        mount = Mountable.Select(on);
        if (null == mount) {
            throw new MountingException(
                    "Luminaire of type '" + type + "' has unknown mounting: '" + on + "'.");
        }
        Place result;
        try {
            result = mount.rotatedLocation(location);
        } catch (MountingException e) {
            throw new MountingException(
                    "Luminaire of type '" + type + "' has location " + location + " which is " +
                            e.getMessage() + " '" + on + "'.");
        }
        return result;
    }

    @Override
    public void verify() throws InvalidXMLException, MountingException, ReferenceException {
        place = location();
        point=place.location();
        origin=place.origin();
        pipeRotation=place.rotation();
        rotation += 180;
//        Integer transformY = origin.y() + SvgElement.OffsetY();
//        transform = "rotate(" + pipeRotation + "," + origin.x() + "," + transformY + ")";
        transform = "rotate(" + pipeRotation + "," + origin.x() + "," + origin.y() + ")";
    }

    Point point() {
        return point;
    }

    Mountable mount() {
        return mount;
    }

    String locationValue() {
        return location;
    }

    String circuit() {
        return circuit;
    }

    String dimmer() {
        return dimmer;
    }

    String channel() {
        return channel;
    }

    /**
     * Provide the rotation required to orient this luminaire's icon to the specified point.
     *
     * @param point towards which luminaire icon should be oriented
     * @return angle
     */
    private Integer alignWithZone(Point point) {
        Zone zone = Zone.Find(target);
        if (null == zone) {
            return 0;
        }
        Double oppositeLength = point.y() - zone.drawY();
        Double adjacentLength = point.x() - zone.drawX();
        Double angle = Math.atan2(oppositeLength, adjacentLength);
        angle = Math.toDegrees(angle);

        return angle.intValue() + 90;
    }


    /**
     * Generate SVG DOM for a {@code Luminaire}, along with its circuit, dimmer, channel, color, and
     * unit information.
     *
     * @param draw Canvas/DOM manager
     * @param view drawing mode
     * @throws MountingException if the {@code Pipe} that this is supposed to be on does not exist
     */
    @Override
    public void dom( Draw draw, View view )
            throws InvalidXMLException, MountingException, ReferenceException {
        if( View.TRUSS == view && !Truss.class.isInstance(mount)) {
            return;
        }

//        System.out.println( "Luminaire.dom: About to draw id: "+id+" type: "+type+" on: "+ on+" location: "+location+".");
        Double x = point.x();
        Double y = point.y();

        Double z = Venue.Height() - point.z();

        SvgElement group = svgClassGroup( draw, LAYERTAG );
        draw.appendRootChild(group);

        // use element for luminaire icon
        SvgElement use = null;

        // TODO Keep this until I resolve how Luminaire knows what type it is.
        LuminaireDefinition definition = LuminaireDefinition.Select( type );
        if( null == definition ) {
            throw new    ReferenceException( "Unable to find definition for "+ type );
        }

        switch (view) {
            case PLAN:
                group.attribute( "transform", transform );

                definition.count();

                use = group.use( draw, type, x, y );

                // This transform is to orient the luminaire.
                // See verify() for the transform that rotates the position of the luminaire to
                // keep it with a truss that has been rotated.
                String transform;
                Double transformX = point.x() + SvgElement.OffsetX();
                Double transformY = point.y() + SvgElement.OffsetY();
                if ( !target.equals("") ) {
                    // With this I lose the alignment with zones. :-(
                    Integer rotation = alignWithZone(point);
                    transform = "rotate(" + rotation + "," + transformX + "," + transformY + ")";
                }
                else {
                    transform = "rotate(" + rotation + "," + transformX + "," + transformY + ")";
                }
                use.attribute("transform", transform );

                // Unit number to overlay on icon
                SvgElement unitText = group.text( draw, unit, x, y, COLOR );
                unitText.attribute("fill", "none");
                unitText.attribute("stroke", "green");
                unitText.attribute("font-family", "sans-serif");
                unitText.attribute("font-weight", "100");
                unitText.attribute("font-size", "7");
                unitText.attribute("text-anchor", "middle");

                break;
            case SECTION:
                group.use( draw, type, point.y(), z );
                break;
            case FRONT:
                group.use( draw, type, point.x(), z );
                break;
            case TRUSS:
                Truss truss = (Truss) mount;
                Point newPoint = truss.relocate( location );
                use = group.use( draw, type, newPoint.x(), newPoint.y() );
                use.attribute("transform", "rotate(" + rotation + "," + newPoint.x() + "," + newPoint.y() + ")" );
                break;
            case SCHEMATIC:
                PagePoint pagePoint = mount.schematicLocation( location );
                group.useAbsolute( draw, type, pagePoint.x(), pagePoint.y() );
                break;
        }
    }

    /**
     * Describe this {@code Luminaire}.
     *
     * @return textual description
     */
    @Override
    public String toString() {
        return "Luminaire: " + type + ", " + circuit;
    }
}
