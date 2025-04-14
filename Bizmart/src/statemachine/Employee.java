package statemachine;

import component.labels.*;
import component.fields.InputField;
import component.buttons.ButtonPrimary;
import component.buttons.Link;
import component.PopupDialog;
import component.ItemScrollPane;
import component.Colors;
import component.panels.CartItem;
import component.panels.DropShadowPanel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.*;
import java.util.ArrayList;
import util.*;

/**
 * Subclass Employee is a POS system, and is accessible to signed-in users of
 * with an "employee" or "manager" type account. This state can move into the
 * states manager, login, item, and checkout.
 */
public class Employee extends State {

    /**
     * The root JPanel for the login state that is placed in the JFrame when
     * this state is entered. Uses a CardLayout.
     */
    private DropShadowPanel employeePanel;

    /**
     * JScrollPane displays a cart.
     */
    private JScrollPane cartScroll;

    /**
     * The JPanel displayed in the cartScrolls view-port that contains items
     * added to the transaction.
     */
    private JPanel cartSummaryPanel;

    /**
     * Stores the CartItems added to the transaction.
     */
    private ArrayList<CartItem> items;

    /**
     * Label dynamically displays order subtotal as items are added to the
     * transaction.
     */
    private BizmartLabel orderTotalLabel;
    /**
     * Field to search items by SKU (ID) number.
     */
    private InputField skuField;
    /**
     * JLabel to be shown when an invalid SKU is entered.
     */
    private WarningLabel invalidSkuWarning;

    /**
     * JDialog help menu.
     */
    private PopupDialog helpDialog;

    /**
     * Shows helpDialog when pressed.
     */
    private Link helpLink;

    /**
     * Button that signs user out of their account and changes state to login
     * state when pressed.
     */
    private ButtonPrimary logoutBttn;

    /**
     * Button that changes state to checkout state.
     */
    private ButtonPrimary checkoutBttn;

    /**
     * Button that clears the transaction when pressed.
     */
    private ButtonPrimary newTransactionBttn;

    /**
     * Button that changes the displayed card in employeePanel to
     * itemSearchPanel when pressed.
     */
    private ButtonPrimary itemSearchBttn;

    /**
     * Button that changes the state to manager state. Only visible if
     * currentUser has a manager type account.
     */
    private ButtonPrimary managerBttn;

    /**
     * JPanel in employeePanel CardLayout. Allows employees to search items by
     * keyword and category/subcategory, show item details to customers, and add
     * items to the transaction.
     */
    private JPanel itemSearchPanel;
    /**
     * ItemScrollPane shows item thumbnails.
     */
    ItemScrollPane itemScroll;

    /**
     * JComboBox used to select the item category to search for items in. When
     * selected value is changed, itemScroll is repopulated with thumbnails from
     * that category.
     */
    JComboBox categories;

    /**
     * JComboBox used to select the item subcategory to search for items in.
     * Only visible if the selected category contains subcategories. When
     * selected value is changed, itemScroll is repopulated with thumbnails from
     * that subcategory.
     */
    JComboBox subcategories;

    /**
     * Field to search for an item by keyword(s).
     */
    InputField searchField;

    /**
     * Employee constructor initializes components needed for the employee
     * state.
     */
    public Employee() {
        //----- INITIALIZE MAIN COMPONENTS -----/
        items = new ArrayList<>();

        // HELP DIALOG
        setupHelpDialog();
        helpLink = new Link("Help");

        // EMPLOYEE PANEL
        employeePanel = new DropShadowPanel(8, Color.WHITE, Colors.SILVER, Colors.DARK_BLUE);
        employeePanel.setLayout(new CardLayout());

        // BUTTONS 
        logoutBttn = new ButtonPrimary("Logout");
        checkoutBttn = new ButtonPrimary("Checkout");
        newTransactionBttn = new ButtonPrimary("Cancel Transaction");
        itemSearchBttn = new ButtonPrimary("Item Search");
        managerBttn = new ButtonPrimary("Manager View");
        managerBttn.setVisible(false);

        // FIELDS && LABELS
        skuField = new InputField();
        FieldLabel skuLabel = new FieldLabel("Item SKU Number");
        orderTotalLabel = new BizmartLabel("Subtotal");
        orderTotalLabel.setFont(new Font("Nunito", Font.PLAIN, 28));
        invalidSkuWarning = new WarningLabel("Invalid SKU number.");

        // CART SCROLLPANE
        cartSummaryPanel = new JPanel();
        cartSummaryPanel.setLayout(new BoxLayout(cartSummaryPanel, BoxLayout.Y_AXIS));
        cartSummaryPanel.setBackground(Colors.SILVER);

        cartScroll = new JScrollPane(cartSummaryPanel);
        cartScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        cartScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        cartScroll.getVerticalScrollBar().setUnitIncrement(16);
        cartScroll.setBorder(new MatteBorder(15, 15, 15, 10, Color.WHITE));

        // ITEM SEARCH PANEL
        setupSearchPanel();

        //----- GROUP COMPONENTS IN NESTED PANELS -----//
        // BUTTON PANEL
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        buttonPanel.setBackground(null);

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        buttonPanel.add(helpLink, gbc);

        gbc.gridy++;
        gbc.weighty = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        buttonPanel.add(logoutBttn, gbc);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 0, 5, 0);
        buttonPanel.add(skuLabel, gbc);

