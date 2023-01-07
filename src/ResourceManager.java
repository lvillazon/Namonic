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
        int x = leftMargin + column * (width * hSpacing);
        BufferedImage subImage = img.getSubimage(x, y, width, height-nameHeight);
        // scale it to a suitable size
        Image scaled = subImage.getScaledInstance(
                                        settings.getInt("DISPLAY_IMAGE_WIDTH")/scale,
                                        settings.getInt("DISPLAY_IMAGE_HEIGHT")/scale,
                                        Image.SCALE_SMOOTH);
        return toBufferedImage(scaled);
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
        int x = leftMargin + column * (width * hSpacing);
        BufferedImage subImage = img.getSubimage(x, y, width, nameHeight);
        // scale it to a suitable size
        double scaledHeight = (double)settings.getInt("DISPLAY_IMAGE_HEIGHT") /
                (double)settings.getInt("GALLERY_IMAGE_HEIGHT") *
                settings.getInt(("GALLERY_NAMEPLATE_HEIGHT"));
        /*
        Image scaled = subImage.getScaledInstance(
                settings.getInt("DISPLAY_IMAGE_WIDTH"),
                (int)scaledHeight,
                Image.SCALE_SMOOTH);
        */
        Image scaled = subImage.getScaledInstance(200,50,Image.SCALE_SMOOTH);
        return toBufferedImage(scaled);

    }
/*
    public static ArrayList<Item> loadItemData(Config settings) {
        ArrayList<String> rawData = FileHandler.readWholeFile(settings.getString("NAME_FILE"));
        ArrayList<Item> results = new ArrayList<>();
        int i = settings.getInt("NAME_FILE_HEADER_LINES");  // skip any header lines in the file
        String category = "none";
        String[] validCategories = settings.getString("NAME_CATEGORIES").split(",");
        int count=0;
        int catCount=0;
        while (i<rawData.size()) {
            String line = rawData.get(i);
            // check for new category
            String prefix = settings.getString("CATEGORY_PREFIX");
            if (line.startsWith(prefix)) {
                for (String cat: validCategories) {
                    // check if the next chars after the category prefix match one of the valid categories
                    if (line.substring(prefix.length()).strip().startsWith(cat.strip())) {
                        System.out.println(catCount+" read in category "+category);
                        category = cat;  // switch to this category
                        catCount=0;
                    }
                }
                i = i + settings.getInt("CATEGORY_HEADER_LINES");  // skip over any header row
            } else if (!line.startsWith(settings.getString("NAME_SKIP_LINE")) && line.strip().length()>0){
                // treat the line as the entry for a new item
                String[] parts = line.split("\t");  // split on tab
                // first part is the name, everything else is metadata
                String name = parts[0];
                String firstName = "";
                if (settings.getBool("SURNAME_FIRSTNAME")) {  // separate out the first name
                    String[] name_parts = name.split(",");
                    if (name_parts.length>1) {
                        firstName = name_parts[1].trim();
                        System.out.println(firstName);
                    } else {
                        ErrorHandler.ModalMessage("Invalid item name:" + name);
                    }
                }
                Item thisItem;
                if (parts.length > 1) {
                    String[] metadata = new String[parts.length-1];
                    for (int j=1; j<parts.length; j++) {
                        metadata[j - 1] = parts[j];
                    }
                    thisItem = new Item(name, firstName, category, metadata);
                } else {
                    thisItem = new Item(name, firstName, category);  // no metadata
                }
                results.add(thisItem);
                count++;
                catCount++;
            }
            i++;
        }
        //for(int j=0; j< results.size(); j++) {
        //    System.out.println(results.get(j).getName());
        //}
        System.out.println(count+" countries read");
        return results;
    }
*/
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
                                if (!isBlank(toBufferedImage(scaled))) {
                                    images.add(new ImageIcon(scaled));
                                    count++;
                                }
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
