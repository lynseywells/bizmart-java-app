package component.labels;

import component.Sizing;
import component.Colors;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A custom JLabel for InputFields in the Bizmart applications. FieldLabel can
 * display a warning to indicate when text in the InputField is invalid.
 */
public class FieldLabel extends JPanel {

    /**
     * An icon indicating text in the InputField is invalid.
     */
    private JLabel warning;

    /**
     * The BizmartLabel containing the label text.
     */
    private BizmartLabel label;

    /**
     * FieldLabel constructor creates a BizmartLabel and locates the warning
     * icon.
     *
     * @param text the label text
     */
    public FieldLabel(String text) {
        //create a JPanel with no space around the edges
        super(new FlowLayout(SwingConstants.LEFT, 0, 0));
        setBackground(null);
        setFocusable(false);

        //setup warning icon
        ImageIcon icon = new ImageIcon(FieldLabel.class.getResource("/images/error_warning.png"));
        icon = Sizing.resizeIcon(icon, 25, 25);
        warning = new JLabel(icon);
        warning.setVisible(false);

        //setup field label
        label = new BizmartLabel(text);

        //add icon & label to panel 
        add(warning);
        add(Box.createHorizontalStrut(5));
        add(label);

        //set maximum size to prevent JPanel from resizing
        setMaximumSize(getPreferredSize());

        //focus listener to indicate when label has focus (only focusable if warning is active)
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                label.setForeground(Colors.LIGHT_RED);
            }

            @Override
            public void focusLost(FocusEvent e) {
                label.setForeground(Colors.RED);
            }
        });
    }

    //-----------------------------//
    //---------- SETTERS ----------//
    /**
     * Sets the font of the FieldLabel.
     *
     * @param f the label Font
     */
    public void setLabelFont(Font f) {
        label.setFont(f);
    }

    /**
     * Sets the text of the FieldLabel.
     *
     * @param str the label text
     */
    public void setText(String str) {
        label.setText(str);
    }

    //-----------------------------------------//
    //---------- FIELD LABEL METHODS ----------//
    /**
     * Makes the warning icon visible and adds tool tip text with more
     * information.
     *
     * @param warningText the warning to display
     */
    public void addWarning(String warningText) {
        setToolTipText(warningText);
        warning.setVisible(true);
        label.setForeground(Colors.RED);
        setFocusable(true);
        //set maximum size to force JPanel to resize
        setMaximumSize(getPreferredSize());
    }

    /**
     * Makes the warning icon invisible and clears tool tip text.
     */
    public void removeWarning() {
        setToolTipText("");
        warning.setVisible(false);
        label.setForeground(Colors.RICH_BLACK);
        setFocusable(false);
        //set maximum size to force JPanel to resize
        setMaximumSize(getPreferredSize());
    }
}
