package com.mobiletheatertech.plot;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.util.Map;
import java.util.TreeMap;

/*
 * Created with IntelliJ IDEA. User: dhs Date: 8/15/13 Time: 6:21 PM To change this template use
 * File | Settings | File Templates.
 */

/**
 * @author dhs
 * @since 0.0.7
 */
public class Legend {

    public static final String TEXTCOLOR = "black";

    private static TreeMap<Integer, Legendable> LEGENDLIST = new TreeMap<>();
    private static Double HEIGHT = 0.0;
    private static Double WIDEST = 0.0;

    private static PagePoint INITIAL;
    private static Draw DRAW;

    static final String CATEGORY = "legend";

    static final Double TEXTOFFSET = 40.0;
    static final Double QUANTITYOFFSET = 200.0;

    /**
     * Register a callback function that will draw an individual legend entry
     *
     * @param callback {@code Legendable} method which will provide the SVG DOM for an individual
     *                 legend entry
     * @param width    to allow for this entry
     * @param height   to allow for this entry
     */
    public static void Register( Legendable callback, Double width, Double height, LegendOrder order ) {

        LEGENDLIST.put( order.next(), callback );

        WIDEST = (width > WIDEST)
                 ? width
                 : WIDEST;

        HEIGHT += height + 7;
    }

    /**
     * After everything that might want a Legend slot has registered, draw the outline and start off
     * the legend with the name of the plot.
     */
    public static void Startup( Draw draw, View mode, Double x, Integer width )
            throws ReferenceException {
        DRAW = draw;

        Element group = draw.document().createElement( "g" );
        group.setAttribute( "class", "legend" );
        draw.appendRootChild( group );

//        Double x = start;
        Double y = 17.0;

        Double center = x + (width / 2);
        headerText( draw, group, center, y, Event.Name() );
        y += 17;
        headerText( draw, group, center, y, Venue.Building() );
        y += 17;
        headerText( draw, group, center, y, Venue.Name() );
        y += 17;

        INITIAL = new PagePoint( x + 25, y );

        // TODO Adding the Y offset to the box height is really cheating
        Double boxHeight = y + HEIGHT + SvgElement.OffsetY();

        Element box = draw.document().createElement( "rect" );
        box.setAttribute( "fill", "none" );
        box.setAttribute( "x", x.toString() );
        box.setAttribute( "y", "1" );
        box.setAttribute( "width", width.toString() );
        box.setAttribute( "height", boxHeight.toString() );

        group.appendChild( box );
    }

    /**
     * Draw a centered heading for the Legend
     *
     * @param draw
     * @param parent
     * @param center
     * @param y
     * @param text
     * @throws ReferenceException
     */
    public static void headerText(
            Draw draw, Element parent, Double center, Double y, String text )
            throws ReferenceException
    {
        Text textNode = draw.document().createTextNode( text );
        SvgElement element = draw.element("text");
        element.attribute( "class", "heading" );
        element.attribute( "x", center.toString() );
        element.attribute( "y", y.toString() );
        element.appendChild( textNode );
        parent.appendChild( element.element() );
    }

    /**
     * Provide the width of the widest legend entry.
     *
     * @return width of widest legend
     * @since 0.0.13
     */
    public static Double Widest() {
        return WIDEST;
    }

    /**
     * Provide a width for the Legend box which will give the completed
     * drawing the proportions of 11" x 17" paper.
     *
     * @return
     * @throws ReferenceException
     */
    public static Integer PlanWidth() throws ReferenceException {
        Double widthAvailable;
        if ( Venue.Depth() < Venue.Width() ) {
            widthAvailable = (Venue.Depth() * 1.54)
                    - Venue.Width() - SvgElement.OffsetX() * 2 - 5 - 2;
        }
        else {
            widthAvailable = (Venue.Depth() * 0.65)
                    - Venue.Width() - SvgElement.OffsetX() * 2 - 5 - 2;
        }

        if (widthAvailable < 0 ) {
            widthAvailable = (Venue.Depth() * 1.54)
                    - Venue.Width() - SvgElement.OffsetX() * 2 - 5 - 2;
        }

        Integer width = widthAvailable.intValue();

        return width;
    }

    /**
     * Invoke all of the registered callbacks.
     *
     * TODO Fix this so that I'm not passing coordinates around.
     * Legend should create a group element with a transform="translate(..)" attribute.
     * The callback should get just the group and populate it as needed.
     * It might also need to return the amount of vertical space required.
     * This will simplify:
     *   a) the logic of this, and so my understanding of it
     *   b) the tests
     */
    public static void Callback() {
        PagePoint start = INITIAL;
        PagePoint finish;
        for (Map.Entry<Integer, Legendable> entry : LEGENDLIST.entrySet()) {
            finish = entry.getValue().domLegendItem( DRAW, start );
            start = new PagePoint( INITIAL.x(), finish.y() + 7 );
        }
    }
}
