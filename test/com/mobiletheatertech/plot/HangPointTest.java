package com.mobiletheatertech.plot;

import mockit.Expectations;
import mockit.Mocked;
import org.testng.annotations.*;
import org.w3c.dom.Element;

import javax.imageio.metadata.IIOMetadataNode;
import java.awt.*;
import java.awt.geom.Line2D;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import static org.testng.Assert.*;

/**
 * Test {@code HangPoint }
 *
 * @author dhs
 * @since 0.0.4
 */
public class HangPointTest {

    Element element = null;

    public HangPointTest() {
    }

    @Test
    public void isMinder() throws Exception {
        HangPoint hangPoint = new HangPoint( element );

        assert Minder.class.isInstance( hangPoint );
    }

    @Test
    public void storesAttributes() throws Exception {
        HangPoint hangPoint = new HangPoint( element );

        assertEquals( TestHelpers.accessString( hangPoint, "id" ), "Blather" );
        assertEquals( TestHelpers.accessInteger( hangPoint, "x" ), (Integer) 296 );
        assertEquals( TestHelpers.accessInteger( hangPoint, "y" ), (Integer) 320 );
    }

    @Test
    public void storesSelf() throws Exception {
        HangPoint hangPoint = new HangPoint( element );

        ArrayList<Minder> thing = Drawable.List();

        assert thing.contains( hangPoint );
    }

    @Test
    public void finds() throws Exception {
        HangPoint hangPoint = new HangPoint( element );

        HangPoint found = HangPoint.Find( "Blather" );

        assertSame( found, hangPoint );
    }

    @Test
    public void findsNothing() throws Exception {
        HangPoint found = HangPoint.Find( "Nothing" );

        assertNull( found );
    }

    @Test
    public void findIgnoresOther() throws Exception {
        Element pipeElement = new IIOMetadataNode( "pipe" );
        pipeElement.setAttribute( "length", "120" );
        pipeElement.setAttribute( "x", "2" );
        pipeElement.setAttribute( "y", "4" );
        pipeElement.setAttribute( "z", "6" );
        new Pipe( pipeElement );
        HangPoint hangPoint = new HangPoint( element );

        HangPoint found = HangPoint.Find( "Blather" );

        assertSame( found, hangPoint );
    }

    @Test
    public void findIgnoresOtherHangPoint() throws Exception {
        Element hangpointElement = new IIOMetadataNode( "hangpoint" );
        hangpointElement.setAttribute( "id", "Not Our Victim" );
        hangpointElement.setAttribute( "x", "2" );
        hangpointElement.setAttribute( "y", "4" );
        new HangPoint( hangpointElement );
        HangPoint hangPoint = new HangPoint( element );

        HangPoint found = HangPoint.Find( "Blather" );

        assertSame( found, hangPoint );
    }

    /*
     * This is to ensure that no exception is thrown if data is OK.
     */
    @Test
    public void justFine() throws Exception {
        new HangPoint( element );
    }

    /**
     * The lack of an id attribute is not an error.
     *
     * @throws Exception
     */
    @Test
    public void noId() throws Exception {
        element.removeAttribute( "id" );
        new HangPoint( element );
    }

    @Test(expectedExceptions = AttributeMissingException.class,
          expectedExceptionsMessageRegExp = "HangPoint \\(Blather\\) is missing required 'x' attribute.")
    public void noX() throws Exception {
        element.removeAttribute( "x" );
        new HangPoint( element );
    }

    @Test(expectedExceptions = AttributeMissingException.class,
          expectedExceptionsMessageRegExp = "HangPoint instance is missing required 'x' attribute.")
    public void noXWithoutID() throws Exception {
        element.removeAttribute( "id" );
        element.removeAttribute( "x" );
        new HangPoint( element );
    }

    @Test(expectedExceptions = AttributeMissingException.class,
          expectedExceptionsMessageRegExp = "HangPoint \\(Blather\\) is missing required 'y' attribute.")
    public void noY() throws Exception {
        element.removeAttribute( "y" );
        new HangPoint( element );
    }

