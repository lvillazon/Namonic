import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class ResourceManager {

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

    public static ArrayList<ImageIcon> loadImages(Config settings) {
        // size, position and spacing of the pictures in the grid imported from SIMS
        int topMargin = settings.getInt("GALLERY_TOP_MARGIN");
        int bottomMargin = settings.getInt("GALLERY_BOTTOM_MARGIN");
        int leftMargin = settings.getInt("GALLERY_LEFT_MARGIN");
        int hSpacing = settings.getInt("GALLERY_H_SPACING");
        int vSpacing = settings.getInt("GALLERY_V_SPACING");
        int width = settings.getInt("GALLERY_IMAGE_WIDTH");
        int height = settings.getInt("GALLERY_IMAGE_HEIGHT");

        ArrayList<ImageIcon> images = new ArrayList<>();

        // look for all image gallery files that match the wildcard expression
        String[] galleryFiles = FileHandler.getMatchingFiles(
                settings.getString("GALLERY_FOLDER"),
                settings.getString("GALLERY_FILE_WILDCARD"));
        if (galleryFiles.length > 0) {
            for (String filename : galleryFiles) {
                try {
                    BufferedImage[] galleryImages = FileHandler.readImages(filename);

                    int count = 0;
                    for (int page=0; page<galleryImages.length; page++) {
                        int y = topMargin;
                        while (y + height < galleryImages[page].getHeight() - bottomMargin) {
                            int x = leftMargin;
                            while (x + width < galleryImages[page].getWidth()) {
                                ImageIcon icon = new ImageIcon(galleryImages[page].getSubimage(x, y, width, height));
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

    // returns true if the image contains pixels all the same colour
    public static boolean isBlank(BufferedImage img) {
        int colour = img.getRGB(0,0);
        for (int x=0; x<img.getWidth(); x++) {
            for (int y=0; y< img.getHeight(); y++) {
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
