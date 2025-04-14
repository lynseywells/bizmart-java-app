package statemachine;

import component.fields.InputField;
import component.labels.FieldLabel;
import component.buttons.ButtonPrimary;
import component.buttons.Link;
import component.PopupDialog;
import component.ItemScrollPane;
import component.Colors;
import component.panels.DropShadowPanel;
import component.labels.BizmartLabel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import util.InventoryItem;
import util.Validation;

/**
 * Subclass Customer is an online shopping interface, and is fully accessible to
 * signed-in users of with a "customer" type account. Guests can access this
 * state without logging in, but can't place orders. This state can move into
 * the states login, item, cart, and checkout.
 */
public class Customer extends State {

    /**
     * The root JPanel for the customer state that is placed in the JFrame when
     * this state is entered. Uses a BoxLayout.
     */
    private DropShadowPanel customerPanel;

    /**
     * ItemScrollPane dynamically displays ItemThumbnails based on search
     * criteria.
     */
    private ItemScrollPane scroll;

    /**
     * JComboBox to sort items in scroll by category.
     */
    private JComboBox categories;

    /**
     * JComboBox to sort items in scroll by subcategory. Only visible if the
     * category chosen in categories has a subcategory.
     */
    private JComboBox subcategories;

    /**
     * InputField to search for items in scroll by number, keyword, or
     * description.
     */
    private InputField searchField;

    /**
     * Shows helpDialog when clicked.
     */
    private Link helpLink;

    /**
     * Button changes state to login when pressed.
     */
    private ButtonPrimary logoutBttn;

    /**
     * Button changes state to cart when pressed.
     */
    private ButtonPrimary cartBttn;

    /**
     * A JDialog help menu.
     */
    private PopupDialog helpDialog;

    /**
     * Customer constructor initializes components needed for the customer
     * state.
     */
    public Customer() {
        //----- INITIALIZE MAIN COMPONENTS -----//
        // CUSTOMER PANEL
        customerPanel = new DropShadowPanel(8, Color.WHITE, Colors.SILVER, Colors.DARK_BLUE);
        customerPanel.setLayout(new BoxLayout(customerPanel, BoxLayout.Y_AXIS));

        // SCROLLPANE
        scroll = new ItemScrollPane();

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

        // LINKS & BUTTONS
        helpLink = new Link("Help");
        logoutBttn = new ButtonPrimary("Logout");
        cartBttn = new ButtonPrimary("Cart");
        cartBttn.setBorderColor(Colors.GREEN);
        cartBttn.setBackground(Colors.MID_GREEN);
        cartBttn.setSelectedBorderColor(Colors.GREEN);
        cartBttn.setSelectedBackground(Colors.MID_GREEN);

        // HELP DIALOG
        helpDialogSetup();

        //----- GROUP COMPONENTS -----//
        // MENU BAR PANEL
        JPanel menu = new JPanel();
        menu.setBackground(Color.WHITE);
        menu.setLayout(new BoxLayout(menu, BoxLayout.X_AXIS));
        menu.setBorder(new MatteBorder(0, 0, 3, 0, Colors.SILVER));

        // components for searching items
        JPanel search = new JPanel(new FlowLayout(SwingConstants.LEFT, 15, 15));
        search.setBackground(null);
        search.add(categories);
        search.add(subcategories);
        search.add(searchField);
        search.setMaximumSize(search.getPreferredSize());

        // buttons
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout(SwingConstants.LEFT, 10, 5));
        buttons.setBackground(null);
        buttons.add(cartBttn);
        buttons.add(logoutBttn);
        buttons.add(helpLink);
        buttons.setMaximumSize(buttons.getPreferredSize());

        // add search && button panels to menu bar panel
        search.setAlignmentY(Component.TOP_ALIGNMENT);
        buttons.setAlignmentY(Component.TOP_ALIGNMENT);
        menu.add(search);
        menu.add(Box.createGlue());
        menu.add(buttons);

