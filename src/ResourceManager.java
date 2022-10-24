import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class ResourceManager {

    public static ArrayList<ImageIcon> loadImages(Config settings) {
        String filename =
                settings.getString("GALLERY_FILE_PREFIX")
                        + "0" + settings.getString("GALLERY_FILE_EXTENSION");
        // size, position and spacing of the pictures in the grid imported from SIMS
        int topMargin = settings.getInt("GALLERY_TOP_MARGIN");
        int leftMargin = settings.getInt("GALLERY_LEFT_MARGIN");
        int columns = settings.getInt("GALLERY_COLUMNS");
        int rows = settings.getInt("GALLERY_ROWS");
        int hSpacing = settings.getInt("GALLERY_H_SPACING");
        int vSpacing = settings.getInt("GALLERY_V_SPACING");
        int width = settings.getInt("GALLERY_IMAGE_WIDTH");
        int height = settings.getInt("GALLERY_IMAGE_HEIGHT");
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
            ErrorHandler.ModalMessage(e.getLocalizedMessage() + "  " + filename);
        }
        return images;

    }
}
