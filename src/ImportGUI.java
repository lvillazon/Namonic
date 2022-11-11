import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class ImportGUI extends JFrame implements ActionListener {

    private final JTextField filepathField;
    private final JTextField wildcardField;
    private final JLabel filesFound;
    //private JLabel gallerySheet;
    private GalleryPanel gallerySheet;
    private final JButton checkButton;
    private final JButton saveButton;
    private final JButton[] valueButtons;
    private final JButton plusOneButton;
    private final JButton plusTenButton;
    private final JButton minusOneButton;
    private final JButton minusTenButton;
    private final JButton prevPageButton;
    private final JButton nextPageButton;
    private final JLabel valueLabel;
    private final Config settings;
    private String[] fileMatches;

    public ImportGUI(Config settings) {
        super("Import Gallery Images");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.settings = settings;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {  // make sure data is autosaved on exit
                super.windowClosing(e);
                settings.save();
            }
        });
/*
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // only called when you release the mouse
                resizeImage();
            }
        });


 */
        int WIDTH = settings.getInt("WINDOW_WIDTH");
        int HEIGHT = settings.getInt("WINDOW_HEIGHT");

        // set up widgets and panels
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel centrePanel = new JPanel(new BorderLayout());
        JPanel southPanel = new JPanel(new BorderLayout());
        add(mainPanel);
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(centrePanel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        // the panel for the gallery folder name
        JPanel galleryFolderPanel = new JPanel(new BorderLayout());
        JLabel galleryLabel = new JLabel("folder: ");
        galleryFolderPanel.add(galleryLabel, BorderLayout.WEST);
        filepathField = new JTextField(settings.getString("GALLERY_FOLDER"));
        galleryFolderPanel.add(filepathField, BorderLayout.CENTER);
        checkButton = new JButton("Check");
        checkButton.addActionListener(this);
        galleryFolderPanel.add(checkButton, BorderLayout.EAST);
        northPanel.add(galleryFolderPanel, BorderLayout.NORTH);

        // gallery file prefix and files found
        JPanel wildcardPanel = new JPanel();
        wildcardPanel.setLayout(new BoxLayout(wildcardPanel, BoxLayout.LINE_AXIS));
        JLabel wildcardLabel = new JLabel("file prefix:");
        wildcardField = new JTextField(settings.getString("GALLERY_FILE_WILDCARD"));
        JLabel filesFoundLabel = new JLabel("files found:");
        filesFound = new JLabel();
        wildcardPanel.add(wildcardLabel);
        wildcardPanel.add(wildcardField);
        wildcardPanel.add(Box.createHorizontalGlue());
        wildcardPanel.add(filesFoundLabel);
        wildcardPanel.add(filesFound);
        northPanel.add(wildcardPanel, BorderLayout.CENTER);

        // the panel that actually shows the gallery image
        gallerySheet = new GalleryPanel(
                settings.getInt("GALLERY_LEFT_MARGIN"),
                settings.getInt("GALLERY_TOP_MARGIN"),
                settings.getInt("GALLERY_BOTTOM_MARGIN"),
                settings.getInt("GALLERY_H_SPACING"),
                settings.getInt("GALLERY_V_SPACING"),
                settings.getInt("GALLERY_IMAGE_WIDTH"),
                settings.getInt("GALLERY_IMAGE_HEIGHT")
        );
        centrePanel.add(gallerySheet);
        checkMatchingFiles();  // preload the number of matching files for the current folder/wildcard

        // the panel for editing and saving settings
        // buttons to choose which margin to edit
        String[][] values = {
                {"left", "top", "bottom"},
                {"width", "height", ""},
                {"x spacing", "y spacing", ""}
        };
        JPanel valuePanel = new JPanel(new GridLayout(values[0].length,values.length));
        valueButtons = new JButton[values[0].length * values.length];
        int i=0;
        for (int row=values.length-1; row>=0; row--) {
            for (int col=0; col<values[row].length; col++) {
                valueButtons[i] = new JButton(values[row][col]);
                if (!values[row][col].equals("")) {
                    valueButtons[i].addActionListener(this);
                } else {
                    valueButtons[i].setEnabled(false); // disable blank buttons (which are only there for padding)
                }
                valuePanel.add(valueButtons[i], row, col);
                i++;
            }
        }
        southPanel.add(valuePanel, BorderLayout.WEST);

        // the buttons to adjust the values themselves
        JPanel pageControlPanel = new JPanel(new BorderLayout());
        prevPageButton = new JButton("<<");
        nextPageButton = new JButton(">>");
        prevPageButton.addActionListener(this);
        nextPageButton.addActionListener(this);
        pageControlPanel.add(prevPageButton, BorderLayout.WEST);
        pageControlPanel.add(nextPageButton, BorderLayout.EAST);
        southPanel.add(pageControlPanel, BorderLayout.CENTER);

        JPanel editPanel = new JPanel(new BorderLayout());
        plusOneButton = new JButton("+1");
        plusOneButton.addActionListener(this);
        plusTenButton = new JButton("+10");
        plusTenButton.addActionListener(this);
        minusOneButton = new JButton("-1");
        minusOneButton.addActionListener(this);
        minusTenButton = new JButton("-10");
        minusTenButton.addActionListener(this);
        valueLabel = new JLabel("0", JLabel.CENTER);
        valueLabel.setBackground(Color.ORANGE);
        editPanel.add(plusTenButton, BorderLayout.NORTH);
        editPanel.add(plusOneButton, BorderLayout.EAST);
        editPanel.add(minusOneButton, BorderLayout.WEST);
        editPanel.add(minusTenButton, BorderLayout.SOUTH);
        editPanel.add(valueLabel, BorderLayout.CENTER);
        pageControlPanel.add(editPanel, BorderLayout.SOUTH);

        selectButton(valueButtons[6]);  // top left button because rows are added starting at the bottom

        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        southPanel.add(saveButton, BorderLayout.EAST);

        setSize(new Dimension(WIDTH, HEIGHT));
    }

    private void checkMatchingFiles() {
        fileMatches = FileHandler.getMatchingFiles(filepathField.getText(), wildcardField.getText());
        filesFound.setText(Integer.toString(fileMatches.length));
        if (fileMatches.length>0) {
            BufferedImage[] newPics = getGalleryImages();
            if (newPics != null) {
                gallerySheet.setGalleryPics(newPics);
            }
        }
    }

    // display the entire gallery sheet from the PDF file
    private BufferedImage[] getGalleryImages() {
            String filename = FileHandler.getMatchingFiles(filepathField.getText(), wildcardField.getText())[0];
        if (filename.toUpperCase().endsWith(".PDF")) {
            return FileHandler.readPDF(filename);
        } else {
            return FileHandler.readImages(filename);
        }
    }

    private void selectButton(JButton selected) {
        // unselect all buttons 1st
        for (JButton b: valueButtons) {
            b.setBackground(null);
        }
        selected.setBackground(Color.red);
        if (selected.getText().equals("left")) {
            valueLabel.setText(Integer.toString(gallerySheet.getLeftMargin()));
        }
        if (selected.getText().equals("top")) {
            valueLabel.setText(Integer.toString(gallerySheet.getTopMargin()));
        }
        if (selected.getText().equals("bottom")) {
            valueLabel.setText(Integer.toString(gallerySheet.getBottomMargin()));
        }
        if (selected.getText().equals("width")) {
            valueLabel.setText(Integer.toString(gallerySheet.getSinglePicWidth()));
        }
        if (selected.getText().equals("height")) {
            valueLabel.setText(Integer.toString(gallerySheet.getSinglePicHeight()));
        }
        if (selected.getText().equals("x spacing")) {
            valueLabel.setText(Integer.toString(gallerySheet.getHSpacing()));
        }
        if (selected.getText().equals("y spacing")) {
            valueLabel.setText(Integer.toString(gallerySheet.getVSpacing()));
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == checkButton) {
            checkMatchingFiles();
        }
        if (e.getSource() == saveButton) {
            settings.setString("GALLERY_FOLDER", filepathField.getText());
            settings.setString("GALLERY_FILE_WILDCARD", wildcardField.getText());
            settings.setString("GALLERY_LEFT_MARGIN", Integer.toString(gallerySheet.getLeftMargin()));
            settings.setString("GALLERY_TOP_MARGIN", Integer.toString(gallerySheet.getTopMargin()));
            settings.setString("GALLERY_BOTTOM_MARGIN", Integer.toString(gallerySheet.getBottomMargin()));
            settings.setString("GALLERY_IMAGE_WIDTH", Integer.toString(gallerySheet.getSinglePicWidth()));
            settings.setString("GALLERY_IMAGE_HEIGHT", Integer.toString(gallerySheet.getSinglePicHeight()));
            settings.setString("GALLERY_H_SPACING", Integer.toString(gallerySheet.getHSpacing()));
            settings.setString("GALLERY_V_SPACING", Integer.toString(gallerySheet.getVSpacing()));
            settings.save();
        }
        // check if we pressed one of the value buttons
        for (JButton b: valueButtons) {
            if (e.getSource()==b) {
                selectButton(b);
            }
        }

        // handle the buttons to increase/decrease the value
        int valueChange = 0;
        if (e.getSource()==plusOneButton) {
            valueChange = 1;
        }
        if (e.getSource()==plusTenButton) {
            valueChange = 10;
        }
        if (e.getSource()==minusTenButton) {
            valueChange = -10;
        }
        if (e.getSource()==minusOneButton) {
            valueChange = -1;
        }
        if (valueChange != 0) {
            if (valueButtons[0].getBackground()==Color.red) {
                gallerySheet.changeHSpacing(valueChange);
                valueLabel.setText(Integer.toString(gallerySheet.getHSpacing()));
            }
            if (valueButtons[1].getBackground()==Color.red) {
                gallerySheet.changeVSpacing(valueChange);
                valueLabel.setText(Integer.toString(gallerySheet.getVSpacing()));
            }
            if (valueButtons[3].getBackground()==Color.red) {
                gallerySheet.changeSinglePicWidth(valueChange);
                valueLabel.setText(Integer.toString(gallerySheet.getSinglePicWidth()));
            }
            if (valueButtons[4].getBackground()==Color.red) {
                gallerySheet.changeSinglePicHeight(valueChange);
                valueLabel.setText(Integer.toString(gallerySheet.getHSpacing()));
            }
            if (valueButtons[6].getBackground()==Color.red) {
                gallerySheet.changeLeftMargin(valueChange);
                valueLabel.setText(Integer.toString(gallerySheet.getLeftMargin()));
            }
            if (valueButtons[7].getBackground()==Color.red) {
                gallerySheet.changeTopMargin(valueChange);
                valueLabel.setText(Integer.toString(gallerySheet.getTopMargin()));
            }
            if (valueButtons[8].getBackground()==Color.red) {
                gallerySheet.changeBottomMargin(valueChange);
                valueLabel.setText(Integer.toString(gallerySheet.getBottomMargin()));
            }

        }
        // switch gallery page
        if (e.getSource() == prevPageButton) {
            gallerySheet.previousPage();
        }
        if (e.getSource() == nextPageButton) {
            gallerySheet.nextPage();
        }
        repaint();
    }

}
