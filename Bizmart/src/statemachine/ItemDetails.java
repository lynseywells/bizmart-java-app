package statemachine;

import component.labels.BizmartLabel;
import component.buttons.ButtonPrimary;
import component.Sizing;
import component.Colors;
import component.panels.DropShadowPanel;
import component.panels.CartItem;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import javax.swing.*;
import util.*;

/**
 * Subclass ItemDetails displays information about an inventory item, and allows
 * the user to add the item to a shopping cart. If the item is already in the
 * cart, the desired quantity is added to the existing quantity. This state can
 * move into the states cart, customer, and employee.
 * <p>
 * ItemDetails is initialized each time the item state is entered to reflect the
 * item selected in the employee or customer state.
 *
 */
public class ItemDetails extends State {

    /**
     * The root JPanel for the item state that is placed in the JFrame when this
     * state is entered. Uses a BoxLayout.
     */
    private DropShadowPanel itemPanel;

    /**
     * JScrollPane for detailsPanel.
     */
    private JScrollPane detailsScroll;

    /**
     * JPanel displays item information.
     */
    private JPanel detailsPanel;

    /**
     * Button shows the cart when pressed.
     * <p>
     * If previousState is customer, changes state to cart state. If
     * previousState is employee, changes state to employee state.
     */
    private ButtonPrimary cartBttn;

    /**
     * Button adds item to the cart when pressed.
     */
    private ButtonPrimary addToCartBttn;

    /**
     * Button goes back to previousState.
     */
    private ButtonPrimary backBttn;

    /**
     * JComboBox to choose the quantity to add to the cart.
     */
    private JComboBox quantityBox;

    /**
     * JTextArea for item information.
     */
    private JTextArea itemName, description;

    /**
     * Labels for item information.
     */
    private BizmartLabel retailPrice, quantity, stock;

    /**
     * JLabel to display item image.
     */
    private JLabel image;

    /**
     * The image to display in the JLabel image.
     */
    private ImageIcon icon;

    /**
     * Boolean determines the type of layout ItemDetails will have based on
     * image dimensions. If the image's width is greater than its height, this
     * variable is true and components are displayed below the image. If it's
     * height is greater than it's width this variable is false and components
     * are displayed next to the image.
     */
    private boolean verticalLayout;

    /**
     * The InventoryItem used to create this state.
     */
    private InventoryItem inventoryItem;

