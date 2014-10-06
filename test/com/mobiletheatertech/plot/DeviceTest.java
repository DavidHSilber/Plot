package com.mobiletheatertech.plot;

import org.testng.annotations.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.metadata.IIOMetadataNode;
import java.util.ArrayList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

/**
 * Created by dhs on 6/22/14.
 */
public class DeviceTest {

    Element tableElement = null;
    Element templateElement = null;
    Element layeredTemplateElement = null;
    Element element = null;
    Element layeredElement = null;

    final String deviceName = "Intercom base station";
    final String tableName = "control table";
    final String templateName = "Clear-Com CS-210";
    final String layeredTemplateName = "Clear-Com RS-100A";
    final String layerName = "intercom";

    Integer tableWidth = 1;
    Integer tableDepth = 2;
    Integer tableHeight = 3;
    Integer tableX = 4;
    Integer tableY = 5;
    Integer tableZ = 6;


    @Test
    public void isA() throws Exception {
        Device device = new Device( element );

        assert MinderDom.class.isInstance( device );
    }

    @Test
    public void storesAttributes() throws Exception {
        // These are optional, so their absence should not cause a problem:
        Device device = new Device( element );

        assertEquals( TestHelpers.accessString( device, "id" ), deviceName );
        assertEquals( TestHelpers.accessString( device, "on" ), tableName );
        assertEquals( TestHelpers.accessString( device, "is" ), templateName );
    }

    @Test(expectedExceptions = AttributeMissingException.class,
            expectedExceptionsMessageRegExp = "Device instance is missing required 'id' attribute.")
    public void noId() throws Exception {
        element.removeAttribute("id");
        new Device(element);
    }

    @Test(expectedExceptions = AttributeMissingException.class,
            expectedExceptionsMessageRegExp = "Device \\(" + deviceName + "\\) is missing required 'on' attribute.")
    public void noOn() throws Exception {
        element.removeAttribute("on");
        new Device(element);
    }

    @Test(expectedExceptions = AttributeMissingException.class,
            expectedExceptionsMessageRegExp = "Device \\("+deviceName+"\\) is missing required 'is' attribute.")
    public void noIs() throws Exception {
        element.removeAttribute("is");
        new Device(element);
    }

    @Test
    public void validIs() throws Exception {
        new Table( tableElement );
        new DeviceTemplate(templateElement);
        Device device = new Device(element);
        device.verify();
    }

    @Test(expectedExceptions = InvalidXMLException.class, expectedExceptionsMessageRegExp =
            "Device \\("+deviceName+"\\) 'is' reference \\("+templateName+"\\) does not exist.")
    public void invalidIs() throws Exception {
        Device device = new Device(element);
        device.verify();
    }

    @Test
    public void validOn() throws Exception {
        new Table( tableElement );
        new DeviceTemplate(templateElement);
        Device device = new Device(element);
        device.verify();
    }

    @Test(expectedExceptions = InvalidXMLException.class, expectedExceptionsMessageRegExp =
            "Device \\("+deviceName+"\\) 'on' reference \\("+tableName+"\\) does not exist.")
    public void invalidOn() throws Exception {
        new DeviceTemplate(templateElement);
        Device device = new Device(element);
        device.verify();
    }

    @Test
    public void validIsOnDefinedAfterDevice() throws Exception {
        Device device = new Device(element);
        new Table( tableElement );
        new DeviceTemplate(templateElement);
        device.verify();
    }

    @Test
    public void storesSelf() throws Exception {
        Device device = new Device( element );

        ArrayList<ElementalLister> thing = ElementalLister.List();

        assert thing.contains( device );
    }

    @Test
    public void recallsNull() {
        assertNull(Device.Select("bogus"));
    }

    @Test
    public void recalls() throws Exception {
        Device device = new Device( element );
        assertSame( Device.Select( deviceName ), device);
    }

    @Test
    public void addsToGearList() throws Exception {
        assertEquals( GearList.Check(templateName), (Integer)0 );

        new Device( element );

        assertEquals( GearList.Check(templateName), (Integer)1 );
    }

    @Test
    public void noLayer() throws Exception {
        new Table( tableElement );
        new DeviceTemplate( templateElement );
        Device device = new Device( element );
        device.verify();

        assertNull( device.layer() );
    }

