import java.util.ArrayList;
import java.util.HashMap;

public class Config {
    private HashMap<String, String> dictionary;
    private String filename;

    public Config(String configFilename) {
        this.filename = configFilename;
        ArrayList<String> rawConfig = FileHandler.readWholeFile(filename);
        dictionary = new HashMap<>();
        // config files expect the following syntax
        // lines beginning with // are ignored
        // so are blank lines
        // other lines should take the form
        // VALUE_NAME = "value",
        // or
        // VALUE_NAME = 0,
        for (String s: rawConfig) {
            if(!s.startsWith("//") && s.contains("=")) {  // ignore comment lines & those without a =
                String[] keyPair = s.split("=");
                String key = keyPair[0].toUpperCase();  // store keys as upper case
                String value = keyPair[1].replace("\"", "");  //remove quotes from value strings
                dictionary.put(key.trim(), value.trim());  // also remove whitespace at either end
            }
        }
    }

    public String getString(String k) {
        String key = k.toUpperCase();  // keys are always stored in upper case
        if (dictionary.containsKey(key)) {
            String value = dictionary.get(key);
            return dictionary.get(key);
        } else {
            ErrorHandler.ModalMessage("config value not found:" + key);
            return "";
        }
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
    public void setString(String k, String newValue) {
        dictionary.put(k.toUpperCase().trim(), newValue.trim());
    }

    // write all keys back to the file
    public void save() {
        ArrayList<String> rawData = new ArrayList<>();
        for (String key: dictionary.keySet()) {
            rawData.add(key + " = " + dictionary.get(key));
        }
        FileHandler.writeWholeFile("test.cfg", rawData);
    }
}
