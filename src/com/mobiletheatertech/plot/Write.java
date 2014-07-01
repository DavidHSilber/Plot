package com.mobiletheatertech.plot;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.jopendocument.dom.OOUtils;


/**
 * <code>Write</code> deals with output file issues and relies on {@link Draw Draw} to generate SVG
 * content.
 *
 * @author dhs
 * @since 0.0.1
 */
public class Write {

    private String home = null;

    private String CSS = "\n"
            + ".heading { font-size: 14pt; text-anchor: middle; font-weight: bold; stroke: none }\n"
            + "iframe { display: inline }\n"
            + "@media print {\n"
            + "  .noprint { display: none }"
            + "}\n";


    /**
     * Draw each of the Plot items that have been defined to a SVG file.
     * <p/>
     * The complete pathname is built as {@literal <user's home directory>}{@code
     * /Plot/out/}{@literal <basename>}{@code .xml}
     * <p/>
     * The non-drawing updates to the generated SVG document have to be done after all of the
     * drawing is completed. Because of this, {@code Write} needs to interact with {@link Draw} and
     * {@link Minder} methods in a very specific order.
     *
     * @param basename basename of the file to be written.
     */
    public Write(String basename) throws MountingException, ReferenceException {
        home = System.getProperty("user.home");
//        if (null == home) {
//            // throw exception
//        }

        String pathname = home + "/Dropbox/Plot/out/" + basename;

        writeDirectory(pathname);
        writeIndex(pathname);
        writeStyles(pathname);
        writePlan(pathname);
        writeSection(pathname);
        writeFront(pathname);
        writeTruss(pathname);
        writeSpreadsheet(pathname);
    }

    private void writeDirectory(String basename) /*throws MountingException, ReferenceException*/ {
        File directory = new File(basename);
        Boolean dir = directory.mkdir();
        System.err.println("Directory: " + basename + ". Good? " + dir.toString());
    }

