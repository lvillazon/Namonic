import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame implements ActionListener {
    private final int MAX_CHOICES = 4;
    private final int MARGIN = 10;

    private final JLabel faceLabel;
    private final JLabel scoreLabel;
    private final JLabel streakLabel;
    private final JButton[] choices;
    private final JCheckBox[] filters;
    private final JButton filterAllButton;
    private final JButton filterClearButton;
    private final Memoriser studentData;
    private int studentIndex;

    public GUI(int width, int height, Memoriser mem) {
        super("test");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // set up widgets and panels
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel westPanel = new JPanel(new BorderLayout());
        JPanel eastPanel = new JPanel(new BorderLayout());
        add(mainPanel);
        mainPanel.add(westPanel, BorderLayout.WEST);
        mainPanel.add(eastPanel, BorderLayout.EAST);

        // the panel that actually shows the photo
        JPanel facePanel = new JPanel(new BorderLayout());
        faceLabel = new JLabel();
        faceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        facePanel.add(faceLabel, BorderLayout.CENTER);
        facePanel.add(Box.createRigidArea(new Dimension(MARGIN, MARGIN)), BorderLayout.WEST);
        facePanel.add(Box.createRigidArea(new Dimension(MARGIN, MARGIN)), BorderLayout.EAST);
        facePanel.add(Box.createRigidArea(new Dimension(MARGIN, MARGIN)), BorderLayout.NORTH);
        facePanel.add(Box.createRigidArea(new Dimension(MARGIN, MARGIN)), BorderLayout.SOUTH);
        westPanel.add(facePanel, BorderLayout.NORTH);

        // name options to choose from
        JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.PAGE_AXIS));
        choices = new JButton[MAX_CHOICES];
        for (int i=0; i<MAX_CHOICES; i++) {
            choices[i] = new JButton("choice "+(i+1));
            choices[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            choicePanel.add(choices[i]);
            choicePanel.add(Box.createRigidArea(new Dimension(MARGIN, MARGIN/2)));
        }
        westPanel.add(choicePanel, BorderLayout.CENTER);

        // score
        JPanel scorePanel = new JPanel(new FlowLayout());
        JLabel scoreText = new JLabel("Score: ");
        scorePanel.add(scoreText);
        scoreLabel = new JLabel("0/0");
        scorePanel.add(scoreLabel);
        JLabel streakText = new JLabel("Streak: ");
        scorePanel.add(streakText);
        streakLabel = new JLabel("0");
        scorePanel.add(streakLabel);
        eastPanel.add(scorePanel, BorderLayout.NORTH);

        // filters
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.PAGE_AXIS));
        JPanel filterButtonPanel = new JPanel();
        filterButtonPanel.setLayout(new BoxLayout(filterButtonPanel, BoxLayout.LINE_AXIS));
        filterButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // needed so that the alignment of the checkboxes works properly
        filterAllButton = new JButton("All");
        filterAllButton.addActionListener(this);
        filterButtonPanel.add(filterAllButton);
        filterClearButton = new JButton("Clear");
        filterClearButton.addActionListener(this);
        filterButtonPanel.add(filterClearButton);
        filterPanel.add(filterButtonPanel);
        filters = new JCheckBox[7];  // years 7 to 13 = 7 filters
        for(int i=0; i<7; i++) {
            int year = i + 7;
            filters[i] = new JCheckBox("Year "+year);
            filters[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            filterPanel.add(filters[i]);
        }
        eastPanel.add(filterPanel, BorderLayout.CENTER);

        setSize(new Dimension(width, height));

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
        if (e.getActionCommand().equals("All")) {
            // set all checkboxes
            for(JCheckBox filter: filters) {
                filter.setSelected(true);
            }
        } else if (e.getActionCommand().equals("Clear")) {
            // clear all checkboxes
            for(JCheckBox filter: filters) {
                filter.setSelected(false);
            }
        }


        // unused test functionality to advance to the next face
        if (e.getActionCommand().equals("next")) {
            studentIndex = (studentIndex+1)%studentData.getTotalStudents();
            faceLabel.setIcon(studentData.getStudent(studentIndex).getFace());
            faceLabel.setText(Integer.toString(studentIndex));
            repaint();
        }
    }
}
