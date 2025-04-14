package component.buttons;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import component.Colors;
import java.util.regex.*;
import javax.swing.*;

/**
 * A custom, styled button for the Bizmart application.
 */
public class ButtonPrimary extends JComponent {

    /**
     * Button width, including focus border width.
     */
    private int width;

    /**
     * Button height, including focus border width.
     */
    private int height;

    /**
     * Corner arch of the button.
     */
    private int arch;

    /**
     * Border line width. Border width is drawn inside of the button, and does
     * not add to the buttons width and height.
     */
    private int borderWidth;

    /**
     * Focus border line width. This is drawn around the button, and adds to the
     * buttons width and height.
     */
    private int focusBorderWidth;

    /**
     * Background color.
     */
    private Color backgroundColor;

    /**
     * Foreground (text) color.
     */
    private Color foregroundColor;

    /**
     * Border line color.
     */
    private Color borderColor;

    /**
     * Focus border line color.
     */
    private Color focusColor;

    /**
     * Background color when button is pressed.
     */
    private Color selectedBackgroundColor;

    /**
     * Foreground (text) color when button is pressed.
     */
    private Color selectedForegroundColor;

    /**
     * Border line color when button is pressed.
     */
    private Color selectedBorderColor;

    /**
     * The text displayed in the center of the button.
     */
    private String text;

    /**
     * Indicates if the button is pressed.
     */
    private boolean isSelected;

    /**
     * Indicates if the button currently has focus.
     */
    private boolean hasFocus;

    /**
     * Variable equals (borderWidth * 2) and is used to draw the button.
     */
    protected int borderX2;

    /**
     * Variable equals (focusBorderWidth * 2) and is used to draw the button.
     */
    protected int focusX2;

    /**
     * Variable equals (focusBorderWidth + borderWidth) and is used to draw the
     * button.
     */
    protected int focusPlusBorder;

    /**
     * Button width when not in focus. Ensures button is not drawn too large
     * when not in focus.
     */
    protected int noFocusWidth;

    /**
     * Button height when not in focus. Ensures button is not drawn too large
     * when not in focus.
     */
    protected int noFocusHeight;

    /**
     * Corner arch of the border line. Equals (arch + borderWidth).
     */
    protected int borderArch;

    /**
     * Corner arch of the focus border line. Equals (arch + focusBorderWidth).
     */
    protected int focusArch;

