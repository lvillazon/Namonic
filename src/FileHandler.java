import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class FileHandler {

    public static ArrayList<String> readWholeFile(String filename) {
        ArrayList<String> results = new ArrayList();
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