package component.panels;

import component.labels.BizmartLabel;
import component.Colors;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import javax.swing.*;
import statemachine.State;
import util.InventoryItem;

/**
 * ItemThumbnail displays an inventory item's image and other information to
 * make it easy for users to identify the item in a list. It is used by
 * ItemScrollPane. ItemThumbnail changes currentState to the item state in the
 * Bizmart application when interacted with, allowing the user to see more
 * details about the item.
 */
public class ItemThumbnail extends JPanel {

    /**
     * JLabel to display item image.
     */
    private JLabel image;

    /**
     * Item ID (SKU) number.
     */
    private int itemNumber;

    /**
     * Size constant for all thumbnails.
     */
    public static final int WIDTH = 200, HEIGHT = 200;

    /**
     * ItemThumbnail constructor creates a JPanel that displays the item's image
     * and identifying information.
     *
     * @param img thumbnail sized item image
     * @param itemName item name
     * @param itemNumber item ID (SKU) number
     * @param price item retail price
     * @param inStock quantity in stock
     */
    public ItemThumbnail(ImageIcon img, String itemName, int itemNumber, double price, int inStock) {
        super();

        //set panel size
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setMaximumSize(new Dimension(WIDTH, HEIGHT));
        setSize(new Dimension(WIDTH, HEIGHT));

        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());
        setFocusable(true);

        //SET CURSOR
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // jpanel to align thumbnail & labels
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(null);

        image = new JLabel(img);
        image.setAlignmentX(Component.CENTER_ALIGNMENT);

        // set item information
        this.itemNumber = itemNumber;

        // add components to jpanel
        String name = itemName;
        if (itemName.length() > 10) {
            String[] s = itemName.split(" ");
            name = s[0] + " " + s[1];
        }
        BizmartLabel n = new BizmartLabel(name);
        n.setAlignmentX(Component.CENTER_ALIGNMENT);
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        BizmartLabel p = new BizmartLabel("$" + df.format(price));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        BizmartLabel s = new BizmartLabel("In Stock: " + inStock);
        s.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(image);
        panel.add(n);
        panel.add(p);
        panel.add(s);

        // center jpanel inside this panel
        add(panel, new GridBagConstraints());

        // focus listener to draw border when thumbnail is in focus
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(BorderFactory.createLineBorder(Colors.EMERALD, 4));
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(null);
            }
        });
    }

    //-----------------------------//
    //---------- GETTERS ----------//
    /**
     * Gets the item number (SKU) of the inventory item this ItemThumbnail
     * represents.
     * 
     * @return item ID (SKU) numbers
     */
    public int getItemNumber() {
        return itemNumber;
    }

    //--------------------------------------------//
    //---------- ITEM THUMBNAIL METHODS ----------//
    /**
     * Adds event listeners to this ItemThumbnail to change currentState to a
     * new item state when interacted with.
     */
    public void addEventListeners() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showItemDetails(itemNumber);
            }
        });
        AbstractAction press = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showItemDetails(itemNumber);
            }
        };

        this.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        this.getActionMap().put("pressed", press);
    }

    /**
     * When an ItemThumbnail is clicked, creates a new ItemDetails and enters
     * the item state of the Bizmart application.
     *
     * @param itemNumber the item number of the clicked ItemThumbnail
     */
    public void showItemDetails(int itemNumber) {
        try {
            ResultSet rs = State.getConnection().getItem(itemNumber);
            rs.next();
            int id = rs.getInt(1);
            String name = rs.getString(2);
            String desc = rs.getString(3);
            int categoryID = rs.getInt(4);
            int subcategoryID = rs.getInt(5);
            double price = rs.getDouble(6);
            double wholesale = rs.getDouble(7);
            int quantity = rs.getInt(8);
            int restock = rs.getInt(9);
            int discontinue = rs.getInt(11);
            int max = rs.getInt(12);
            // convert the item image blob in the database to an image icon
            // syntax for MySQL
            // Blob blob = rs.getBlob(10);
            // byte[] b = blob.getBytes(1, (int) blob.length());
            byte[] b = rs.getBytes(10);
            ImageIcon img = new ImageIcon(b);

            InventoryItem item = new InventoryItem(id, name, desc, categoryID, subcategoryID, price, wholesale, quantity, restock, max, img);

            if (discontinue == 1) {
                item.setDiscontinued(true);
            }
            // create item panel and add it to frame
            State.createItemState(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
