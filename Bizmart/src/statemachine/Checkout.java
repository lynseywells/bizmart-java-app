package statemachine;

import component.labels.FieldLabel;
import component.labels.WarningLabel;
import component.labels.FormTitle;
import component.labels.FieldRequiredLabel;
import component.labels.BizmartLabel;
import component.fields.InputField;
import component.buttons.ButtonPrimary;
import component.buttons.Link;
import component.PopupDialog;
import component.Colors;
import component.panels.DropShadowPanel;
import component.panels.CartItem;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.*;
import util.*;

/**
 * Subclass Checkout can only be entered if the cart of currentState contains
 * items, and a new Checkout should be initialized each time the state is
 * entered. Checkout shows an order summary and provides a form to retrieve
 * payment and finalize the order. This state can move into the states cart,
 * customer, manager, and employee.
 * <p>
 * Once the order is finalized, Checkout utilizes the HTML class to
 * automatically generate a receipt file and open it in the web browser. The
 * checkout form and generated receipt are different depending on currentState
 * (cart/customer, manager, or employee).
 */
public class Checkout extends State {

    /**
     * The root JPanel for the checkout state that is placed in the JFrame when
     * this state is entered. Uses a BorderLayout.
     */
    private DropShadowPanel checkoutPanel;

    /**
     * JScrollPane for formPanel
     */
    private JScrollPane scroll;

    /**
     * JPanel that contains the checkout form.
     */
    private JPanel formPanel;

    /**
     * Field to add a discount to the order.
     */
    private InputField discountField;

    /**
     * Fields for credit card information.
     */
    private InputField ccNumField, ccvField, expDateField;

    /**
     * Button that goes back to the previous state when pressed.
     */
    private ButtonPrimary backBttn;

    /**
     * Button that finalizes the order when pressed.
     */
    private ButtonPrimary orderBttn;

    /**
     * Stores line item rows for the JTable table.
     */
    private ArrayList<String[]> items;

    /**
     * Stores total price rows for the JTable table.
     */
    private ArrayList<String[]> totals;

    /**
     * Stores the item ID and quantity ordered for each item.
     */
    private ArrayList<int[]> inventoryItems;

    /**
     * The ID number of the discount applied to the order.
     */
    private int discountID;

    /**
     * True if the applied discount is cart level, false if it is item level.
     */
    private boolean isCartLevel;

    /**
     * If the applied discount is item level, the items ID number is stored
     * here.
     */
    private int discountItemID;

    /**
     * If the applied discount is item level, the items retail price is stored
     * here. Used to calculate item-level percentage discounts.
     */
    private double itemPrice;

    /**
     * The subtotal of the order before tax.
     */
    private double subtotal;

    /**
     * The taxes applied to the order.
     */
    private double tax;

    /**
     * The final total of the order after taxes and discount.
     */
    private double total;

    /**
     * Warning that is displayed when an invalid discount code is entered.
     */
    private WarningLabel discountWarning;
    /**
     * Labels for required fields.
     */
    private FieldRequiredLabel ccNumLabel, ccvLabel, expDateLabel;

    /**
     * Table that displays the order summary.
     */
    private JTable table;

    /**
     * Link that opens discountDialog when pressed.
     * <p>
     * Only available if previousState is employee.
     */
    private Link discountLink;
    /**
     * Link appears when a customer is added to the order, and removes the
     * customer when pressed.
     * <p>
     * Only available if previousState is employee.
     */
    private Link changeCustomerLink;

    /**
     * Label for customerField.
     * <p>
     * Only available if previousState is employee.
     */
    private FieldRequiredLabel customerLabel;

    /**
     * Field to search for customers to add to the transaction by name, phone
     * number, or email. Opens customerDialog when search is executed.
     * <p>
     * Only available if previousState is employee.
     */
    private InputField customerField;

    /**
     * Stores the customers found when executing a search with customerField.
     * <p>
     * Only available if the previousState is employee.
     */
    private ArrayList<Person> foundCustomers;

    /**
     * Link shows helpDialog when pressed.
     */
    private Link helpLink;
    /**
     * JDialog help menu.
     */
    private PopupDialog helpDialog;

    /**
     * JDialog that displays foundCustomers in a selectable list.
     * <p>
     * Only available if the previousState is employee.
     */
    private PopupDialog customerDialog;

    /**
     * Selectable JList of found customers.
     * <p>
     * customerDialog component. Only available if the previousState is
     * employee.
     */
    private JList customerList;

    /**
     * JScrollPane for customerList.
     * <p>
     * customerDialog component. Only available if the previousState is
     * employee.
     */
    private JScrollPane customerScroll;

    /**
     * Button that adds the customer selected in customerList to the order when
     * pressed.
     * <p>
     * customerDialog component. Only available if the previousState is
     * employee.
     */
    private ButtonPrimary addCustomerBttn;

    /**
     * JDialog that displays all active discounts in a selectable list.
     */
    private PopupDialog discountDialog;

