package component.panels;

import component.labels.BizmartLabel;
import component.buttons.IconButton;
import component.Sizing;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import util.Money;
import statemachine.State;
import util.InventoryItem;
import util.User;

/**
 * CartItem is used by the employee, customer, and manager states of the Bizmart
 * application to add an item to a shopping cart for the user to view and edit.
 * CartItem contains components to change the desired quantity, and remove the
 * item from the cart.
 */
public class CartItem extends JPanel {

    /**
     * InventoryItem represented by this CartItem.
     */
    private InventoryItem item;

    /**
     * Quantity of the item in the cart.
     */
    private int quantity;

    /**
     * Quantity of the item in an unreceived store order. Used only by the
     * manager state to prevent managers from over ordering products.
     */
    private int orderedQuantity;

    /**
     * BizmartLabel for quantityBox.
     */
    private BizmartLabel itemQuantity;

    /**
     * BizmartLabel shows the total price for the selected quantity of the item.
     */
    private BizmartLabel totalPrice;

    /**
     * JComboBox determines quantity of the item in the cart.
     */
    private JComboBox quantityBox;

    /**
     * Restricts the characters that can be used to display the item's name.
     * <p>
     * If the name is longer than this JPanel, it breaks the entire layout of
     * the Employee state (i.e. buttons disappear and all CartItems move right
     * and get cut off by the containing panel).
     */
    private static final int maxNameLength = 65;

