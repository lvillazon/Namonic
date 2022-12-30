import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

// abstracts a class of children from the gallery PDF exported from SIMS
public class TeachingClass {
    private String name;
    private BufferedImage[] galleryPics;
    private ArrayList<Student> students;
    private Config settings;

    public TeachingClass(Config settings, String galleryFilePath) {
        this.settings = settings;
        galleryPics = ResourceManager.loadGallery(galleryFilePath);
        // grab the filename foobar.pdf from the full path c:\foo\bar\foobar.pdf
        String[] filePathParts = galleryFilePath.split(Pattern.quote(File.separator));
        // discard the file extension
        String fileNameParts = filePathParts[filePathParts.length-1];
        this.name = fileNameParts.split(Pattern.quote("."))[0];

        // create a student object for each face in the gallery
        students = new ArrayList<>();
        for (BufferedImage page: galleryPics) {
            int maxRows = ResourceManager.maxRows(settings, page);
            int maxCols = ResourceManager.maxColumns(settings, page);
            for (int row=0; row<maxRows; row++) {
                for (int col=0; col<maxCols; col++) {
                    BufferedImage pic = ResourceManager.getImageAt(settings, page, row, col);
                    if (!ResourceManager.isBlank(pic)) {
                        students.add(new Student(row, col));
                    }
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public int size() {
        return students.size();
    }
}
