import javax.swing.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Config settings = new Config("namonic.cfg");
        Memoriser students = new Memoriser(settings);
        GUI mainGUI = new GUI(settings, students); // size of window
        mainGUI.setVisible(true);
    }
}
