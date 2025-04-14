package component.labels;

import component.Colors;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * A JLabel used to create headings for forms in the Bizmart application.
 */
public class FormTitle extends JPanel {

    /**
     * The width of the underline displayed beneath text.
     */
    private int underlineWidth;

    /**
     * The thickness of the underline displayed beneath text.
     */
    private int thickness;

    /**
     * The amount of space between the text baseline and underline.
     */
    private double offset;

    /**
     * The JLabel containing the title text.
     */
    private JLabel title;

    /**
     * The color of the underline.
     */
    private Color underlineColor;

    /**
     * FormTitle constructor creates and styles a JLabel as a header without an
     * underline.
     *
     * @param str the title text
     */
    public FormTitle(String str) {
        super();
        title = new JLabel(str);
        title.setFont(new Font("Nunito", Font.PLAIN, 30));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(Colors.RICH_BLACK);
        underlineColor = title.getForeground();
        setBackground(Color.WHITE);
        underlineWidth = 0;
        thickness = 0;
        offset = 0;
        add(title);
    }

    /**
     * FormTitle constructor creates and styles a JLabel as a header with an
     * underline.
     *
     * @param str the title text
     * @param underlineWidth underline width in pixels
     * @param thickness underline thickness in pixels
     */
    public FormTitle(String str, int underlineWidth, int thickness) {
        this(str);
        this.underlineWidth = underlineWidth;
        this.thickness = thickness;
        offset = 2;
        FontMetrics metrics = getFontMetrics(title.getFont());
        setPreferredSize(new Dimension(underlineWidth, metrics.getHeight() + thickness + (int) (this.thickness * offset)));
        add(title);
    }

    //-----------------------------//
    //---------- GETTERS ----------//
    /**
     * Gets the JLabel underline's width in pixels.
     *
     * @return width in pixels
     */
    public int getUnderlineWidth() {
        return underlineWidth;
    }

    /**
     * Gets the JLabel underline's thickness in pixels.
     *
     * @return thickness in pixels
     */
    public int getUnderlineThickness() {
        return thickness;
    }

    /**
     * Gets the vertical offset of the underline.
     *
     * @return offset amount
     */
    public double getUnderlineOffset() {
        return offset;
    }

    /**
     * Gets the Color of the underline.
     *
     * @return underline color
     */
    public Color getUnderlineColor() {
        return underlineColor;
    }

    //-----------------------------//
    //---------- SETTERS ----------//
    /**
     * Sets the JLabel underline's width in pixels.
     *
     * @param width width in pixels
     */
    public void setUnderlineWidth(int width) {
        underlineWidth = width;
        FontMetrics metrics = getFontMetrics(title.getFont());
        setPreferredSize(new Dimension(this.underlineWidth, metrics.getHeight() + thickness + (int) (this.thickness * offset)));
        repaint();
    }

    /**
     * Sets the JLabel underline's thickness in pixels.
     *
     * @param thickness thickness in pixels
     */
    public void setUnderlineThickness(int thickness) {
        this.thickness = thickness;
        FontMetrics metrics = getFontMetrics(title.getFont());
        setPreferredSize(new Dimension(this.underlineWidth, metrics.getHeight() + thickness + (int) (this.thickness * offset)));
        repaint();
    }

    /**
     * Sets the vertical offset of the underline. Higher numbers move the offset
     * down, and lower numbers move the offset closer to the text baseline.
     *
     * @param d offset amount.
     */
    public void setUnderlineOffset(double d) {
        offset = d;
        FontMetrics metrics = getFontMetrics(title.getFont());
        setPreferredSize(new Dimension(this.underlineWidth, metrics.getHeight() + thickness + (int) (this.thickness * offset)));
        repaint();
    }

    /**
     * Sets the underline's Color.
     *
     * @param c underline color
     */
    public void setUnderlineColor(Color c) {
        underlineColor = c;
    }

    /**
     * Sets the text of the FormTitle.
     *
     * @param str label text
     */
    public void setText(String str) {
        title.setText(str);
    }

    //----------------------------------------//
    //---------- FORM TITLE METHODS ----------//
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //convert to Graphics2D to use the fill method
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (underlineWidth > 0 && thickness > 0) {
            FontMetrics m = getFontMetrics(title.getFont());
            int y = m.getHeight() + (int) (thickness * offset);

            RoundRectangle2D.Double rect = new RoundRectangle2D.Double(0, y, underlineWidth, thickness, thickness, thickness);
            g2d.setColor(underlineColor);
            g2d.fill(rect);
        }
    }
}
