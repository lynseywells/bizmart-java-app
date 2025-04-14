package component.buttons;

import component.Colors;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.util.*;
import javax.swing.*;

/**
 * A JLabel for the Bizmart application that acts as a navigation link.
 */
public class Link extends JLabel {

    /**
     * The Link's Font.
     */
    Font font;

    /**
     * Link constructor creates a JLabel and adds typical navigation link
     * interactivity (such as changing color and adding an underline when
     * hovering over the text).
     *
     * @param str the link text
     */
    public Link(String str) {
        super(str);
        //SET FONT & COLOR
        setForeground(Colors.BLUE);
        font = new Font("Nunito", Font.PLAIN, 20);
        setFont(font);

        //SEt FOCUSABLE
        setFocusable(true);

        //SET CURSOR
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        //ADD FOCUS LISTENER
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                //underline text when in focus
                Font font = new Font("Nunito", Font.PLAIN, 20);
                Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                setForeground(Colors.LIGHT_BLUE);
                //reset label font
                setFont(font.deriveFont(attributes));
            }

            @Override
            public void focusLost(FocusEvent e) {
                setForeground(Colors.BLUE);
                setFont(font);
            }
        });

        //ADD A MOUSE LISTENER
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                //underline text
                Font font = new Font("Nunito", Font.PLAIN, 20);
                Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                setForeground(Colors.LIGHT_BLUE);
                //reset label font
                setFont(font.deriveFont(attributes));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setForeground(Colors.BLUE);
                setFont(font);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setForeground(Colors.EMERALD);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setForeground(Colors.LIGHT_BLUE);
            }
        });
    }
}
