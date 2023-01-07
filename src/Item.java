/* Item has now been replaced with Student

import javax.swing.*;
import java.util.HashMap;

// a generic item to be memorised
// each item has an image associated with a name
// plus a category and some metadata used for tracking memorisation of other subsets
// and some stats to track how often it has been shown, guessed correctly etc
public class Item {
    private ImageIcon picture;
    private String fullName;
    private String firstName;
    private String category;
    private String[] metadata;
    private int showCount;
    private int correctCount;

    public Item(ImageIcon pic) {
        this.picture = pic;
        // place holder defaults for other fields for now
        fullName = "Anon";
        firstName = "";
        category = "nope";
        metadata = null;
        showCount = 0;
        correctCount = 0;
    }

    public Item(String fullName, String firstName, String category) {
        this.picture = null;
        this.firstName = firstName;
        this.fullName = fullName;
        this.category = category;
        metadata = null;
        showCount = 0;
        correctCount = 0;
    }

    public Item(String fullName, String firstName, String category, String[] metadata) {
        this.picture = null;
        this.firstName = firstName;
        this.fullName = fullName;
        this.category = category;
        this.metadata = metadata;
        showCount = 0;
        correctCount = 0;
    }

    public ImageIcon getPicture() {
        return picture;
    }

    public void setPicture(ImageIcon pic) {
        this.picture = pic;
    }

    public String getName() {
        if (firstName.equals("")) {
            return fullName;
        } else {
            return firstName;
        }
    }

    public String getFullName() {
        return fullName;
    }

    public String getCategory() { return category;}
    public String getMetadata(int i) {
        if (metadata!=null && i<metadata.length) {
            return metadata[i];
        } else {
            return "";
        }
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

    // return -ve if "less than", +ve if "greater than" or 0 if the same
    // the item with the lowest correctCount is the least
    // in the event of a draw, the showCount is used to decide
    public int compareTo(Item other) {
        int difference = correctCount - other.correctCount;
        if (difference == 0) {
            difference = showCount - other.showCount;
        }
        return difference;
    }
}


 */