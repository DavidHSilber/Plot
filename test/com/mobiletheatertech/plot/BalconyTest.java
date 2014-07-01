package com.mobiletheatertech.plot;

import org.testng.annotations.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.metadata.IIOMetadataNode;
import java.util.ArrayList;
import java.util.HashMap;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * Created by dhs on 4/14/14.
 *
 * @author dhs
 * @since 0.0.24
 */
public class BalconyTest {

    Element element = null;
    Element prosceniumElement = null;

    Integer x = 12;
    Integer y = 23;
    Integer z = 34;
    Integer floorHeight = 194;
    Integer underHeight = 100;
    Integer wallHeight = 37;
    Integer railHeight = 9;

    String length = "120";   // 10' pipe.

    Integer prosceniumX = 200;
    Integer prosceniumY = 144;
    Integer prosceniumZ = 12;
//    final String balconyId = "balconyId";

    @Test
    public void constantLayerName() {
        assertEquals(Balcony.LAYERNAME, "Balconies");
    }

    @Test
    public void constantLayerTag() {
        assertEquals(Balcony.LAYERTAG, "balcony");
    }

    @Test
    public void isMinderDom() throws Exception {
        Balcony balcony = new Balcony(element);

        assert MinderDom.class.isInstance( balcony );
    }

    @Test
    public void storesAttributes() throws Exception {
        Balcony balcony = new Balcony(element);

//        assertEquals( TestHelpers.accessString( balcony, "id" ), balconyId);
        assertEquals( TestHelpers.accessInteger( balcony, "floorHeight" ), floorHeight);
        assertEquals( TestHelpers.accessInteger( balcony, "underHeight" ), underHeight);
        assertEquals( TestHelpers.accessInteger( balcony, "wallHeight" ), wallHeight);
        assertEquals( TestHelpers.accessInteger( balcony, "railHeight" ), railHeight);
    }

//    @Test
//    public void storesOptionalAttributes() throws Exception {
//    }

    @Test
    public void registersLayer() throws Exception {
        new Balcony(element);

        HashMap<String, String> layers = Layer.List();

        assertTrue(layers.containsKey(Balcony.LAYERNAME));
        assertEquals(layers.get(Balcony.LAYERNAME), Balcony.LAYERTAG);
    }

    @Test
    public void storesSelf() throws Exception {
        Balcony balcony = new Balcony(element);

        ArrayList<MinderDom> thing = Drawable.List();

        assert thing.contains(balcony);
    }

    /*
     * This is to ensure that no exception is thrown if data is OK.
     */
    @Test
    public void justFine() throws Exception {
        new Balcony(element);
    }

    @Test(expectedExceptions = AttributeMissingException.class,
            expectedExceptionsMessageRegExp = "Balcony instance is missing required 'floor-height' attribute.")
    public void noFloorHeight() throws Exception {
        element.removeAttribute("floor-height");
        new Balcony(element);
    }

    @Test(expectedExceptions = AttributeMissingException.class,
            expectedExceptionsMessageRegExp = "Balcony instance is missing required 'under-height' attribute.")
    public void noUnderHeight() throws Exception {
        element.removeAttribute("under-height");
        new Balcony(element);
    }

    @Test(expectedExceptions = AttributeMissingException.class,
            expectedExceptionsMessageRegExp = "Balcony instance is missing required 'wall-height' attribute.")
    public void noWallHeight() throws Exception {
        element.removeAttribute("wall-height");
        new Balcony(element);
    }

    @Test(expectedExceptions = AttributeMissingException.class,
            expectedExceptionsMessageRegExp =
                    "Balcony instance is missing required 'rail-height' attribute.")
    public void noRailHeight() throws Exception {
        element.removeAttribute("rail-height");
        new Balcony(element);
    }

    @Test(expectedExceptions = InvalidXMLException.class,
            expectedExceptionsMessageRegExp =
                    "Balcony instance value for 'floor-height' attribute should not be zero.")
    public void tooSmallFloorHeightZero() throws Exception {
        element.setAttribute("floor-height", "0");
        new Balcony(element);
    }