    @Test
    public void layer() throws Exception {
        new Table( tableElement );
        new DeviceTemplate( layeredTemplateElement );
        Device device = new Device( layeredElement );
        device.verify();

        assertEquals( device.layer(), layerName );
    }

    @Test
    public void is() throws Exception {
        new Table( tableElement );
        new DeviceTemplate( templateElement );
        Device device = new Device( element );
        device.verify();

        assertEquals( device.is(), templateName );
    }

    @Test
    public void location() throws Exception {
        new Table( tableElement );
        new DeviceTemplate(templateElement);
        Device device = new Device( element );
        device.verify();

        Point place = device.location();

        assertEquals( (Integer)place.x, tableX );
        assertEquals( (Integer)place.y, tableY );
        assertEquals( place.z, tableZ + tableHeight );
    }

    @Test
    public void domPlan() throws Exception {
        DeviceTemplate deviceTemplate =  new DeviceTemplate(templateElement);
        Table table = new Table( tableElement );

        Draw draw = new Draw();

        draw.establishRoot();
        Device device = new Device( element );
        device.verify();

        NodeList existingGroups = draw.root().getElementsByTagName("rect");
        assertEquals(existingGroups.getLength(), 0);

        device.dom(draw, View.PLAN);

        NodeList rectangles = draw.root().getElementsByTagName("rect");
        assertEquals(rectangles.getLength(), 1);
        Node groupNode = rectangles.item(0);
        assertEquals(groupNode.getNodeType(), Node.ELEMENT_NODE);
        Element deviceElement = (Element) groupNode;
//        assertEquals(tableElement.attribute("class"), Table.LAYERTAG);

        Solid deviceTemplateShape = deviceTemplate.getSolid();
        Point tableLocation = table.things.get(0).point;

        assertEquals(deviceElement.getAttribute("x"), tableLocation.x().toString() );
        assertEquals(deviceElement.getAttribute("y"), tableLocation.y().toString() );
        Integer width = deviceTemplateShape.getWidth().intValue();
        assertEquals(deviceElement.getAttribute("width"), width.toString() );
        // Plot attribute is 'depth'. SVG attribute is 'height'.\
        Integer height = deviceTemplateShape.getDepth().intValue();
        assertEquals(deviceElement.getAttribute("height"), height.toString() );
        assertEquals(deviceElement.getAttribute("fill"), "grey");
        assertEquals(deviceElement.getAttribute("stroke"), "black");
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
//        TestResets.GearListReset();
        TestResets.ElementalListerReset(); // so that Tables are reset.
//        TestResets.TableThingsReset();
        TestResets.DeviceTemplateReset();
        TestResets.DeviceReset();
        TestResets.StackableReset();

        Element venueElement = new IIOMetadataNode();
        venueElement.setAttribute("room", "Test Name");
        venueElement.setAttribute("width", "550");
        venueElement.setAttribute("depth", "400");
        venueElement.setAttribute("height", "240");
        new Venue(venueElement);

        tableElement = new IIOMetadataNode("table");
        tableElement.setAttribute("id", tableName);
        tableElement.setAttribute("width", tableWidth.toString() );
        tableElement.setAttribute("depth", tableDepth.toString() );
        tableElement.setAttribute("height", tableHeight.toString() );
        tableElement.setAttribute("x", tableX.toString() );
        tableElement.setAttribute("y", tableY.toString() );
        tableElement.setAttribute("z", tableZ.toString() );

        templateElement = new IIOMetadataNode("device-template");
        templateElement.setAttribute("type", templateName );
        templateElement.setAttribute("width", "7");
        templateElement.setAttribute("depth", "8");
        templateElement.setAttribute("height", "9");

        layeredTemplateElement = new IIOMetadataNode("device-template");
        layeredTemplateElement.setAttribute("type", layeredTemplateName );
        layeredTemplateElement.setAttribute("width", "7");
        layeredTemplateElement.setAttribute("depth", "8");
        layeredTemplateElement.setAttribute("height", "9");
        layeredTemplateElement.setAttribute("layer", layerName );

        element = new IIOMetadataNode( "device" );
        element.setAttribute( "id", deviceName );
        element.setAttribute( "on", tableName );
        element.setAttribute( "is", templateName );

        layeredElement = new IIOMetadataNode( "device" );
        layeredElement.setAttribute( "id", deviceName );
        layeredElement.setAttribute( "on", tableName );
        layeredElement.setAttribute( "is", layeredTemplateName );
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }
}
