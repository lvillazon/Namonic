import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class ResourceManager {

    public static ArrayList<ImageIcon> loadImages(Config settings) {
        // size, position and spacing of the pictures in the grid imported from SIMS
        int topMargin = settings.getInt("GALLERY_TOP_MARGIN");
        int leftMargin = settings.getInt("GALLERY_LEFT_MARGIN");
        int hSpacing = settings.getInt("GALLERY_H_SPACING");
        int vSpacing = settings.getInt("GALLERY_V_SPACING");
        int width = settings.getInt("GALLERY_IMAGE_WIDTH");
        int height = settings.getInt("GALLERY_IMAGE_HEIGHT");

        // look for all image gallery files that match the wildcard expression
        String[] galleryFiles = FileHandler.getMatchingFiles(
                settings.getString("GALLERY_FOLDER"),
                settings.getString("GALLERY_FILE_WILDCARD"));
        if (galleryFiles.length == 0) {
            ErrorHandler.ModalMessage("No gallery files found! Check cfg file");
        }
        ArrayList<ImageIcon> images = new ArrayList<>();
        for(String filename: galleryFiles) {
            try{
                BufferedImage galleryImage = ImageIO.read(new File(filename));
                int y = topMargin;
                while (y+height < galleryImage.getHeight()) {
                    int x = leftMargin;
                    while (x + width < galleryImage.getWidth()) {
                        ImageIcon icon = new ImageIcon(galleryImage.getSubimage(x, y, width, height));
                        images.add(icon);
                        x = x + width + hSpacing;
                    }
                    y = y + height + vSpacing;
                }
            } catch(Exception e){
                ErrorHandler.ModalMessage(e.getLocalizedMessage() + "  " + filename);
            }
        }
        return images;
    }
}
