package component.fields;

import component.Colors;
import java.awt.*;
import javax.swing.*;

/**
 * A custom JPasswordField for the Bizmart application.
 */
public class PasswordField extends JPasswordField {

    /**
     * PasswordField width in pixels.
     */
    private int width;

    /**
     * PasswordField height in pixels.
     */
    private int height;

    /**
     * PasswordField constructor creates a JPasswordField with styling.
     */
    public PasswordField() {
        super();
        //set field size
        width = 350;
        height = 45;
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        //set font
        setFont(new Font("Nunito", Font.PLAIN, 35));
        //set margin between text and border
        setMargin(new Insets(5, 5, 5, 5));
        //set colors
        setForeground(Colors.DARK_BLUE);
        setBackground(Colors.GHOST_WHITE);
        //set masking character
        setEchoChar('•');
    }

    //-----------------------------//
    //---------- GETTERS ----------//
    /**
     * Gets PasswordField width.
     *
     * @return field width in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets PasswordField height.
     *
     * @return field height in pixels
     */
    public int getHeight() {
        return height;
    }

    //-----------------------------//
    //---------- SETTERS ----------//
    /**
     * Sets PasswordField width.
     *
     * @param width field width in pixels
     */
    public void setWidth(int width) {
        this.width = width;
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
    }

    /**
     * Sets PasswordField height.
     *
     * @param height field height in pixels
     */
    public void setHeight(int height) {
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
    }

    //--------------------------------------------//
    //---------- PASSWORD FIELD METHODS ----------//
    /**
     * Hides the text in the PasswordField.
     */
    public void hide() {
        setEchoChar('•');
        setFont(new Font("Nunito", Font.PLAIN, 35));
    }

    /**
     * Shows the text in the PasswordField.
     */
    public void show() {
        setEchoChar((char) 0);
        setFont(new Font("Nunito", Font.PLAIN, 20));
    }
}
