import javax.swing.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Memoriser students = new Memoriser("C:\\Users\\lpvil\\Downloads\\faces.jpg");
        GUI mainGUI = new GUI(800, 600, students); // size of window
        mainGUI.setVisible(true);
    }
}
