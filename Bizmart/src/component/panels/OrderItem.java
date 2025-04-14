package component.panels;

import component.labels.BizmartLabel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import statemachine.State;
import util.InventoryItem;
import util.OrderDetails;
import statemachine.Manager;
import util.Money;

/**
 * OrderItem is used by the manager state of the Bizmart application to show an
 * item in a store order and allow managers to receive the item.
 */
public class OrderItem extends JPanel {

    /**
     * Item ordered.
     */
    private InventoryItem item;

    /**
     * Details about the store order line item.
     */
    private OrderDetails orderDetails;

    /**
     * Quantity of the item received.
     */
    private int quantityReceived;

    /**
     * Descriptive label.
     */
    private BizmartLabel qtyOrderedLabel, totalPriceLabel, qtyReceivedLabel;

    /**
     * JComboBox to select the quantity received.
     */
    private JComboBox quantityBox;

    /**
     * OrderItem constructor creates a JPanel that shows information about a
     * product in a store order, and allow the user to receive it.
     *
     * @param o details about a line item
     * @param i item ordered
     */
    public OrderItem(OrderDetails o, InventoryItem i) {
        //----- COMPONENT SETUP -----//
        super();
        setMaximumSize(new Dimension(1000, 150));
        item = i;
        orderDetails = o;
        quantityReceived = o.getQuantity();

        // LAYOUT && BACKGROUND COLOR
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        setBackground(Color.WHITE);

        // ITEM NAME
        BizmartLabel name = new BizmartLabel(i.getItemName());

        // ITEM PRICE && QUANTITY
        BizmartLabel price = new BizmartLabel("Wholesale Price: $" + Money.DF.format(i.getWholesalePrice()));
        // price will be charged depending on the quantity received in the shipment
        totalPriceLabel = new BizmartLabel("Subtotal: $" + Money.DF.format(i.getWholesalePrice() * quantityReceived));
        qtyOrderedLabel = new BizmartLabel("Quantity Ordered: " + o.getQuantity());
        qtyReceivedLabel = new BizmartLabel("Quantity Received: ");

        // SET LABEL FONTS
        price.setFont(new Font("Nunito", Font.PLAIN, 18));
        totalPriceLabel.setFont(new Font("Nunito", Font.PLAIN, 18));
        qtyOrderedLabel.setFont(new Font("Nunito", Font.PLAIN, 18));
        qtyReceivedLabel.setFont(new Font("Nunito", Font.PLAIN, 18));

        // QUANTITY COMBO BOX
        quantityBox = new JComboBox();
        for (int j = 0; j <= o.getQuantity(); j++) {
            quantityBox.addItem(j);
        }

        quantityBox.setSelectedItem(o.getQuantity());
        quantityBox.setFont(new Font("Nunito", Font.PLAIN, 18));
        quantityBox.setMaximumSize(new Dimension((int) quantityBox.getPreferredSize().getWidth(), 30));

        // label for quantity combo box
        BizmartLabel quantityLabel = new BizmartLabel("Quantity Received");
        quantityLabel.setFont(new Font("Nunito", Font.PLAIN, 18));

        //----- ADD COMPONENTS TO PANEL -----//
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // gridwidth spans two rows
        // weightx distributes items over entire space 
        // by default, components take up the minimum amount of space and are clustered in the center of the panel
        gbc.weightx = 1;
        gbc.gridwidth = 2;
        add(name, gbc);

        gbc.gridy += 1;
        add(price, gbc);

        gbc.gridy += 1;
        add(qtyOrderedLabel, gbc);

        // gridwith spans one row - total price label && quantity combobox appear on one row
        gbc.gridwidth = 1;
        gbc.gridy += 1;
        add(totalPriceLabel, gbc);

        JPanel qtyPanel = new JPanel();
        qtyPanel.setLayout(new BoxLayout(qtyPanel, BoxLayout.X_AXIS));
        qtyPanel.setBackground(null);
        qtyPanel.add(qtyReceivedLabel);
        qtyPanel.add(Box.createHorizontalStrut(5));
        qtyPanel.add(quantityBox);
        gbc.gridx += 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(qtyPanel, gbc);

        //----- EVENT LISTENERS -----//
        // QUANTITY COMBO BOX
        quantityBox.addItemListener((ItemEvent e) -> {
            // IF THE QUANTITY IS CHANGED, RECALCULATE THE TOTAL AND REPAINT THIS JPANEL
            if (e.getStateChange() == ItemEvent.SELECTED) {
                quantityReceived = Integer.parseInt(quantityBox.getSelectedItem().toString());

                totalPriceLabel.setText("Total: $" + Money.DF.format(quantityReceived * item.getWholesalePrice()));
                ((Manager) State.getManagerState()).calculateOrderTotal();
                repaint();
            }
        });
    }

    //-----------------------------//
    //---------- GETTERS ----------//
    /**
     * Gets the quantity of the item received.
     *
     * @return quantity received
     */
    public int getQuantityReceived() {
        return quantityReceived;
    }

    /**
     * Gets the inventory item ordered.
     *
     * @return inventory item
     */
    public InventoryItem getInventoryItem() {
        return item;
    }

    /**
     * Gets the details about this store order line item.
     *
     * @return order details
     */
    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    //-----------------------------//
    //---------- SETTERS ----------//
    /**
     * Sets the quantity of the item received.
     *
     * @param qty quantity received
     */
    public void setQuantityReceived(int qty) {
        quantityReceived = qty;
        quantityBox.setSelectedItem(qty);
        totalPriceLabel.setText("Total: $" + Money.DF.format(quantityReceived * item.getWholesalePrice()));
        repaint();
    }

    //----------------------------------------//
    //---------- ORDER ITEM METHODS ----------//
    /**
     * Changes order item to view-only mode. Used when managers look up an
     * already received order.
     *
     * @param qty quantity received
     */
    public void orderIsReceived(int qty) {
        qtyReceivedLabel.setText("Quantity Received: " + qty);
        quantityBox.setVisible(false);
        setQuantityReceived(qty);
        ((Manager) State.getManagerState()).calculateOrderTotal();
    }
}
