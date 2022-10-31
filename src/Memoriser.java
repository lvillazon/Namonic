import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Predicate;

public class Memoriser {
    private ArrayList<Item> allItems;
    private ArrayList<Item> filteredItems;
    private String[] metadataNames;  // titles of item metadata, eg "Gender", "PP"
    private String[] categoryList;  // titles of item categories, eg "Year 7", "Year 8"
    private boolean[] categoryIncluded;  // whether or not this category is included in random choices of items
    private Random rng;
    private int streak;
    private Config settings;

    public Memoriser(Config settings) {
        this.settings = settings;
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
        // remove items with name "BLANK" - these are just used to pad the name list so it matches the gallery grid
        String blankName = settings.getString("PADDING_NAME");
        Predicate<Item> blankItem = item -> item.getName().equals(blankName);
        allItems.removeIf(blankItem);
        System.out.println(allItems.size() + " items stored, after removing blanks");
        loadScores();
        filteredItems = getFilteredItems();
        rng = new Random();  // used for all random choices
        streak = 0;
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
            // keep picking random items until it is not the same as the correct one
            // and isn't one we have already selected
            // and is the same category as the correct answer
            boolean acceptable = false;
            while (!acceptable) {
                j = rng.nextInt(allItems.size()-1);
                if (allItems.get(j) != correct && allItems.get(j).getCategory().equals(correct.getCategory())) {
                    acceptable = true;  // at least so far...
                    // ... but check we haven't already picked it
                    for (String alreadyPicked: results) {
                        if (allItems.get(j).getName().equals(alreadyPicked)) {
                            acceptable = false;
                        }
                    }
                }
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
            i = rng.nextInt(allItems.size()-1);
            for (int j=0; j<categoryList.length; j++) {
                if (categoryIncluded[j] && allItems.get(i).getCategory().equals(categoryList[j])) {
                    ok = true;
                }
            }
        }
        System.out.println("choosing item "+i+" = "+ allItems.get(i).getName());
        return allItems.get(i);
    }

    // return a random item from those that have the lowest scores in the currently selected categories
    public Item chooseWorstRemembered() {
        // sort the currently selected items by score & return the bottom item
        // if we sort in descending order, then the last item is guaranteed to have the lowest score after 1 pass
        // avoiding all the inefficiencies of normal bubble sorting
        for (int i=0; i<filteredItems.size()-1; i++) {
            if (filteredItems.get(i).compareTo(filteredItems.get(i + 1)) < 0) {
                Item temp = filteredItems.get(i);
                filteredItems.set(i, filteredItems.get(i+1));
                filteredItems.set(i+1, temp);
            }
        }
        return filteredItems.get(filteredItems.size()-1);
    }

    // return total correct answers this game
    public int getScore() {
        // add up correct answers for all items
        int total = 0;
        for (Item i: allItems) {
            total = total + i.getCorrectCount();
        }
        return total;
    }

    public int getAsked() {
        // add up correct answers for all items
        int total = 0;
        for (Item i: allItems) {
            total = total + i.getShowCount();
        }
        return total;
    }

    public void markCorrect(Item picked) {
        picked.markCorrect();
        picked.markShown();
        streak++;
    }

    public void markWrong(Item picked) {
        picked.markShown();
        streak = 0;
    }

    public int getStreak() {
        return streak;
    }

    // save scores for every item
    public void save() {
        for (Item i: allItems) {
            String scoreStats = i.getCorrectCount() + "/" + i.getShowCount();
            settings.setString("[scores]", i.getFullName(), scoreStats);
        }
        settings.save();
    }

    // restore the scores saved from the previous session in the config file
    private void loadScores() {
        for (Item i: allItems) {
            String[] scoreStats = settings.getString(i.getFullName()).split("/");
            if (scoreStats.length>0) {
                try {
                i.setScore(Integer.parseInt(scoreStats[0]));
                i.setShown(Integer.parseInt(scoreStats[1]));
                }
                catch (NumberFormatException e) {
                    ErrorHandler.ModalMessage("invalid score value "+scoreStats[0]+"/"+scoreStats[1]+" for "+i.getFullName());
                }
            }
        }
    }

    // percentage correct over all sessions, rounded to int
    public int getCategoryScore(String cat) {
        int totalScore = 0;
        int totalShown = 0;
        for (int i=0; i<allItems.size(); i++) {
            if (allItems.get(i).getCategory().equals(cat)) {
                totalScore += allItems.get(i).getCorrectCount();
                totalShown += allItems.get(i).getShowCount();
            }
        }
        if (totalShown>0) {
            return (int) ((double)totalScore / (double)totalShown * 100);
        } else {
            return 0;
        }
    }

}
