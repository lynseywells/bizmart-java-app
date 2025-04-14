package component.labels;

import component.Colors;
import java.awt.*;
import javax.swing.*;

/**
 * A custom JLabel for the Bizmart application.
 */
public class BizmartLabel extends JLabel {

    /**
     * BizmartLabel constructor creates a styled JLabel.
     *
     * @param str the label text
     */
    public BizmartLabel(String str) {
        super(str);
        setFont(new Font("Nunito", Font.PLAIN, 22));
        setForeground(Colors.RICH_BLACK);
    }
}