    /**
     * JScrollPane for discountList.
     * <p>
     * discountDialog component. Only available if the previousState is
     * employee.
     */
    private JScrollPane discountScroll;

    /**
     * Button that adds the discount selected in discountList to the order when
     * pressed.
     * <p>
     * discountDialog component. Only available if the previousState is
     * employee.
     */
    private ButtonPrimary addDiscountBttn;

    /**
     * Selectable JList of active discounts that's displayed in discountDialog.
     * <p>
     * discountDialog component. Only available if the previousState is
     * employee.
     */
    private JList discountList;

    /**
     * Checkout constructor initializes components needed for the checkout
     * state. Should be called every time the checkout state is entered.
     */
    public Checkout() {
        //----- INITIALIZE COMPONENTS -----//
        foundCustomers = new ArrayList<>();
        checkoutPanel = new DropShadowPanel(8, Color.WHITE, Colors.SILVER, Colors.DARK_BLUE);
        checkoutPanel.setLayout(new BorderLayout());
        formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        // SCROLLPANES
        scroll = new JScrollPane(formPanel);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        // TITLE
        FormTitle title = new FormTitle("Checkout");

        //TABLE 
        // columns
        String[] columns = {"Item", "Item Price", "Quantity", "Total Price"};

        // rows (get items from the cart to create rows in the table)
        subtotal = 0;
        items = new ArrayList<>();
        inventoryItems = new ArrayList<>();
        discountID = 0;
        discountItemID = 0;

        if (currentUser.getPosition() == User.CUSTOMER) {
            for (CartItem ci : State.getCartState().getCart()) {
                String[] temp = new String[4];
                InventoryItem i = ci.getInventoryItem();
                // add the inventory items id & quantity to inventoryItems array
                // this will be used when order is placed to subtract items from the database
                inventoryItems.add(new int[]{i.getItemNumber(), ci.getQuantity()});

                // add to items array - to be added as a row in the JTable
                temp[0] = i.getItemName();
                temp[1] = "$ " + Money.DF.format(i.getRetailPrice());
                temp[2] = "x " + Integer.toString(ci.getQuantity());
                temp[3] = "$ " + Money.DF.format(i.getRetailPrice() * ci.getQuantity());
                items.add(temp);
                // add to the cart's subtotal
                subtotal += i.getRetailPrice() * ci.getQuantity();
            }
        } else if (currentState.equals(State.getEmployeeState())) {
            for (CartItem ci : State.getEmployeeState().getCart()) {
                String[] temp = new String[4];
                InventoryItem i = ci.getInventoryItem();
                // add the inventory items id & quantity to inventoryItems array
                // this will be used when order is placed to subtract items from the database
                inventoryItems.add(new int[]{i.getItemNumber(), ci.getQuantity()});

                // add to items array - to be added as a row in the JTable
                temp[0] = i.getItemName();
                temp[1] = "$ " + Money.DF.format(i.getRetailPrice());
                temp[2] = "x " + Integer.toString(ci.getQuantity());
                temp[3] = "$ " + Money.DF.format(i.getRetailPrice() * ci.getQuantity());
                items.add(temp);
                // add to the cart's subtotal
                subtotal += i.getRetailPrice() * ci.getQuantity();
            }
        } else {
            for (CartItem ci : State.getManagerState().getCart()) {
                String[] temp = new String[4];
                InventoryItem i = ci.getInventoryItem();
                // add the inventory items id & quantity to inventoryItems array
                // this will be used when order is placed to subtract items from the database
                inventoryItems.add(new int[]{i.getItemNumber(), ci.getQuantity()});

                // add to items array - to be added as a row in the JTable
                temp[0] = i.getItemName();
                temp[1] = "$ " + Money.DF.format(i.getWholesalePrice());
                temp[2] = "x " + Integer.toString(ci.getQuantity());
                temp[3] = "$ " + Money.DF.format(i.getWholesalePrice() * ci.getQuantity());
                items.add(temp);
                // add to the cart's subtotal
                subtotal += i.getWholesalePrice() * ci.getQuantity();
            }
        }

        tax = subtotal * .0825;
        total = (subtotal + tax);
        totals = new ArrayList<>();
        totals.add(new String[]{"", "", "Subtotal", "$" + Money.DF.format(subtotal)});
        totals.add(new String[]{"", "", "Tax (8.25%)", "$" + Money.DF.format(tax)});
        totals.add(new String[]{"", "", "Total", "$" + Money.DF.format(total)});

        // convert the ArrayList to a regular array
        String[][] rows = new String[items.size() + totals.size()][];
        int itemsSize = items.size();
        int totalsSize = totals.size();
        for (int i = 0; i < itemsSize; i++) {
            rows[i] = items.get(i);
        }
        for (int i = 0; i < totalsSize; i++) {
            rows[i + itemsSize] = totals.get(i);
        }

        // create & style table
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        // add rows
        int length = rows.length;
        for (int i = 0; i < length; i++) {
            model.addRow(rows[i]);
        }
        table.setEnabled(false);
        table.setFont(new Font("Nunito", Font.PLAIN, 18));
        table.getTableHeader().setFont(new Font("Nunito", Font.PLAIN, 20));

        // set column sizes
        table.getColumnModel().getColumn(0).setPreferredWidth(400);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);

