package component.labels;

import component.Colors;
import java.awt.*;
import java.awt.font.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * A custom JTextPane used in the Bizmart application to add instructions to
 * forms.
 */
public class InstructionLabel extends JTextPane {

    /**
     * Alignment indicates how the text should be aligned.
     */
    public enum Alignment {
        /**
         * Indicates left text alignment.
         */
        LEFT,
        /**
         * Indicates center text alignment.
         */
        CENTER,
        /**
         * Indicates right text alignment.
         */
        RIGHT;
    }

    /**
     * Width of the JTextPane.
     */
    private int width;

    /**
     * Line height of the Font.
     */
    private int lineHeight;

    /**
     * The number of rows in the JTextPane.
     */
    private int rows;

    /**
     * Font for the JTextPane.
     */
    private Font font;

    /**
     * InstructionLabel constructor creates a single row InstructionLabel with a
     * width of 200 pixels and center text alignment.
     *
     * @param text instructions to display
     */
    public InstructionLabel(String text) {
        super();
        //set data fields
        width = 200;
        font = new Font("Nunito", Font.PLAIN, 20);
        LineMetrics m = font.getLineMetrics(text, new FontRenderContext(null, true, false));
        //get the line height of the text (adding underlineOffset compensates for space between lines of text)
        lineHeight = (int) (m.getHeight() + m.getUnderlineOffset());
        rows = 1;

        //make the component non-editable
        setEditable(false);
        setFocusable(false);

        //set font/text/colors
        setFont(font);
        setText(text);
        setForeground(Colors.BLUE);
        setBackground(null);
        setBorder(null);

        //set the preferred size of this JTextPane
        setPreferredSize(new Dimension(width, lineHeight));
        setMinimumSize(new Dimension(width, lineHeight));

        //center text
        setAlignment(Alignment.CENTER);
    }

    /**
     * Sets the text alignment of this InstructionLabel.
     *
     * @param alignment an Alignment constant
     */
    public void setAlignment(Alignment alignment) {
        StyledDocument style = getStyledDocument();
        SimpleAttributeSet attribute = new SimpleAttributeSet();
        if (alignment == Alignment.LEFT) {
            StyleConstants.setAlignment(attribute, StyleConstants.ALIGN_LEFT);
        } else if (alignment == Alignment.CENTER) {
            StyleConstants.setAlignment(attribute, StyleConstants.ALIGN_CENTER);
        } else if (alignment == Alignment.RIGHT) {
            StyleConstants.setAlignment(attribute, StyleConstants.ALIGN_RIGHT);
        }
        style.setParagraphAttributes(0, style.getLength(), attribute, false);
    }

    /**
     * Sets the width of this InstructionLabel.
     *
     * @param width the width in pixels
     */
    public void setWidth(int width) {
        this.width = width;
        setPreferredSize(new Dimension(width, (lineHeight * rows)));
    }

    /**
     * Sets the number of rows this InstructionLabel has.
     *
     * @param rows the number of rows
     */
    public void setRows(int rows) {
        this.rows = rows;
        setPreferredSize(new Dimension(width, (lineHeight * rows)));
    }

    /**
     * Sets the width and number of rows of this InstructionLabel.
     *
     * @param width the width in pixels
     * @param rows the number of rows
     */
    public void setSize(int width, int rows) {
        this.width = width;
        this.rows = rows;
        setPreferredSize(new Dimension(width, (lineHeight * rows)));
    }
}
