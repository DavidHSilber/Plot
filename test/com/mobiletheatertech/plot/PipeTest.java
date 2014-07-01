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

/**
 * Test {@code Pipe}.
 *
 * @author dhs
 * @since 0.0.6
 */
public class PipeTest {

    Element element = null;
    Element prosceniumElement = null;

    Integer x = 12;
    Integer y = 23;
    Integer z = 34;
    Integer lengthInt = 120;
    String length = "120";   // 10' pipe.

    Integer prosceniumX = 200;
    Integer prosceniumY = 144;
    Integer prosceniumZ = 12;
    final String pipeId = "balconyId";

    @Test
    public void isMountable() throws Exception {
        Pipe pipe = new Pipe(element);

        assert Mountable.class.isInstance(pipe);
    }

    @Test
    public void constantDiameter() {
        assertEquals(Pipe.DIAMETER, (Integer) 2);
    }

    @Test
    public void constantLayerName() {
        assertEquals(Pipe.LAYERNAME, "Pipes");
    }

    @Test
    public void constantLayerTag() {
        assertEquals(Pipe.LAYERTAG, "pipe");
    }

    @Test
    public void storesAttributes() throws Exception {
        // These are optional, so their absence should not cause a problem:
//        element.removeAttribute("orientation");
//        element.removeAttribute("offsetx");

        Pipe pipe = new Pipe(element);

        assertEquals( TestHelpers.accessString( pipe, "id" ), pipeId );
        assertEquals( TestHelpers.accessInteger( pipe, "length" ), lengthInt );
//        assertEquals( TestHelpers.accessString( pipe, "x" ), x.toString() );
//        assertEquals( TestHelpers.accessString( pipe, "y" ), y.toString() );
//        assertEquals( TestHelpers.accessString( pipe, "z" ), z.toString() );
        assertEquals(TestHelpers.accessPoint(pipe, "start"), new Point(12, 23, 34));
        assertEquals( TestHelpers.accessDouble( pipe, "orientation" ), 0.0 );
        assertEquals( TestHelpers.accessInteger( pipe, "offsetX" ), (Integer)0 );
    }
//    @Test
//    public void storesAttributes() throws Exception {
//        Pipe pipe = new Pipe(element);
//
//        assertEquals(TestHelpers.accessInteger(pipe, "length"), (Integer) 120);
//        assertTrue(new Point(12, 23, 34).equals(TestHelpers.accessPoint(pipe, "start")));
//        assertEquals(TestHelpers.accessString(pipe, "id"), balconyId);
//    }


    @Test
    public void storesOptionalAttributes() throws Exception {
        element.setAttribute("orientation", "-90");
        element.setAttribute("offsetx", "-50");

        Pipe pipe = new Pipe(element);

        assertEquals( TestHelpers.accessString( pipe, "id" ), pipeId );
        assertEquals( TestHelpers.accessInteger( pipe, "length" ), lengthInt );
//        assertEquals( TestHelpers.accessInteger( pipe, "length" ), length );
//        assertEquals( TestHelpers.accessString( pipe, "x" ), x.toString() );
//        assertEquals( TestHelpers.accessString( pipe, "y" ), y.toString() );
//        assertEquals( TestHelpers.accessString( pipe, "z" ), z.toString() );
        assertEquals(TestHelpers.accessPoint(pipe, "start"), new Point(12, 23, 34));
        assertEquals( TestHelpers.accessDouble( pipe, "orientation" ), -90.0 );
        assertEquals( TestHelpers.accessInteger( pipe, "offsetX" ), (Integer)(-50) );
    }

    @Test
    public void stores() throws Exception {
        ArrayList<Mountable> list1 = (ArrayList<Mountable>)
                TestHelpers.accessStaticObject("com.mobiletheatertech.plot.Mountable", "MOUNTABLELIST");
        assertEquals(list1.size(), 0);

        Pipe pipe = new Pipe(element);

        ArrayList<Mountable> list2 = (ArrayList<Mountable>)
                TestHelpers.accessStaticObject("com.mobiletheatertech.plot.Mountable", "MOUNTABLELIST");
        assert list2.contains(pipe);
    }

