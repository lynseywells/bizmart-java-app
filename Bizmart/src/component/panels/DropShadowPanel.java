package component.panels;

import javax.swing.*;
import java.awt.*;

/**
 * JPanel styled to have a drop shadow. Used by the Bizmart application.
 */
public class DropShadowPanel extends JPanel {

    /**
     * The side of the shadow on all sides of the JPanel in pixels.
     */
    private int shadowSize;

    /**
     * JPanel background color.
     */
    private Color background;

    /**
     * The color the drop shadow will blend out to. By default, the drop shadow
     * fades out to white.
     */
    private Color blendColor;

    /**
     * The color of the drop shadow. By default, the drop shadow is black.
     */
    private Color shadowColor;

    /**
     * DropShadowPanel constructor creates a JPanel with the default drop
     * shadow.
     *
     * @param shadowSize size in pixels
     */
    public DropShadowPanel(int shadowSize) {
        super();
        this.shadowSize = shadowSize;
        shadowColor = Color.BLACK;

        /* adds an invisible border to the panel - this is so the drop shadow is drawn outside of the 
        ** actual panel preventing components from covering it */
        setBorder(BorderFactory.createEmptyBorder(shadowSize, shadowSize, shadowSize, shadowSize));
    }

    /**
     * DropShadowPanel constructor creates a JPanel with the default drop
     * shadow.
     *
     * @param shadowSize size in pixels
     * @param background background color
     */
    public DropShadowPanel(int shadowSize, Color background) {
        this(shadowSize);
        this.background = background;
    }

    /**
     * DropShadowPanel constructor creates a JPanel with a black drop shadow
     * that fades into the specified color.
     *
     * @param shadowSize size in pixels
     * @param background background color
     * @param blendColor blend color for drop shadow
     */
    public DropShadowPanel(int shadowSize, Color background, Color blendColor) {
        this(shadowSize, background);
        this.blendColor = blendColor;
    }

    /**
     * DropShadowPanel constructor creates a JPanel with a drop shadow gradient
     * of the specified colors.
     *
     * @param shadowSize size in pixels
     * @param background background color
     * @param blendColor blend color for drop shadow
     * @param shadowColor drop shadow color
     */
    public DropShadowPanel(int shadowSize, Color background, Color blendColor, Color shadowColor) {
        this(shadowSize, background, blendColor);
        this.shadowColor = shadowColor;
    }

    //-----------------------------//
    //---------- GETTERS ----------//
    @Override
    public Color getBackground() {
        return background;
    }

    /**
     * Gets the color the drop shadow blends out to.
     *
     * @return blend color
     */
    public Color getBlendColor() {
        return blendColor;
    }

    /**
     * Gets drop shadow size in pixels.
     *
     * @return size in pixels
     */
    public int getShadowSize() {
        return shadowSize;
    }

    /**
     * Gets drop shadow color.
     *
     * @return shadow color
     */
    public Color getShadowColor() {
        return shadowColor;
    }

    //-----------------------------//
    //---------- SETTERS ----------//
    @Override
    public void setBackground(Color c) {
        background = c;
    }

    /**
     * Sets the color the drop shadow blends out to.
     *
     * @param c blend color
     */
    public void setBlendColor(Color c) {
        blendColor = c;
    }

    /**
     * Sets drop shadow size in pixels.
     *
     * @param size size in pixels
     */
    public void setShadowSize(int size) {
        shadowSize = size;
    }

    /**
     * Sets drop shadow color.
     *
     * @param c shadow color
     */
    public void setShadowColor(Color c) {
        shadowColor = c;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //cast Graphics to Graphics2D for antialiasing
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //the darkest the shadow can be
        int maxOpacity = 60;

        //paints the background for the shadow to blend into
        //w/o this, the drop shadow blends to white regardless of the containers background color
        if (blendColor != null) {
            g2d.setColor(blendColor);
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        }

        //creates a new rectangle until the number of pixels is reached
        for (int i = 0; i < shadowSize; i++) {
            /* sets the color of the current rectangle to a shade of gray
         ** the alpha value (0-255) get's darker as the loop continues
         ** creating a transition effect from light to dark as the 
         ** rectangle is drawn further inside the panel*/
            int b = shadowColor.getBlue();
            int r = shadowColor.getRed();
            int gr = shadowColor.getGreen();
            g2d.setColor(new Color(r, gr, b, ((maxOpacity / shadowSize) * i)));

            /* drawRect draws a one pixel wide rectangle from (x, y) to (x + width, y + height)
         ** in each iteration, a rectangle is drawn inside of the previous rectangle b/c 
         ** the width and height is subtracted by a larger value each loop */
            g2d.drawRect(i, i, this.getWidth() - ((i * 2) + 1), this.getHeight() - ((i * 2) + 1));
        }

        //paint the inside of the panel if the user specifies a background color
        if (background != null) {
            g2d.setColor(background);
            g2d.fillRect(shadowSize, shadowSize, this.getWidth() - ((shadowSize * 2) + 1), this.getHeight() - (shadowSize * 2) + 1);
        }
    }
}
