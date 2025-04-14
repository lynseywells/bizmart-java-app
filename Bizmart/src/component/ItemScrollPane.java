package component;

import component.panels.ItemThumbnail;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import statemachine.*;
import util.InventoryItem;
import java.sql.*;

/**
 * ItemScrollPane is used by the Bizmart application to create and display
 * ItemThumbnail objects in a scrollable view. Contains methods to search for
 * items.
 */
public class ItemScrollPane extends JScrollPane {

    /**
     * JPanel displayed in ItemScrollPane. Contains all ItemThumbnails.
     */
    private JPanel imagesPanel;

    /**
     * True if the ItemScrollPane contains no ItemThumbnails, false otherwise.
     */
    private boolean empty;

    /**
     * ItemScrollPane constructor creates a JScrollPane and the JPanel it
     * displays, then sets attributes for both.
     * <p>
     * ItemScrollPane is blank after initialization, call sortItems() to add
     * ItemThumbnails to it.
     */
    public ItemScrollPane() {
        super();

        imagesPanel = new JPanel();
        imagesPanel.setLayout(new GridBagLayout());
        /* sets alignment of images panel so images appear to be populated 
        ** starting from the top-center of the panel */
        imagesPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        /* outer panel is used to control the size of the images panel
        ** otherwise, the images panel fills the entire scrollpane. */
        JPanel outerImgPanel = new JPanel();
        outerImgPanel.setLayout(new BoxLayout(outerImgPanel, BoxLayout.Y_AXIS));
        outerImgPanel.add(Box.createVerticalStrut(10));
        outerImgPanel.add(imagesPanel);

        // SETUP THIS JSCROLLPANE
        setViewportView(outerImgPanel);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        getVerticalScrollBar().setUnitIncrement(16);
        setBorder(null);
        empty = true;
    }

    /**
     * Clears the ItemScrollPane and starts a thread to create and display new
     * ItemThumbnails in it. The thumbnails are created using a ResultSet of a
     * query on the Inventory table in the database.
     *
     * @param rs a ResultSet of inventory items
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void sortItems(ResultSet rs) throws SQLException {
        // NOTE: this GridBagLayout mimics GridLayout(0, 5), except it doesn't vertically resize components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 6, 12, 6);
        Thread thread = new Thread(() -> {
            int i = 0;
            int y = 0;

            imagesPanel.removeAll();
            revalidate();
            repaint();
            try {
                while (rs.next()) {
                    String name = rs.getString(1);
                    double price = Double.parseDouble(rs.getString(2));
                    int quantity = Integer.parseInt(rs.getString(3));
                    int itemNum = Integer.parseInt(rs.getString(5));

                    // every five loops, move to the next row
                    if (i % 5 == 0) {
                        y++;
                        gbc.gridy = y;
                    }

                    // convert the item image blob in the database to an image icon
//                    Blob blob = rs.getBlob(4);
//                    byte[] b = blob.getBytes(1, (int) blob.length());
                    byte[] b = rs.getBytes(4);
                    ImageIcon icon = new ImageIcon(b);

                    ItemThumbnail t = new ItemThumbnail(icon, name, itemNum, price, quantity);
                    t.addEventListeners();
                    imagesPanel.add(t, gbc);

                    // resize the images panel to fix contents exactly
                    // makes rows start from the top of the outer panel
                    imagesPanel.setMaximumSize(imagesPanel.getPreferredSize());
                    revalidate();
                    repaint();
                    i++;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        thread.start();
        empty = false;
    }

    /**
     * Indicates if the ItemScrollPane is empty.
     *
     * @return true if the ItemScrollPane is empty, false otherwise
     */
    public boolean isEmpty() {
        return empty;
    }
}
