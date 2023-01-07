import javax.swing.*;
import java.awt.image.BufferedImage;

public class Student {
//    private int row;
//    private int column;
    private int id;  // should be unique for each student in a class
    private ImageIcon pic;
    private ImageIcon nameplate;
    private int showCount;
    private int correctCount;

    public Student(int id, BufferedImage pic, BufferedImage nameplate) {
        this.id = id;
        this.pic = new ImageIcon(pic);
        this.nameplate = new ImageIcon(nameplate);
        showCount = 0;
        correctCount = 0;
    }

    // return -ve if "less than", +ve if "greater than" or 0 if the same
    // the item with the lowest correctCount is the least
    // in the event of a draw, the showCount is used to decide
    public int compareTo(Student other) {
        int difference = correctCount - other.correctCount;
        if (difference == 0) {
            difference = showCount - other.showCount;
        }
        return difference;
    }

    public String getID() {
        return Integer.toString(id);
    }

    public ImageIcon getPicture() {
        return pic;
    }

    public int getShowCount() { return showCount; }
    public void markShown() { showCount++; }
    public int getCorrectCount() { return correctCount; }
    public void markCorrect() { correctCount++; }
    public void setScore(int s) {
        correctCount = s;
    }

    public void setShown(int s) {
        if (s>correctCount) {
            showCount = s;
        } else {
            showCount = correctCount;  // impossible to have got it right more times than you were shown it!
        }
    }

    // TODO this will be replaced by the cropped bottom part of the image, showing the name,
    // as an ImageIcon, that can be displayed directly on the JButton
    // longterm, maybe replace with OCRed text
    public ImageIcon getNameplate() {
        return nameplate;
    }

}