    @Test
    public void storesOnlyWhenGood() throws Exception {
        ArrayList<Mountable> list1 = (ArrayList<Mountable>)
                TestHelpers.accessStaticObject("com.mobiletheatertech.plot.Mountable", "MOUNTABLELIST");
        assertEquals(list1.size(), 0);

        element.setAttribute("length", "0");
        try {
            new Pipe(element);
        } catch (Exception e) {
        }

        ArrayList<Mountable> list2 = (ArrayList<Mountable>)
                TestHelpers.accessStaticObject("com.mobiletheatertech.plot.Mountable", "MOUNTABLELIST");
        assertEquals(list2.size(), 0);
    }

    @Test
    public void UnstoresWhenBad() throws Exception {
        ArrayList<Mountable> list1 = (ArrayList<Mountable>)
                TestHelpers.accessStaticObject("com.mobiletheatertech.plot.Mountable", "MOUNTABLELIST");
        assertEquals(list1.size(), 0);

        element.setAttribute("length", "339");
        Pipe pipe = new Pipe(element);
        assert list1.contains(pipe);

        try {
            pipe.verify();
        } catch (Exception e) {
        }

        ArrayList<Mountable> list2 = (ArrayList<Mountable>)
                TestHelpers.accessStaticObject("com.mobiletheatertech.plot.Mountable", "MOUNTABLELIST");
        assertFalse(list2.contains(pipe));
    }

    @Test
    public void UnstoresWhenBadWithProscenium() throws Exception {
        ArrayList<Mountable> list1 = (ArrayList<Mountable>)
                TestHelpers.accessStaticObject("com.mobiletheatertech.plot.Mountable", "MOUNTABLELIST");
        assertEquals(list1.size(), 0);

        new Proscenium(prosceniumElement);
        element.setAttribute("length", "339");
        Pipe pipe = new Pipe(element);
        assert list1.contains(pipe);

        try {
            pipe.verify();
        } catch (Exception e) {
        }

        ArrayList<Mountable> list2 = (ArrayList<Mountable>)
                TestHelpers.accessStaticObject("com.mobiletheatertech.plot.Mountable", "MOUNTABLELIST");
        assertFalse(list2.contains(pipe));
    }

    @Test
    public void registersLayer() throws Exception {
        Pipe pipe = new Pipe(element);

        HashMap<String, String> layers = Layer.List();

        assertTrue(layers.containsKey(Pipe.LAYERNAME));
        assertEquals(layers.get(Pipe.LAYERNAME), Pipe.LAYERTAG);
    }

    @Test
    public void recallsNull() {
        assertNull(Mountable.Select("bogus"));
    }

    @Test
    public void select() throws Exception {
        element.setAttribute("id", "friendly");
        Pipe pipe = new Pipe(element);
        assertSame(Mountable.Select("friendly"), pipe);
    }

    @Test
    public void selectCoexistsWithTruss() throws Exception {
        Element hangPoint1 = new IIOMetadataNode("hangpoint");
        hangPoint1.setAttribute("id", "jim");
        hangPoint1.setAttribute("x", "100");
        hangPoint1.setAttribute("y", "102");
        new HangPoint(hangPoint1);

        Element hangPoint2 = new IIOMetadataNode("hangpoint");
        hangPoint2.setAttribute("id", "joan");
        hangPoint2.setAttribute("x", "200");
        hangPoint2.setAttribute("y", "202");
        new HangPoint(hangPoint2);

        Element suspendElement1 = new IIOMetadataNode("suspend");
        suspendElement1.setAttribute("ref", "jim");
        suspendElement1.setAttribute("distance", "1");

        Element suspendElement2 = new IIOMetadataNode("suspend");
        suspendElement2.setAttribute("ref", "joan");
        suspendElement2.setAttribute("distance", "2");

        Element trussElement = new IIOMetadataNode("truss");
        trussElement.setAttribute("id", "fred");
        trussElement.setAttribute("size", "12");
        trussElement.setAttribute("length", "120");
        trussElement.appendChild(suspendElement1);
        trussElement.appendChild(suspendElement2);
        Truss truss = new Truss(trussElement);


        element.setAttribute("id", "friendly");
        Pipe pipe = new Pipe(element);
        assertSame(Mountable.Select("friendly"), pipe);
        assertSame(Mountable.Select("fred"), truss);
    }

    @Test
    public void storesSelf() throws Exception {
        Pipe pipe = new Pipe(element);

        ArrayList<MinderDom> thing = Drawable.List();

        assert thing.contains(pipe);
    }

