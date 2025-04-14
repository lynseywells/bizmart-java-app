package component.buttons;

import component.Sizing;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * SortButton is used to sort a list by ascending or descending order. It
 * changes it's icon to indicate the sort order.
 */
public class SortButton extends JLabel {

    /**
     * IconState indicates how a list should be sorted.
     */
    public enum IconState {
        /**
         * Indicates sort order is ascending.
         */
        ASCENDING,
        /**
         * Indicates sort order is descending.
         */
        DESCENDING
    }

    /**
     * Icon that is displayed when the IconState is ASCENDING. Indicates to the
     * user what the sort order is.
     */
    private ImageIcon ascending;

    /**
     * Icon that is displayed when the IconState is DESCENDING. Indicates to the
     * user what the sort order is.
     */
    private ImageIcon descending;

    /**
     * Icon that is displayed when the IconState is ASCENDING, and the button is
     * pressed.
     */
    private ImageIcon ascendingBlue;

    /**
     * Icon that is displayed when the IconState is DESCENDING, and the button
     * is pressed.
     */
    private ImageIcon descendingBlue;

    /**
     * Icon that is displayed when the IconState is ASCENDING, and the button is
     * in focus.
     */
    private ImageIcon ascendingFocus;

    /**
     * Icon that is displayed when the IconState is DESCENDING, and the button
     * is in focus.
     */
    private ImageIcon descendingFocus;

    /**
     * The current IconState of the button.
     */
    private IconState iconState;

    /**
     * SortButton constructor locates all necessary icons and creates the
     * button.
     */
    public SortButton() {
        super();
        //setup icons
        ascending = new ImageIcon(SortButton.class.getResource("/images/drop-up.png"));
        descending = new ImageIcon(SortButton.class.getResource("/images/drop-down.png"));
        ascending = Sizing.resizeIcon(ascending, 40, 40);
        descending = Sizing.resizeIcon(descending, 40, 40);
        //setup alternate color icons
        ascendingBlue = new ImageIcon(SortButton.class.getResource("/images/drop-up-blue.png"));
        descendingBlue = new ImageIcon(SortButton.class.getResource("/images/drop-down-blue.png"));
        ascendingBlue = Sizing.resizeIcon(ascendingBlue, 40, 40);
        descendingBlue = Sizing.resizeIcon(descendingBlue, 40, 40);
        //set up focus color icons
        ascendingFocus = new ImageIcon(SortButton.class.getResource("/images/drop-up-focus.png"));
        descendingFocus = new ImageIcon(SortButton.class.getResource("/images/drop-down-focus.png"));
        ascendingFocus = Sizing.resizeIcon(ascendingFocus, 40, 40);
        descendingFocus = Sizing.resizeIcon(descendingFocus, 40, 40);
        //set current icon state
        iconState = IconState.ASCENDING;
        //set the initial icon of this JLabel
        setIcon(ascending);
        setToolTipText("Ascending");

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
                if (iconState == IconState.ASCENDING) {
                    setIcon(ascendingFocus);
                } else {
                    setIcon(descendingFocus);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (iconState == IconState.ASCENDING) {
                    setIcon(ascending);
                } else {
                    setIcon(descending);
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
        if (state == IconState.ASCENDING) {
            setIcon(descending);
            setToolTipText("Descending");
            iconState = IconState.DESCENDING;
        } else {
            setToolTipText("Ascending");
            setIcon(ascending);
            iconState = IconState.ASCENDING;
        }
    }

    /**
     * Changes the icon when the button is pressed. Visually indicates that the
     * button has been pressed.
     */
    public void iconDown() {
        if (iconState == IconState.ASCENDING) {
            setIcon(ascendingBlue);
        } else {
            setIcon(descendingBlue);
        }
    }

    /**
     * Changes the icon when the button is released. Visually indicates that the
     * button has been released.
     */
    public void iconUp() {
        if (iconState == IconState.ASCENDING) {
            if (hasFocus()) {
                setIcon(descendingFocus);
            } else {
                setIcon(descending);
            }
            iconState = IconState.DESCENDING;
        } else {
            if (hasFocus()) {
                setIcon(ascendingFocus);
            } else {
                setIcon(ascending);
            }
            iconState = IconState.ASCENDING;
        }
    }

    /**
     * Gets the current IconState of the button.
     *
     * @return the current IconState
     */
    public IconState getIconState() {
        return iconState;
    }
}
