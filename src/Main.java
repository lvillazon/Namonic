import javax.swing.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Config settings = new Config("namonic.cfg");
        new Memoriser(settings);
    }
}
