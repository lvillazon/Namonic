import java.util.ArrayList;
import java.util.HashMap;

public class Config {
    // each config section is a hashmap. The section name is used as a key for a hashmap of hashmaps
    private HashMap<String, HashMap<String, String>> dictionaries;  // one hashmap per section
    private String filename;

    public Config(String configFilename) {
        this.filename = configFilename;
        ArrayList<String> rawConfig = FileHandler.readWholeFile(filename);
        dictionaries = new HashMap<>();
        HashMap<String, String> sectionDict = null;
        // config files expect the following syntax
        // lines beginning with // are ignored
        // so are blank lines
        // lines beginning with [ are assumed to be section headers
        // other lines should take the form
        // VALUE_NAME = "value",
        // or
        // VALUE_NAME = 0,
        String sectionName = "[default]";
        for (String s: rawConfig) {
            if(!s.startsWith("//")) {  // ignore comment lines
                if (s.startsWith("[")) {
                  sectionName = s;
                } else if (s.contains("=")) { // assume this is a key = value
                    String[] keyPair = s.split("=");
                    String key = keyPair[0].toUpperCase();  // store keys as upper case
                    String value = keyPair[1].replace("\"", "");  //remove quotes from value strings
                    setString(sectionName, key.trim(), value.trim());  // also remove whitespace at either end
                }
            }
        }
    }

    public String getString(String k) {
        String key = k.toUpperCase();  // keys are always stored in upper case
        // check all section dictionaries for the 1st matching key
        for (String d: dictionaries.keySet()) {
            if (dictionaries.get(d).containsKey(key)) {
                String value = dictionaries.get(d).get(key);
                return value;  // scruffy early return but the alternatives all seem messier
            }
        }
        ErrorHandler.ModalMessage("config value not found:" + key);
        return "";
    }

    public int getInt(String key) {
        try {
            int value = Integer.parseInt(getString(key));
            return value;
        }
        catch (NumberFormatException e) {
            ErrorHandler.ModalMessage("invalid integer config value for " + key);
            return 0;
        }
    }

    public boolean getBool(String key) {
        String value = getString(key).toUpperCase();
        if (value.equals("TRUE") || value.equals("T") || value.equals("YES")) {
            return true;
        } else {
            return false;
        }
    }

    // if a key with the name exists, update it, otherwise create it
    public void setString(String section, String k, String newValue) {
        // check the section dictionary for a 1st matching key
        String key = k.toUpperCase();  // keys are always stored in upper case
        if (!dictionaries.containsKey(section)) {  // create the section dict if necessary
            dictionaries.put(section, new HashMap<>());
        }
        dictionaries.get(section).put(key, newValue);
    }

    // write all keys back to the file
    public void save() {
        ArrayList<String> rawData = new ArrayList<>();
        for (String section: dictionaries.keySet()) {
            rawData.add(section);
            for (String key: dictionaries.get(section).keySet()) {
                rawData.add(key + " = " + dictionaries.get(section).get(key));
            }
        }
        FileHandler.writeWholeFile(filename, rawData);
    }
}
