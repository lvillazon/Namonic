import javax.swing.*;
import java.util.HashMap;

// a generic item to be memorised
// each item has an image associated with a name
// plus a category and some metadata used for tracking memorisation of other subsets
// and some stats to track how often it has been shown, guessed correctly etc
public class Item {
    private ImageIcon picture;
    private String name;
    private String category;
    private String[] metadata;

    public Item(ImageIcon pic) {
        this.picture = pic;
        // place holder defaults for other fields for now
        name = "Anon";
        category = "none";
        metadata = null;
    }

    public Item(String name, String category) {
        this.picture = null;
        this.name = name;
        this.category = category;
        metadata = null;
    }
    public Item(String name, String category, String[] metadata) {
        this.picture = null;
        this.name = name;
        this.category = category;
        this.metadata = metadata;
    }
    public ImageIcon getPicture() {
        return picture;
    }

    public void setPicture(ImageIcon pic) {
        this.picture = pic;
    }

    public String getName() { return name;}
    public String getCategory() { return category;}
}
