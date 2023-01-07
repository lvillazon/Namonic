import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Memoriser {
    //private ArrayList<Item> allItems; - replaced with allClasses
    private ArrayList<Student> filteredItems;
    private ArrayList<TeachingClass> allClasses;
    private String[] metadataNames;  // titles of item metadata, eg "Gender", "PP"
    private ArrayList<String> categoryList;  // titles of item categories, eg "Year 7", "Year 8"
    private boolean[] categoryIncluded;  // whether or not this category is included in random choices of items
    private Random rng;
    private int streak;
    private Config settings;
    private ImportGUI studentDataImporter;
    private GUI memoryTestUI;

    private class CallBackHandler implements CallBack {
        public void trigger() {
            System.out.println("callback triggered");
            finaliseData();
        }
    }

    public Memoriser(Config settings) {
        this.settings = settings;
        rng = new Random();
        // look for PDF files exported from SIMS for each class
        String[] galleryFiles = ResourceManager.getGalleryFiles(settings);
        if (galleryFiles.length == 0) {
            // TODO show config frame so we can point to the gallery files
        } else {
            // use the names of each file to create category names
            // create a teachingClass object for each one
            allClasses = new ArrayList<>();
            categoryList = new ArrayList<>();
            for (String filePath: galleryFiles) {
                TeachingClass teach = new TeachingClass(settings, filePath);
                allClasses.add(teach);
                System.out.println("class " + teach.getName() + " created with " + teach.size() + " students");
                // assign each class to a separate category for now
                categoryList.add(teach.getName());
                categoryIncluded = new boolean[categoryList.size()];
                for (int i=0; i<categoryIncluded.length; i++) {  // initialise with all categoriess selected
                    categoryIncluded[i] = true;
                }            }
            // show the main UI to play the memory game
            memoryTestUI = new GUI(settings, this);
        }
    }

    /*    LEFTOVER STUFF FROM THE OLD CONSTRUCTOR
        allItems = ResourceManager.loadItemData(settings);
        if (allItems.size() > 0) { // images & names were previously imported
            // finalise any book-keeping
            memoryTestUI = new GUI(settings, this);
        } else {
            // show the import dialog to allow the raw PDFs from SIMs to be imported
            studentDataImporter = new ImportGUI(settings, new CallBackHandler());
            studentDataImporter.setVisible(true);
        }
    }

     */

    private void finaliseData() {
        System.out.println("data found...");
        ArrayList<ImageIcon> data = ResourceManager.loadImages(settings);
        System.out.println(data.size() + " images");
        memoryTestUI = new GUI(settings, this);
    }

    /*
    private void initialiseData(ArrayList<ImageIcon> itemImages) {
        // take the chopped thumbnails and extracts the names & catego
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

     */

    /*public Item getItem(int i) {
        return allItems.get(i);
    }

     */

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
        if (i<categoryList.size()) {
            return categoryList.get(i);
        } else {
            return "";
        }
    }

    public int getCategoryCount() {
        return categoryList.size();
    }

    public boolean isCategoryIncluded(int i) {
        return categoryIncluded[i];
    }

    // does the same as the above but uses the category name string , instead of its index number
    public boolean isCategoryIncluded(String categoryName) {
        int catNumber = -1;
        for (int i=0; i<categoryList.size(); i++) {
            if (categoryList.get(i).equals(categoryName)) {
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
        filteredItems = reloadFilteredItems();
    }

    // return a list of only those items that match current filters
    private ArrayList<Student> reloadFilteredItems() {
        ArrayList<Student> results = new ArrayList<>();
        for (TeachingClass t: allClasses) {
            if (isCategoryIncluded(t.getName())) {
                for (int i=0; i<t.size(); i++) {
                    results.add(t.getStudent(i));
                }
            }
        }
        return results;
    }

    // return a list of possible answers, 1 of which is correct, the others are random
    // names should also be chosen from the currently selected categories
    public Student[] getChoices(Student correct, int numberOfChoices) {
        Student[] results = new Student[numberOfChoices];
        for (int i=0; i<numberOfChoices; i++) {
            int j = 0;
            // keep picking random items until it is not the same as the correct one
            // and isn't one we have already selected
            boolean acceptable = false;
            while (!acceptable) {
                j = rng.nextInt(filteredItems.size()-1);
                if (filteredItems.get(j) != correct) {
                    acceptable = true;  // at least so far...
                    // ... but check we haven't already picked it
                    for (Student alreadyPicked: results) {
                        if (filteredItems.get(j) == alreadyPicked) {
                            acceptable = false;
                        }
                    }
                }
            }
            results[i] = filteredItems.get(j);
        }
        // overwrite one of the choice at random with the correct option
        results[rng.nextInt(numberOfChoices)] = correct;
        return results;
    }

    // pick an item at random from all the currently selected categories
    public Student chooseRandomly() {
        int i = rng.nextInt(filteredItems.size() - 1);
        return filteredItems.get(i);
    }

    // return a random item from those that have the lowest scores in the currently selected categories
    public Student chooseWorstRemembered() {
        // sort the currently selected items by score & return the bottom item
        // if we sort in descending order, then the last item is guaranteed to have the lowest score after 1 pass
        // avoiding all the inefficiencies of the multiple passes required for normal bubble sorting
        // Also, by sorting the filteredItems list directly, rather than a temp list, we minimise the sorting required
        // since the list will gradually become more and more sorted over multiple calls to the method.
        for (int i=0; i<filteredItems.size()-1; i++) {
            if (filteredItems.get(i).compareTo(filteredItems.get(i + 1)) < 0) {
                Student temp = filteredItems.get(i);
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
        for (TeachingClass t: allClasses) {
            for (int i=0; i<t.size(); i++) {
                total = total + t.getStudent(i).getCorrectCount();
            }
        }
        return total;
    }

    public int getAsked() {
        // add up number of times you have been asked to guess a student
        int total = 0;
            for (TeachingClass t: allClasses) {
                for (int i = 0; i < t.size(); i++) {
                    total = total + t.getStudent(i).getShowCount();
                }
            }
        return total;
    }

    public void markCorrect(Student picked) {
        picked.markCorrect();
        picked.markShown();
        streak++;
    }

    public void markWrong(Student picked) {
        picked.markShown();
        streak = 0;
    }

    public int getStreak() {
        return streak;
    }

    // save stats for every class
    public void save() {
        for (TeachingClass t: allClasses) {
            for (int i=0; i<t.size(); i++) {
                Student s = t.getStudent(i);
                String scoreStats = s.getCorrectCount() + "/" + s.getShowCount();
                settings.setString("["+t.getName()+"]", s.getID(), scoreStats);
            }
        }
        settings.save();
    }

    // restore the scores saved from the previous session in the config file
    private void loadScores() {
        for (TeachingClass t: allClasses) {
            for (int i=0; i<t.size(); i++) {
                Student s = t.getStudent(i);
                // read the stats for this student, if they have been previously saved
                if (settings.keyExists(t.getName(), s.getID())) {
                    String statLine = settings.getStringFrom(t.getName(), s.getID());
                    if (!statLine.equals("")) {
                        String[] scoreStats = statLine.split("/");
                        if (scoreStats.length > 0) {
                            try {
                                s.setScore(Integer.parseInt(scoreStats[0]));
                                s.setShown(Integer.parseInt(scoreStats[1]));
                            } catch (NumberFormatException e) {
                                ErrorHandler.ModalMessage("invalid score value " +
                                        scoreStats[0] + "/" + scoreStats[1] +
                                        " for class " + t.getName() + " student " + s.getID());
                            }
                        }
                    } else {
                        s.setScore(0);
                        s.setShown(0);
                    }
                }
            }
        }
    }

}