        gbc.gridy++;
        buttonPanel.add(skuField, gbc);

        gbc.gridy++;
        buttonPanel.add(invalidSkuWarning, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        buttonPanel.add(itemSearchBttn, gbc);

        gbc.gridy++;
        buttonPanel.add(checkoutBttn, gbc);

        gbc.gridy++;
        buttonPanel.add(newTransactionBttn, gbc);

        gbc.gridy++;
        buttonPanel.add(managerBttn, gbc);

        gbc.gridy++;
        gbc.weighty = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 10, 0);
        buttonPanel.add(orderTotalLabel, gbc);

        //----- SIZE COMPONENTS -----//
        // EMPLOYEE PANEL
        int width = frame.getWidth() - 100;
        int height = frame.getHeight() - 100;
        Dimension d = new Dimension(width, height);
        employeePanel.setPreferredSize(d);
        employeePanel.setMaximumSize(d);

        // BUTTONS
        logoutBttn.setPreferredSize(new Dimension(125, 54));
        checkoutBttn.setPreferredSize(new Dimension(300, 54));
        itemSearchBttn.setPreferredSize(new Dimension(300, 54));
        newTransactionBttn.setPreferredSize(new Dimension(300, 54));
        managerBttn.setPreferredSize(new Dimension(300, 54));

        // BUTTON PANEL
        buttonPanel.setMaximumSize(new Dimension(buttonPanel.getPreferredSize().width, height));

        // FIELDS
        skuField.setWidth(300);

