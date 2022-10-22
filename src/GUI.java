import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GUI extends JFrame implements ActionListener {
    private JPanel questionPanel;
    private JLabel faceLabel;
    private Memoriser studentData;
    private int studentIndex;

    public GUI(int width, int height, Memoriser mem) {
        super("test");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // set up widgets and panels
        questionPanel = new JPanel(new BorderLayout());
        faceLabel = new JLabel("Face");
        questionPanel.add(faceLabel, BorderLayout.CENTER);
        add(questionPanel);
        setSize(new Dimension(width, height));
        JButton next = new JButton("next");
        next.addActionListener(this);
        questionPanel.add(next, BorderLayout.SOUTH);

        // load student data
        studentData = mem;
        studentIndex = 0;
        faceLabel.setIcon(studentData.getStudent(studentIndex).getFace());

        faceLabel.setVisible(true);
        repaint();
        //pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("next")) {
            studentIndex = (studentIndex+1)%studentData.getTotalStudents();
            faceLabel.setIcon(studentData.getStudent(studentIndex).getFace());
            faceLabel.setText(Integer.toString(studentIndex));
            repaint();
        }
    }
}
