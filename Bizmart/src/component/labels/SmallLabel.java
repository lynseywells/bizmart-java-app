package component.labels;

import component.Colors;
import java.awt.*;
import javax.swing.*;

/**
 * A custom JLabel for secondary information in the Bizmart applications.
 */
public class SmallLabel extends JLabel {

    /**
     * SmallLabel constructor creates a JLabel with styling. Font size is set to
     * 20.
     *
     * @param str label text
     */
    public SmallLabel(String str) {
        super(str);
        setFont(new Font("Nunito", Font.PLAIN, 20));
        setForeground(Colors.RICH_BLACK);
    }
}