        // set row sizes
        table.setRowHeight(30);

        // right align columns
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(renderer);
        table.getColumnModel().getColumn(2).setCellRenderer(renderer);
        table.getColumnModel().getColumn(3).setCellRenderer(renderer);

        // panel to display table
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(table, BorderLayout.CENTER);

        // INPUT FIELDS 
        discountField = new InputField();
        ccNumField = new InputField();
        ccvField = new InputField();
        expDateField = new InputField();
        customerField = new InputField("search for customer account...");

        // LABELS
        FieldLabel discountLabel = new FieldLabel("Apply Discount Code");
        ccNumLabel = new FieldRequiredLabel("Credit Card Number");
        ccvLabel = new FieldRequiredLabel("CCV");
        expDateLabel = new FieldRequiredLabel("Expiration Date");
        customerLabel = new FieldRequiredLabel("Customer");

        // WARNINGS
        discountWarning = new WarningLabel("Invalid discount code.");

        // BUTTONS
        // checkout button
        orderBttn = new ButtonPrimary("Place Order");
        orderBttn.setBorderColor(Colors.GREEN);
        orderBttn.setBackground(Colors.MID_GREEN);
        orderBttn.setSelectedBorderColor(Colors.GREEN);
        orderBttn.setSelectedBackground(Colors.MID_GREEN);
        orderBttn.setPreferredSize(new Dimension(175, 54));
        // button nested inside of JPanel b/c when the button was added to the button panel, 
        // Box.createGlue() made the hitbox bigger 
        JPanel c = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        c.add(orderBttn);
        c.setMaximumSize(new Dimension((int) c.getPreferredSize().getWidth(), (int) c.getMaximumSize().getHeight()));
        c.setBackground(null);

        // back button
        backBttn = new ButtonPrimary("Go Back");
        backBttn.setBorderColor(Colors.GREEN);
        backBttn.setBackground(Colors.MID_GREEN);
        backBttn.setSelectedBorderColor(Colors.GREEN);
        backBttn.setSelectedBackground(Colors.MID_GREEN);
        backBttn.setPreferredSize(new Dimension(150, 54));
        // button nested inside of JPanel b/c when the button was added to the button panel, 
        // Box.createGlue() made the hitbox bigger 
        JPanel b = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        b.add(backBttn);
        b.setMaximumSize(new Dimension((int) b.getPreferredSize().getWidth(), (int) b.getMaximumSize().getHeight()));
        b.setBackground(null);

        //LINKS
        helpLink = new Link("Help");
        discountLink = new Link("Search Discounts");
        changeCustomerLink = new Link("Change");
        changeCustomerLink.setVisible(false);

        // HELP DIALOG
        setupHelpDialog();
        setupCustomerDialog();
        setupDiscountDialog();

        //----- GROUP COMPONENTS IN PANELS -----//
        // TITLE PANEL
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(null);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.add(Box.createHorizontalStrut(10)); // to make the title appear more centered
        titlePanel.add(Box.createGlue());
        titlePanel.add(title);
        titlePanel.add(Box.createGlue());
        titlePanel.add(helpLink);
        titlePanel.add(Box.createHorizontalStrut(10));
        titlePanel.setBorder(new MatteBorder(0, 0, 3, 0, Colors.SILVER));

