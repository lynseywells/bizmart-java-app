package component.fields;

import component.Colors;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A custom JTextField for the Bizmart application.
 */
public class InputField extends JTextField {

    /**
     * InputField width in pixels.
     */
    private int width;

    /**
     * InputField height in pixels.
     */
    private int height;

    /**
     * Indicates if place holder text is visible.
     */
    private boolean placeholderActive;

    /**
     * InputField constructor creates a JTextField with styling.
     */
    public InputField() {
        super();
        //set field size
        width = 350;
        height = 45;
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        //set font
        setFont(new Font("Nunito", Font.PLAIN, 20));
        //set margin between text and border
        setMargin(new Insets(5, 5, 5, 5));
        placeholderActive = false;
        //set colors
        setBackground(Colors.GHOST_WHITE);
        setForeground(Colors.DARK_BLUE);
    }

    /**
     * InputField constructor creates a JTextField with styling and placeholder
     * text.
     *
     * @param placeholder example text displayed when the InputField is empty
     */
    public InputField(String placeholder) {
        super();
        //set field size
        width = 350;
        height = 45;
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        //set font
        setFont(new Font("Nunito", Font.ITALIC, 20));
        //set margin between text and border
        setMargin(new Insets(5, 5, 5, 5));
        //set colors
        setBackground(Colors.GHOST_WHITE);
        setForeground(Colors.GRAY);
        setText(placeholder);
        placeholderActive = true;
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (getText().equals(placeholder)) {
                    setText("");
                    //set font
                    setFont(new Font("Nunito", Font.PLAIN, 20));
                    setForeground(Colors.DARK_BLUE);
                    placeholderActive = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                //set font
                if (getText().equals("")) {
                    setText(placeholder);
                    setFont(new Font("Nunito", Font.ITALIC, 20));
                    setForeground(Colors.GRAY);
                    placeholderActive = true;
                }
            }
        });
    }

    //-----------------------------//
    //---------- GETTERS ----------//
    /**
     * Gets the InputField's height.
     *
     * @return height in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the InputField's width.
     *
     * @return width in pixels
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets placeholder visibility status.
     *
     * @return true if the placeholder text is active, false otherwise
     */
    public boolean isPlaceholderActive() {
        return placeholderActive;
    }

    //-----------------------------//
    //---------- SETTERS ----------//
    /**
     * Sets the InputField's height.
     *
     * @param height field height in pixels
     */
    public void setHeight(int height) {
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
    }

    /**
     * Sets the InputField's width.
     *
     * @param width field width in pixels
     */
    public void setWidth(int width) {
        this.width = width;
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
    }

    /**
     * Overridden paste() prevents the user from pasting text into the
     * InputField.
     */
    @Override
    public void paste() {
        return;
    }
}
