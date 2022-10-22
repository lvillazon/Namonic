import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class ResourceManager {

    // size, position and spacing of the pictures in the grid imported from SIMS
    private static final int topMargin = 3;
    private static final int leftMargin = 3;
    private static final int columns = 6;
    private static final int rows = 4;
    private static final int hSpacing = 14;
    private static final int vSpacing = 15;
    private static final int width = 147;
    private static final int height = 140;

    public static ArrayList<ImageIcon> loadImages(String filename) {
        ArrayList<ImageIcon> images = new ArrayList<>();
        try{
            BufferedImage galleryImage = ImageIO.read(new File(filename));
            for (int row=0; row<rows; row++) {
                for (int col=0; col<columns; col++) {
                    ImageIcon icon = new ImageIcon(galleryImage.getSubimage(
                            leftMargin + col * (width + hSpacing),
                            topMargin + row * (height + vSpacing),
                            width,
                            height));
                    images.add(icon);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return images;

    }
}
