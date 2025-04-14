package component;

import javax.swing.*;
import java.awt.event.*;

/**
 * PopupDialog is used to create a dialog window with keyboard accessibility.
 */
public class PopupDialog extends JDialog {

    /**
     * PopupDialog constructor creates a JDialog with the default close
     * operation DISPOSE_ON_CLOSE and adds key binding to dispose the JDialog
     * when "ESCAPE" is pressed while the JDialog is in focus.
     *
     * @param frame the JFrame owner of this PopupDialog
     */
    public PopupDialog(JFrame frame) {
        super(frame);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // ADD KEY BINDING
        Action closePress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };

        ((JPanel) getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "pressed");
        ((JPanel) getContentPane()).getActionMap().put("pressed", closePress);
    }
}
