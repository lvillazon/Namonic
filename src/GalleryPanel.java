import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

// shows the gallery images, together with the lines indicating how they will be sliced
// into individual images for memorising
public class GalleryPanel extends JPanel {
    private BufferedImage galleryPic;
    private double aspectRatio;
    private int leftMargin;
    private int topMargin;
    private int bottomMargin;
    private int hSpacing;
    private int vSpacing;
    private int singlePicWidth;
    private int singlePicHeight;

    public GalleryPanel(int leftMargin, int topMargin, int bottomMargin, int hSpacing, int vSpacing, int width, int height) {
        super();
        this.leftMargin = leftMargin;
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
        this.hSpacing = hSpacing;
        this.vSpacing = vSpacing;
        this.singlePicWidth = width;
        this.singlePicHeight = height;
    }

    public void setGalleryPic(BufferedImage img) {
        galleryPic = img;
        aspectRatio = (float)img.getWidth()/(float)img.getHeight();
    }

    public int getLeftMargin() {
        return leftMargin;
    }

    public void changeLeftMargin(int delta) {
        leftMargin += delta;
    }

    public int getTopMargin() {
        return topMargin;
    }

    public void changeTopMargin(int delta) {
        topMargin += delta;
    }

    public int getBottomMargin() {
        return bottomMargin;
    }

    public void changeBottomMargin(int delta) {
        bottomMargin += delta;
    }

    public int getHSpacing() {
        return hSpacing;
    }

    public void changeHSpacing(int delta) {
        hSpacing += delta;
    }

    public int getVSpacing() {
        return vSpacing;
    }

    public void changeVSpacing(int delta) {
        vSpacing += delta;
    }

    public int getSinglePicWidth() {
        return singlePicWidth;
    }

    public void changeSinglePicWidth(int delta) {
        singlePicWidth += delta;
    }

    public int getSinglePicHeight() {
        return singlePicHeight;
    }

    public void changeSinglePicHeight(int delta) {
        singlePicHeight += delta;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // calculate new image size based on the size of the container panel
        // while preserving the original aspect ratio
        int newWidth;
        int newHeight;
        /* As a reminder:
        aspectRatio = imgWidth/imgHeight
        imgWidth = aspectRatio * imgHeight
        imgHeight = imgWidth/aspectRatio
         */

        if ((float) getWidth() / (float) getHeight() > aspectRatio) {
            // constrain new image size to the height of the panel
            newHeight = getHeight();
            newWidth = (int) (aspectRatio * newHeight);
        } else {
            // constrain to width of panel
            newWidth = getWidth();
            newHeight = (int) (newWidth / aspectRatio);
        }
        double scaling = (float)newWidth/galleryPic.getWidth();

        if (newWidth > 0 && newHeight > 0) {
            BufferedImage scaled = new BufferedImage(newWidth, newHeight, galleryPic.getType());
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g2.drawImage(
                    galleryPic,
                    0, 0, newWidth, newHeight,
                    0, 0, galleryPic.getWidth(), galleryPic.getHeight(),
                    null);

            // paint grid lines on top
            int scaledLeftMargin = (int)(leftMargin * scaling);
            int scaledTopMargin = (int)(topMargin * scaling);
            int scaledBottomMargin = (int)(bottomMargin * scaling);
            int scaledWidth = (int)(singlePicWidth * scaling);
            int scaledHeight = (int)(singlePicHeight * scaling);
            int scaledHSpacing = (int)(hSpacing * scaling);
            int scaledVSpacing = (int)(vSpacing * scaling);
            g2.setColor(Color.red);
            // page margins
            g2.drawLine(scaledLeftMargin,0, scaledLeftMargin, getHeight());
            g2.drawLine(0, scaledTopMargin, getWidth(), scaledTopMargin);
            g2.drawLine(
                    0, scaled.getHeight()-scaledBottomMargin,
                    getWidth(), scaled.getHeight()-scaledBottomMargin);

            // bounding box around individual pics
            int y = scaledTopMargin;
            while (y<scaled.getHeight() - scaledBottomMargin - scaledHeight) {
                int x = scaledLeftMargin;
                while (x<scaled.getWidth()-scaledWidth) {
                    g2.drawRect(x, y, scaledWidth, scaledHeight);
                    x = x + scaledWidth + scaledHSpacing;
                }
                y = y + scaledHeight + scaledVSpacing;
            }
            g2.dispose();
        }
    }
}
