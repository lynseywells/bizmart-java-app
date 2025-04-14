package component.buttons;

import component.fields.PasswordField;
import component.Sizing;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * TogglePasswordButton is used to show or hide the text of a PasswordField in
 * the Bizmart application.
 */
public class TogglePasswordButton extends JLabel {

    /**
     * IconState indicates whether PasswordField text should be shown or hidden,
     * and what icon the TogglePasswordButton should show.
     */
    public enum IconState {
        /**
         * Indicates passwordField text should be shown, and hideIcon should be
         * displayed.
         */
        SHOW,
        /**
         * Indicates passwordField text should be hidden, and showIcon should be
         * displayed.
         */
        HIDE
    }

    /**
     * Icon that is displayed when the IconState is HIDE. Indicates to the user
     * that pressing the button will show the password.
     */
    private ImageIcon showIcon;

    /**
     * Icon that is displayed when the IconState is SHOW. Indicates to the user
     * that pressing the button will hide the password again.
     */
    private ImageIcon hideIcon;

    /**
     * Icon that is displayed when the IconState is HIDE, and the button is
     * pressed.
     */
    private ImageIcon showBlue;

    /**
     * Icon that is displayed when the IconState is SHOW, and the button is
     * pressed.
     */
    private ImageIcon hideBlue;

    /**
     * Icon that is displayed when the IconState is HIDE, and the button is in
     * focus.
     */
    private ImageIcon showFocus;

    /**
     * Icon that is displayed when the IconState is SHOW, and the button is in
     * focus.
     */
    private ImageIcon hideFocus;

    /**
     * The PasswordField the button toggles the text of.
     */
    private PasswordField passwordField;

    /**
     * The current IconState of the button. PasswordField text is shown when
     * iconState is SHOW, and hidden when iconState is HIDE.
     */
    private IconState iconState;

    /**
     * TogglePasswordButton constructor locates all necessary icons and creates
     * the button.
     *
     * @param p the PasswordField for the button
     */
    public TogglePasswordButton(PasswordField p) {
        super();
        //setup icons
        showIcon = new ImageIcon(TogglePasswordButton.class.getResource("/images/show.png"));
        hideIcon = new ImageIcon(TogglePasswordButton.class.getResource("/images/hide.png"));
        showIcon = Sizing.resizeIcon(showIcon, 40, 40);
        hideIcon = Sizing.resizeIcon(hideIcon, 40, 40);
        //setup alternate color icons
        showBlue = new ImageIcon(TogglePasswordButton.class.getResource("/images/show_blue.png"));
        hideBlue = new ImageIcon(TogglePasswordButton.class.getResource("/images/hide_blue.png"));
        showBlue = Sizing.resizeIcon(showBlue, 40, 40);
        hideBlue = Sizing.resizeIcon(hideBlue, 40, 40);
        //set up focus color icons
        showFocus = new ImageIcon(TogglePasswordButton.class.getResource("/images/show_focus.png"));
        hideFocus = new ImageIcon(TogglePasswordButton.class.getResource("/images/hide_focus.png"));
        showFocus = Sizing.resizeIcon(showFocus, 40, 40);
        hideFocus = Sizing.resizeIcon(hideFocus, 40, 40);
        //set current icon state
        iconState = IconState.SHOW;
        //set the initial icon of this JLabel
        setIcon(showIcon);
        //link the password field associated with this toggle
        passwordField = p;
        //set the cursor that appears when the button is hovered over
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFocusable(true);

        //MOUSE ADAPTER TO LISTEN FOR MOUSE CLICKS
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                iconDown();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                iconUp();
            }
        });

        //SET UP KEYBINDING FOR KEYBOARD USERS
        Action press = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                iconDown();
            }
        };
        Action release = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                iconUp();
            }
        };

        this.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        this.getActionMap().put("pressed", press);
        this.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "released");
        this.getActionMap().put("released", release);

        //FOCUS LISTENER - ALSO FOR KEYBOARD USERS TO INDICATE ICON CAN BE PRESSED
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (iconState == IconState.SHOW) {
                    setIcon(showFocus);
                } else {
                    setIcon(hideFocus);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (iconState == IconState.SHOW) {
                    setIcon(showIcon);
                } else {
                    setIcon(hideIcon);
                }
            }
        });
    }

    /**
     * Changes the button's IconState and the displayed icon.
     *
     * @param state the new IconState
     */
    public void changeState(IconState state) {
        if (state == IconState.SHOW) {
            passwordField.show();
            setIcon(hideIcon);
            iconState = IconState.HIDE;
        } else {
            passwordField.hide();
            setIcon(showIcon);
            iconState = IconState.SHOW;
        }
    }

    /**
     * Changes the icon when the button is pressed. Visually indicates that the
     * button has been pressed.
     */
    public void iconDown() {
        if (iconState == IconState.SHOW) {
            setIcon(showBlue);
        } else {
            setIcon(hideBlue);
        }
    }

    /**
     * Changes the icon when the button is released. Visually indicates that the
     * button has been released.
     */
    public void iconUp() {
        if (iconState == IconState.SHOW) {
            passwordField.show();
            if (hasFocus()) {
                setIcon(hideFocus);
            } else {
                setIcon(hideIcon);
            }
            iconState = IconState.HIDE;
        } else {
            passwordField.hide();
            if (hasFocus()) {
                setIcon(showFocus);
            } else {
                setIcon(showIcon);
            }
            iconState = IconState.SHOW;
        }
    }
}
