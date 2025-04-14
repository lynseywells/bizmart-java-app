package component.labels;

import component.Colors;
import java.awt.*;
import javax.swing.*;

/**
 * A custom JLabel to visually indicate invalid form input in the Bizmart
 * application.
 */
public class WarningLabel extends JLabel {

    /**
     * WarningLabel constructor creates a JLabel styled to visually indicate an
     * error or warning in the program. Invisible by default.
     *
     * @param str label text
     */
    public WarningLabel(String str) {
        super(str);
        setFont(new Font("Nunito", Font.PLAIN, 22));
        setForeground(Colors.RED);
        setVisible(false);
    }
}
