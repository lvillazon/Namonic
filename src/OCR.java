import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

public class OCR {
    public static String[] readTextFromImage(BufferedImage image) throws TesseractException, IOException {
        ITesseract instance = new Tesseract();
        instance.setDatapath(LoadLibs.extractTessResources("tessdata").getAbsolutePath()); // path to tessdata directory
        instance.setLanguage("eng"); // language for OCR

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        // Use OCR to read text from image
        String result = instance.doOCR(image);

        // Split text into an array of lines
        String[] lines = result.split("\\r?\\n");

        return lines;
    }
}