        //----- SIZE COMPONENTS -----//
        int width = frame.getWidth() - 100;
        int height = frame.getHeight() - 100;
        Dimension d = new Dimension(width, height);
        customerPanel.setPreferredSize(d);
        customerPanel.setMaximumSize(d);

        menu.setPreferredSize(new Dimension(width, (int) menu.getPreferredSize().getHeight()));
        menu.setMaximumSize(new Dimension(width, (int) menu.getPreferredSize().getHeight()));

        //search field
        searchField.setWidth(250);
        searchField.setHeight(40);

        // comboboxes
        categories.setFont(new Font("Nunito", Font.PLAIN, 18));
        subcategories.setFont(new Font("Nunito", Font.PLAIN, 18));
        categories.setPreferredSize(new Dimension(210, searchField.getHeight()));
        categories.setMaximumSize(new Dimension(210, searchField.getHeight()));
        subcategories.setPreferredSize(new Dimension(210, searchField.getHeight()));
        subcategories.setMaximumSize(new Dimension(210, searchField.getHeight()));

        // buttons
        logoutBttn.setPreferredSize(new Dimension(150, 54));
        cartBttn.setPreferredSize(new Dimension(110, 54));

        //----- ADD COMPONENTS TO REGISTER PANEL -----//
        // add all jpanels to one jpanel
        customerPanel.add(menu);
        customerPanel.add(scroll);

