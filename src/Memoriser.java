import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class Memoriser {
    private ArrayList<Item> allItems;
    private ArrayList<Item> filteredItems;
    private String[] metadataNames;  // titles of item metadata, eg "Gender", "PP"
    private String[] categoryList;  // titles of item categories, eg "Year 7", "Year 8"
    private boolean[] categoryIncluded;  // whether or not this category is included in random choices of items
    private Random rng;

    public Memoriser(Config settings) {
        ArrayList<ImageIcon> pictures = ResourceManager.loadImages(settings);
        allItems = ResourceManager.loadItemData(settings);
        metadataNames = settings.getString("ITEM_META").split(",");
        categoryList = settings.getString("NAME_CATEGORIES").split(",");
        categoryIncluded = new boolean[categoryList.length];
        for (int i=0; i<categoryIncluded.length; i++) {  // initialise with all cats selected
            categoryIncluded[i] = true;
        }
        // assign the pictures to the items - should be a 1:1 correspondence
        if (allItems.size()==pictures.size()) {
            int index = 0;
            for (Item i: allItems) {
                i.setPicture(pictures.get(index));
                index++;
            }
        } else {
            ErrorHandler.ModalMessage("image count doesn't match name count:" + pictures.size() + " vs " + allItems.size());
        }
        filteredItems = getFilteredItems();
        rng = new Random();  // used for all random choices
    }

    public Item getItem(int i) {
        return allItems.get(i);
    }

    public int getTotalItems() {
        return filteredItems.size();
    }

    public String getMetadataName(int i) {
        if (i<metadataNames.length) {
            return metadataNames[i];
        } else {
            return "";
        }
    }

    public int getMetadataCount() {
        return metadataNames.length;
    }

    public String getCategoryName(int i) {
        if (i<categoryList.length) {
            return categoryList[i];
        } else {
            return "";
        }
    }

    public int getCategoryCount() {
        return categoryList.length;
    }

    public boolean isCategoryIncluded(int i) {
        return categoryIncluded[i];
    }

    // does the same as the above but uses the category name string , instead of its index number
    public boolean isCategoryIncluded(String categoryName) {
        int catNumber = -1;
        for (int i=0; i<categoryList.length; i++) {
            if (categoryList[i].equals(categoryName)) {
                catNumber = i;
            }
        }
        if (catNumber!=-1 && isCategoryIncluded(catNumber)) {
            return true;
        } else {
            return false;
        }
    }

    public void setCategoryIncluded(int i, boolean state) {
        categoryIncluded[i] = state;
        filteredItems = getFilteredItems();
    }

    // return a list of only those items that match current filters
    private ArrayList<Item> getFilteredItems() {
        ArrayList<Item> results = new ArrayList<>();
        for (Item i: allItems) {
            if (isCategoryIncluded(i.getCategory())) {
                results.add(i);
            }
        }
        return results;
    }

    // return a list of possible answers, 1 of which is correct, the others are random
    // names should also be chosen from the same category
    // and optionally where the metadata also matches
    public String[] getChoices(Item correct, int numberOfChoices) {
        String[] results = new String[numberOfChoices];
        for (int i=0; i<numberOfChoices; i++) {
            int j = rng.nextInt(getTotalItems());
            // keep picking random items until it is not the same as the correct one but is the same category
            while (allItems.get(j) == correct || !allItems.get(j).getCategory().equals(correct.getCategory())) {
                j = rng.nextInt(getTotalItems());
            }
            results[i] = allItems.get(j).getName();
        }
        // overwrite one of the choice at random with the correct option
        results[rng.nextInt(numberOfChoices)] = correct.getName();
        return results;
    }

    // pick an item at random from all the currently selected categories
    public Item chooseRandomly() {
        // keep picking random items until it is one of the currently selected categories
        boolean ok = false;
        int i = -1;
        while (!ok) {
            i = rng.nextInt(getTotalItems());
            for (int j=0; j<categoryList.length; j++) {
                if (categoryIncluded[j] && allItems.get(i).getCategory().equals(categoryList[j])) {
                    ok = true;
                }
            }
        }
        System.out.println("choosing item "+i+" = "+ allItems.get(i).getName());
        return allItems.get(i);
    }
}