    @Test(expectedExceptions = AttributeMissingException.class,
          expectedExceptionsMessageRegExp = "HangPoint instance is missing required 'y' attribute.")
    public void noYWithoutID() throws Exception {
        element.removeAttribute( "id" );
        element.removeAttribute( "y" );
        new HangPoint( element );
    }

    @Test(expectedExceptions = LocationException.class,
          expectedExceptionsMessageRegExp =
                  "HangPoint x value outside boundary of the venue")
    public void tooLargeX() throws Exception {
        element.setAttribute( "x", "351" );
        new HangPoint( element );
    }

    @Test(expectedExceptions = LocationException.class,
          expectedExceptionsMessageRegExp =
                  "HangPoint y value outside boundary of the venue")
    public void tooLargeY() throws Exception {
        element.setAttribute( "y", "401" );
        new HangPoint( element );
    }

    @Test(expectedExceptions = LocationException.class,
          expectedExceptionsMessageRegExp =
                  "HangPoint x value outside boundary of the venue")
    public void tooSmallX() throws Exception {
        element.setAttribute( "x", "-1" );
        new HangPoint( element );
    }

    @Test(expectedExceptions = LocationException.class,
          expectedExceptionsMessageRegExp =
                  "HangPoint y value outside boundary of the venue")
    public void tooSmallY() throws Exception {
        element.setAttribute( "y", "-1" );
        new HangPoint( element );
    }


    @Test
    public void parseTwoHangPoints() throws Exception {
        String xml = "<plot>" +
                "<hangpoint x=\"20\" y=\"30\" />" +
                "<hangpoint x=\"25\" y=\"35\" />" +
                "</plot>";
        InputStream stream = new ByteArrayInputStream( xml.getBytes() );

        TestResets.MinderReset();

        new Parse( stream );

        // Final size of list
        ArrayList<Minder> list = Drawable.List();
        assertEquals( list.size(), 2 );

        Minder hangpoint = list.get( 0 );
        assert Minder.class.isInstance( hangpoint );
        assert HangPoint.class.isInstance( hangpoint );

        Minder hangpoint2 = list.get( 1 );
        assert Minder.class.isInstance( hangpoint2 );
        assert HangPoint.class.isInstance( hangpoint2 );

        assertNotSame( hangpoint, hangpoint2 );
    }

    @Test
    public void locate() throws Exception {
        HangPoint hangPoint = new HangPoint( element );

        Point location = hangPoint.locate();
        assertEquals( location.x(), (Integer) 296 );
        assertEquals( location.y(), (Integer) 320 );
        assertEquals( location.z(), 240 );
    }

    @Mocked
    Graphics2D mockCanvas;

    @Test
    public void drawPlan() throws Exception {
        HangPoint hangPoint = new HangPoint( element );

        new Expectations() {
            {
                mockCanvas.setPaint( Color.BLUE );
                mockCanvas.draw( new Line2D.Float( 294, 318, 298, 322 ) );
                mockCanvas.draw( new Line2D.Float( 298, 318, 294, 322 ) );
            }
        };
        hangPoint.drawPlan( mockCanvas );
    }

    @Test
    public void drawSection() throws Exception {
        HangPoint hangPoint = new HangPoint( element );

        new Expectations() {
            {
            }
        };
        hangPoint.drawSection( mockCanvas );
    }

    @Test
    public void drawFront() throws Exception {
        HangPoint hangPoint = new HangPoint( element );

        new Expectations() {
            {
            }
        };
        hangPoint.drawFront( mockCanvas );
    }

    @Test
    public void domUnused() throws Exception {
        HangPoint hangPoint = new HangPoint( element );

        hangPoint.dom( null, View.PLAN );
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
        TestResets.MinderReset();

        Element venueElement = new IIOMetadataNode();
        venueElement.setAttribute( "name", "Test Name" );
        venueElement.setAttribute( "width", "350" );
        venueElement.setAttribute( "depth", "400" );
        venueElement.setAttribute( "height", "240" );
        new Venue( venueElement );
//        Venue.Height();

        element = new IIOMetadataNode( "hangpoint" );
        element.setAttribute( "id", "Blather" );
        element.setAttribute( "x", "296" );
        element.setAttribute( "y", "320" );
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }
}