        // create a new cart for this customer
        State.cart = new Cart();
        State.cart.addEventListeners();
    }

    //--------------------------------------------//
    //---------- ABSTRACT STATE METHODS ----------//
    //---------- ENTER STATE ----------//
    @Override
    protected void enterState() {
        State.frame.setTitle("Bizmart Customer Page");
        State.frame.getContentPane().removeAll();

        // check if the user is signed in 
        if (State.currentUser == null) {
            cartBttn.setEnabled(false);
            logoutBttn.setText("Login");
        } else {
            cartBttn.setEnabled(true);
            logoutBttn.setText("Logout");
        }

        // clear search field
        searchField.setText("");
        categories.setSelectedIndex(0);

        // add customer panel to frame
        // default GridBagConstraints centers panel in frame & respects sizing
        State.frame.add(customerPanel, new GridBagConstraints());

        // repaint the frame
        State.frame.repaint();
        State.frame.setVisible(true);

        if (scroll.isEmpty()) {
            try {
                ResultSet rs = con.searchTop100();
                scroll.sortItems(rs);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }

        // request focus
        categories.requestFocusInWindow();

    }

    //----- ADD EVENT LISTENERS TO COMPONENTS -----//
    @Override
    protected void addEventListeners() {
        //----- LOGOUT BUTTON -----//
        Action logoutPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // clear the cart
                State.cart = new Cart();
                State.cart.addEventListeners();
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

        //----- VIEW CART BUTTON -----//
        Action cartPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (cartBttn.isEnabled()) {
                    changeState(State.cart);
                }
            }
        };
        cartBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        cartBttn.getActionMap().put("pressed", cartPress);

        cartBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                cartBttn.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (cartBttn.isEnabled()) {
                    cartBttn.mouseReleased();
                    changeState(State.cart);
                }
            }
        });

        //----- CATEGORY COMBOBOX -----//
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

                        scroll.sortItems(r);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        //----- SUBCATEGORY COMBOBOX -----//
        subcategories.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (subcategories.getSelectedIndex() != 0) {
                    try {
                        // populate the JScrollPane with thumbnails of items from the selected subcategory

                        ResultSet r = con.searchSubcategory(subcategories.getSelectedItem().toString());
                        scroll.sortItems(r);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });

        //----- SEARCH FIELD -----//
        Action enter = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                String text = searchField.getText();
                String[] keywords = text.trim().split(" ");

                // populate the JScrollPane with thumbnails of items whose name includes the keyword(s)
                try {
                    ResultSet r;
                    if (keywords.length > 1) {
                        r = con.searchItems(keywords, currentState);
                        scroll.sortItems(r);
                    } else {
                        if (Validation.isNumeric(text)) {
                            r = con.getItem(Integer.parseInt(text));

                            if (r.isBeforeFirst()) {
                                r.next();
                                int id = r.getInt(1);
                                String name = r.getString(2);
                                String desc = r.getString(3);
                                int categoryID = r.getInt(4);
                                int subcategoryID = r.getInt(5);
                                double price = r.getDouble(6);
                                double wholesale = r.getDouble(7);
                                int quantity = r.getInt(8);
                                int restock = r.getInt(9);
                                int discontinue = r.getInt(11);
                                int max = r.getInt(12);
                                // convert the item image blob in the database to an image icon
                                Blob blob = r.getBlob(10);
                                byte[] b = blob.getBytes(1, (int) blob.length());
                                ImageIcon img = new ImageIcon(b);
                                InventoryItem item = new InventoryItem(id, name, desc, categoryID, subcategoryID, price, wholesale, quantity, restock, max, img);
                                if (discontinue == 1) {
                                    PopupDialog notFound = new PopupDialog(State.getFrame());
                                    notFound.setTitle("Item Not Found");

                                    notFound.setLayout(new GridBagLayout());
                                    notFound.setSize(new Dimension(400, 100));
                                    notFound.setLocationRelativeTo(null);

                                    notFound.add(new FieldLabel("Item " + text + " not found."), new GridBagConstraints());
                                    notFound.setVisible(true);
                                } else {
                                    item.setDiscontinued(false);
                                    // create item panel and add it to frame
                                    State.createItemState(item);
                                }
                            } else {
                                PopupDialog notFound = new PopupDialog(State.getFrame());
                                notFound.setTitle("Item Not Found");

                                notFound.setLayout(new GridBagLayout());
                                notFound.setSize(new Dimension(400, 100));
                                notFound.setLocationRelativeTo(null);

                                notFound.add(new FieldLabel("Item " + text + " not found."), new GridBagConstraints());
                                notFound.setVisible(true);
                            }
                        } else {
                            r = con.searchItems(text, currentState);
                            scroll.sortItems(r);
                        }
                    }
                } catch (Exception exc) {
                    exc.printStackTrace();
                }

            }
        ;
        };
        searchField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        searchField.getActionMap().put("pressed", enter);

        //----- HELP LINK -----//
        Action helpPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                helpDialog.setVisible(true);
            }
        };
        helpLink.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        helpLink.getActionMap().put("pressed", helpPress);

        helpLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                helpDialog.setVisible(true);
            }
        });
    }

    //--------------------------------------------//
    //---------- CUSTOMER STATE METHODS ----------//
    /**
     * Initialize and place components in helpDialog. Add event listeners to
     * helpDialog components.
     */
    private void helpDialogSetup() {
        helpDialog = new PopupDialog(State.frame);
        helpDialog.setTitle("Help");
        helpDialog.setLayout(new GridBagLayout());
        helpDialog.setResizable(false);
        helpDialog.getContentPane().setBackground(Color.WHITE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        // (0, 0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        BizmartLabel helpLabel = new BizmartLabel("Customer Help");
        helpLabel.setFont(new Font("Nunito", Font.BOLD, 24));
        panel.add(helpLink, gbc);
        // (0, 1)
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(new BizmartLabel("Search products by selecting a category from the dropdown"), gbc);
        // (0, 2)
        gbc.gridy = 2;
        panel.add(new BizmartLabel("Use the search bar to search within a category by keyword."), gbc);
        // (0, 3)
        gbc.gridy = 3;
        panel.add(new BizmartLabel("To search by keyword only, don't select a category from the drop-down."), gbc);
        //(0, 4)
        gbc.gridy = 4;
        panel.add(new BizmartLabel("Select an items thumbnail to see more details, or add it to your cart."), gbc);
        //(0, 5)
        gbc.gridy = 5;
        panel.add(new BizmartLabel("You must sign-in to an account to order items and view the cart."), gbc);

        helpDialog.add(panel, new GridBagConstraints());
        helpDialog.setSize(new Dimension(800, 450));
        panel.setPreferredSize(new Dimension(730, 350));
        helpDialog.setLocationRelativeTo(null);
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
}
