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
        int id=0;
        for (BufferedImage page: galleryPics) {
            int maxRows = ResourceManager.maxRows(settings, page);
            int maxCols = ResourceManager.maxColumns(settings, page);
            for (int row=0; row<maxRows; row++) {
                for (int col=0; col<maxCols; col++) {
                    BufferedImage pic = ResourceManager.getImageAt(settings, page, row, col);
                    if (!ResourceManager.isBlank(pic)) {
                        BufferedImage namePlate = ResourceManager.getNameplateAt(settings, page, row, col);
                        students.add(new Student(id, pic, namePlate));
                        id++;
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

    // return a student by index number
    public Student getStudent(int i) {
        return students.get(i);
    }

    // percentage correct over all sessions, rounded to int
    public int classScore() {
        int totalScore = 0;
        int totalShown = 0;
        for (int i=0; i<size(); i++) {
            totalScore += getStudent(i).getCorrectCount();
            totalShown += getStudent(i).getShowCount();
        }
        if (totalShown>0) {
            return (int) ((double)totalScore / (double)totalShown * 100);
        } else {
            return 0;
        }
    }

}
