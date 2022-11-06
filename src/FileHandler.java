import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
// download from

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class FileHandler {

    public static ArrayList<String> readWholeFile(String filename) {
        ArrayList<String> results = new ArrayList<>();
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while (line != null) {
                results.add(line);
                //System.out.println(line);
                line = br.readLine();
            }
        }
        catch (IOException e)
        {
            ErrorHandler.ModalMessage(e.getLocalizedMessage() + "  " + filename);
        }
        return results;
    }

    public static BufferedImage readPDF(String filename) {
        try {
            // load PDF
            File file = new File(filename);
            PDDocument document = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage pdfImage = renderer.renderImage(0);  //TODO try other pages
            return pdfImage;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage() + "  " + filename);
            return null;
        }
    }

    public static BufferedImage readNormalImage(String filename) {
        try {
            BufferedImage image = ImageIO.read(new File(filename));
            return image;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage() + "  " + filename);
            return null;
        }
    }
    public static BufferedImage readImage(String filename) {
        if (filename.toUpperCase().endsWith(".PDF")) {
            return readPDF(filename);
        } else {
            return readNormalImage(filename);
        }
    }

    public static void writeToFile(String filename, String text, boolean append) {
        // write text to fileName
        // either overwriting (append = false) or appending (append = true)
        try (
                FileWriter fw = new FileWriter(filename, append);
                PrintWriter pw = new PrintWriter(fw)
        ) {
            pw.println(text);
        }
        catch (IOException e) {
            ErrorHandler.ModalMessage(e.getLocalizedMessage() + "  " + filename);
        }
    }

    public static void writeWholeFile(String filename, ArrayList<String> lines) {
        try (
                FileWriter fw = new FileWriter(filename, false);
                PrintWriter pw = new PrintWriter(fw)
        ) {
            for (String line: lines) {
                pw.println(line);
            }
        }
        catch (IOException e) {
            ErrorHandler.ModalMessage(e.getLocalizedMessage() + "  " + filename);
        }

    }

    public static boolean fileExists(String fileName) {
        File f = new File(fileName);
        return (f.exists());
    }

    public static String[] getAllFiles(String folder) {
        //create a File object from the folder name
        File directoryPath = new File(folder);
        //List of all files and directories
        return directoryPath.list();
    }

    public static String[] getMatchingFiles(String folder, String wildcardExpression) {
        // allows wildcard expressions taking any of these common forms:
        //  foo*.*
        //  *foo.*
        //  foo.*
        //  foo*.bar
        //  *foo.bar
        //  foo.bar
        ArrayList<String> results = new ArrayList<>();
        String[] fileParts = wildcardExpression.split("\\.");
        String matchName = fileParts[0];
        String matchExtension;
        if (fileParts.length>1) {
            matchExtension = fileParts[1];
        } else {
            matchExtension = "";
        }
        String[] allFiles = getAllFiles(folder);
        if (allFiles != null) {
            // loop through all files in the folder and keep the ones that match
            for (String f : getAllFiles(folder)) {
                String[] fParts = f.split("\\.");
                // check the extension matches 1st
                if (matchExtension.equals("") || matchExtension.equals("*") ||
                        (fParts.length > 1 && fParts[1].equals(matchExtension))) {
                    // now check the filename matches
                    if (matchName.startsWith("*")) {
                        String nonWildPart = matchName.substring(1);  // all but the initial *
                        if (fParts[0].endsWith(nonWildPart)) {
                            results.add(folder + "\\" + f);
                            //System.out.println("starting match " + f);
                        }
                    } else if (matchName.endsWith("*")) {
                        String nonWildPart = matchName.substring(0, matchName.length() - 1);  // all but trailing *
                        if (fParts[0].startsWith(nonWildPart)) {
                            results.add(folder + "\\" + f);
                            //System.out.println("ending match " + f);
                        }
                    } else if (matchName.equals(fParts[0])) {
                        results.add(folder + "\\" + f);
                    }
                }
            }
            return results.toArray(new String[results.size()]);
        } else {
            return new String[0];  // return empty array if no files found in the folder
        }
    }

    // save a JPanel as a PNG file (overwrites if it already exists)
    public static void savePanelImage(String filename, JPanel p) {
        try {
            Dimension A4 = new Dimension(297,210); // A4 aspect ratio
            BufferedImage image = new BufferedImage(p.getWidth(), p.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();
            // scale to A4
            //AffineTransform at = new AffineTransform();
            //at.scale(1131, 800);
            p.paint(g2);
            g2.scale(A4.getWidth()/A4.getHeight()*p.getHeight()/p.getWidth(),1.0);
            ImageIO.write(image, "png", new File(filename+".png"));
        }
        catch(Exception e) {
            ErrorHandler.ModalMessage(e.getLocalizedMessage() + "  " + filename);
        }
    }


}