import javax.swing.*;

public class Student {
    private ImageIcon face;
    private String name;
    private int year;
    private String className;
    private int gender;

    public Student(ImageIcon face) {
        this.face = face;
        // place holder defaults for other fields for now
        name = "Anon";
        year = -1;
        className = "class";
        gender = -1;
    }

    public ImageIcon getFace() {
        return face;
    }
}