    /**
     * ButtonPrimary constructor creates a button with default Colors and
     * sizing, and displays custom text in the center.
     *
     * @param str the text to display inside the button
     */
    public ButtonPrimary(String str) {
        //SET TEXT & FONT
        text = str;
        setFont(new Font("Nunito", Font.BOLD, 26));

        //SET FOCUSABLE
        setFocusable(true);
        focusColor = Colors.EMERALD;
        focusBorderWidth = 4;

        //SET SIZE & ARCH
        width = 204;
        height = 64;
        setPreferredSize(new Dimension(width, height));
        arch = 20;
        borderWidth = 6;

        //SET COLOR
        backgroundColor = Colors.LIGHT_BLUE;
        foregroundColor = Color.WHITE;
        borderColor = Colors.BLUE;

        //SET SELECTED COLOR
        selectedBackgroundColor = Colors.MID_BLUE;
        selectedBorderColor = Colors.BLUE;
        selectedForegroundColor = Colors.GHOST_WHITE;

        //SET CURSOR
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        //ADD FOCUS LISTENER TO INDICATE WHEN BUTTON HAS FOCUS FOR KEYBOARD USERS
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                hasFocus = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                hasFocus = false;
                repaint();
            }
        });

        //CALCULATE BUTTON DRAWING VARIABLES
        //button border
        borderX2 = borderWidth * 2;
        borderArch = arch + borderWidth;

        //focus border
        focusX2 = focusBorderWidth * 2;
        focusPlusBorder = borderWidth + focusBorderWidth;
        focusArch = borderArch + focusBorderWidth;

        //button width/height when not in focus (if this isn't calculated, button will be drawn too large when not in focus)
        noFocusWidth = width - (focusBorderWidth * 2);
        noFocusHeight = height - (focusBorderWidth * 2);
    }

    //-----------------------------//
    //---------- GETTERS ----------//
    /**
     * Gets the button's corner arch size.
     *
     * @return corner arch size
     */
    public int getArch() {
        return arch;
    }

    /**
     * Gets the button's border width.
     *
     * @return border width
     */
    public int getBorderWidth() {
        return borderWidth;
    }

    @Override
    public Color getBackground() {
        return backgroundColor;
    }

    /**
     * Gets the background color of the button when pressed.
     *
     * @return pressed background color
     */
    public Color getSelectedBackground() {
        return selectedBackgroundColor;
    }

    @Override
    public Color getForeground() {
        return foregroundColor;
    }

    /**
     * Gets the foreground (text) color of the button when pressed.
     *
     * @return pressed foreground color
     */
    public Color getSelectedForeground() {
        return selectedForegroundColor;
    }

    /**
     * Gets the border line color of the button.
     *
     * @return border line color
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * Gets the border line color of the button when pressed.
     *
     * @return pressed border line color
     */
    public Color getSelectedBorderColor() {
        return selectedBorderColor;
    }

    /**
     * Gets the buttons text.
     *
     * @return button text
     */
    public String getText() {
        return text;
    }

    //-----------------------------//
    //---------- SETTERS ----------//
    @Override
    public void setPreferredSize(Dimension d) {
        //change the width and height fields
        width = (int) d.getWidth() + focusBorderWidth;
        height = (int) d.getHeight() + focusBorderWidth;
        //button width/height when not in focus (if this isn't calculated, button will be drawn too large when not in focus)
        noFocusWidth = width - (focusBorderWidth * 2);
        noFocusHeight = height - (focusBorderWidth * 2);
        //call the super setPreferredSize
        super.setPreferredSize(new Dimension(width, height));
    }

    /**
     * Sets the corner arch amount of the button. Calculates the arch amount for
     * the button's border and focus border line.
     *
     * @param arch corner arch amount
     */
    public void setArch(int arch) {
        this.arch = arch;
        borderArch = arch + borderWidth;
        focusArch = borderArch + focusBorderWidth;
    }

    /**
     * Sets the border line's width in pixels.
     *
     * @param pixels width
     */
    public void setBorderWidth(int pixels) {
        borderWidth = pixels;
        borderX2 = borderWidth * 2;
        focusPlusBorder = borderWidth + focusBorderWidth;
        borderArch = arch + borderWidth;
    }

    @Override
    public void setBackground(Color c) {
        backgroundColor = c;
    }

    /**
     * Sets the background color of the button when pressed.
     *
     * @param c background color
     */
    public void setSelectedBackground(Color c) {
        selectedBackgroundColor = c;
    }

    @Override
    public void setForeground(Color c) {
        foregroundColor = c;
    }

    /**
     * Sets the foreground (text) color of the button when pressed.
     *
     * @param c foreground color
     */
    public void setSelectedForeground(Color c) {
        selectedForegroundColor = c;
    }

    /**
     * Sets the border line color of the button.
     *
     * @param c border color
     */
    public void setBorderColor(Color c) {
        borderColor = c;
    }

    /**
     * Sets the border line color of the button when pressed.
     *
     * @param c border color
     */
    public void setSelectedBorderColor(Color c) {
        selectedBorderColor = c;
    }

    /**
     * Sets the text displayed in the center of the button.
     *
     * @param str text to display
     */
    public void setText(String str) {
        text = str;
    }

    //--------------------------------------------//
    //---------- BUTTON PRIMARY METHODS ----------//
    /**
     * When adding a MouseListener to ButtonPrimary, add this method to the
     * MouseListener method mousePressed() to add animation to the button.
     */
    /* Originally, animation was done in a MouseListener in the constructor. 
    ** When adding another MouseListener to add functionality to the button,
    ** sometimes only the first MouseListener would be triggered. 
    ** Implementing animation this way fixed the issue. */
    public void mousePressed() {
        if (isEnabled()) {
            isSelected = true;
            setFont(new Font("Nunito", Font.BOLD, 25));
            setBorderWidth(borderWidth / 2);
            repaint();
        }
    }

    /**
     * When adding a MouseListener to ButtonPrimary, add this method to the
     * MouseListener method mouseReleased() to add animation to the button.
     */
    public void mouseReleased() {
        if (isEnabled()) {
            isSelected = false;
            setFont(new Font("Nunito", Font.BOLD, 26));
            setBorderWidth(borderWidth * 2);
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //cast Graphics to Graphics2D for antialiasing
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //set the font
        g2d.setFont(getFont());
        //measures the width and height of the string using the set font
        FontMetrics metrics = g2d.getFontMetrics();
        int stringWidth = metrics.stringWidth(text);
        int stringHeight;
        //calculate the height based on the characters in the string
        Pattern p = Pattern.compile("[gjqpy]+");
        Matcher m = p.matcher(text);
        if (m.find()) {
            /* adding descent to the height makes the text appear more evenly 
         centered verically when the text has a descender */
            stringHeight = metrics.getHeight() + (metrics.getDescent() / 2);
        } else {
            stringHeight = metrics.getHeight();
        }

        RoundRectangle2D.Double border;
        RoundRectangle2D.Double fill;

        /* render the button based on if it's selected or not - if the button is selected, 
        ** it will appear smaller so it looks like a button being pressed. the object's actual
        ** size is maintained to not disrupt the containing panels layout. */
        if (isSelected && isEnabled()) {
            //draw a rectangle at the specific width and height (without focus) - 6 pixels
            border = new RoundRectangle2D.Double(focusBorderWidth + 3, focusBorderWidth + 3, noFocusWidth - 6, noFocusHeight - 6,
                    borderArch, borderArch);
            g2d.setColor(selectedBorderColor);
            g2d.fill(border);
            //draw a smaller rectangle on top of the first - creates a "border" with rounded edges
            fill = new RoundRectangle2D.Double(focusPlusBorder + 3, focusPlusBorder + 3, noFocusWidth - borderX2 - 6,
                    noFocusHeight - borderX2 - 6, arch, arch);
            g2d.setColor(selectedBackgroundColor);
            g2d.fill(fill);
            //set text color
            g2d.setColor(selectedForegroundColor);
        } else {
            //render an outline if the button is in focus   
            if (hasFocus) {
                //draw a rectangle
                RoundRectangle2D focus = new RoundRectangle2D.Double(0, 0, width, height, focusArch, focusArch);
                g2d.setColor(focusColor);
                g2d.fill(focus);
            }

            //draw a rectangle at the specific width and height (minus focus border width)
            border = new RoundRectangle2D.Double(focusBorderWidth, focusBorderWidth, width - focusX2, height - focusX2,
                    borderArch, borderArch);
            if (isEnabled()) {
                g2d.setColor(borderColor);
            } else {
                g2d.setColor(Color.DARK_GRAY);
            }
            g2d.fill(border);

            //draw a rectangle inside the other rectangle
            fill = new RoundRectangle2D.Double(focusPlusBorder, focusPlusBorder, width - focusX2 - borderX2,
                    height - focusX2 - borderX2, arch, arch);
            if (isEnabled()) {
                g2d.setColor(backgroundColor);
            } else {
                g2d.setColor(Color.GRAY);
            }

            g2d.fill(fill);

            //set text color
            g2d.setColor(foregroundColor);
        }

        //draws a centered string using provided text
        g2d.drawString(text, getAlignmentX() + ((width - stringWidth) / 2), getAlignmentY() + ((height - stringHeight) / 2) + metrics.getAscent());
    }
}
