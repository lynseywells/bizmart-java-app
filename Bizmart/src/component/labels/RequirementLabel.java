package component.labels;

import component.Colors;
import component.Sizing;
import java.awt.*;
import javax.swing.*;

/**
 * A label used by the Bizmart application to indicate if a requirement for an
 * InputField is met.
 */
public class RequirementLabel extends JPanel {

    /**
     * Icon that indicates whether the requirement is met or not.
     */
    private JLabel icon;

    /**
     * Label that displays requirement text.
     */
    private JLabel label;

    /**
     * ImageIcon displayed in the icon JLabel that indicates the requirement is
     * not met.
     */
    private ImageIcon error;

    /**
     * ImageIcon displayed in the icon JLabel that indicates the requirement is
     * met.
     */
    private ImageIcon check;

    /**
     * Width of icon.
     */
    private int imgWidth;

    /**
     * Height of icon.
     */
    private int imgHeight;

    /**
     * Indicates if the requirement is met or not.
     */
    private boolean isMet;

    /**
     * RequirementLabel constructor locates icon images and creates a
     * RequirementLabel with isMet set to false.
     *
     * @param text label text
     */
    public RequirementLabel(String text) {
        super();
        //sets layout so there's no margin around the label
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        imgWidth = 22;
        imgHeight = 22;

        //requirement not met image
        error = new ImageIcon(RequirementLabel.class.getResource("/images/error.png"));
        error = Sizing.resizeIcon(error, imgWidth, imgHeight);

        //requirement met image
        check = new ImageIcon(RequirementLabel.class.getResource("/images/check.png"));
        check = Sizing.resizeIcon(check, imgWidth, imgHeight);

        //starting icon & label text
        icon = new JLabel(error);
        label = new JLabel(text);
        label.setFont(new Font("Nunito", Font.PLAIN, 20));
        label.setForeground(Colors.RED);

        add(icon);
        add(Box.createHorizontalStrut(5));
        add(label);

        setPreferredSize(getPreferredSize());
        setMaximumSize(getPreferredSize());
        setBackground(null);

        isMet = false;
    }

    /**
     * Sets the requirement as met and styles this RequirementLabel accordingly.
     *
     * @param isMet true to set requirement met, false otherwise
     */
    public void requirementMet(boolean isMet) {
        if (isMet) {
            label.setForeground(Colors.GREEN);
            icon.setIcon(check);
        } else {
            label.setForeground(Colors.RED);
            icon.setIcon(error);
        }
        this.isMet = isMet;
        repaint();
    }

    /**
     * Gets the requirement met status of this RequirementLabel.
     *
     * @return true if the requirement is met, false otherwise
     */
    public boolean getIsMet() {
        return isMet;
    }
}
