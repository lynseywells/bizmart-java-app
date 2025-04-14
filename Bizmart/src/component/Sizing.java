package component;

import static component.panels.ItemThumbnail.WIDTH;
import java.awt.*;
import javax.swing.*;

/**
 * Sizing contains methods to size ImageIcons in the Bizmart application.
 */
public abstract class Sizing {

    /**
     * Resizes an ImageIcon to the specified width and height.
     *
     * @param icon the ImageIcon to resize
     * @param width the new width
     * @param height the new height
     * @return the resized ImageIcon
     */
    public static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image i = icon.getImage();
        Image resize = i.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resize);
    }

    /**
     * When uploading a new inventory item or editing the image of an existing
     * item, this method is called to scale the image down into a thumbnail.
     *
     * @param icon the ImageIcon to be resized
     * @return a new ImageIcon at thumbnail size
     */
    public static Image createThumbnail(ImageIcon icon) {
        // get image size
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();

        // calculate scale from height to new height
        int newHeight = 100;
        double scale = (double) newHeight / height;

        // use scale to set the new width while maintaining aspect ratio
        int newWidth = (int) Math.round(width * scale);

        // if image is too wide, subtract from size until image fits in jpanel
        while (newWidth >= WIDTH - 30) {
            newWidth -= 10;
            newHeight -= 10;
        }

        Image img = icon.getImage();
        Image sizeImg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return sizeImg;
    }
}
