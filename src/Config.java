import java.util.ArrayList;
import java.util.HashMap;

public class Config {
    private HashMap<String, String> dictionary;

    public Config(String configFilename) {
        ArrayList<String> rawConfig = FileHandler.readWholeFile(configFilename);
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
}
