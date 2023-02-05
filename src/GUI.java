import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class GUI extends JFrame implements ActionListener {


    private final JLabel faceLabel;
    private final JLabel scoreLabel;
    private final JLabel streakLabel;
    private final JLabel correctLabel;
    private final JButton[] choiceButtons;
    private final JLabel[] categoryScores;
    private final JCheckBox[] filters;
    private final JButton configButton;
    private final JButton filterAllButton;
    private final JButton filterClearButton;
    private final Timer answerDelay;  // used for delays when showing correct/wrong & showing a new question
    private final Memoriser itemData;
    private final Config settings;
    private int itemIndex;
    private Student currentStudent;

    private class CallBackHandler implements CallBack {
        public void trigger() {
            System.out.println("callback triggered");
            finaliseData();
        }
    }

    public GUI(Config settings, Memoriser mem) {
        super("Namonic");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {  // make sure data is autosaved on exit
                super.windowClosing(e);
                itemData.save();
            }
        });
        itemData = mem;
        this.settings = settings;

        int MARGIN = settings.getInt("CHOICE_MARGIN");
        int MAX_CHOICES = settings.getInt("MAX_CHOICES");
        int WIDTH = settings.getInt("WINDOW_WIDTH");
        int HEIGHT = settings.getInt("WINDOW_HEIGHT");

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
        GridLayout choiceLayout = new GridLayout(0,1);
        choiceLayout.setHgap(MARGIN/2);
        choiceLayout.setVgap(MARGIN/2);
        choicePanel.setLayout(choiceLayout);
        choiceButtons = new JButton[MAX_CHOICES];
        for (int i=0; i<MAX_CHOICES; i++) {
            choiceButtons[i] = new JButton(); //"choice "+(i+1));
            choiceButtons[i].setPreferredSize(new Dimension(200,50));
            choiceButtons[i].setSize(new Dimension(150,40));
            choiceButtons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            choiceButtons[i].setBackground(Color.WHITE);
            choiceButtons[i].addActionListener(this);
            choicePanel.add(choiceButtons[i]);
        }
        westPanel.add(choicePanel, BorderLayout.CENTER);
        answerDelay = new Timer(0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nextQuestion();
            }
        });
        answerDelay.setRepeats(false);

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

        correctLabel = new JLabel();  // shows correct or wrong, when you answer
        correctLabel.setHorizontalAlignment(JLabel.CENTER);
        correctLabel.setFont(correctLabel.getFont().deriveFont(72.0f));
        mainPanel.add(correctLabel, BorderLayout.CENTER);

        // filters & config
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.PAGE_AXIS));
        JPanel filterButtonPanel = new JPanel();
        filterButtonPanel.setLayout(new BoxLayout(filterButtonPanel, BoxLayout.LINE_AXIS));
        filterButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // needed so that the alignment of the checkboxes works properly
        configButton = new JButton("Config");
        configButton.addActionListener(this);
        filterButtonPanel.add(configButton);
        filterAllButton = new JButton("All");
        filterAllButton.addActionListener(this);
        filterButtonPanel.add(filterAllButton);
        filterClearButton = new JButton("Clear");
        filterClearButton.addActionListener(this);
        filterButtonPanel.add(filterClearButton);
        filterPanel.add(filterButtonPanel);
        filters = new JCheckBox[mem.getCategoryCount()];  // 1 filter checkbox for each category
        categoryScores = new JLabel[mem.getCategoryCount()];  // also one score %
        for(int i=0; i<filters.length; i++) {
            JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2,0));
            //categoryPanel.setPreferredSize(new Dimension(100,10));
            categoryScores[i] = new JLabel("0%");
            categoryPanel.add(categoryScores[i]);
            filters[i] = new JCheckBox(mem.getCategoryName(i));
            filters[i].setPreferredSize(new Dimension(100,20));
            filters[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            filters[i].setSelected(true);
            itemData.setCategoryIncluded(i, true);  // also set the Memoriser state to match the GUI checkbox
            filters[i].addActionListener(this);
            categoryPanel.add(filters[i]);
            filterPanel.add(categoryPanel);
            filterPanel.add(Box.createRigidArea(new Dimension(10,5)));
        }
        filterPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        filterPanel.add(Box.createVerticalGlue());
        eastPanel.add(filterPanel, BorderLayout.CENTER);

        setSize(new Dimension(WIDTH, HEIGHT));

        if (itemData.getTotalItems() >0) {
            // load initial item data
            currentStudent = itemData.chooseRandomly();
            //currentItem = itemData.getItem(20);
            setStudent(currentStudent);
            updateCategoryScores();
            setVisible(true);
        }
    }

    private void finaliseData() {
        System.out.println("data found...");
        ArrayList<ImageIcon> data = ResourceManager.loadImages(settings);
        System.out.println(data.size() + " images");
        //memoryTestUI = new GUI(settings, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        // check filter checkboxes
        boolean clickedFilter = false;
        for (int i=0; i<filters.length; i++) {
            if (e.getSource() == filters[i]) {
                itemData.setCategoryIncluded(i, filters[i].isSelected());
                clickedFilter = true;
                System.out.println("change filter "+i+" to " + filters[i].isSelected());
            }
        }
        // DEBUG nameCount.setText(Integer.toString(itemData.getTotalItems()));
        if (!clickedFilter) {

            if (e.getActionCommand().equals("All")) {
                // set all checkboxes
                for (int i = 0; i < filters.length; i++) {
                    filters[i].setSelected(true);
                    itemData.setCategoryIncluded(i, true);
                }
            } else if (e.getActionCommand().equals("Clear")) {
                // clear all checkboxes
                for (int i = 0; i < filters.length; i++) {
                    filters[i].setSelected(false);
                    itemData.setCategoryIncluded(i, false);
                }
            }

            // show config panel if Config button clicked
            if (e.getSource() == configButton) {
                new ImportGUI(settings, new CallBackHandler());
            }

            // check if we clicked a choice button
            boolean choiceClicked = false;
            for(JButton b: choiceButtons) {
                if (e.getSource()==b) {
                    choiceClicked = true;
                }
            }
            // check if we chose the right name
            if (choiceClicked) {
                // whether or not we are right, highlight the correct choice
                JButton correctChoice = choiceButtons[0];
                for (JButton b: choiceButtons) {
                    if (b.getIcon() == currentStudent.getNameplate()) {
                        correctChoice = b;
                    }
                }
                correctChoice.setBackground(Color.green);
                System.out.println(correctChoice.getText());
                repaint();
                if (e.getSource() ==correctChoice) {
                    correctLabel.setText("✔");
                    correctLabel.setForeground(Color.green);
                    itemData.markCorrect(currentStudent);
                    currentStudent.markShown();
                    answerDelay.setInitialDelay(settings.getInt("RIGHT_DELAY"));
                    answerDelay.start();
                } else {
                    correctLabel.setText("❌");
                    correctLabel.setForeground(Color.red);
                    itemData.markWrong(currentStudent);
                    answerDelay.setInitialDelay(settings.getInt("WRONG_DELAY"));
                    answerDelay.start();
                }

                updateCategoryScores();
            }
        }
    }

    private void updateCategoryScores() {
        // update category scores
        for (int i=0; i<categoryScores.length; i++) {
            // TODO fix this
//            categoryScores[i].setText(itemData.getCategoryScore(filters[i].getText()) + "%");
        }
    }

    private void nextQuestion() {
        // reset all buttons to default appearance
        for (JButton b: choiceButtons) {
            b.setBackground(null);
        }
        correctLabel.setText("");
        currentStudent = itemData.chooseWorstRemembered();
        setStudent(currentStudent);
    }

    // update all widgets to reflect the current item
    private void setStudent(Student s) {
        if (s != null) {
            faceLabel.setIcon(s.getPicture());

            // add random choices for this pic - one 1 is correct
            Student[] answers = itemData.getChoices(s, choiceButtons.length);
            for (int j = 0; j < choiceButtons.length; j++) {
                // display the nameplate image showing the name of the student
                choiceButtons[j].setIcon(answers[j].getNameplate());
            }

            // update score stats
            scoreLabel.setText(itemData.getScore() + "/" + itemData.getAsked());
            streakLabel.setText(Integer.toString(itemData.getStreak()));

            // DEBUG just to check - these values should be hidden in the real game
            /*
            nameLabel.setText(i.getName());
            catLabel.setText(i.getCategory());
            String metaText = "";
            for (int j=0; j<itemData.getMetadataCount(); j++) {
                metaText = metaText + itemData.getMetadataName(j) + "=" + i.getMetadata(j) + ", ";
            }
            metadataLabel.setText(metaText);
             */
        }
        repaint();
    }

}
