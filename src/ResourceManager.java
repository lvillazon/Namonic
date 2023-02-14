import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class ResourceManager {

    public static String[] getGalleryFiles(Config settings) {
        // look for all image gallery files that match the wildcard expression in the config file
        String[] galleryFiles = FileHandler.getMatchingFiles(
                settings.getString("GALLERY_FOLDER"),
                settings.getString("GALLERY_FILE_WILDCARD"));
        return galleryFiles;
    }

    // grab each page of a specific PDF and return as BufferedImages
    public static BufferedImage[] loadGallery(String filepath) {
        try {
            BufferedImage[] galleryImages = FileHandler.readImages(filepath);
            return galleryImages;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage() + "  " + filepath);
            return null;
        }
    }

    // how many rows of portrait images would fit in the gallery grid
    public static int maxRows(Config settings, BufferedImage img) {
        return (img.getHeight()
                - settings.getInt("GALLERY_TOP_MARGIN")
                - settings.getInt("GALLERY_BOTTOM_MARGIN"))
                / (settings.getInt("GALLERY_IMAGE_HEIGHT") + settings.getInt("GALLERY_V_SPACING"));
    }

    // how many columns of portrait images would fit in the gallery grid
    public static int maxColumns(Config settings, BufferedImage img) {
        return (img.getWidth()
                - settings.getInt("GALLERY_LEFT_MARGIN"))
                / (settings.getInt("GALLERY_IMAGE_WIDTH") + settings.getInt("GALLERY_H_SPACING"));
    }

    public static BufferedImage getImageAt(Config settings, BufferedImage img, int row, int column) {
        // size, position and spacing of the pictures in the grid imported from SIMS
        int scale = 1;
        int topMargin = settings.getInt("GALLERY_TOP_MARGIN");
        int leftMargin = settings.getInt("GALLERY_LEFT_MARGIN");
        int hSpacing = settings.getInt("GALLERY_H_SPACING");
        int vSpacing = settings.getInt("GALLERY_V_SPACING");
        int width = settings.getInt("GALLERY_IMAGE_WIDTH");
        int nameHeight = settings.getInt("GALLERY_NAMEPLATE_HEIGHT");
        int height = settings.getInt("GALLERY_IMAGE_HEIGHT");

        int y = topMargin + row * (height + vSpacing);
        int x = leftMargin + column * (width + hSpacing);
        // clip image if it extends beyond the gallery page
        if (x+width > img.getWidth()) {
            x = img.getWidth()-width;
        }
        if (y+height > img.getHeight()) {
            y = img.getHeight()-height;
        }
        // the offsets are to crop slightly, which removes any border artefacts
        BufferedImage subImage = img.getSubimage(x+2, y+2, width-4, height-nameHeight-4);
        return toBufferedImage(subImage);
    }

    public static BufferedImage getNameplateAt(Config settings, BufferedImage img, int row, int column) {
        // size, position and spacing of the pictures in the grid imported from SIMS
        int topMargin = settings.getInt("GALLERY_TOP_MARGIN");
        int bottomMargin = settings.getInt("GALLERY_BOTTOM_MARGIN");
        int leftMargin = settings.getInt("GALLERY_LEFT_MARGIN");
        int hSpacing = settings.getInt("GALLERY_H_SPACING");
        int vSpacing = settings.getInt("GALLERY_V_SPACING");
        int width = settings.getInt("GALLERY_IMAGE_WIDTH");
        int nameHeight = settings.getInt("GALLERY_NAMEPLATE_HEIGHT");
        int height = settings.getInt("GALLERY_IMAGE_HEIGHT");

        int y = topMargin + row * (height + vSpacing) + height - nameHeight;
        int x = leftMargin + column * (width + hSpacing);
        // the offsets are to crop slightly, which removes any border artefacts
        BufferedImage subImage = img.getSubimage(x+2, y+1, width-4, nameHeight-2);
        return toBufferedImage(subImage);
    }

    public static ArrayList<ImageIcon> loadImages(Config settings) {
        // size, position and spacing of the pictures in the grid imported from SIMS
        int topMargin = settings.getInt("GALLERY_TOP_MARGIN");
        int bottomMargin = settings.getInt("GALLERY_BOTTOM_MARGIN");
        int leftMargin = settings.getInt("GALLERY_LEFT_MARGIN");
        int hSpacing = settings.getInt("GALLERY_H_SPACING");
        int vSpacing = settings.getInt("GALLERY_V_SPACING");
        int width = settings.getInt("GALLERY_IMAGE_WIDTH");
        int height = settings.getInt("GALLERY_IMAGE_HEIGHT");
        int nameHeight = settings.getInt("GALLERY_NAMEPLATE_HEIGHT");

        ArrayList<ImageIcon> images = new ArrayList<>();

        // look for all image gallery files that match the wildcard expression
        String[] galleryFiles = FileHandler.getMatchingFiles(
                settings.getString("GALLERY_FOLDER"),
                settings.getString("GALLERY_FILE_WILDCARD"));
        if (galleryFiles.length > 0) {
            for (String filename : galleryFiles) {
                try {
                    BufferedImage[] galleryImages = FileHandler.readImages(filename);
                    /* OCR disabled for now
                    String[] text = OCR.readTextFromImage(galleryImages[0]);
                    for(String s:text) {
                        System.out.println(s);
                    }

                     */
                    int count = 0;
                    for (int page=0; page<galleryImages.length; page++) {
                        int y = topMargin;
                        while (y + height < galleryImages[page].getHeight() - bottomMargin) {
                            int x = leftMargin;
                            while (x + width < galleryImages[page].getWidth()) {
                                ImageIcon icon = new ImageIcon(galleryImages[page].getSubimage(x, y, width, height-100));
                                // scale it to a suitable size
                                Image scaled = icon.getImage().getScaledInstance(
                                        settings.getInt("DISPLAY_IMAGE_WIDTH"),
                                        settings.getInt("DISPLAY_IMAGE_HEIGHT"),
                                        Image.SCALE_SMOOTH);
                                // DEBUG disabled the check for a blank image
                                // testing this for by checking every pixel is too slow and generates
                                // false negatives from edge effects, as well as false positives from
                                // incorrect offsets in the config file
                                //if (!isBlank(toBufferedImage(scaled))) {
                                    images.add(new ImageIcon(scaled));
                                    count++;
                                //}
                                x = x + width + hSpacing;
                            }
                            y = y + height + vSpacing;
                        }
                    }
                    System.out.println(count + " images read from " + filename);
                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage() + "  " + filename);
                }
            }
        }
        return images;
    }


    // TODO OCR the class names from the top of each gallery sheet
    /*
    public ArrayList<String> getCategoryNames(){
        ArrayList<String> text = new ArrayList<>();
        for (BufferedImage sheet: getGalleryImages()) {
            text.add(OCRClass.getStringWithOCR(sheet, null, null));
        }
        return text;
    }

     */

    // checks for an empty image by scanning a 10 x 10 grid of pixels
    // in the middle of the image. If they are all the same colour, we assume the image is blank
    // since these are student portraits against a white background
    // the central pixels should contain face, and therefore will be a variety of different colours
    // This method is somewhat tolerant of cropping glitches, where a small amount of image from an
    // adjacent pic is included at one edge
    public static boolean isBlank(BufferedImage img) {
        int middleX = img.getWidth()/2;
        int middleY = img.getHeight()/2;
        int colour = img.getRGB(middleX, middleY);
        for (int x=middleX-5; x<middleX+5; x++) {
            for (int y=middleY-5; y<middleY+5; y++) {
                if (img.getRGB(x,y) != colour) {
                    return false;
                }
            }
        }
        return true;
    }

    // conversion routine
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
