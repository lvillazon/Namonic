import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class ImportGUI extends JFrame implements ActionListener {

    private final JTextField filepathField;
    private final JTextField wildcardField;
    private final JLabel filesFound;
    private JLabel gallerySheet;
    private final JButton checkButton;
    private final JButton saveButton;
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
        gallerySheet = new JLabel();
        centrePanel.add(gallerySheet);
        gallerySheet.setIcon(getGalleryImage());
        checkMatchingFiles();  // preload the number of matching files for the current folder/wildcard

        // the panel for editing and saving settings
        JPanel editPanel = new JPanel();
        editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.LINE_AXIS));
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        editPanel.add(saveButton);
        southPanel.add(editPanel, BorderLayout.CENTER);

        setSize(new Dimension(WIDTH, HEIGHT));
        drawSlicingGrid();
    }

    private void checkMatchingFiles() {
        fileMatches = FileHandler.getMatchingFiles(filepathField.getText(), wildcardField.getText());
        filesFound.setText(Integer.toString(fileMatches.length));
        if (fileMatches.length>0) {
            StretchIcon newIcon = getGalleryImage();
            if (newIcon != null) {
                gallerySheet.setIcon(newIcon);  // update the image
            }
        }
    }

    // display the entire gallery sheet from the PDF file
    private StretchIcon getGalleryImage() {
        String filename = FileHandler.getMatchingFiles(filepathField.getText(), wildcardField.getText())[0];
        if (filename.toUpperCase().endsWith(".PDF")) {
            return new StretchIcon(FileHandler.readPDF(filename));
        } else {
            return new StretchIcon((FileHandler.readImage(filename)));
        }
    }

    // overlay gridlines to show how the image will be sliced into individual memorisable items
    private void drawSlicingGrid() {
        // TODO add code here
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == checkButton) {
            checkMatchingFiles();
        }
        if (e.getSource() == saveButton) {
            settings.setString("GALLERY_FOLDER", filepathField.getText());
            settings.setString("GALLERY_FILE_WILDCARD", wildcardField.getText());
        }
    }
}
