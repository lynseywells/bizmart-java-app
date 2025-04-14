package component.buttons;

import component.Sizing;
import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;

/**
 * A custom button for the Bizmart application that looks like an icon.
 */
public class IconButton extends JLabel {

    /**
     * Default ImageIcon for the button.
     */
    private ImageIcon icon;

    /**
     * ImageIcon for when the button is in focus.
     */
    private ImageIcon focus;

    /**
     * ImageIcon for when the button is pressed.
     */
    private ImageIcon press;

    /**
     * IconButton constructor creates a button with the passed icon.
     *
     * @param iconImg a URL to the default icon image
     * @param focusImg a URL to the focus icon image
     * @param pressImg a URL to the pressed icon image
     */
    public IconButton(URL iconImg, URL focusImg, URL pressImg) {
        super();
        //setup icons
        icon = new ImageIcon(iconImg);
        focus = new ImageIcon(focusImg);
        press = new ImageIcon(pressImg);
        icon = Sizing.resizeIcon(icon, 40, 40);
        focus = Sizing.resizeIcon(focus, 40, 40);
        press = Sizing.resizeIcon(press, 40, 40);

        //set the initial icon of this JLabel
        setIcon(icon);

        //set the cursor that appears when the button is hovered over
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFocusable(true);

        //MOUSE ADAPTER TO LISTEN FOR MOUSE CLICKS
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setIcon(press);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (hasFocus()) {
                    setIcon(focus);
                } else {
                    setIcon(icon);
                }
            }
        });

        //FOCUS LISTENER - ALSO FOR KEYBOARD USERS TO INDICATE ICON CAN BE PRESSED
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                setIcon(focus);
            }

            @Override
            public void focusLost(FocusEvent e) {
                setIcon(icon);
            }
        });

        //SET UP KEYBINDING FOR KEYBOARD USERS
        Action pressed = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setIcon(press);
            }
        };
        Action release = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setIcon(focus);
            }
        };

        this.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        this.getActionMap().put("pressed", pressed);
        this.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "released");
        this.getActionMap().put("released", release);
    }
}