    private void writeIndex(String basename) throws ReferenceException {
        String filename = basename + "/index.html";
        File file = new File(filename);

        Integer width = Venue.Width() + Legend.PlanWidth();
        width += width / 100 + 5;

        String output = "" +
                "<!DOCTYPE html>\n" +
                "<head>\n" +
                "<link rel=\"stylesheet\" href=\"styles.css\" type=\"text/css\">\n" +
                "<title>" + Venue.Name() + "</title>\n" +
                "<script>\n" +
                "function show( victim )\n" +
                "{\n" +
                "  var links = plan.document.getElementsByClassName( victim );\n" +
                "  for (var i=0; i < links.length; i++) {\n" +
                "      links[i].setAttribute(\"visibility\", \"visible\");\n" +
                "  } \n" +
                "}\n" +
                "function hide( victim )\n" +
                "{\n" +
                "  var links = plan.document.getElementsByClassName( victim );\n" +
                "  for (var i=0; i < links.length; i++) {\n" +
                "      links[i].setAttribute(\"visibility\", \"hidden\");\n" +
                "  } \n" +
                "}\n" +
//                "function process()\n" +
//                "{\n" +
//                "  if( document.getElementById('process').checked)\n" +
//                "    show( \"chairblock\" );\n" +
//                "  else\n" +
//                "    hide( \"chairblock\" );\n" +
//                "}\n" +
                HTML.SelectFunction(ChairBlock.LAYERTAG) +
                HTML.SelectFunction(HangPoint.LAYERTAG) +
                HTML.SelectFunction(Luminaire.LAYERTAG) +
                HTML.SelectFunction(Luminaire.INFOLAYERTAG) +
                HTML.SelectFunction(Pipe.LAYERTAG) +
                HTML.SelectFunction(Zone.LAYERTAG) +

//                "function selectLayer()\n" +
//                "{\n" +
//                "  if( document.getElementById('" + HangPoint.LAYERTAG + "layer').checked)\n" +
//                "    show( \"" + HangPoint.LAYERTAG + "\" );\n" +
//                "  else\n" +
//                "    hide( \"" + HangPoint.LAYERTAG + "\" );\n" +
//                "}\n" +
//                "function selectLayer()\n" +
//                "{\n" +
//                "  if( document.getElementById('" + Luminaire.LAYERTAG + "layer').checked)\n" +
//                "    show( \"" + Luminaire.LAYERTAG + "\" );\n" +
//                "  else\n" +
//                "    hide( \"" + Luminaire.LAYERTAG + "\" );\n" +
//                "}\n" +
                "function selectsetup()\n" +
                "{\n" +
                "  for(i=0;i<document.configure.setup.length; i++) {\n" +
                "    if( document.configure.setup[i].checked==true) {\n" +
                "      setsetup( document.configure.setup[i].value );\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "function setsetup(tag)\n" +
                "{\n" +
                "  alert( tag );\n" +
                "}\n" +
                "</script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"noprint\" >\n" +
                "<form name=\"configure\" >\n" +
                Setup.List() +
                HTML.Checkboxes(Layer.List()) +
//                "<input type=\"checkbox\" onclick=\"parent.process();\" name=\"show chairs\"" +
//                " id=\"process\" checked=\"checked\" /> Session Chairs\n" +
                "</form>\n" +
                "</div>\n" +
                "<iframe id=\"plan\" src=\"plan.svg\" height=\"" + Venue.Depth() + "\" width=\"" +
//                Venue.Width() + "\" ></iframe>\n" +
//                "<iframe id=\"plan\" src=\"legend.html\" height=\"" + Venue.Depth() + "\" width=\"" +
                width + "\" ></iframe>\n" +
                "</body>\n" +
                "</html>\n";

        try (FileOutputStream stream = new FileOutputStream(file)) {
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] bytes = output.getBytes();

            stream.write(bytes);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeStyles(String basename) throws ReferenceException {
        String filename = basename + "/styles.css";
        File file = new File(filename);


        try (FileOutputStream stream = new FileOutputStream(file)) {
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] bytes = CSS.getBytes();

            stream.write(bytes);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writePlan(String basename) throws MountingException, ReferenceException {
//        System.out.println( "writePlan: About to start.");
        Draw draw = new Draw();

        Minder.DrawAllPlan(draw.canvas());

//        Hack.Draw(draw.canvas());

        draw.getRoot();

        // Specify the size of the generated SVG so that when it is larger than the display area,
        // scrollbars will be provided.
        Element rootElement = draw.root();
        Integer width = Venue.Width() + Legend.PlanWidth();
        width += width / 100;
        rootElement.setAttribute("width", width.toString());
        Integer height = Venue.Depth();
        height += height / 100;
        rootElement.setAttribute("height", height.toString());
//        rootElement.setAttribute( "overflow", "visible" );

        Text textNode = draw.document().createCDATASection(CSS);
//                .createTextNode( "\\<![CDATA[" +CSS+"]]\\>");
        Element style = draw.element("style");
        style.setAttribute("type", "text/css");
        style.appendChild(textNode);
        rootElement.appendChild(style);


        Grid.DOM(draw);

        Legend.Startup(draw, View.PLAN, Venue.Width() + 5, Legend.PlanWidth() );

//        System.out.println( "writePlan: About to MinderDom.DomAllPlan.");

        MinderDom.DomAllPlan(draw);

//        System.out.println( "writePlan: About to Hack.Dom.");
        Hack.Dom(draw, View.PLAN );

//        System.out.println( "writePlan: About to Legend.Callback.");
        Legend.Callback();

        String pathname = basename + "/plan.svg";

        draw.create(pathname);
//        System.out.println( "writePlan: done.");
    }

    private void writeSection(String basename) throws MountingException, ReferenceException {
        Draw draw = new Draw();

        Minder.DrawAllSection(draw.canvas());

//        Hack.Draw( drawPlan.canvas() );

        draw.getRoot();

        Minder.DomAllSection(draw);

        Hack.Dom(draw, View.SECTION);

        String pathname = basename + "/section.svg";

        draw.create(pathname);
    }

    private void writeFront(String basename) throws MountingException, ReferenceException {
        Draw draw = new Draw();

        Minder.DrawAllFront(draw.canvas());

//        Hack.Draw( drawPlan.canvas() );

        draw.getRoot();

        Minder.DomAllFront(draw);

        Hack.Dom(draw, View.FRONT);

        String pathname = basename + "/front.svg";

        draw.create(pathname);
    }

    private void writeTruss(String basename) throws MountingException, ReferenceException {
        System.out.println( "writeTruss: About to start.");
        Draw draw = new Draw();

        draw.getRoot();

        // Specify the size of the generated SVG so that when it is larger than the display area,
        // scrollbars will be provided.
        Element rootElement = draw.root();
        Integer width = 1000;
        width += width / 100;
        rootElement.setAttribute("width", width.toString());
        Integer height = Venue.Depth();
        height += height / 100;
        rootElement.setAttribute("height", height.toString());
//        rootElement.setAttribute( "overflow", "visible" );

        Text textNode = draw.document().createCDATASection(CSS);
//                .createTextNode( "\\<![CDATA[" +CSS+"]]\\>");
        Element style = draw.element("style");
        style.setAttribute("type", "text/css");
        style.appendChild(textNode);
        rootElement.appendChild(style);


//        Grid.DOM(draw);

        // Hardcoded values here are for Arisia '14 flying truss.
        Legend.Startup(draw, View.TRUSS, 700, 300 );

//        System.out.println( "writeTruss: About to MinderDom.DomAllTruss.");

        MinderDom.DomAllTruss(draw);

//        System.out.println( "writeTruss: About to Hack.Dom.");
        Hack.Dom(draw, View.TRUSS);

//        System.out.println( "writeTruss: About to Legend.Callback.");
        Legend.Callback();

        String pathname = basename + "/truss.svg";

        draw.create(pathname);
//        System.out.println( "writeTruss: done.");
    }

    private void writeSpreadsheet( String basename) {
        // Create the data to save.
        final Object[][] data = GearList.Report();
//        new Object[6][2];
//        data[0] = new Object[] { "January", 1 };
//        data[1] = new Object[] { "February", 3 };
//        data[2] = new Object[] { "March", 8 };
//        data[3] = new Object[] { "April", 10 };
//        data[4] = new Object[] { "May", 15 };
//        data[5] = new Object[] { "June", 18 };

        String[] columns = new String[] { "Item", "Quantity" };

        TableModel model = new DefaultTableModel(data, columns);

        // Save the data to an ODS file and open it.
        final File file = new File(basename+"/gear.ods");

        try {
        SpreadSheet.createEmpty(model).saveAs(file);

//        OOUtils.open(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        };

    }

//
//    private String checkboxes( HashMap<String,String> data){
//        return "";
//    }
}