    @Test(expectedExceptions = InvalidXMLException.class,
            expectedExceptionsMessageRegExp =
                    "Balcony instance value for 'floor-height' attribute should not be negative.")
    public void tooSmallFloorHeight() throws Exception {
        element.setAttribute("floor-height", "-1");
        new Balcony(element);
    }

    @Test(expectedExceptions = LocationException.class,
            expectedExceptionsMessageRegExp =
                    "Balcony instance should not extend beyond the boundaries of the venue.")
    public void tooLargeFloorHeight() throws Exception {
        element.setAttribute("floor-height", "195");
        Balcony balcony = new Balcony(element);
        balcony.verify();
    }

    @Test(expectedExceptions = InvalidXMLException.class,
            expectedExceptionsMessageRegExp =
                    "Balcony instance value for 'wall-height' attribute should not be zero.")
    public void tooSmallWallHeightZero() throws Exception {
        element.setAttribute("wall-height", "0");
        new Balcony(element);
    }

    @Test(expectedExceptions = InvalidXMLException.class,
            expectedExceptionsMessageRegExp =
                    "Balcony instance value for 'wall-height' attribute should not be negative.")
    public void tooSmallWallHeight() throws Exception {
        element.setAttribute("wall-height", "-1");
        new Balcony(element);
    }

    @Test(expectedExceptions = LocationException.class,
            expectedExceptionsMessageRegExp =
                    "Balcony instance should not extend beyond the boundaries of the venue.")
    public void tooLargeWallHeight() throws Exception {
        element.setAttribute("wall-height", "38");
        Balcony balcony = new Balcony(element);
        balcony.verify();
    }

    @Test(expectedExceptions = InvalidXMLException.class,
            expectedExceptionsMessageRegExp =
                    "Balcony instance value for 'rail-height' attribute should not be zero.")
    public void tooSmallRailHeightZero() throws Exception {
        element.setAttribute("rail-height", "0");
        new Balcony(element);
    }

    @Test(expectedExceptions = InvalidXMLException.class,
            expectedExceptionsMessageRegExp =
                    "Balcony instance value for 'rail-height' attribute should not be negative.")
    public void tooSmallRailHeight() throws Exception {
        element.setAttribute("rail-height", "-1");
        new Balcony(element);
    }

    @Test(expectedExceptions = LocationException.class,
            expectedExceptionsMessageRegExp =
                    "Balcony instance should not extend beyond the boundaries of the venue.")
    public void tooLargeRailHeight() throws Exception {
        element.setAttribute("rail-height", "10");
        Balcony balcony = new Balcony(element);
        balcony.verify();
    }

    @Test(expectedExceptions = InvalidXMLException.class,
            expectedExceptionsMessageRegExp =
                    "Balcony instance value for 'under-height' attribute should not be zero.")
    public void tooSmallUnderHeightZero() throws Exception {
        element.setAttribute("under-height", "0");
        new Balcony(element);
    }

    @Test(expectedExceptions = InvalidXMLException.class,
            expectedExceptionsMessageRegExp =
                    "Balcony instance value for 'under-height' attribute should not be negative.")
    public void tooSmallUnderHeight() throws Exception {
        element.setAttribute("under-height", "-1");
        new Balcony(element);
    }

    @Test(expectedExceptions = LocationException.class,
            expectedExceptionsMessageRegExp =
                    "Balcony instance under-height should be lower then the floor-height.")
    public void tooLargeUnderHeight() throws Exception {
        element.setAttribute("under-height", "194");
        Balcony balcony = new Balcony(element);
        balcony.verify();
    }

