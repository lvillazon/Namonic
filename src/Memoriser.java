import javax.swing.*;
import java.util.ArrayList;

public class Memoriser {
    private ArrayList<Item> items;

    public Memoriser(Config settings) {
        ArrayList<ImageIcon> pictures = ResourceManager.loadImages(settings);
        items = ResourceManager.loadItemData(settings);
        // assign the pictures to the items - should be a 1:1 correspondence
        if (items.size()==pictures.size()) {
            int index = 0;
            for (Item i: items) {
                i.setPicture(pictures.get(index));
                index++;
            }
        } else {
            ErrorHandler.ModalMessage("image count doesn't match name count:" + pictures.size() + " vs " + items.size());
        }
    }

    public Item getItem(int i) {
        return items.get(i);
    }

    public int getTotalItems() {
        return items.size();
    }
}
