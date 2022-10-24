import javax.swing.*;
import java.util.ArrayList;

public class Memoriser {
    private ArrayList<Student> students;

    public Memoriser(Config settings) {
        ArrayList<ImageIcon> faces = ResourceManager.loadImages(settings);
        students = new ArrayList<>();
        for (int i=0; i<faces.size(); i++) {
            students.add(new Student(faces.get(i)));
        }
    }

    public Student getStudent(int i) {
        return students.get(i);
    }

    public int getTotalStudents() {
        return students.size();
    }
}