    /*
     * This is to ensure that no exception is thrown if data is OK.
     */
    @Test
    public void justFine() throws Exception {
        new Pipe(element);
    }

    @Test(expectedExceptions = AttributeMissingException.class,
            expectedExceptionsMessageRegExp = "Pipe \\(" + pipeId + "\\) is missing required 'length' attribute.")
    public void noLength() throws Exception {
        element.removeAttribute("length");
        new Pipe(element);
    }

    @Test(expectedExceptions = AttributeMissingException.class,
            expectedExceptionsMessageRegExp = "Pipe \\(" + pipeId + "\\) is missing required 'x' attribute.")
    public void noX() throws Exception {
        element.removeAttribute("x");
        new Pipe(element);
    }

    @Test(expectedExceptions = AttributeMissingException.class,
            expectedExceptionsMessageRegExp = "Pipe \\(" + pipeId + "\\) is missing required 'y' attribute.")
    public void noY() throws Exception {
        element.removeAttribute("y");
        new Pipe(element);
    }

    @Test(expectedExceptions = AttributeMissingException.class,
            expectedExceptionsMessageRegExp = "Pipe \\(" + pipeId + "\\) is missing required 'z' attribute.")
    public void noZ() throws Exception {
        element.removeAttribute("z");
        new Pipe(element);
    }

    @Test(expectedExceptions = SizeException.class,
            expectedExceptionsMessageRegExp =
                    "Pipe \\{ origin=Point \\{ x=12, y=23, z=34 \\}, length=0 \\} should have a positive length.")
    public void tooSmallLengthZero() throws Exception {
        element.setAttribute("length", "0");
        new Pipe(element);
    }

    @Test(expectedExceptions = SizeException.class,
            expectedExceptionsMessageRegExp =
                    "Pipe \\{ origin=Point \\{ x=12, y=23, z=34 \\}, length=-1 \\} should have a positive length.")
    public void tooSmallLength() throws Exception {
        element.setAttribute("length", "-1");
        new Pipe(element);
    }

    @Test(expectedExceptions = LocationException.class,
            expectedExceptionsMessageRegExp =
                    "Pipe \\(" + pipeId + "\\) should not extend beyond the boundaries of the venue.")
    public void tooLargeLength() throws Exception {
        element.setAttribute("length", "339");
        Pipe pipe = new Pipe(element);
        pipe.verify();
    }

    @Test(expectedExceptions = LocationException.class,
            expectedExceptionsMessageRegExp =
                    "Pipe \\(" + pipeId + "\\) should not extend beyond the boundaries of the venue.")
    public void tooLargeLengthProscenium() throws Exception {
        new Proscenium(prosceniumElement);

        element.setAttribute("length", "339");
        Pipe pipe = new Pipe(element);
        pipe.verify();
    }

    @Test(expectedExceptions = LocationException.class,
            expectedExceptionsMessageRegExp =
                    "Pipe \\(" + pipeId + "\\) should not extend beyond the boundaries of the venue.")
    public void tooSmallX() throws Exception {
        element.setAttribute("x", "-1");
        Pipe pipe = new Pipe(element);
        pipe.verify();
    }

    @Test(expectedExceptions = LocationException.class,
            expectedExceptionsMessageRegExp =
                    "Pipe \\(" + pipeId + "\\) should not extend beyond the boundaries of the venue.")
    public void tooLargeX() throws Exception {
        element.setAttribute("x", "351");
        Pipe pipe = new Pipe(element);
        pipe.verify();
    }

    @Test(expectedExceptions = LocationException.class,
            expectedExceptionsMessageRegExp =
                    "Pipe \\(" + pipeId + "\\) should not extend beyond the boundaries of the venue.")
    public void tooSmallY() throws Exception {
        element.setAttribute("y", "-1");
        Pipe pipe = new Pipe(element);
        pipe.verify();
    }

    @Test(expectedExceptions = LocationException.class,
            expectedExceptionsMessageRegExp =
                    "Pipe \\(" + pipeId + "\\) should not extend beyond the boundaries of the venue.")
    public void tooLargeY() throws Exception {
        element.setAttribute("y", "401");
        Pipe pipe = new Pipe(element);
        pipe.verify();
    }