    /**
     * ItemDetails constructor initializes components needed for the item state.
     * Should be called each time the item state is entered.
     *
     * @param item item to show details of
     */
    public ItemDetails(InventoryItem item) {
        //----- INITIALIZE COMPONENTS -----//
        inventoryItem = item;

        // ITEM PANEL
        itemPanel = new DropShadowPanel(8, Color.WHITE, Colors.SILVER, Colors.DARK_BLUE);
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.X_AXIS));

        // LABELS
        DecimalFormat df = new DecimalFormat("###,###,###.00");
        retailPrice = new BizmartLabel("Price: $" + df.format(item.getRetailPrice()));
        retailPrice.setFont(new Font("Nunito", Font.BOLD, 22));
        stock = new BizmartLabel("In Stock: " + item.getQuantity());
        stock.setFont(new Font("Nunito", Font.BOLD, 22));
        this.quantity = new BizmartLabel("Quantity");
        this.quantity.setFont(new Font("Nunito", Font.BOLD, 24));

        // ITEM IMAGE LABEL
        icon = item.getImageIcon();
        image = new JLabel(icon);
        sizeImage();

        // TEXT AREAS
        // item name
        itemName = new JTextArea(item.getItemName());
        itemName.setFont(new Font("Nunito", Font.BOLD, 28));
        itemName.setLineWrap(true);
        itemName.setWrapStyleWord(true);
        itemName.setForeground(Colors.RICH_BLACK);
        itemName.setEditable(false);
        // setting height to MAX_VALUE forces JPanel to pack it down to minimum height needed to display all rows
        itemName.setSize(new Dimension(590, Integer.MAX_VALUE));

        // item description
        description = new JTextArea(item.getDescription());
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setFont(new Font("Nunito", Font.PLAIN, 22));
        description.setForeground(Colors.RICH_BLACK);
        description.setEditable(false);

        // BUTTONS
        // view cart button
        cartBttn = new ButtonPrimary("View Cart");
        cartBttn.setBorderColor(Colors.GREEN);
        cartBttn.setBackground(Colors.MID_GREEN);
        cartBttn.setSelectedBorderColor(Colors.GREEN);
        cartBttn.setSelectedBackground(Colors.MID_GREEN);
        // add item to cart button
        addToCartBttn = new ButtonPrimary("Add to Cart");
        // back button
        backBttn = new ButtonPrimary("Go Back");
        backBttn.setBorderColor(Colors.GREEN);
        backBttn.setBackground(Colors.MID_GREEN);
        backBttn.setSelectedBorderColor(Colors.GREEN);
        backBttn.setSelectedBackground(Colors.MID_GREEN);

        // if no user is logged in, cart functionality is disabled
        if (State.currentUser == null) {
            cartBttn.setEnabled(false);
            addToCartBttn.setEnabled(false);
        }

        // COMBOBOX
        quantityBox = new JComboBox();
        for (int i = 1; i <= item.getQuantity(); i++) {
            quantityBox.addItem(i);
        }
        quantityBox.setFont(new Font("Nunito", Font.PLAIN, 22));

        //----- GROUP COMPONENTS -----//
        // BUTTONS PANEL
        JPanel buttons = new JPanel();
        buttons.setBackground(null);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(backBttn);
        buttons.add(Box.createGlue());

        // create JPanel with no space around the edges so buttons appear flush with right side of outer panel
        JPanel cartButtons = new JPanel(new FlowLayout(SwingConstants.LEFT, 0, 0));
        cartButtons.setBackground(null);
        cartButtons.add(cartBttn);
        cartButtons.add(Box.createHorizontalStrut(10));
        cartButtons.add(addToCartBttn);

        // add JPanel to buttons panel
        buttons.add(cartButtons);

        // QUANTITY COMBOBOX PANEL
        JPanel qtyPanel = new JPanel();
        qtyPanel.setBackground(null);
        qtyPanel.setLayout(new BoxLayout(qtyPanel, BoxLayout.X_AXIS));
        qtyPanel.add(this.quantity);
        qtyPanel.add(Box.createHorizontalStrut(10));
        qtyPanel.add(quantityBox);
        qtyPanel.add(Box.createHorizontalStrut(10));

        //----- SIZE COMPONENTS -----//
        int width = frame.getWidth() - 100;
        int height = frame.getHeight() - 100;
        Dimension d = new Dimension(width, height);
        itemPanel.setPreferredSize(d);
        itemPanel.setMaximumSize(d);
        sizeImage();

        cartBttn.setPreferredSize(new Dimension(165, 54));
        addToCartBttn.setPreferredSize(new Dimension(200, 54));
        backBttn.setPreferredSize(new Dimension(150, 54));

        // setting height to MAX_VALUE forces JPanel to pack it down to minimum height needed to display all rows
        description.setSize(new Dimension(590, Integer.MAX_VALUE));

        //----- ADD COMPONENTS TO ITEM PANEL -----//
        // ADD COMPONENTS TO SCROLLPANE
        // layout changes based on image dimensions
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridBagLayout());
        detailsPanel.setBackground(null);
        GridBagConstraints gbc = new GridBagConstraints();

        if (!verticalLayout) {
            gbc.gridheight = 5;
            gbc.gridx = 0;
            gbc.insets = new Insets(0, 5, 8, 5);
        } else {
            gbc.insets = new Insets(0, 0, 8, 0);
        }
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        image.setMaximumSize(image.getMaximumSize());
        detailsPanel.add(image, gbc);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        if (!verticalLayout) {
            gbc.gridy = 0;
            gbc.gridx = 1;
            gbc.gridheight = 1;
        } else {
            gbc.gridy = 1;
        }

        detailsPanel.add(itemName, gbc);
        gbc.gridy += 1;
        detailsPanel.add(description, gbc);
        gbc.gridy += 1;
        detailsPanel.add(retailPrice, gbc);
        gbc.gridy += 1;
        detailsPanel.add(stock, gbc);

        gbc.gridy += 1;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        detailsPanel.add(qtyPanel, gbc);

        gbc.gridy += 1;
        if (!verticalLayout) {
            gbc.gridwidth = 2;
            gbc.gridx = 0;
        }
        gbc.fill = GridBagConstraints.HORIZONTAL;
        detailsPanel.add(buttons, gbc);
        detailsPanel.setPreferredSize(detailsPanel.getPreferredSize());

        // CREATE SCROLLPANE
        detailsScroll = new JScrollPane(detailsPanel);
        detailsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        detailsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        detailsScroll.getVerticalScrollBar().setUnitIncrement(16);
        detailsScroll.setBorder(null);
        detailsScroll.getViewport().setBackground(Color.WHITE);

        // ADD SCROLLPANE TO ITEM PANEL
        itemPanel.add(detailsScroll);
    }

    //--------------------------------------------//
    //---------- ABSTRACT STATE METHODS ----------//
    //---------- ENTER STATE ----------//
    @Override
    public void enterState() {
        State.frame.setTitle("Item Details");
        State.frame.getContentPane().removeAll();

        // add item panel to frame
        // default GridBagConstraints centers panel in frame & respects sizing
        State.frame.add(itemPanel, new GridBagConstraints());

        // repaint the frame
        State.frame.repaint();
        State.frame.setVisible(true);

        // request focus
        detailsScroll.requestFocusInWindow();
    }

    //---------- ADD EVENT LISTENERS TO COMPONENTS ----------//
    @Override
    protected void addEventListeners() {
        //----- BACK BUTTON -----//
        AbstractAction backPress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentUser == null || currentUser.getPosition() == User.CUSTOMER) {
                    changeState(State.customer);
                } else {
                    changeState(State.employee);
                }
            }
        };
        backBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        backBttn.getActionMap().put("pressed", backPress);

        backBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                backBttn.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                backBttn.mouseReleased();
                backPress.actionPerformed(new ActionEvent(backBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- ADD ITEM TO CARD BUTTON -----//
        AbstractAction addPress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (addToCartBttn.isEnabled()) {
                    addToCart();
                }
            }
        };
        addToCartBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        addToCartBttn.getActionMap().put("pressed", addPress);

        addToCartBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (addToCartBttn.isEnabled()) {
                    addToCartBttn.mousePressed();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (addToCartBttn.isEnabled()) {
                    addToCartBttn.mouseReleased();
                    addToCart();
                }
            }
        });

        //----- VIEW CART BUTTON -----//
        AbstractAction cartPress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cartBttn.isEnabled()) {
                    if (currentUser.getPosition() == User.CUSTOMER) {
                        changeState(State.cart);
                    } else {
                        changeState(State.employee);
                        ((Employee) State.employee).showPanel("cart");
                    }
                }
            }
        };
        cartBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        cartBttn.getActionMap().put("pressed", cartPress);

        cartBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (cartBttn.isEnabled()) {
                    cartBttn.mousePressed();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (cartBttn.isEnabled()) {
                    cartBttn.mouseReleased();
                    if (currentUser.getPosition() == User.CUSTOMER) {
                        changeState(State.cart);
                    } else {
                        changeState(State.employee);
                        ((Employee) State.employee).showPanel("cart");
                    }
                }
            }
        });
    }

    //----------------------------------------//
    //---------- ITEM STATE METHODS ----------//
    /**
     * Creates a new CartItem from inventoryItem and adds it to previousState
     * cart.
     */
    public void addToCart() {
        // create a cart item (extended JPanel)
        CartItem c = new CartItem(inventoryItem, Integer.parseInt(quantityBox.getSelectedItem().toString()));

        if (currentUser.getPosition() == User.CUSTOMER) {
            for (CartItem ci : State.cart.getCart()) {
                // if the item is already in the cart
                if (ci.getInventoryItem().getItemNumber() == c.getInventoryItem().getItemNumber()) {
                    // add the quantity from the cart with the quantity of the selected item
                    int newQty = c.getQuantity() + ci.getQuantity();
                    // get the maximum stock for this item
                    int inStock = Integer.parseInt(quantityBox.getItemAt(quantityBox.getItemCount() - 1).toString());

                    if (newQty > inStock) {
                        newQty = inStock;
                    }

                    ci.setQuantity(newQty);
                    return;
                }
            }
            // if the item wasn't in the cart, add it
            State.cart.addItemToCart(c);
        } else {
            for (CartItem ci : State.employee.getCart()) {
                // if the item is already in the cart
                if (ci.getInventoryItem().getItemNumber() == c.getInventoryItem().getItemNumber()) {
                    // add the quantity from the cart with the quantity of the selected item
                    int newQty = c.getQuantity() + ci.getQuantity();
                    // get the maximum stock for this item
                    int inStock = Integer.parseInt(quantityBox.getItemAt(quantityBox.getItemCount() - 1).toString());

                    if (newQty > inStock) {
                        newQty = inStock;
                    }

                    ci.setQuantity(newQty);
                    return;
                }
            }
            // if the item wasn't in the cart, add it
            State.employee.addItemToCart(c);
        }
    }

    /**
     * Scales the item image to fit inside itemPanel. Also determines the type
     * of layout itemPanel will have based on image dimensions.
     */
    protected void sizeImage() {
        // get size of this panel
        int height = (int) itemPanel.getPreferredSize().getHeight();

        // get image size
        int imgWidth = icon.getIconWidth();
        int imgHeight = icon.getIconHeight();
        int newWidth, newHeight;
        if (imgWidth > imgHeight) {
            // calculate scale from width to new width
            newWidth = 400;
            double scale = (double) newWidth / imgWidth;

            // use scale to set the new width while maintaining aspect ratio
            newHeight = (int) Math.round(imgHeight * scale);

            // if image is too tall, subtract from size until image fits in jpanel
            while (newHeight >= height - 100) {
                newWidth -= 10;
                newHeight -= 10;
            }
            verticalLayout = true;
        } else {
            // calculate scale from height to new height
            newHeight = 500;
            double scale = (double) newHeight / imgHeight;

            // use scale to set the new width while maintaining aspect ratio
            newWidth = (int) Math.round(imgWidth * scale);

            // if image is too wide, subtract from size until image fits in jpanel
            while (newWidth >= 400) {
                newWidth -= 10;
                newHeight -= 10;
            }
            verticalLayout = false;
        }

        // create a new icon to preserve original icons sizing
        ImageIcon newIcon = Sizing.resizeIcon(icon, newWidth, newHeight);
        image.setIcon(newIcon);
    }
}