    /**
     * CartItem constructor creates a JPanel that displays an inventory item
     * added to currentState's shopping cart. Creates components to edit
     * quantity or remove the item from the cart.
     *
     * @param i inventory item in the cart
     * @param qty quantity of the item
     */
    public CartItem(InventoryItem i, int qty) {
        //----- INITIALIZE COMPONENTS -----//
        super();
        item = i;
        quantity = qty;

        // CHANGE THE JPANELS SIZE BASED ON THE USERS ACCOUNT TYPE
        if (State.getCurrentUser().getPosition() == User.CUSTOMER) {
            setMaximumSize(new Dimension(1000, 150));
        } else {
            setMaximumSize(new Dimension(680, 150));
        }

        // LAYOUT && BACKGROUND COLOR
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        setBackground(Color.WHITE);

        // ITEM IMAGE
        ImageIcon icon = Sizing.resizeIcon(i.getImageIcon(), 125, 125);
        JLabel imgLabel = new JLabel(icon);

        // ITEM NAME
        String itemName = i.getItemName();
        while (itemName.length() > maxNameLength) {
            // remove a part of the title (separated by ",") until the length fits in the panel
            int index = itemName.lastIndexOf(",");
            itemName = itemName.substring(0, index);
        }

        BizmartLabel name = new BizmartLabel(itemName);

        // ITEM PRICE && QUANTITY
        // if the current state is manager, the items price is the wholesale price
        // otherwise, it's being purchased by a customer and is the retail price
        BizmartLabel price;
        if (State.getCurrentState().equals(State.getManagerState())) {
            price = new BizmartLabel("Wholesale Price: $" + Money.DF.format(i.getWholesalePrice()));
            totalPrice = new BizmartLabel("Total: $" + Money.DF.format(i.getWholesalePrice() * quantity));
            itemQuantity = new BizmartLabel("In Stock: " + i.getQuantity());
        } else {
            price = new BizmartLabel("Item Price: $" + Money.DF.format(i.getRetailPrice()));
            totalPrice = new BizmartLabel("Total: $" + Money.DF.format(i.getRetailPrice() * quantity));
            itemQuantity = new BizmartLabel("Quantity: " + quantity);
        }

        // SET LABEL FONTS
        price.setFont(new Font("Nunito", Font.PLAIN, 18));
        totalPrice.setFont(new Font("Nunito", Font.PLAIN, 18));
        itemQuantity.setFont(new Font("Nunito", Font.PLAIN, 18));

        // QUANTITY COMBO BOX
        quantityBox = new JComboBox();

        if (State.getCurrentState().equals(State.getManagerState())) {
            // increment combobox items up to the quantity that can be reordered
            // without exceeding the max quantity allowed for the item
            int max = i.getMaxQuantity() - i.getQuantity();
            for (int j = 1; j <= max; j++) {
                quantityBox.addItem(j);
            }
        } else {
            // increment combobox items up to the quantity of the item in stock
            for (int j = 1; j <= i.getQuantity(); j++) {
                quantityBox.addItem(j);
            }
        }

        quantityBox.setSelectedItem(quantity);
        quantityBox.setFont(new Font("Nunito", Font.PLAIN, 18));
        quantityBox.setMaximumSize(new Dimension((int) quantityBox.getPreferredSize().getWidth(), 30));

        // label for quantity combo box
        BizmartLabel quantityLabel = new BizmartLabel("Quantity");
        quantityLabel.setFont(new Font("Nunito", Font.PLAIN, 18));

        // DELETE BUTTON
        IconButton deleteBttn = new IconButton(CartItem.class.getResource("/images/delete.png"),
                CartItem.class.getResource("/images/delete_focus.png"),
                CartItem.class.getResource("/images/delete_blue.png"));

        //----- ADD COMPONENTS TO PANEL -----//
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // IMAGE IS ONLY ADDED TO CUSTOMER STATE CARTS
        if (State.getCurrentUser().getPosition() == User.CUSTOMER) {
            // image spans 4 rows
            gbc.gridheight = 4;
            add(imgLabel, gbc);

            // move added components to the right of the image
            gbc.gridx = 1;
        }

        // gridwidth spans two rows
        // weightx distributes items over entire space 
        // by default, components take up the minimum amount of space and are clustered in the center of the panel
        gbc.weightx = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        add(name, gbc);

        gbc.gridy += 1;
        add(price, gbc);

        gbc.gridy += 1;
        add(itemQuantity, gbc);

        if (State.getCurrentState().equals(State.getManagerState())) {
            gbc.gridy += 1;
            BizmartLabel max = new BizmartLabel("Max Quantity: " + i.getMaxQuantity());
            max.setFont(new Font("Nunito", Font.PLAIN, 18));
            add(max, gbc);
        }

        // gridwith spans one row - total price label && quantity combobox appear on one row
        gbc.gridwidth = 1;
        gbc.gridy += 1;
        add(totalPrice, gbc);

        gbc.gridx += 1;
        gbc.anchor = GridBagConstraints.EAST;

        // ADD QUANTITY COMBO BOX AND DELETE BUTTON TO JPANEL
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(quantityLabel);
        p.add(Box.createHorizontalStrut(10));
        p.add(quantityBox);
        p.add(Box.createHorizontalStrut(15));
        p.add(deleteBttn);

        // ADD NEW JPANEL TO THIS PANEL
        add(p, gbc);

        //----- EVENT LISTENERS -----//
        // DELETE ITEM BUTTON
        deleteBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // DELETE THE ITEM FROM THE CART && RECALCULATE THE TOTAL
                // check which state the program is in to edit the correct cart
                if (State.getCurrentUser().getPosition() == User.CUSTOMER) {
                    State.getCartState().deleteItem(item.getItemNumber());
                    State.getCartState().calculateTotal();
                } else if (State.getCurrentState().equals(State.getEmployeeState())) {
                    State.getEmployeeState().deleteItem(item.getItemNumber());
                    State.getEmployeeState().calculateTotal();
                } else {
                    State.getManagerState().deleteItem(item.getItemNumber());
                    State.getManagerState().calculateTotal();
                }
            }
        });
        AbstractAction deletePress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (State.getCurrentUser().getPosition() == User.CUSTOMER) {
                    State.getCartState().deleteItem(item.getItemNumber());
                    State.getCartState().calculateTotal();
                } else if (State.getCurrentState().equals(State.getEmployeeState())) {
                    State.getEmployeeState().deleteItem(item.getItemNumber());
                    State.getEmployeeState().calculateTotal();
                } else {
                    State.getManagerState().deleteItem(item.getItemNumber());
                    State.getManagerState().calculateTotal();
                }
            }
        };
        deleteBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        deleteBttn.getActionMap().put("pressed", deletePress);

        // QUANTITY COMBO BOX
        quantityBox.addItemListener((ItemEvent e) -> {
            // IF THE QUANTITY IS CHANGED, RECALCULATE THE TOTAL AND REPAINT THIS JPANEL
            if (e.getStateChange() == ItemEvent.SELECTED) {
                quantity = Integer.parseInt(quantityBox.getSelectedItem().toString());
                if (!State.getCurrentState().equals(State.getManagerState())) {
                    itemQuantity.setText("Quantity: " + quantity);
                }

                if (State.getCurrentUser().getPosition() == User.CUSTOMER) {
                    totalPrice.setText("Total: $" + Money.DF.format(quantity * item.getRetailPrice()));
                    State.getCartState().calculateTotal();
                } else if (State.getCurrentState().equals(State.getEmployeeState())) {
                    totalPrice.setText("Total: $" + Money.DF.format(quantity * item.getRetailPrice()));
                    State.getEmployeeState().calculateTotal();
                } else {
                    totalPrice.setText("Total: $" + Money.DF.format(quantity * item.getWholesalePrice()));
                    State.getManagerState().calculateTotal();
                }
                repaint();
            }
        });
    }

    //-----------------------------//
    //---------- GETTERS ----------//
    /**
     * Gets the quantity of the inventory item in the shopping cart.
     *
     * @return selected quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Gets the inventory item this CartItem represents.
     *
     * @return the item
     */
    public InventoryItem getInventoryItem() {
        return item;
    }

    //-----------------------------//
    //---------- SETTERS ----------//
    /**
     * Sets the quantity of the inventory item in the shopping cart.
     *
     * @param qty desired quantity
     */
    public void setQuantity(int qty) {
        quantity = qty;
        quantityBox.setSelectedItem(qty);
        itemQuantity.setText("Quantity: " + quantity);
        if (State.getPreviousState().equals(State.getManagerState())) {
            totalPrice.setText("Total: $" + Money.DF.format(quantity * item.getWholesalePrice()));
        } else {
            totalPrice.setText("Total: $" + Money.DF.format(quantity * item.getRetailPrice()));
        }

        repaint();
    }

    /**
     * Sets the quantity of the inventory item in an unreceived store order.
     * Used by the manager state to prevent managers from over ordering a
     * product.
     *
     * @param qty unreceived quantity
     */
    public void setOrderedQuantity(int qty) {
        orderedQuantity = qty;
        itemQuantity.setText("Quantity In Stock: " + item.getQuantity() + ", Quantity in Order(s): " + orderedQuantity);

        // increment combobox items up to the quantity that can be reordered
        // without exceeding the max quantity allowed for the item
        int max = item.getMaxQuantity() - item.getQuantity() - orderedQuantity;
        quantityBox.removeAllItems();
        for (int j = 1; j <= max; j++) {
            quantityBox.addItem(j);
        }
    }
}