    @Test(expectedExceptions = LocationException.class,
            expectedExceptionsMessageRegExp =
                    "Pipe \\(" + pipeId + "\\) should not extend beyond the boundaries of the venue.")
    public void tooSmallZ() throws Exception {
        element.setAttribute("z", "-1");
        Pipe pipe = new Pipe(element);
        pipe.verify();
    }

    @Test(expectedExceptions = LocationException.class,
            expectedExceptionsMessageRegExp =
                    "Pipe \\(" + pipeId + "\\) should not extend beyond the boundaries of the venue.")
    public void tooLargeZ() throws Exception {
        element.setAttribute("z", "241");
        Pipe pipe = new Pipe(element);
        pipe.verify();
    }

    @Test
    public void location() throws Exception {
        Pipe pipe = new Pipe(element);

        Point place = pipe.location("15");
        assert place.equals(new Point(27, 23, 34));
    }

    @Test(expectedExceptions = InvalidXMLException.class,
            expectedExceptionsMessageRegExp = "Pipe \\(" + pipeId + "\\) location is not a number.")
    public void locationNotNumber() throws Exception {
        Pipe pipe = new Pipe(element);

        pipe.location("a");
    }

    @Test(expectedExceptions = MountingException.class,
            expectedExceptionsMessageRegExp = "beyond the end of Pipe")
    public void locationOffPipe() throws Exception {
        Pipe pipe = new Pipe(element);

        pipe.location("121");
    }

    @Test(expectedExceptions = MountingException.class,
            expectedExceptionsMessageRegExp = "beyond the end of Pipe")
    public void locationNegativeOffPipe() throws Exception {
        Pipe pipe = new Pipe(element);

        pipe.location("-1");
    }

    @Test
    public void locationWithProscenium() throws Exception {
        new Proscenium(prosceniumElement);

        Pipe pipe = new Pipe(element);

        Point place = pipe.location("15");
        assertEquals(place.x, 227);
        assertEquals(place.y, 122);
        assertEquals(place.z, 45);
    }

    @Test(expectedExceptions = MountingException.class,
            expectedExceptionsMessageRegExp = "beyond the end of Pipe")
    public void locationOffPipeWithProscenium() throws Exception {
        new Proscenium(prosceniumElement);

        Pipe pipe = new Pipe(element);

        pipe.location("121");
    }

    @Test(expectedExceptions = MountingException.class,
            expectedExceptionsMessageRegExp = "beyond the end of Pipe")
    public void locationNegativeOffPipeWithProscenium() throws Exception {
        new Proscenium(prosceniumElement);

        Pipe pipe = new Pipe(element);

        pipe.location("-1");
    }

    @Test
    public void locationPipeCrossesCenterlineWithProscenium() throws Exception {
        new Proscenium(prosceniumElement);

        element.setAttribute("x", "-12");
        Pipe pipe = new Pipe(element);

        Point place = pipe.location("15");
        assertEquals(place.x, 215);
        assertEquals(place.y, 122);
        assertEquals(place.z, 45);
    }

    @Test
    public void locationNegativePipeCrossesCenterlineWithProscenium() throws Exception {
        new Proscenium(prosceniumElement);

        element.setAttribute("x", "-12");
        Pipe pipe = new Pipe(element);

        Point place = pipe.location("-5");
        assertEquals(place.x, 195);
        assertEquals(place.y, 122);
        assertEquals(place.z, 45);
    }

    @Test(expectedExceptions = MountingException.class,
            expectedExceptionsMessageRegExp = "beyond the end of Pipe")
    public void locationOffPipeCrossingCenterlineOfProscenium() throws Exception {
        new Proscenium(prosceniumElement);

        element.setAttribute("x", "-12");
        Pipe pipe = new Pipe(element);

        pipe.location("120");
    }

    @Test(expectedExceptions = MountingException.class,
            expectedExceptionsMessageRegExp = "beyond the end of Pipe")
    public void locationNegativeOffPipeCrossingCenterlineOfProscenium() throws Exception {
        new Proscenium(prosceniumElement);

        element.setAttribute("x", "-12");
        Pipe pipe = new Pipe(element);

        pipe.location("-15");
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
        element.setAttribute("id", pipeId);
        element.setAttribute("length", length);
        element.setAttribute("x", x.toString());
        element.setAttribute("y", y.toString());
        element.setAttribute("z", z.toString());
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }
}
