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

    public Item(ImageIcon pic) {
        this.picture = pic;
        // place holder defaults for other fields for now
        fullName = "Anon";
        firstName = "";
        category = "nope";
        metadata = null;
    }

    public Item(String fullName, String firstName, String category) {
        this.picture = null;
        this.firstName = firstName;
        this.fullName = fullName;
        this.category = category;
        metadata = null;
    }
    public Item(String fullName, String firstName, String category, String[] metadata) {
        this.picture = null;
        this.firstName = firstName;
        this.fullName = fullName;
        this.category = category;
        this.metadata = metadata;
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
        return metadata[i];
    }
}