    @Test
    public void domPlan() throws Exception {
        Draw draw = new Draw();
        draw.getRoot();
        Pipe pipe = new Pipe(element);
        pipe.verify();

        pipe.dom(draw, View.PLAN);

        NodeList group = draw.root().getElementsByTagName("g");
        assertEquals(group.getLength(), 2);
        Node groupNode = group.item(1);
        assertEquals(groupNode.getNodeType(), Node.ELEMENT_NODE);
        Element groupElement = (Element) groupNode;
        assertEquals(groupElement.getAttribute("class"), Pipe.LAYERTAG);

        NodeList list = groupElement.getElementsByTagName("rect");
        assertEquals(list.getLength(), 1);
        Node node = list.item(0);
        assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
        Element element = (Element) node;
        assertEquals(element.getAttribute("width"), length);
        assertEquals(element.getAttribute("height"), Pipe.DIAMETER.toString());
        assertEquals(element.getAttribute("fill"), "none");
    }

    @Test
    public void domPlanProscenium() throws Exception {
        Draw draw = new Draw();
        draw.getRoot();
        new Proscenium(prosceniumElement);
        Pipe pipe = new Pipe(element);
        pipe.verify();

        pipe.dom(draw, View.PLAN);

        NodeList list = draw.root().getElementsByTagName("rect");
        assertEquals(list.getLength(), 1);
        Node node = list.item(0);
        assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
        Element element = (Element) node;
        Integer ex = prosceniumX + x;
        Integer wy = prosceniumY - (y - 1);
        assertEquals(element.getAttribute("x"), ex.toString());
        assertEquals(element.getAttribute("y"), wy.toString());
    }

    @Test
    public void domPlanNoProscenium() throws Exception {
        Draw draw = new Draw();
        draw.getRoot();
        Pipe pipe = new Pipe(element);
        pipe.verify();

        pipe.dom(draw, View.PLAN);

        NodeList list = draw.root().getElementsByTagName("rect");
        assertEquals(list.getLength(), 1);
        Node node = list.item(0);
        assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
        Element element = (Element) node;
        assertEquals(element.getAttribute("x"), x.toString());
        assertEquals(element.getAttribute("y"), ((Integer) (y - 1)).toString());
    }

    @Test
    public void domSection() throws Exception {
        Draw draw = new Draw();
        draw.getRoot();
        Pipe pipe = new Pipe(element);
        pipe.verify();

        pipe.dom(draw, View.SECTION);

        NodeList group = draw.root().getElementsByTagName("g");
        assertEquals(group.getLength(), 2);
        Node groupNode = group.item(1);
        assertEquals(groupNode.getNodeType(), Node.ELEMENT_NODE);
        Element groupElement = (Element) groupNode;
        assertEquals(groupElement.getAttribute("class"), Pipe.LAYERTAG);

        NodeList list = groupElement.getElementsByTagName("rect");
        assertEquals(list.getLength(), 1);
        Node node = list.item(0);
        assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
        Element element = (Element) node;
        assertEquals(element.getAttribute("width"), Pipe.DIAMETER.toString());
        assertEquals(element.getAttribute("height"), Pipe.DIAMETER.toString());
        assertEquals(element.getAttribute("fill"), "none");
    }

    @Test
    public void domSectionProscenium() throws Exception {
        Draw draw = new Draw();
        draw.getRoot();
        new Proscenium(prosceniumElement);
        Pipe pipe = new Pipe(element);
        pipe.verify();

        pipe.dom(draw, View.SECTION);

        NodeList list = draw.root().getElementsByTagName("rect");
        assertEquals(list.getLength(), 1);
        Node node = list.item(0);
        assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
        Element element = (Element) node;
        Integer wye = prosceniumY - (y - 1);
        Integer zee = Venue.Height() - (prosceniumZ + z - 1);
        assertEquals(element.getAttribute("x"), wye.toString());
        assertEquals(element.getAttribute("y"), zee.toString());
    }