        //----- ADD COMPONENTS TO CONTAINER PANEL -----//
        // add cart scroll && buttons to one panel
        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.X_AXIS));
        cartPanel.setBackground(null);
        cartPanel.add(cartScroll);
        cartPanel.add(Box.createHorizontalStrut(10));
        cartPanel.add(buttonPanel);
        cartPanel.add(Box.createHorizontalStrut(12));

        employeePanel.add(cartPanel, "cart");
        employeePanel.add(itemSearchPanel, "search");
    }

    //--------------------------------------------//
    //---------- ABSTRACT STATE METHODS ----------//
    //---------- ENTER STATE ----------//
    @Override
    protected void enterState() {
        State.frame.setTitle("Bizmart POS System");
        State.frame.getContentPane().removeAll();

        if (currentUser.getPosition() == User.MANAGER) {
            managerBttn.setVisible(true);
        } else {
            managerBttn.setVisible(false);
        }

        // add items to cart
        if (!items.isEmpty()) {
            cartSummaryPanel.removeAll();
            for (CartItem i : items) {
                cartSummaryPanel.add(Box.createVerticalStrut(10));
                i.setAlignmentX(Component.CENTER_ALIGNMENT);
                cartSummaryPanel.add(i);
            }
            cartSummaryPanel.add(Box.createVerticalStrut(10));
        }

        // calculate subtotal
        calculateTotal();

        // add employee panel to frame
        // default GridBagConstraints centers panel in frame & respects sizing
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        State.frame.add(connectionStatus, gbc);
        gbc.gridy = 1;
        State.frame.add(employeePanel, gbc);

        // repaint the frame
        cartScroll.revalidate();
        cartScroll.repaint();
        State.frame.repaint();
        State.frame.setVisible(true);

        // request focus
        cartScroll.requestFocusInWindow();
    }

    //---------- ADD EVENT LISTENERS TO COMPONENTS ----------//
    @Override
    protected void addEventListeners() {
        //----- NEW TRANSACTION BUTTON -----//
        Action transPress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearCart();
            }
        };
        newTransactionBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        newTransactionBttn.getActionMap().put("pressed", transPress);

        newTransactionBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                newTransactionBttn.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                newTransactionBttn.mouseReleased();
                clearCart();
            }
        });

        //----- LOGOUT BUTTON -----//
        Action managerPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // clear the cart
                clearCart();
                changeState(State.manager);
            }
        };
        managerBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        managerBttn.getActionMap().put("pressed", managerPress);

        managerBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                managerBttn.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                managerBttn.mouseReleased();
                managerPress.actionPerformed(new ActionEvent(managerBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- LOGOUT BUTTON -----//
        Action logoutPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                clearCart();
                State.currentUser = null;
                changeState(State.login);
            }
        };
        logoutBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        logoutBttn.getActionMap().put("pressed", logoutPress);

        logoutBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                logoutBttn.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                logoutBttn.mouseReleased();
                logoutPress.actionPerformed(new ActionEvent(logoutBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- CHECKOUT BUTTON -----//
        Action checkoutPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!items.isEmpty()) {
                    State.checkout = new Checkout();
                    State.checkout.addEventListeners();
                    changeState(State.checkout);
                }
            }
        };
        checkoutBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        checkoutBttn.getActionMap().put("pressed", checkoutPress);

        checkoutBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                checkoutBttn.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                checkoutBttn.mouseReleased();
                checkoutPress.actionPerformed(new ActionEvent(checkoutBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- ITEM SEARCH BUTTON -----//
        Action itemSearchPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                showPanel("search");
            }
        };
        itemSearchBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        itemSearchBttn.getActionMap().put("pressed", itemSearchPress);

        itemSearchBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                itemSearchBttn.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                itemSearchBttn.mouseReleased();
                showPanel("search");
            }
        });

        //----- SKU SEARCH FIELD -----//
        skuField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

        });

        Action skuPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ResultSet rs = con.getItem(Integer.parseInt(skuField.getText()));

                    if (!rs.isBeforeFirst()) {
                        invalidSkuWarning.setVisible(true);
                        return;
                    }

                    invalidSkuWarning.setVisible(false);
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
                    int max = rs.getInt(12);
                    // convert the item image blob in the database to an image icon
                    byte[] b = rs.getBytes(10);
                    ImageIcon img = new ImageIcon(b);
                    // create a cart item (extended JPanel)
                    CartItem c = new CartItem(new InventoryItem(id, name, desc, categoryID, subcategoryID, price, wholesale, quantity, restock, max, img), 1);

                    for (CartItem ci : items) {
                        // if the item is already in the cart
                        if (ci.getInventoryItem().getItemNumber() == c.getInventoryItem().getItemNumber()) {
                            // add the quantity from the cart with the quantity of the selected item
                            int newQty = c.getQuantity() + ci.getQuantity();
                            // get the maximum stock for this item
                            int inStock = ci.getInventoryItem().getQuantity();

                            if (newQty > inStock) {
                                newQty = inStock;
                            }

                            ci.setQuantity(newQty);
                            calculateTotal();
                            return;
                        }
                    }
                    // if the item wasn't in the cart, add it
                    addItemToCart(c);
                    calculateTotal();

                    // add item to cart
                    if (items.size() == 1) {
                        cartSummaryPanel.add(Box.createVerticalStrut(10));
                    }

                    skuField.setText("");
                    c.setAlignmentX(Component.CENTER_ALIGNMENT);
                    cartSummaryPanel.add(c);
                    cartSummaryPanel.add(Box.createVerticalStrut(10));
                    cartScroll.revalidate();
                    cartScroll.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        skuField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        skuField.getActionMap().put("pressed", skuPress);

        //----- HELP LINK -----//
        Action helpPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                helpDialog.setVisible(true);
            }
        };
        helpLink.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        helpLink.getActionMap().put("pressed", helpPress);

        helpLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                helpDialog.setVisible(true);
            }
        });

    }

    //----------------------------------------------//
    //---------- POLYMORPHIC CART METHODS ----------//
    @Override
    protected void addItemToCart(CartItem c) {
        items.add(c);
    }

    @Override
    public void deleteItem(int itemNumber) {
        // delete the item with itemNumber
        for (CartItem c : items) {
            if (c.getInventoryItem().getItemNumber() == itemNumber) {
                items.remove(c);
                break;
            }
        }

        // re-add items to cart
        cartSummaryPanel.removeAll();
        for (CartItem i : items) {
            cartSummaryPanel.add(Box.createVerticalStrut(15));
            cartSummaryPanel.add(i);
        }

        // recalulate total
        calculateTotal();

        // repaint the scrollpane
        cartScroll.revalidate();
        cartScroll.repaint();
    }

    @Override
    public void calculateTotal() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        double subtotal = 0;
        for (CartItem i : items) {
            subtotal += i.getInventoryItem().getRetailPrice() * i.getQuantity();
        }
        orderTotalLabel.setText("Subtotal: $" + df.format(subtotal));
    }

    @Override
    public ArrayList<CartItem> getCart() {
        return items;
    }

    @Override
    public void clearCart() {
        skuField.setText("");

        items = new ArrayList<>();

        // remove items
        cartSummaryPanel.removeAll();

        // recalulate total
        calculateTotal();

        // repaint the scrollpane
        cartScroll.revalidate();
        cartScroll.repaint();
    }

    //--------------------------------------------//
    //---------- EMPLOYEE STATE METHODS ----------//
    /**
     * Initialize and place components in itemSearchPanel. Add event listeners
     * to itemSearchPanel components.
     */
    public void setupSearchPanel() {
        itemSearchPanel = new JPanel();
        itemSearchPanel.setLayout(new BorderLayout());

        itemScroll = new ItemScrollPane();

        // JCOMBOBOX & FIELDS
        categories = new JComboBox();
        categories.addItem("Shop by Category");
        subcategories = new JComboBox();
        subcategories.setVisible(false);
        try {
            ResultSet rs = con.getItemCategories();
            while (rs.next()) {
                categories.addItem(rs.getString(2));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        searchField = new InputField("search by keyword...");

        // BUTTONS
        ButtonPrimary backBttn = new ButtonPrimary("Back");
        backBttn.setPreferredSize(new Dimension(150, 54));

        //----- GROUP COMPONENTS -----//
        // MENU BAR PANEL
        JPanel menu = new JPanel();
        menu.setBackground(Color.WHITE);
        menu.setLayout(new BoxLayout(menu, BoxLayout.X_AXIS));
        menu.setBorder(new MatteBorder(0, 0, 3, 0, Colors.SILVER));

        JPanel search = new JPanel(new FlowLayout(SwingConstants.LEFT, 15, 15));
        search.setBackground(null);
        search.add(categories);
        search.add(subcategories);
        search.add(searchField);
        search.setMaximumSize(search.getPreferredSize());

        JPanel buttons = new JPanel();
        buttons.setBackground(null);
        buttons.add(backBttn);
        buttons.setMaximumSize(buttons.getPreferredSize());

        search.setAlignmentY(Component.TOP_ALIGNMENT);
        buttons.setAlignmentY(Component.TOP_ALIGNMENT);
        menu.add(search);
        menu.add(Box.createGlue());
        menu.add(buttons);

        //----- SIZE COMPONENTS -----//
        // SEARCH FIELD
        searchField.setWidth(250);
        searchField.setHeight(40);

        // COMBOBOXES
        categories.setFont(new Font("Nunito", Font.PLAIN, 18));
        subcategories.setFont(new Font("Nunito", Font.PLAIN, 18));
        categories.setPreferredSize(new Dimension(210, searchField.getHeight()));
        categories.setMaximumSize(new Dimension(210, searchField.getHeight()));
        subcategories.setPreferredSize(new Dimension(210, searchField.getHeight()));
        subcategories.setMaximumSize(new Dimension(210, searchField.getHeight()));

        //----- ADD COMPONENTS TO PANEL -----//
        itemSearchPanel.add(menu, BorderLayout.NORTH);
        itemSearchPanel.add(itemScroll, BorderLayout.CENTER);

        //----- ADD EVENT LSITENERS TO PANEL -----//
        // CATEGORY COMBO BOX
        categories.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                try {
                    //if the selected category has subcategorys, populate the subcategory combobox
                    subcategories.removeAllItems();
                    subcategories.addItem("Choose a Subcategory");
                    ResultSet rs = con.getSubcategory(categories.getSelectedItem().toString());
                    // isBeforeFirst returns false if the cursor is not before the first record OR if there are no rows in the ResultSet.
                    if (rs.isBeforeFirst()) {
                        while (rs.next()) {
                            subcategories.addItem(rs.getString(1));
                        }
                    }
                    if (subcategories.getItemCount() > 1) {
                        subcategories.setVisible(true);
                    } else {
                        subcategories.setVisible(false);
                    }

                    // populate the JScrollPane with thumbnails of items from the selected category
                    try {
                        ResultSet r;
                        if (categories.getSelectedIndex() == 0) {
                            r = con.searchTop100();
                        } else {
                            r = con.searchCategory(categories.getSelectedItem().toString());
                        }

                        itemScroll.sortItems(r);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // SUBCATEGORY COMBO BOX
        subcategories.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (subcategories.getSelectedIndex() != 0) {
                    // populate the JScrollPane with thumbnails of items from the selected subcategory
                    try {
                        ResultSet r = con.searchSubcategory(subcategories.getSelectedItem().toString());
                        itemScroll.sortItems(r);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }

                }
            }
        });

        // SEARCH FIELD
        Action enter = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                String text = searchField.getText();
                String[] keywords = text.trim().split(" ");

                // populate the JScrollPane with thumbnails of items whose name includes the keyword(s)
                try {
                    ResultSet r;
                    if (keywords.length > 1) {
                        r = con.searchItems(keywords, currentState);
                    } else {
                        r = con.searchItems(text, currentState);
                    }
                    itemScroll.sortItems(r);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        };
        searchField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        searchField.getActionMap().put("pressed", enter);

        // BACK BUTTON
        Action backPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                showPanel("cart");
            }
        };
        backBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        backBttn.getActionMap().put("pressed", backPress);

        backBttn.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                backBttn.mouseReleased();
                showPanel("cart");
            }

            public void mousePressed(MouseEvent e) {
                backBttn.mousePressed();
            }
        });
    }

    /**
     * Gets the subcategory selected in the subcategories JComboBox. Used by the
     * SQL connection manager.
     *
     * @return the subcategory's name
     */
    public String getSelectedSubcategory() {
        if (subcategories.getSelectedIndex() > 0 && subcategories.isVisible()) {
            return subcategories.getSelectedItem().toString();
        } else {
            return "";
        }
    }

    /**
     * Gets the category selected in the categories JComboBox. Used by the SQL
     * connection manager.
     *
     * @return the category's name
     */
    public String getSelectedCategory() {
        return categories.getSelectedItem().toString();
    }

    /**
     * Changes the JPanel displayed in the employeePanel's CardLayout.
     *
     * @param s the name assigned to the JPanel
     */
    public void showPanel(String s) {
        CardLayout cl = (CardLayout) employeePanel.getLayout();
        cl.show(employeePanel, s);
    }

    /**
     * Initialize and place components in helpDialog. Add event listeners to
     * helpDialog components.
     */
    private void setupHelpDialog() {
        helpDialog = new PopupDialog(State.frame);
        helpDialog.setTitle("Help");
        helpDialog.setLayout(new GridBagLayout());
        helpDialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        BizmartLabel helpLabel = new BizmartLabel("POS System Help");
        helpLabel.setFont(new Font("Nunito", Font.BOLD, 24));
        panel.add(helpLabel, gbc);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(new BizmartLabel("Add items to the transaction in one of two ways."), gbc);

        gbc.gridy++;
        panel.add(new BizmartLabel("  1. Click the \"Item Search\" button to sort items by keyword or category."), gbc);

        gbc.gridy++;
        panel.add(new BizmartLabel("      From here, you can show customers item details and add items to the order."), gbc);

        gbc.gridy++;
        panel.add(new BizmartLabel("  2. Enter the items SKU number into the text field and press ENTER."), gbc);

        gbc.gridy++;
        panel.add(new BizmartLabel("Customer must have an active account to go through with the transaction"), gbc);

        helpDialog.add(panel, new GridBagConstraints());
        helpDialog.setSize(new Dimension(850, 350));
        helpDialog.getContentPane().setBackground(Color.WHITE);
        helpDialog.setLocationRelativeTo(null);
    }
}
