package component.labels;

import component.Colors;
import component.Sizing;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A custom JLabel for required InputFields in the Bizmart applications.
 * FieldRequiredLabel has an icon indicating it's required, which can also
 * display a warning to notify the user when text in the InputField is invalid.
 */
public class FieldRequiredLabel extends JPanel {

    /**
     * The icon displayed next to the JLabel.
     */
    private JLabel icon;

    /**
     * An icon marking the InputField as required.
     */
    private ImageIcon warning;
    /**
     * An icon indicating text in the InputField is invalid.
     */
    private ImageIcon error;

    /**
     * The BizmartLabel containing the label text.
     */
    private BizmartLabel label;

    /**
     * Indicates if the warning icon should be displayed or not.
     */
    private boolean warningActive;

    /**
     * FieldLabel constructor creates a BizmartLabel and locates the warning and
     * required icons.
     *
     * @param text the label text
     */
    public FieldRequiredLabel(String text) {
        //create a JPanel with no space around the edges
        super(new FlowLayout(SwingConstants.LEFT, 0, 0));
        setBackground(null);
        setFocusable(false);

        //setup warning & error icons
        warning = new ImageIcon(FieldRequiredLabel.class.getResource("/images/warning.png"));
        warning = Sizing.resizeIcon(warning, 25, 25);
        error = new ImageIcon(FieldRequiredLabel.class.getResource("/images/error_warning.png"));
        error = Sizing.resizeIcon(error, 25, 25);

        //set the icon of the JLabel
        icon = new JLabel(warning);
        setToolTipText("This field is required.");
        warningActive = false;

        //setup field label
        label = new BizmartLabel(text);

        //add icon & label to panel 
        add(icon);
        add(Box.createHorizontalStrut(5));
        add(label);

        //set maximum size to prevent JPanel from resizing
        setMaximumSize(getPreferredSize());

        //focus listener to indicate when label has focus
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (warningActive) {
                    label.setForeground(Colors.LIGHT_RED);
                } else {
                    label.setForeground(Colors.MID_BLUE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (warningActive) {
                    label.setForeground(Colors.RED);
                } else {
                    label.setForeground(Colors.RICH_BLACK);
                }
            }
        });
    }

    //-----------------------------//
    //---------- SETTERS ----------//
    /**
     * Sets the text of the FieldRequiredLabel.
     *
     * @param str the label text
     */
    public void setText(String str) {
        label.setText(str);
    }

    //--------------------------------------------------//
    //---------- FIELD REQUIRED LABEL METHODS ----------//
    /**
     * Makes the warning icon visible and changes tool tip text to show more
     * information.
     *
     * @param warningText the warning to display
     */
    public void addWarning(String warningText) {
        setToolTipText(warningText);
        icon.setIcon(error);
        label.setForeground(Colors.RED);
        warningActive = true;
    }

    /**
     * Changes the icon back to the required icon and resets tool tip text.
     */
    public void removeWarning() {
        setToolTipText("This field is required.");
        icon.setIcon(warning);
        label.setForeground(Colors.RICH_BLACK);
        warningActive = false;
    }
}