    @Test
    public void domSectionNoProscenium() throws Exception {
        Draw draw = new Draw();
        draw.getRoot();
        Pipe pipe = new Pipe(element);
        pipe.verify();

        pipe.dom(draw, View.SECTION);

        NodeList list = draw.root().getElementsByTagName("rect");
        assertEquals(list.getLength(), 1);
        Node node = list.item(0);
        assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
        Element element = (Element) node;
        Integer wye = y - 1;
        Integer zee = Venue.Height() - (z - 1);
        assertEquals(element.getAttribute("x"), wye.toString());
        assertEquals(element.getAttribute("y"), zee.toString());
    }

    @Test
    public void domFront() throws Exception {
        Draw draw = new Draw();
        draw.getRoot();
        Pipe pipe = new Pipe(element);
        pipe.verify();

        pipe.dom(draw, View.FRONT);

        NodeList group = draw.root().getElementsByTagName("g");
        assertEquals(group.getLength(), 2);
        Node groupNode = group.item(1);
        assertEquals(groupNode.getNodeType(), Node.ELEMENT_NODE);
        Element groupElement = (Element) groupNode;
        assertEquals(groupElement.getAttribute("class"), Pipe.LAYERTAG);

        NodeList list = groupElement.getElementsByTagName("rect");
        assertEquals(list.getLength(), 1);
        Node node = list.item(0);
        assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
        Element element = (Element) node;
        assertEquals(element.getAttribute("width"), length);
        assertEquals(element.getAttribute("height"), Pipe.DIAMETER.toString());
        assertEquals(element.getAttribute("fill"), "none");
    }

    @Test
    public void domFrontProscenium() throws Exception {
        Draw draw = new Draw();
        draw.getRoot();
        new Proscenium(prosceniumElement);
        Pipe pipe = new Pipe(element);
        pipe.verify();

        pipe.dom(draw, View.FRONT);

        NodeList list = draw.root().getElementsByTagName("rect");
        assertEquals(list.getLength(), 1);
        Node node = list.item(0);
        assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
        Element element = (Element) node;
        Integer exe = prosceniumX + x;
        Integer zee = Venue.Height() - (prosceniumZ + z - 1);
        assertEquals(element.getAttribute("x"), exe.toString());
        assertEquals(element.getAttribute("y"), zee.toString());
    }

    @Test
    public void domFrontNoProscenium() throws Exception {
        Draw draw = new Draw();
        draw.getRoot();
        Pipe pipe = new Pipe(element);
        pipe.verify();

        pipe.dom(draw, View.FRONT);

        NodeList list = draw.root().getElementsByTagName("rect");
        assertEquals(list.getLength(), 1);
        Node node = list.item(0);
        assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
        Element element = (Element) node;
        Integer zee = Venue.Height() - (z - 1);
        assertEquals(element.getAttribute("x"), x.toString());
        assertEquals(element.getAttribute("y"), zee.toString());
    }


    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
        TestResets.ProsceniumReset();
        TestResets.MountableReset();

        Element venueElement = new IIOMetadataNode("venue");
        venueElement.setAttribute("room", "Test Name");
        venueElement.setAttribute("width", "350");
        venueElement.setAttribute("depth", "400");
        venueElement.setAttribute("height", "240");
        new Venue(venueElement);

        prosceniumElement = new IIOMetadataNode("proscenium");
        prosceniumElement.setAttribute("width", "260");
        prosceniumElement.setAttribute("height", "200");
        prosceniumElement.setAttribute("depth", "22");
        prosceniumElement.setAttribute("x", prosceniumX.toString());
        prosceniumElement.setAttribute("y", prosceniumY.toString());
        prosceniumElement.setAttribute("z", prosceniumZ.toString());

        element = new IIOMetadataNode("pipe");
//        element.setAttribute("id", balconyId);
        element.setAttribute("floor-height", floorHeight.toString());
        element.setAttribute("under-height", underHeight.toString());
        element.setAttribute("wall-height", wallHeight.toString());
        element.setAttribute("rail-height", railHeight.toString());
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }
}
