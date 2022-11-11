import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

// shows the gallery images, together with the lines indicating how they will be sliced
// into individual images for memorising
public class GalleryPanel extends JPanel {
    private BufferedImage[] galleryPics;
    private double aspectRatio;
    private int leftMargin;
    private int topMargin;
    private int bottomMargin;
    private int hSpacing;
    private int vSpacing;
    private int singlePicWidth;
    private int singlePicHeight;
    private int pageNumber;

    public GalleryPanel(int leftMargin, int topMargin, int bottomMargin, int hSpacing, int vSpacing, int width, int height) {
        super();
        this.leftMargin = leftMargin;
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
        this.hSpacing = hSpacing;
        this.vSpacing = vSpacing;
        this.singlePicWidth = width;
        this.singlePicHeight = height;
        pageNumber = 0;
    }

    public void setGalleryPics(BufferedImage[] imgs) {
        galleryPics = imgs;
        aspectRatio = (float)imgs[0].getWidth()/(float)imgs[0].getHeight();
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

    public void nextPage() {
        if (pageNumber<galleryPics.length-1) {
            pageNumber++;
        }
    }

    public void previousPage() {
        if (pageNumber>0) {
            pageNumber--;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (galleryPics == null) {
            return;  // bail on the rest, since there is no image to draw
        }
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
        double scaling = (float)newWidth/galleryPics[0].getWidth();

        if (newWidth > 0 && newHeight > 0) {
            BufferedImage scaled = new BufferedImage(newWidth, newHeight, galleryPics[0].getType());
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g2.drawImage(
                    galleryPics[pageNumber],
                    0, 0, newWidth, newHeight,
                    0, 0,
                    galleryPics[pageNumber].getWidth(), galleryPics[pageNumber].getHeight(),
                    null);

            // paint grid lines on top
            double scaledLeftMargin = leftMargin * scaling;
            double scaledTopMargin = topMargin * scaling;
            double scaledBottomMargin = bottomMargin * scaling;
            double scaledWidth = singlePicWidth * scaling;
            double scaledHeight = singlePicHeight * scaling;
            double scaledHSpacing = hSpacing * scaling;
            double scaledVSpacing = vSpacing * scaling;
            g2.setColor(Color.red);
            // page margins
            g2.drawLine((int)scaledLeftMargin,0, (int)scaledLeftMargin, getHeight());
            g2.drawLine(0, (int)scaledTopMargin, getWidth(), (int)scaledTopMargin);
            g2.drawLine(
                    0, scaled.getHeight()-(int)scaledBottomMargin,
                    getWidth(), scaled.getHeight()-(int)scaledBottomMargin);

            // bounding box around individual pics
            double y = scaledTopMargin;
            int unscaledY = topMargin;
            while (y<scaled.getHeight() - scaledBottomMargin - scaledHeight) {
                double x = scaledLeftMargin;
                int unscaledX = leftMargin;
                while (x<scaled.getWidth()-scaledWidth) {
                    // check if this individual pic is blank
                    BufferedImage checkImg = galleryPics[pageNumber].getSubimage(
                            (int)unscaledX, (int)unscaledY,
                            (int)singlePicWidth, (int)singlePicHeight);
                    if (!ResourceManager.isBlank(checkImg)) {
                        g2.drawRect((int) x, (int) y, (int) scaledWidth, (int) scaledHeight);
                    }
                    x = x + scaledWidth + scaledHSpacing;
                    unscaledX = unscaledX + singlePicWidth + hSpacing;
                }
                y = y + scaledHeight + scaledVSpacing;
                unscaledY = unscaledY + singlePicHeight + vSpacing;
            }
            g2.dispose();
        }
    }
}