        // BUTTON PANEL
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(b);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(c);
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new MatteBorder(3, 0, 0, 0, Colors.SILVER));

        // DISCOUNT PANEL
        JPanel discountPanel = new JPanel();
        discountPanel.setLayout(new BoxLayout(discountPanel, BoxLayout.Y_AXIS));
        discountPanel.setBackground(null);
        discountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        discountPanel.add(discountLabel);
        discountPanel.add(Box.createVerticalStrut(5));
        discountField.setAlignmentX(Component.LEFT_ALIGNMENT);
        discountPanel.add(discountField);
        discountPanel.add(Box.createVerticalStrut(5));

        // if an employee is using the checkout, add a link to search through discounts
        if (currentState.equals(State.getEmployeeState())) {
            discountPanel.add(discountLink);
            discountPanel.add(Box.createVerticalStrut(5));
        }

        discountPanel.add(discountWarning);
        discountPanel.add(Box.createVerticalStrut(5));

        // CREDIT CARD PANEL
        JPanel ccPanel = new JPanel();
        ccPanel.setLayout(new BoxLayout(ccPanel, BoxLayout.Y_AXIS));
        ccPanel.setBackground(null);
        ccNumLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ccPanel.add(ccNumLabel);
        ccPanel.add(Box.createVerticalStrut(5));
        ccNumField.setAlignmentX(Component.LEFT_ALIGNMENT);
        ccPanel.add(ccNumField);
        ccPanel.add(Box.createVerticalStrut(5));

        // CCV PANEL
        JPanel ccvPanel = new JPanel();
        ccvPanel.setLayout(new BoxLayout(ccvPanel, BoxLayout.Y_AXIS));
        ccvPanel.setBackground(null);
        ccvLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ccvPanel.add(ccvLabel);
        ccvPanel.add(Box.createVerticalStrut(5));
        ccvField.setAlignmentX(Component.LEFT_ALIGNMENT);
        ccvPanel.add(ccvField);
        ccvPanel.add(Box.createVerticalStrut(5));

        // EXP DATE PANEL
        JPanel expPanel = new JPanel();
        expPanel.setLayout(new BoxLayout(expPanel, BoxLayout.Y_AXIS));
        expPanel.setBackground(null);
        expDateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        expPanel.add(expDateLabel);
        expPanel.add(Box.createVerticalStrut(5));
        expDateField.setAlignmentX(Component.LEFT_ALIGNMENT);
        expPanel.add(expDateField);
        expPanel.add(Box.createVerticalStrut(5));

        //CCV & EXP PANEL
        JPanel ccvExpPanel = new JPanel();
        ccvExpPanel.setBackground(null);
        ccvExpPanel.setLayout(new BoxLayout(ccvExpPanel, BoxLayout.X_AXIS));
        ccvExpPanel.add(ccvPanel);
        ccvExpPanel.add(Box.createHorizontalStrut(20));
        ccvExpPanel.add(expPanel);

        // CUSTOMER PANEL (FOR POS USER ONLY)
        JPanel customerPanel = new JPanel();
        customerPanel.setBackground(null);
        customerPanel.setLayout(new BoxLayout(customerPanel, BoxLayout.Y_AXIS));
        customerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        customerPanel.add(customerLabel);
        customerPanel.add(Box.createVerticalStrut(5));
        customerField.setAlignmentX(Component.LEFT_ALIGNMENT);
        customerPanel.add(customerField);
        customerPanel.add(Box.createVerticalStrut(5));
        customerPanel.add(changeCustomerLink);
        customerPanel.add(Box.createVerticalStrut(5));

        //----- SIZE COMPONENTS -----//
        int width = frame.getWidth() - 100;
        int height = frame.getHeight() - 100;
        Dimension d = new Dimension(width, height);
        checkoutPanel.setPreferredSize(d);
        checkoutPanel.setMaximumSize(d);

        discountField.setWidth(400);
        ccNumField.setWidth(400);
        ccvField.setWidth(120);
        expDateField.setWidth(260);
        customerField.setWidth(400);
        ccvExpPanel.setPreferredSize(ccvExpPanel.getPreferredSize());

        //----- ADD TO CHECKOUT PANEL -----//
        // ADD TO FORM PANEL (Used GridBagLayout b/c BoxLayout was causing weird problems with JTextFields)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        formPanel.add(tablePanel, gbc);

        // checks current state because this state has not been entered yet
        if (currentState.equals(State.getEmployeeState())) {
            gbc.gridy += 1;
            formPanel.add(customerPanel, gbc);
        }

        if (!currentState.equals(State.getManagerState())) {
            gbc.gridy += 1;
            formPanel.add(discountPanel, gbc);
        }
        gbc.gridy += 1;
        formPanel.add(ccPanel, gbc);
        gbc.gridy += 1;
        formPanel.add(ccvExpPanel, gbc);

        // ADD TO CHECKOUT PANEL
        checkoutPanel.add(titlePanel, BorderLayout.NORTH);
        checkoutPanel.add(scroll, BorderLayout.CENTER);
        checkoutPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    //--------------------------------------------//
    //---------- ABSTRACT STATE METHODS ----------//
    //---------- ENTER STATE ----------//
    @Override
    protected void enterState() {
        State.frame.setTitle("Bizmart Checkout");
        State.frame.getContentPane().removeAll();

        // add checkout panel to frame
        // default GridBagConstraints centers panel in frame & respects sizing
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        State.frame.add(connectionStatus, gbc);
        gbc.gridy = 1;
        State.frame.add(checkoutPanel, gbc);

        // repaint the frame
        State.frame.repaint();
        State.frame.setVisible(true);

        // request focus
        scroll.requestFocusInWindow();
    }

    //---------- ADD EVENT LISTENERS TO COMPONENTS ----------//
    @Override
    protected void addEventListeners() {
        //----- BACK BUTTON -----//
        AbstractAction backPress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentUser.getPosition() == User.CUSTOMER) {
                    changeState(State.cart);
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

        //----- CHECKOUT BUTTON -----//
        AbstractAction checkoutPress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitForm();
            }
        };
        orderBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        orderBttn.getActionMap().put("pressed", checkoutPress);

        orderBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                orderBttn.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                orderBttn.mouseReleased();
                submitForm();
            }
        });

        //----- CCV FIELD -----//
        ccvField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar()) || ccvField.getText().length() >= 3) {
                    e.consume();
                }
            }
        });

        //----- CC NUM FIELD -----//
        ccNumField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar()) || ccNumField.getText().length() >= 16) {
                    e.consume();
                }
            }
        });

        //----- EXP DATE FIELD -----//
        expDateField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar()) && e.getKeyChar() != '/' || expDateField.getText().length() >= 7) {
                    e.consume();
                }
            }
        });

        //----- DISCOUNT FIELD -----//
        discountField.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                validateDiscountCode();
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
        });

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

        //----- CUSTOMER FIELD -----//
        Action customerPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = customerField.getText();
                    String[] words = text.trim().split(" ");
                    ResultSet rs;
                    if (words.length > 1) {
                        rs = con.getCustomer(words[0], words[1]);
                    } else {
                        rs = con.getCustomer(text);
                    }

                    if (rs.isBeforeFirst()) {
                        customerLabel.removeWarning();
                        DefaultListModel model = (DefaultListModel) customerList.getModel();
                        model.clear();
                        foundCustomers = new ArrayList<>();
                        int i = 0;
                        while (rs.next()) {
                            foundCustomers.add(i, new Person(rs.getInt(1), rs.getString(2), rs.getString(4), rs.getString(6), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12)));
                            model.add(i, rs.getString(2) + " " + rs.getString(4) + ", " + rs.getString(12));
                        }
                        customerDialog.setVisible(true);
                    } else {
                        customerLabel.addWarning("Customer not found.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        customerField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        customerField.getActionMap().put("pressed", customerPress);

        //----- CHANGE CUSTOMER LINK -----//
        AbstractAction changePress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customerField.setEditable(true);
                customerField.setText("");
                customerField.requestFocus();
                changeCustomerLink.setVisible(false);
                currentCustomer = null;
            }
        };
        changeCustomerLink.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        changeCustomerLink.getActionMap().put("pressed", changePress);

        changeCustomerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                changePress.actionPerformed(new ActionEvent(changeCustomerLink, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- DISCOUNT LINK -----//
        AbstractAction discountPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                getActiveDiscounts();
            }
        };
        discountLink.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        discountLink.getActionMap().put("pressed", discountPress);

        discountLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                getActiveDiscounts();
            }
        });
    }

    //--------------------------------------------//
    //---------- CHECKOUT STATE METHODS ----------//
    /**
     * Calculates the discount based on the amount, type, and level of the
     * discount. Recalculates subtotal with discount, and adds the discount and
     * new subtotal to the JTable table.
     *
     * @param d the discount amount
     * @param type the type of discount, either percentage or dollar amount
     * @param level the discount level, either cart or item
     */
    public void addDiscount(double d, int type, int level) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        // remove rows
        model.setRowCount(0);

        // add items again
        for (String[] s : items) {
            model.addRow(s);
        }

        // calculate new totals
        totals = new ArrayList<>();
        totals.add(new String[]{"", "", "Subtotal", "$" + Money.DF.format(subtotal)});

        double newSubtotal;
        if (type == Discount.PERCENT_TYPE) {
            double discount;
            if (level == Discount.CART_LEVEL) {
                discount = subtotal * d;
            } else {
                discount = itemPrice * d;
            }
            newSubtotal = subtotal - discount;
            totals.add(new String[]{"", "Discount", Math.round(d * 100) + "%", "-($" + Money.DF.format(discount) + ")"});
            totals.add(new String[]{"", "", "Discounted Subtotal", "$" + Money.DF.format(newSubtotal)});
        } else {
            newSubtotal = subtotal - d;
            totals.add(new String[]{"", "Discount", "$" + Money.DF.format(d), "-($" + Money.DF.format(d) + ")"});
            totals.add(new String[]{"", "", "Discounted Subtotal", "$" + Money.DF.format(newSubtotal)});
        }

        tax = newSubtotal * .0825;
        totals.add(new String[]{"", "", "Tax (8.25%)", "$" + Money.DF.format(tax)});
        totals.add(new String[]{"", "", "Total", "$" + Money.DF.format(newSubtotal + tax)});
        total = newSubtotal + tax;

        // add new rows
        int size = totals.size();
        for (int i = 0; i < size; i++) {
            model.addRow(totals.get(i));
        }
    }

    /**
     * Validates the credit card information entered, and checks if a customer
     * was added if the previous state was employee. If information is valid,
     * the order is placed and a receipt is generated. If information is
     * invalid, warnings are displayed.
     */
    protected void submitForm() {
        String ccv = ccvField.getText();
        String ccNum = ccNumField.getText();
        String expDate = expDateField.getText();

        int validFields = 0;
        int requiredValid = 3;

        if (previousState.equals(State.getEmployeeState())) {
            requiredValid++;
        }

        if (ccv.equals("")) {
            ccvLabel.addWarning("Enter a 3-digit CCV code.");
        } else {
            if (ccv.length() == 3) {
                validFields++;
                ccvLabel.removeWarning();
            } else {
                ccvLabel.addWarning("CCV code must be 3-digits long.");
            }
        }
        if (ccNum.equals("")) {
            ccNumLabel.addWarning("Enter a 16-digit credit card number.");
        } else {
            if (ccNum.length() == 16) {
                validFields++;
                ccNumLabel.removeWarning();
            } else {
                ccNumLabel.addWarning("Credit card must be 16-digits long.");
            }
        }
        if (expDate.equals("")) {
            expDateLabel.addWarning("Enter an expiration date in the format MM/YY or MM/YYYY");
        } else {
            if (Validation.isValidDate(expDate)) {
                YearMonth ym;
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/yy");
                DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("MM/yyyy");
                try {
                    ym = YearMonth.parse(expDate, dtf);
                } catch (Exception ex) {
                    ym = YearMonth.parse(expDate, dtf2);
                }

                if (ym.isBefore(YearMonth.now())) {
                    expDateLabel.addWarning("Card is expired.");
                } else if (ym.isAfter(YearMonth.now().plusYears(5))) {
                    expDateLabel.addWarning("Invalid expiration date. Must be within 5 years from now.");
                } else {
                    validFields++;
                    expDateLabel.removeWarning();
                }
            } else {
                expDateLabel.addWarning("Expiration date must be in the format MM/YY or MM/YYYY");
            }
        }

        if (previousState.equals(State.getEmployeeState())) {
            // check if currentCustomer is null
            if (currentCustomer == null) {
                customerLabel.addWarning("Customer must be added to order.");
            } else {
                validFields++;
                customerLabel.removeWarning();
            }
        }
        if (validFields == requiredValid) {
            // add the order to the database
            Order order;
            if (State.currentUser.getPosition() == User.CUSTOMER) {
                order = new Order(State.currentUser.getPersonID(), LocalDate.now(), Long.parseLong(ccNum), expDate, Integer.parseInt(ccv), total);
            } else if (State.previousState.equals(State.getEmployeeState())) {
                order = new Order(State.currentCustomer.getPersonID(), LocalDate.now(), Long.parseLong(ccNum), expDate, Integer.parseInt(ccv), total);
                order.setEmployeeID(State.currentUser.getPersonID());
            } else {
                StoreOrder sOrder = new StoreOrder(State.currentUser.getPersonID(), total, LocalDate.now(), Long.parseLong(ccNum), expDate, Integer.parseInt(ccv));
                try {
                    con.insertOrder(sOrder);
                    // upload new order details for each inventory item in the cart
                    for (int[] arr : inventoryItems) {
                        OrderDetails details = new OrderDetails(arr[0], arr[1]);
                        con.insertOrderDetails(details, sOrder);
                    }
                    HTML.createReceipt((Checkout) State.getCheckoutState(), con.getStoreOrderNumber());
                    ((Manager) State.manager).clearCart();
                    ((Manager) State.manager).showPanel(Manager.OPTIONS);
                    changeState(State.manager);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            if (discountID != 0 && isCartLevel) {
                order.setDiscountID(discountID);
                discountID = 0;
            }

            try {
                con.insertOrder(order);
                // upload new order details for each inventory item in the cart
                for (int[] arr : inventoryItems) {
                    OrderDetails details = new OrderDetails(arr[0], arr[1]);
                    if (discountID != 0 && !isCartLevel) {
                        if (discountItemID == arr[0]) {
                            details.setDiscountID(discountID);
                            discountID = 0;
                        }
                    }
                    con.insertOrderDetails(details, order);
                    con.subtractInventory(details);
                }
                HTML.createReceipt((Checkout) State.getCheckoutState(), con.getOrderNumber());
                if (currentUser.getPosition() == User.CUSTOMER) {
                    getCartState().clearCart();
                    changeState(State.customer);
                } else {
                    ((Employee) State.employee).clearCart();
                    changeState(State.employee);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets active discounts and adds them to discountList, then shows
     * discountDialog.
     */
    private void getActiveDiscounts() {
        try {
            DefaultListModel model = (DefaultListModel) discountList.getModel();
            model.clear();
            ResultSet rs = con.getActiveDiscounts();
            int i = 0;
            while (rs.next()) {
                model.add(i, rs.getString(1) + " | " + rs.getString(2));
                i++;
            }
            discountDialog.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Validates the discount code typed into discountField.
     */
    private void validateDiscountCode() {
        // IF A DISCOUNT CODE WAS ENTERED, CHECK IF ITS VALID FOR THIS TRANSACTION
        if (!discountField.getText().equals("")) {
            try {
                ResultSet rs = con.checkDiscountCode(discountField.getText());
                // CHECK IF THE DISCOUNT CODE EXISTS AND IS NOT DISABLED
                if (rs.isBeforeFirst()) {
                    discountWarning.setVisible(false);
                    rs.next();
                    // CHECK IF THE DISCOUNT CODE IS WITHIN THE VALID DATE RANGE
                    String start = rs.getString(8);
                    LocalDate endDate = LocalDate.parse(rs.getString(9));

                    // if there is a start date, ensure the current date is after start date
                    if (start != null) {
                        LocalDate startDate = LocalDate.parse(start);
                        if (startDate.isAfter(LocalDate.now())) {
                            discountWarning.setText("Code doesn't start until " + rs.getString(8) + ".");
                            discountWarning.setVisible(true);
                            discountID = 0;
                            discountItemID = 0;
                            return;
                        }
                    }

                    // check if the current date is after the expiration date
                    if (!endDate.isAfter(LocalDate.now())) {
                        discountWarning.setText("Code ended " + rs.getString(9) + ".");
                        discountWarning.setVisible(true);
                        discountID = 0;
                        discountItemID = 0;
                        return;
                    }

                    // IF DISCOUNT CODE IS WITHIN VALID DATE, CHECK WHAT LEVEL IT IS (CART OR ITEM)
                    int type = rs.getInt(5);
                    int level = rs.getInt(3);
                    discountID = rs.getInt(1);
                    if (level == Discount.CART_LEVEL) {
                        // CART LEVEL DISCOUNT
                        double discount;
                        // CHECK DISCOUNT TYPE
                        if (type == Discount.PERCENT_TYPE) {
                            discount = rs.getDouble(6);
                        } else {
                            discount = rs.getInt(7);
                        }
                        // add the discount to the transaction
                        addDiscount(discount, type, level);
                        discountItemID = 0;
                        isCartLevel = true;
                    } else {
                        // ITEM LEVEL DISCOUNT
                        int itemNum = rs.getInt(4);
                        if (itemNum != 0) {
                            boolean inCart = false;
                            //TODO: Move .getCart() to a variable outside of the loop
                            if (currentUser.getPosition() == User.CUSTOMER) {
                                for (CartItem ci : State.getCartState().getCart()) {
                                    if (ci.getInventoryItem().getItemNumber() == itemNum) {
                                        inCart = true;
                                        break;
                                    }
                                }
                            } else {
                                for (CartItem ci : State.getEmployeeState().getCart()) {
                                    if (ci.getInventoryItem().getItemNumber() == itemNum) {
                                        inCart = true;
                                        break;
                                    }
                                }
                            }

                            double discount;
                            if (type == Discount.PERCENT_TYPE) {
                                discount = rs.getDouble(6);
                            } else {
                                discount = rs.getDouble(7);
                            }

                            if (inCart) {
                                discountWarning.setVisible(false);
                                ResultSet itemRs = con.getItemPrice(itemNum);
                                itemRs.next();
                                itemPrice = itemRs.getDouble(1);
                                addDiscount(discount, type, level);
                                discountItemID = itemNum;
                                isCartLevel = false;
                            } else {
                                discountWarning.setText("Add item " + itemNum + " to use this code.");
                                discountWarning.setVisible(true);
                                discountID = 0;
                                discountItemID = 0;
                            }
                        } else {
                            discountWarning.setText("Invalid discount code.");
                            discountWarning.setVisible(true);
                            discountID = 0;
                            discountItemID = 0;
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Gets the line items displayed in the JTable table. Used by HTML class to
     * generate a receipt.
     *
     * @return the ArrayList items
     */
    public ArrayList<String[]> getCartItems() {
        return items;
    }

    /**
     * Gets the totals displayed in the JTable table. Used by HTML class to
     * generate a receipt.
     *
     * @return the ArrayList totals
     */
    public ArrayList<String[]> getCartTotals() {
        return totals;
    }

    /**
     * Initialize and place components in customerDialog. Add event listeners to
     * customerDialog components.
     */
    private void setupCustomerDialog() {
        customerDialog = new PopupDialog(State.frame);
        customerDialog.setTitle("Customer Accounts");
        customerDialog.setLayout(new GridBagLayout());
        customerDialog.setResizable(false);
        customerDialog.getContentPane().setBackground(Colors.SILVER);

        //jlist && scrollpane
        customerList = new JList(new DefaultListModel());
        customerList.setFont(new Font("Nunito", Font.PLAIN, 18));
        customerList.setForeground(Colors.RICH_BLACK);
        customerScroll = new JScrollPane(customerList);
        customerScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        customerScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        customerScroll.getVerticalScrollBar().setUnitIncrement(16);
        customerScroll.getViewport().setBackground(Color.WHITE);
        customerScroll.setBorder(null);
        customerScroll.setPreferredSize(new Dimension(600, 200));

        //add customer button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        addCustomerBttn = new ButtonPrimary("Add Selected Customer");
        addCustomerBttn.setPreferredSize(new Dimension(350, 54));
        addCustomerBttn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(Color.WHITE);
        panel.add(Box.createHorizontalStrut(123));
        panel.add(addCustomerBttn);
        panel.add(Box.createHorizontalStrut(123));

        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new MatteBorder(4, 0, 0, 0, Colors.SILVER));
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(panel);
        buttonPanel.add(Box.createVerticalStrut(5));
        // title
        FormTitle title = new FormTitle("Accounts Found", 600, 4);
        title.setUnderlineColor(Colors.SILVER);

        // add components to dialog
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        customerDialog.add(title, gbc);
        gbc.gridy = 1;
        customerDialog.add(customerScroll, gbc);
        gbc.gridy = 2;
        customerDialog.add(buttonPanel, gbc);

        customerDialog.setSize(new Dimension(700, 450));
        customerDialog.setLocationRelativeTo(null);

        // ADD CUSTOMER BUTTON EVENT LISTENERS
        addCustomerBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                addCustomerBttn.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                addCustomerBttn.mouseReleased();
                if (customerList.getSelectedValue() != null) {
                    String name = customerList.getSelectedValue().toString();
                    String[] arr = name.split(",");
                    customerField.setText(arr[0]);
                    customerField.setEditable(false);
                    currentCustomer = foundCustomers.get(customerList.getSelectedIndex());
                    changeCustomerLink.setVisible(true);
                    customerDialog.dispose();
                }
            }
        });

        AbstractAction addPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (customerList.getSelectedValue() != null) {
                    String name = customerList.getSelectedValue().toString();
                    String[] arr = name.split(",");
                    customerField.setText(arr[0]);
                    customerField.setEditable(false);
                    currentCustomer = foundCustomers.get(customerList.getSelectedIndex());
                    changeCustomerLink.setVisible(true);
                    customerDialog.dispose();
                }
            }
        };
        addCustomerBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        addCustomerBttn.getActionMap().put("pressed", addPress);
    }

    /**
     * Initialize and place components in discountDialog. Add event listeners to
     * discountDialog components.
     */
    private void setupDiscountDialog() {
        discountDialog = new PopupDialog(State.frame);
        discountDialog.setTitle("Customer Accounts");
        discountDialog.setLayout(new GridBagLayout());
        discountDialog.setResizable(false);
        discountDialog.getContentPane().setBackground(Colors.SILVER);

        //jlist && scrollpane
        discountList = new JList(new DefaultListModel());
        discountList.setFont(new Font("Nunito", Font.PLAIN, 18));
        discountList.setForeground(Colors.RICH_BLACK);
        discountScroll = new JScrollPane(discountList);
        discountScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        discountScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        discountScroll.getVerticalScrollBar().setUnitIncrement(16);
        discountScroll.getViewport().setBackground(Color.WHITE);
        discountScroll.setBorder(null);
        discountScroll.setPreferredSize(new Dimension(600, 200));

        //add customer button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        addDiscountBttn = new ButtonPrimary("Add Selected Discount");
        addDiscountBttn.setPreferredSize(new Dimension(350, 54));
        addDiscountBttn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(Color.WHITE);
        panel.add(Box.createHorizontalStrut(123));
        panel.add(addDiscountBttn);
        panel.add(Box.createHorizontalStrut(123));

        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new MatteBorder(4, 0, 0, 0, Colors.SILVER));
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(panel);
        buttonPanel.add(Box.createVerticalStrut(5));

        // title
        FormTitle title = new FormTitle("Active Discounts", 600, 4);
        title.setUnderlineColor(Colors.SILVER);

        // add components to dialog
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        discountDialog.add(title, gbc);
        gbc.gridy = 1;
        discountDialog.add(discountScroll, gbc);
        gbc.gridy = 2;
        discountDialog.add(buttonPanel, gbc);

        discountDialog.setSize(new Dimension(700, 450));
        discountDialog.setLocationRelativeTo(null);

        // ADD DISCOUNT BUTTON EVENT LISTENERS
        addDiscountBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                addDiscountBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                if (discountList.getSelectedValue() != null) {
                    addDiscountBttn.mouseReleased();
                    String selected = discountList.getSelectedValue().toString();
                    String[] discount = selected.split(" ");
                    discountField.setText(discount[0]);
                    validateDiscountCode();
                    discountDialog.dispose();
                }
            }
        });

        AbstractAction discountBttnPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (discountList.getSelectedValue() != null) {
                    addDiscountBttn.mouseReleased();
                    String selected = discountList.getSelectedValue().toString();
                    String[] discount = selected.split(" ");
                    discountField.setText(discount[0]);
                    validateDiscountCode();
                    discountDialog.dispose();
                }
            }
        };
        addDiscountBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        addDiscountBttn.getActionMap().put("pressed", discountBttnPress);
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
        // (0, 0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(new BizmartLabel("Checkout Help"), gbc);
        // (0, 1)
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(new BizmartLabel("Enter your credit card information to order."), gbc);
        // (0, 2)
        gbc.gridy = 2;
        panel.add(new BizmartLabel("If you know a promo-code, add it for a discount."), gbc);
        // (0, 3)
        gbc.gridy = 3;
        panel.add(new BizmartLabel("Go back before submitting payment to change your order."), gbc);
        // (0, 4)
        gbc.gridy = 4;
        panel.add(new BizmartLabel("Once the payment goes through, a receipt will be provided."), gbc);

        helpDialog.add(panel, new GridBagConstraints());
        helpDialog.setSize(new Dimension(700, 450));
        panel.setPreferredSize(new Dimension(630, 350));
        helpDialog.setLocationRelativeTo(null);
    }
}
