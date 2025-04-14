package statemachine;

import component.labels.*;
import component.buttons.*;
import component.*;
import component.panels.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import component.fields.InputField;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.border.MatteBorder;
import static statemachine.State.con;
import static statemachine.State.currentState;
import util.*;
import java.time.LocalDate;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import component.labels.WarningLabel;
import component.panels.CartItem;
import java.util.*;
import java.sql.SQLException;

/**
 * Subclass Manager is an interface for business management, and is accessible
 * only to signed-in users with an account type of "manager". This state can
 * move into the states employee, login, item, and checkout.
 * <p>
 * The Manager class can be used to edit or create inventory items, edit or
 * create accounts of any type (customer, employee, or manager), edit or create
 * discount codes, and place and receive store orders to restock inventory.
 */
public class Manager extends State {

    /**
     * String constants used with the showPanel() method to change the JPanel
     * being displayed in cardPanel.
     */
    public static final String OPTIONS = "options", EDIT_ITEM = "edit-item", NEW_ITEM = "add-new-item", NEW_PERSON = "add-new-person",
            EDIT_PERSON = "edit-person", SEARCH_PERSON = "search-person", NEW_DISCOUNT = "add-new-discount", EDIT_DISCOUNT = "edit-discount",
            REPORT = "report", SEARCH_DISCOUNT = "search-discount", SEARCH_ORDER = "search-order", PLACE_ORDER = "place-order",
            MANAGE_ORDER = "manage-order";

    //---------- MAIN JPANELS ----------//
    /**
     * The root JPanel for the manager state that is placed in the JFrame when
     * this state is entered. Directly contains the cardPanel and menuPanel
     * JPanels. Uses a BorderLayout.
     *
     */
    private DropShadowPanel managerPanel;

    /**
     * A JPanel with a CardLayout used to display JPanels for each interface of
     * the manager state. Displayed in the CENTER area of managerPanel.
     */
    private JPanel cardPanel;

    /**
     * A JPanel that contains a back and logout button. Displayed in the NORTH
     * area of managerPanel so it is visible no matter what card of the
     * cardPanel is displayed.
     */
    private JPanel menuPanel;

    //---------- JPANELS FOR EACH CARD IN THE CARD LAYOUT ----------//
    /**
     * A card in cardPanel that contains buttons to navigate to all other card
     * JPanels. Show with the OPTIONS constant.
     */
    private JPanel optionPanel;

    /**
     * A card in cardPanel that allows the user to edit inventory item
     * information. Show with the EDIT_ITEM constant.
     */
    private JPanel editItemPanel;

    /**
     * A card in cardPanel that allows the user to create a new inventory item.
     * Show with the NEW_ITEM constant.
     */
    private JPanel newItemPanel;

    /**
     * A card in cardPanel that allows the user to create a new user account.
     * Show with the NEW_PERSON constant.
     */
    private JPanel newPersonPanel;

    /**
     * A card in cardPanel that allows the user to search for an existing
     * account to edit. Show with the SEARCH_PERSON constant.
     */
    private JPanel searchPersonPanel;

    /**
     * A card in cardPanel that allows the user to edit the account selected in
     * searchPersonPanel. Show with the EDIT_PERSON constant. This panel can
     * only be accessed from searchPersonPanel.
     */
    private JPanel editPersonPanel;

    /**
     * A card in cardPanel that allows the user to create a new promo
     * code/discount code. Show with the NEW_DISCOUNT constant.
     */
    private JPanel newDiscountPanel;

    /**
     * A card in cardPanel that allows the user to search for an existing
     * discount to edit. Show with the SEARCH_DISCOUNT constant.
     */
    private JPanel searchDiscountPanel;

    /**
     * A card in cardPanel allows the user to edit the discount selected in
     * searchDiscountPanel. Show with the EDIT_DISCOUNT constant. This panel can
     * only be accessed from searchDiscountPanel.
     */
    private JPanel editDiscountPanel;

    /**
     * A card in cardPanel that allows the user to place a store order to
     * restock inventory. Show with the PLACE_ORDER constant.
     */
    private JPanel placeOrderPanel;

    /**
     * A card in cardPanel that allows the user to search for existing orders in
     * order to received them. Show with the SEARCH_ORDER constant.
     */
    private JPanel searchOrderPanel;

    /**
     * A card in cardPanel that allows the user to receive (or view) the order
     * selected in searchOrderPanel. Show with the MANAGE_ORDER constant. This
     * panel can only be accessed from searchOrderPanel.
     */
    private JPanel manageOrderPanel;

    /**
     * A card in cardPanel that allows the user to generate HTML sales reports
     * (daily, weekly and/or monthly) and inventory reports (what needs to be
     * restocked, what's available, and what's discontinued). Show with the
     * REPORT constant.
     */
    private JPanel reportPanel;

    //---------- OPTION PANEL COMPONENTS ----------//
    /**
     * Button that changes the displayed JPanel in cardPanel when pressed.
     * <p>
     * optionPanel component.
     */
    private ButtonPrimary editItemBttn, addNewItemBttn, newPersonBttn, editPersonBttn,
            newDiscountBttn, editDiscountBttn, reportBttn, posBttn,
            searchOrderBttn, placeOrderBttn;

    /**
     * Shows restockDialog when pressed. Only visible if there are inventory
     * items below their restock threshold.
     * <p>
     * optionPanel component.
     *
     */
    private Link showRestockLink;

    //---------- DIALOG BOXES ----------//
    /**
     * A JDialog help menu that has information relevant to the JPanel that is
     * displayed in cardPanel.
     */
    private PopupDialog helpDialog;

    /**
     * A JPanel with a CardLayout that allows helpDialog to swap information
     * based on the JPanel displayed in cardPanel.
     * <p>
     * helpDialog component.
     */
    private JPanel helpCardPanel;

    /**
     * A JDialog menu that that displays the items that need to be restocked.
     * Automatically shown when the manager state is first entered, unless no
     * items are below their restock threshold.
     */
    private JDialog restockDialog;

    //---------- MENU PANEL COMPONENTS ----------//
    /**
     * Button that signs user out of their account and changes state to login
     * state when pressed.
     * <p>
     * menuPanel component.
     */
    private ButtonPrimary logoutBttn;

    /**
     * Button that displays the optionPanel in cardPanel when pressed. Visible
     * only when cardPanel isn't displaying optionPanel.
     * <p>
     * menuPanel component.
     */
    private ButtonPrimary backBttn;

    /**
     * Shows helpDialog when pressed.
     * <p>
     * menuPanel component.
     */
    private Link showHelpLink;

    //---------- EDIT ITEM PANEL COMPONENTS ----------//
    /**
     * Displays items to be edited. When one is selected, the state changes to a
     * new ItemEdit state where the selected item can be edited.
     * <p>
     * editItemPanel component.
     */
    private ItemScrollPane itemScroll;

    /**
     * JComboBox to sort items in itemScroll by category.
     * <p>
     * editItemPanel component.
     */
    private JComboBox categoriesComboBox;

    /**
     * JComboBox to sort items in itemScroll by subcategory. Only visible if the
     * category chosen in categoriesComboBox has a subcategory.
     * <p>
     * editItemPanel component.
     */
    private JComboBox subcategoriesComboBox;

    /**
     * InputField to search for items by number, keyword, or description.
     * <p>
     * editItemPanel component.
     */
    private InputField itemSearchField;

    //---------- SEARCH PERSON PANEL COMPONENTS ----------//
    /**
     * ArrayList of People objects that represent all accounts found in
     * searchPersonPanel.
     * <p>
     * searchPersonPanel variable.
     */
    private ArrayList<Person> foundAccountsArray;

    /**
     * FieldLabel for personSearchField. Can display a warning when no accounts
     * are found after executing a search.
     * <p>
     * searchPersonPanel component.
     */
    private FieldLabel personSearchLabel;

    /**
     * InputField to search for accounts to be edited by the account holder's
     * name, email, or phone number.
     * <p>
     * searchPersonPanel component.
     */
    private InputField personSearchField;

    /**
     * JPanel that displays a list of found accounts. Only visible after a
     * successful search.
     * <p>
     * searchPersonPanel component.
     */
    private JPanel foundAccountPanel;

    /**
     * Selectable JList of found accounts.
     * <p>
     * searchPersonPanel component.
     */
    private JList accountList;

    /**
     * JScrollPane for accountList.
     * <p>
     * searchPersonPanel component.
     */
    private JScrollPane accountScroll;

    /**
     * Button that changes panel displayed in cardPanel to editPersonPanel when
     * an account is selected in accountList.
     * <p>
     * searchPersonPanel component.
     */
    private ButtonPrimary editAccountBttn;

    //---------- REPORT PANEL COMPONENTS ----------//
    /**
     * ArrayList of People objects that represent all customer accounts found in
     * reportPanel when generating customer sales reports.
     * <p>
     * reportPanel variable.
     */
    private ArrayList<Person> foundCustomersArray;

    /**
     * JComboBox to specify the subtype of report to generate. Options change
     * based on the overall report type selected.
     * <p>
     * reportPanel component.
     */
    private JComboBox<String> reportSubTypeComboBox;

    /**
     * Used when generating sales reports by date to select the desired date and
     * ensure it's valid.
     * <p>
     * reportPanel component.
     */
    private DatePicker datePicker;

    /**
     * FieldLabel for customerField. Displays a warning when no accounts are
     * found after executing a search.
     * <p>
     * reportPanel component.
     */
    private FieldLabel customerLabel;

    /**
     * InputField to search for customer accounts by name, email, or phone
     * number. Displays customerDialog that shows found accounts in when a
     * search is successful.
     * <p>
     * reportPanel component.
     */
    private InputField customerField;

    /**
     * Selectable JList of found customers which is displayed in customerDialog.
     * <p>
     * reportPanel component.
     *
     */
    private JList customerList;

    /**
     * JScrollPane that displays customerList in customerDialog.
     * <p>
     * reportPanel component.
     */
    private JScrollPane customerScroll;

    /**
     * JDialog that displays list of found customers after searching in
     * reportPanel.
     * <p>
     * reportPanel component.
     */
    private PopupDialog customerDialog;

    /**
     * Button found in customerDialog that generates a report on the selected
     * customer in customerList when pressed.
     * <p>
     * reportPanel component.
     */
    private ButtonPrimary generateCustomerReportBttn;

    //---------- PLACE STORE ORDER PANEL COMPONENTS ----------//
    /**
     * ArrayList representing the cart of a store order.
     * <p>
     * placeOrderPanel variable.
     */
    private ArrayList<CartItem> cartArray;

    /**
     * InputField used to add items to the cart of a store order. Items can only
     * be added by their item number.
     * <p>
     * placeOrderPanel component.
     */
    private InputField skuField;

    /**
     * JPanel that displays each CartItem added to the store order.
     * <p>
     * placeOrderPanel component.
     */
    private JPanel cartScrollPanel;

    /**
     * JScrollPane that displays cartScrollPanel.
     * <p>
     * placeOrderPanel component.
     */
    private JScrollPane cartScroll;

    /**
     * JLabel that displays the estimated cost of the order. Label text is
     * updated every time an item is added or removed from the cart, or when an
     * item's quantity changes.
     * <p>
     * placeOrderPanel component.
     */
    private BizmartLabel orderTotalLabel;

    //---------- SEARCH STORE ORDER PANEL COMPONENTS ----------//
    /**
     * ArrayList that stores the order number and order date of store orders to
     * be displayed in orderList.
     * <p>
     * searchOrderPanel variable.
     */
    private ArrayList<String> storeOrderArray;

    /**
     * Marks whether an order has been received or not when it's search by order
     * number. The value of this variable determines if the order is editable in
     * the manageOrderPanel to prevent orders from being received twice.
     * <p>
     * searchOrderPanel variable.
     */
    private boolean orderReceived;
    /**
     * JScrollPane to display orderList JList.
     * <p>
     * searchOrderpanel component.
     */
    private JScrollPane searchOrderScroll;

    /**
     * Selectable JList that contains all unreceived store orders.
     * <p>
     * searchOrderPanel component.
     */
    private JList orderList;

    //---------- MANAGE STORE ORDER PANEL COMPONENTS ----------//
    /**
     * StoreOrder object that allows an item to be marked as received in the
     * database.
     * <p>
     * manageOrderPanel variable.
     */
    private StoreOrder storeOrder;

    /**
     * ArrayList of OrderDetails that allows each item to be marked received in
     * the database.
     * <p>
     * manageOrderPanel variable.
     */
    private ArrayList<OrderDetails> orderDetailsArray;

    /**
     * ArrayList of OrderItems that represents an item's summary in an existing
     * order. Used to mark how many of each item in the order were received.
     * <p>
     * manageOrderPanel variable.
     */
    private ArrayList<OrderItem> orderItemArray;

    /**
     * Label that displays the actual cost of the order, depending on the
     * quantity of each item received.
     * <p>
     * manageOrderPanel component.
     */
    private BizmartLabel totalChargedLabel;

    /**
     * Variable used to calculate the total charged for an order, including tax.
     * <p>
     * manageOrderPanel variable.
     */
    private double totalCharged;

    /**
     * Manager constructor initializes components needed for the manager state.
     */
    public Manager() {
        //----- INITIALIZE MAIN COMPONENTS -----//
        // MANAGER PANEL
        managerPanel = new DropShadowPanel(8, Color.WHITE, Colors.SILVER, Colors.DARK_BLUE);
        managerPanel.setLayout(new BorderLayout());

        // CARD PANEL
        cardPanel = new JPanel(new CardLayout());
        cardPanel.setBackground(null);

        // OPTION PANEL
        optionPanel = new JPanel();
        optionPanel.setBackground(null);
        optionPanel.setLayout(new GridBagLayout());

        // EDIT ITEM PANEL 
        editItemPanel = new JPanel();
        editItemPanel.setBackground(null);
        editItemPanel.setLayout(new BorderLayout());

        // NEW ITEM PANEL
        newItemPanel = new JPanel();
        newItemPanel.setBackground(Color.WHITE);
        newItemPanel.setLayout(new BorderLayout());

        // NEW PERSON PANEL
        newPersonPanel = new JPanel(new BorderLayout());
        newPersonPanel.setBackground(null);

        // EDIT PERSON PANEL
        editPersonPanel = new JPanel();
        editPersonPanel.setBackground(null);
        editPersonPanel.setLayout(new BorderLayout());

        // NEW DISCOUNT PANEL
        newDiscountPanel = new JPanel();
        newDiscountPanel.setBackground(null);
        newDiscountPanel.setLayout(new BorderLayout());

        // REPORT PANEL
        reportPanel = new JPanel();
        reportPanel.setBackground(null);
        reportPanel.setLayout(new GridBagLayout());

        // SEARCH PERSON PANEL
        searchPersonPanel = new JPanel();
        searchPersonPanel.setBackground(null);
        searchPersonPanel.setLayout(new GridBagLayout());

        // SEARCH DISCOUNT PANEL
        searchDiscountPanel = new JPanel();
        searchDiscountPanel.setLayout(new GridBagLayout());
        searchDiscountPanel.setBackground(null);

        // EDIT DISCOUNT PANEL
        editDiscountPanel = new JPanel();
        editDiscountPanel.setBackground(null);
        editDiscountPanel.setLayout(new BorderLayout());

        // SEARCH STORE ORDER PANEL
        searchOrderPanel = new JPanel();
        searchOrderPanel.setBackground(null);
        searchOrderPanel.setLayout(new BorderLayout());

        // PLACE STORE ORDER PANEL
        placeOrderPanel = new JPanel();
        placeOrderPanel.setBackground(null);
        placeOrderPanel.setLayout(new BoxLayout(placeOrderPanel, BoxLayout.X_AXIS));

        // MANAGE STORE ORDER PANEL
        manageOrderPanel = new JPanel();
        manageOrderPanel.setBackground(null);
        manageOrderPanel.setLayout(new BorderLayout());

        // OPTION PANEL BUTTONS
        editItemBttn = new ButtonPrimary("Manage Inventory");
        addNewItemBttn = new ButtonPrimary("Add New Product");
        newPersonBttn = new ButtonPrimary("Create New Account");
        editPersonBttn = new ButtonPrimary("Manage User Data");
        newDiscountBttn = new ButtonPrimary("Create Promo Code");
        reportBttn = new ButtonPrimary("Generate Reports");
        posBttn = new ButtonPrimary("POS System");
        logoutBttn = new ButtonPrimary("Logout");
        backBttn = new ButtonPrimary("Back");
        editDiscountBttn = new ButtonPrimary("Edit Promo Code");
        showHelpLink = new Link("Help");
        showRestockLink = new Link("Show Items Below Restock Threshold");
        placeOrderBttn = new ButtonPrimary("Order Products");
        searchOrderBttn = new ButtonPrimary("Receive Orders");

        //----- SETUP JPANELS -----//
        // MENU PANEL
        // logoutPanel jpanel adds padding around button so the focus outline isn't cut off
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 2));
        logoutPanel.setBackground(null);
        logoutPanel.add(logoutBttn);
        // flowlayout centers help link vertically better than boxlayout
        logoutPanel.add(showHelpLink);
        logoutPanel.setPreferredSize(logoutPanel.getPreferredSize());
        logoutPanel.setMaximumSize(logoutPanel.getPreferredSize());

        // backPanel jpanel used to add padding around button so focus outline isn't cut off
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        backPanel.setBackground(null);
        backPanel.add(backBttn);
        backPanel.setPreferredSize(backPanel.getPreferredSize());
        backPanel.setMaximumSize(backPanel.getPreferredSize());

        // add buttons to menu panel
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.X_AXIS));
        menuPanel.setBackground(null);
        menuPanel.add(Box.createHorizontalStrut(10));
        backPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        menuPanel.add(backPanel);
        menuPanel.add(Box.createGlue());
        logoutPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        menuPanel.add(logoutPanel);
        menuPanel.add(Box.createHorizontalStrut(10));

        // CARD PANELS
        setupOptionPanel();
        setupEditItemPanel();
        setupSearchPersonPanel();
        setupNewItemPanel();
        setupDiscountPanel();
        setupReportPanel();
        setupPlaceOrderPanel();

        // DIALOGS
        setupHelpDialog();
        setupCustomerDialog();

        //----- SIZE COMPONENTS -----//
        // MANAGER PANEL
        int width = frame.getWidth() - 100;
        int height = frame.getHeight() - 100;
        Dimension d = new Dimension(width, height);
        managerPanel.setPreferredSize(d);
        managerPanel.setMaximumSize(d);

        // OPTION PANEL BUTTONS
        d = new Dimension(300, 64);
        editItemBttn.setPreferredSize(d);
        addNewItemBttn.setPreferredSize(d);
        newPersonBttn.setPreferredSize(d);
        editPersonBttn.setPreferredSize(d);
        newDiscountBttn.setPreferredSize(d);
        reportBttn.setPreferredSize(d);
        posBttn.setPreferredSize(d);
        editDiscountBttn.setPreferredSize(d);
        placeOrderBttn.setPreferredSize(d);
        searchOrderBttn.setPreferredSize(d);

        // MENU PANEL BUTTONS
        // logout button
        logoutBttn.setPreferredSize(new Dimension(150, 54));
        logoutBttn.setMaximumSize(new Dimension(150, 54));
        // back button
        backBttn.setPreferredSize(new Dimension(150, 54));
        backBttn.setMaximumSize(new Dimension(125, 54));
        backBttn.setVisible(false);

        //----- ADD COMPONENTS TO MANAGER PANEL -----//
        // ADD CARDS TO CARD LAYOUT PANEL
        cardPanel.add(optionPanel, OPTIONS);
        cardPanel.add(editItemPanel, EDIT_ITEM);
        cardPanel.add(newItemPanel, NEW_ITEM);
        cardPanel.add(newPersonPanel, NEW_PERSON);
        cardPanel.add(editPersonPanel, EDIT_PERSON);
        cardPanel.add(newDiscountPanel, NEW_DISCOUNT);
        cardPanel.add(reportPanel, REPORT);
        cardPanel.add(searchPersonPanel, SEARCH_PERSON);
        cardPanel.add(searchDiscountPanel, SEARCH_DISCOUNT);
        cardPanel.add(editDiscountPanel, EDIT_DISCOUNT);
        cardPanel.add(placeOrderPanel, PLACE_ORDER);
        cardPanel.add(searchOrderPanel, SEARCH_ORDER);
        cardPanel.add(manageOrderPanel, MANAGE_ORDER);

        // ADD MENU PANEL && CARD LAYOUT PANEL TO MANAGER PANEL
        managerPanel.add(menuPanel, BorderLayout.NORTH);
        managerPanel.add(cardPanel, BorderLayout.CENTER);
    }

    //--------------------------------------------//
    //---------- ABSTRACT STATE METHODS ----------//
    //---------- ENTER STATE ----------//
    @Override
    protected void enterState() {
        // SET JFRAME TITLE && REMOVE ALL COMPONENTS
        State.frame.setTitle("Bizmart Management");
        State.frame.getContentPane().removeAll();

        // ADD MANAGER PANEL TO JFRAME
        // NOTE: default GridBagConstraints centers panel in frame && respects sizing
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        State.frame.add(connectionStatus, gbc);
        gbc.gridy++;
        State.frame.add(managerPanel, gbc);

        // REPAINT THE JFRAME
        State.frame.repaint();
        State.frame.setVisible(true);

        // REQUEST FOCUS IN JFRAME
        editItemBttn.requestFocusInWindow();

        // IF THE USER JUST LOGGED IN, SHOW THE RESTOCK DIALOG (IF NEEDED)
        if (previousState == State.login) {
            showPanel(OPTIONS);
            try {
                ResultSet rs = con.getRestockItems();
                if (rs.isBeforeFirst()) {
                    // setup restock dialog
                    restockDialog = new JDialog(State.frame);
                    restockDialog.setSize(new Dimension(700, 350));
                    restockDialog.setLayout(new BorderLayout());
                    restockDialog.setTitle("Item Restock");
                    restockDialog.getContentPane().setBackground(Color.WHITE);
                    restockDialog.setLocationRelativeTo(null);

                    // jpanel && scrollpane to display names of items that need to be restocked
                    JScrollPane scroll = new JScrollPane();
                    scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                    JPanel panel = new JPanel();
                    panel.setBackground(null);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                    // add each item to the panel
                    restockDialog.add(new FormTitle("Items Below Restock Threshold"), BorderLayout.NORTH);
                    while (rs.next()) {
                        panel.add(new BizmartLabel(rs.getString(1) + " | " + rs.getString(2)));
                    }

                    // add the panel to the scrollpane
                    scroll.setViewportView(panel);

                    // add the scrollpane to the restock dialog && then show it
                    restockDialog.add(scroll, BorderLayout.CENTER);
                    restockDialog.setVisible(true);
                    showRestockLink.setVisible(true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //---------- ADD EVENT LISTENERS TO COMPONENTS ----------//
    @Override
    protected void addEventListeners() {
        //----- BACK BUTTON -----//
        Action backPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                clearAccountSearch();
                clearCart();
                backBttn.setVisible(false);
                showPanel(OPTIONS);
            }
        };
        backBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        backBttn.getActionMap().put("pressed", backPress);

        backBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                backBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                backBttn.mouseReleased();
                // call actionPerformed to execute the backPress Action
                backPress.actionPerformed(new ActionEvent(backBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- LOGOUT BUTON -----//
        Action logoutPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                backBttn.setVisible(false);
                State.currentUser = null;
                changeState(State.login);
            }
        };
        logoutBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        logoutBttn.getActionMap().put("pressed", logoutPress);

        logoutBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                logoutBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                logoutBttn.mouseReleased();
                logoutPress.actionPerformed(new ActionEvent(logoutBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- ADD NEW ITEM BUTTON -----//
        Action newItemPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                backBttn.setVisible(true);
                showPanel(NEW_ITEM);
            }
        };
        addNewItemBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        addNewItemBttn.getActionMap().put("pressed", newItemPress);

        addNewItemBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                addNewItemBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                addNewItemBttn.mouseReleased();
                newItemPress.actionPerformed(new ActionEvent(addNewItemBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- EDIT ITEM BUTTON -----//
        Action editItemPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                menuPanel.setVisible(false);
                showPanel(EDIT_ITEM);
            }
        };
        editItemBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        editItemBttn.getActionMap().put("pressed", editItemPress);

        editItemBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                editItemBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                editItemBttn.mouseReleased();
                editItemPress.actionPerformed(new ActionEvent(editItemBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- NEW PERSON BUTTON -----//
        Action newPersonPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                backBttn.setVisible(true);
                newPersonPanel.removeAll();
                newPersonPanel.add(new AccountPanel(), BorderLayout.CENTER);
                showPanel(NEW_PERSON);
            }
        };
        newPersonBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        newPersonBttn.getActionMap().put("pressed", newPersonPress);

        newPersonBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                newPersonBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                newPersonBttn.mouseReleased();
                newPersonPress.actionPerformed(new ActionEvent(newPersonBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- EDIT PERSON BUTTON -----//
        Action editPersonPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                backBttn.setVisible(true);
                showPanel(SEARCH_PERSON);
            }
        };
        editPersonBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        editPersonBttn.getActionMap().put("pressed", editPersonPress);

        editPersonBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                editPersonBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                editPersonBttn.mouseReleased();
                editPersonPress.actionPerformed(new ActionEvent(editPersonBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- NEW DISCOUNT BUTTON -----//
        Action newDiscountPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                backBttn.setVisible(true);
                showPanel(NEW_DISCOUNT);
            }
        };
        newDiscountBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        newDiscountBttn.getActionMap().put("pressed", newDiscountPress);

        newDiscountBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                newDiscountBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                newDiscountBttn.mouseReleased();
                newDiscountPress.actionPerformed(new ActionEvent(newDiscountBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- EDIT DISCOUNT BUTTON -----//
        Action editDiscountPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                backBttn.setVisible(true);
                showPanel(SEARCH_DISCOUNT);
            }
        };
        editDiscountBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        editDiscountBttn.getActionMap().put("pressed", editDiscountPress);

        editDiscountBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                editDiscountBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                editDiscountBttn.mouseReleased();
                editDiscountPress.actionPerformed(new ActionEvent(editDiscountBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- REPORT BUTTON -----//
        Action reportPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                backBttn.setVisible(true);
                showPanel(REPORT);
            }
        };
        reportBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        reportBttn.getActionMap().put("pressed", reportPress);

        reportBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                reportBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                reportBttn.mouseReleased();
                reportPress.actionPerformed(new ActionEvent(reportBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- POS BUTTON -----//
        Action posPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                changeState(State.employee);
            }
        };
        posBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        posBttn.getActionMap().put("pressed", posPress);

        posBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                posBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                posBttn.mouseReleased();
                changeState(State.employee);
            }
        });

        //----- PLACE ORDER BUTTON -----//
        Action placeOrderPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                backBttn.setVisible(true);
                showPanel(PLACE_ORDER);
            }
        };
        placeOrderBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        placeOrderBttn.getActionMap().put("pressed", placeOrderPress);

        placeOrderBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                placeOrderBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                placeOrderBttn.mouseReleased();
                placeOrderPress.actionPerformed(new ActionEvent(placeOrderBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- SEARCH ORDER BUTTON -----//
        Action searchOrderPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                backBttn.setVisible(true);
                // setup search order panel each time so orders placed in the same instance are shown
                setupSearchOrderPanel();
                showPanel(SEARCH_ORDER);
            }
        };
        searchOrderBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        searchOrderBttn.getActionMap().put("pressed", searchOrderPress);

        searchOrderBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                searchOrderBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                searchOrderBttn.mouseReleased();
                searchOrderPress.actionPerformed(new ActionEvent(searchOrderBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- SHOW HELP LINK -----//
        Action helpPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                helpDialog.setVisible(true);
            }
        };
        showHelpLink.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        showHelpLink.getActionMap().put("pressed", helpPress);

        showHelpLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                helpDialog.setVisible(true);
            }
        });

        //----- SHOW RESTOCK LINK -----//
        Action restockPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                showRestockDialog();
            }
        };
        showRestockLink.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        showRestockLink.getActionMap().put("pressed", restockPress);

        showRestockLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showRestockDialog();
            }
        });
    }

    //------------------------------------------------------------//
    //---------- MANAGER STATE METHODS TO SET UP PANELS ----------//
    /**
     * Places components in optionPanel.
     */
    public void setupOptionPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = 2;
        optionPanel.add(new FormTitle("Management", 700, 2), gbc);
        gbc.gridy = 1;
        optionPanel.add(showRestockLink, gbc);
        gbc.gridwidth = 1;
        gbc.weightx = 2;
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.EAST;
        optionPanel.add(editItemBttn, gbc);
        gbc.gridy = 3;
        optionPanel.add(addNewItemBttn, gbc);
        gbc.gridy = 4;
        optionPanel.add(newPersonBttn, gbc);
        gbc.gridy = 5;
        optionPanel.add(editPersonBttn, gbc);
        gbc.gridy = 6;
        optionPanel.add(placeOrderBttn, gbc);
        gbc.gridy = 2;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        optionPanel.add(newDiscountBttn, gbc);
        gbc.gridy = 3;
        optionPanel.add(editDiscountBttn, gbc);
        gbc.gridy = 4;
        optionPanel.add(reportBttn, gbc);
        gbc.gridy = 5;
        optionPanel.add(posBttn, gbc);
        gbc.gridy = 6;
        optionPanel.add(searchOrderBttn, gbc);
    }

    /**
     * Initializes and places components in editItemPanel. Adds event listeners
     * to editItemPanel components.
     */
    public void setupEditItemPanel() {
        //----- SETUP COMPONENTS -----//
        editItemPanel.removeAll();

        // ITEM SCROLLPANE
        itemScroll = new ItemScrollPane();

        // JCOMBOBOXES
        categoriesComboBox = new JComboBox();
        categoriesComboBox.addItem("Shop by Category");
        subcategoriesComboBox = new JComboBox();
        subcategoriesComboBox.setVisible(false);
        try {
            ResultSet rs = con.getItemCategories();
            while (rs.next()) {
                categoriesComboBox.addItem(rs.getString(2));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // SEARCH FIELD
        itemSearchField = new InputField("search by keyword...");

        // BUTTONS
        ButtonPrimary scrollBackBttn = new ButtonPrimary("Back");
        scrollBackBttn.setPreferredSize(new Dimension(150, 54));

        //----- GROUP COMPONENTS -----//
        // MENU BAR PANEL
        JPanel scrollMenuPanel = new JPanel();
        scrollMenuPanel.setBackground(Color.WHITE);
        scrollMenuPanel.setLayout(new BoxLayout(scrollMenuPanel, BoxLayout.X_AXIS));
        scrollMenuPanel.setBorder(new MatteBorder(0, 0, 3, 0, Colors.SILVER));

        // group comboboxes && search input field together
        JPanel searchPanel = new JPanel(new FlowLayout(SwingConstants.LEFT, 15, 15));
        searchPanel.setBackground(null);
        searchPanel.add(categoriesComboBox);
        searchPanel.add(subcategoriesComboBox);
        searchPanel.add(itemSearchField);
        searchPanel.setMaximumSize(searchPanel.getPreferredSize());

        // button panel to prevent focus outline from being cut off
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(null);
        buttonPanel.add(scrollBackBttn);
        buttonPanel.setMaximumSize(buttonPanel.getPreferredSize());

        // add jpanels to menu bar panel
        searchPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        buttonPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        scrollMenuPanel.add(searchPanel);
        scrollMenuPanel.add(Box.createGlue());
        scrollMenuPanel.add(buttonPanel);

        //----- SIZE COMPONENTS -----//
        //SEARCH FIELD
        itemSearchField.setWidth(250);
        itemSearchField.setHeight(40);

        //COMBOBOXES
        categoriesComboBox.setFont(new Font("Nunito", Font.PLAIN, 18));
        subcategoriesComboBox.setFont(new Font("Nunito", Font.PLAIN, 18));
        categoriesComboBox.setPreferredSize(new Dimension(210, itemSearchField.getHeight()));
        categoriesComboBox.setMaximumSize(new Dimension(210, itemSearchField.getHeight()));
        subcategoriesComboBox.setPreferredSize(new Dimension(210, itemSearchField.getHeight()));
        subcategoriesComboBox.setMaximumSize(new Dimension(210, itemSearchField.getHeight()));

        //----- ADD COMPONENTS TO PANEL -----//
        editItemPanel.add(scrollMenuPanel, BorderLayout.NORTH);
        editItemPanel.add(itemScroll, BorderLayout.CENTER);

        //----- ADD EVENT LSITENERS TO PANEL -----//
        // CATEGORY COMBO BOX
        categoriesComboBox.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                try {
                    //if the selected category has subcategorys, populate the subcategory combobox
                    subcategoriesComboBox.removeAllItems();
                    subcategoriesComboBox.addItem("Choose a Subcategory");
                    ResultSet rs = con.getSubcategory(categoriesComboBox.getSelectedItem().toString());
                    // isBeforeFirst returns false if the cursor is not before the first record OR if there are no rows in the ResultSet.
                    if (rs.isBeforeFirst()) {
                        while (rs.next()) {
                            subcategoriesComboBox.addItem(rs.getString(1));
                        }
                    }
                    if (subcategoriesComboBox.getItemCount() > 1) {
                        subcategoriesComboBox.setVisible(true);
                    } else {
                        subcategoriesComboBox.setVisible(false);
                    }

                    // populate the JScrollPane with thumbnails of items from the selected category
                    try {
                        ResultSet r;
                        if (categoriesComboBox.getSelectedIndex() > 0) {
                            r = con.searchEntireCategory(categoriesComboBox.getSelectedItem().toString());
                            itemScroll.sortItems(r);
                        }

                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // SUBCATEGORY COMBOBOX
        subcategoriesComboBox.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (subcategoriesComboBox.getSelectedIndex() != 0) {
                    // populate the JScrollPane with thumbnails of items from the selected subcategory

                    try {
                        ResultSet r = con.searchEntireSubcategory(subcategoriesComboBox.getSelectedItem().toString());
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
                String text = itemSearchField.getText();
                String[] keywords = text.trim().split(" ");

                // populate the JScrollPane with thumbnails of items whose name includes the keyword(s)
                try {
                    ResultSet rs;
                    if (keywords.length > 1) {
                        rs = con.searchAllItems(keywords, currentState);
                    } else {
                        // if the user entered an item number, go directly to an item edit state
                        if (Validation.isNumeric(text)) {
                            rs = con.getItem(Integer.parseInt(text));
                            if (rs.isBeforeFirst()) {
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
                                byte[] b = rs.getBytes(10);
                                ImageIcon img = new ImageIcon(b);
                                InventoryItem item = new InventoryItem(id, name, desc, categoryID, subcategoryID, price, wholesale, quantity, restock, max, img);
                                if (discontinue == 1) {
                                    item.setDiscontinued(true);
                                }
                                // create item panel and add it to frame
                                State.createItemState(item);
                                return;
                            }
                        }
                        rs = con.searchAllItems(text, currentState);
                    }
                    itemScroll.sortItems(rs);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        };
        itemSearchField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        itemSearchField.getActionMap().put("pressed", enter);

        // BACK BUTTON
        Action backPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                menuPanel.setVisible(true);
                showPanel(OPTIONS);
            }
        };
        scrollBackBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        scrollBackBttn.getActionMap().put("pressed", backPress);

        scrollBackBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                scrollBackBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                scrollBackBttn.mouseReleased();
                backPress.actionPerformed(new ActionEvent(scrollBackBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });
    }

    /**
     * Initializes and places components in searchPersonPanel. Adds event
     * listeners to searchPersonPanel components.
     */
    public void setupSearchPersonPanel() {
        //----- SETUP COMPONENTS -----//
        //SEARCH FIELD
        personSearchLabel = new FieldLabel("Search Accounts");
        personSearchField = new InputField();

        // SCROLLPANE && JLIST
        // list of found accounts 
        accountList = new JList(new DefaultListModel());
        accountList.setFont(new Font("Nunito", Font.PLAIN, 18));
        accountList.setForeground(Colors.RICH_BLACK);

        // scrollpane to display account list
        accountScroll = new JScrollPane(accountList);
        accountScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        accountScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        accountScroll.getVerticalScrollBar().setUnitIncrement(16);
        accountScroll.getViewport().setBackground(Color.WHITE);
        accountScroll.setBorder(null);
        accountScroll.setPreferredSize(new Dimension(600, 200));

        // PANEL TO DISPLAY SCROLLPANE
        foundAccountPanel = new JPanel(new GridBagLayout());
        foundAccountPanel.setBorder(new MatteBorder(5, 5, 5, 5, Colors.SILVER));
        foundAccountPanel.setVisible(false);

        // EDIT ACCOUNT BUTTON
        editAccountBttn = new ButtonPrimary("Edit Selected Account");
        editAccountBttn.setPreferredSize(new Dimension(350, 54));
        editAccountBttn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // TITLE
        FormTitle title = new FormTitle("Accounts Found", 600, 4);
        title.setUnderlineColor(Colors.SILVER);

        //----- ADD COMPONENTS TO FOUND ACCOUNT PANEL -----//
        // center edit account button horizontally
        JPanel editAccountBttnPanel = new JPanel();
        editAccountBttnPanel.setLayout(new BoxLayout(editAccountBttnPanel, BoxLayout.X_AXIS));
        editAccountBttnPanel.setBackground(Color.WHITE);
        editAccountBttnPanel.add(Box.createHorizontalStrut(123));
        editAccountBttnPanel.add(editAccountBttn);
        editAccountBttnPanel.add(Box.createHorizontalStrut(123));

        // add space above && below button && add a border above the button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new MatteBorder(4, 0, 0, 0, Colors.SILVER));
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(editAccountBttnPanel);
        buttonPanel.add(Box.createVerticalStrut(5));

        // add components to found account panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        foundAccountPanel.add(title, gbc);
        gbc.gridy = 1;
        foundAccountPanel.add(accountScroll, gbc);
        gbc.gridy = 2;
        foundAccountPanel.add(buttonPanel, gbc);

        //----- ADD COMPONENTS TO SEARCH PERSON PANEL -----//
        gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        personSearchLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchPersonPanel.add(personSearchLabel, gbc);

        // add search field to jpanel to fix alignment
        JPanel personSearchFieldPanel = new JPanel();
        personSearchFieldPanel.add(personSearchField);
        personSearchFieldPanel.setMaximumSize(personSearchFieldPanel.getPreferredSize());
        personSearchFieldPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        personSearchFieldPanel.setBackground(null);
        gbc.gridx = 1;
        searchPersonPanel.add(personSearchFieldPanel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        foundAccountPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchPersonPanel.add(foundAccountPanel, gbc);

        //----- EVENT LISTENERS -----//
        // EDIT CUSTOMER BUTTON
        AbstractAction editPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // get the selected account from accountList
                if (accountList.getSelectedValue() != null) {
                    Person p = foundAccountsArray.get(accountList.getSelectedIndex());
                    User user = null;
                    try {
                        ResultSet rs = con.getUser(p.getPersonID());
                        rs.next();

                        int id = rs.getInt(1);
                        String uName = rs.getString(3);
                        String pass = rs.getString(4);
                        int q1 = rs.getInt(5);
                        int q2 = rs.getInt(6);
                        int q3 = rs.getInt(7);
                        String a1 = rs.getString(8);
                        String a2 = rs.getString(9);
                        String a3 = rs.getString(10);
                        int pos = rs.getInt(11);

                        boolean disabled;
                        if (rs.getInt(12) == 0) {
                            disabled = false;
                        } else {
                            disabled = true;
                        }

                        boolean deleted;
                        if (rs.getInt(13) == 0) {
                            deleted = false;
                        } else {
                            deleted = true;
                        }

                        user = new User(uName, pass, q1, q2, q3, a1, a2, a3, pos, p, disabled, deleted, id);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    // populate editPersonPanel with data from the selected account
                    editPersonPanel.removeAll();
                    editPersonPanel.add(new AccountPanel(user, p), BorderLayout.CENTER);
                    showPanel(EDIT_PERSON);
                }
            }
        };
        editAccountBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        editAccountBttn.getActionMap().put("pressed", editPress);

        editAccountBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                editAccountBttn.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                editAccountBttn.mouseReleased();
                editPress.actionPerformed(new ActionEvent(editAccountBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        }
        );

        // SEARCH PERSON FIELD
        Action customerPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // search for customers in the database based on the entered text
                try {
                    String text = personSearchField.getText();
                    String[] words = text.trim().split(" ");
                    ResultSet rs;
                    if (words.length > 1) {
                        rs = con.getCustomer(words[0], words[1]);
                    } else {
                        rs = con.getCustomer(text);
                    }

                    if (rs.isBeforeFirst()) {
                        personSearchLabel.removeWarning();
                        DefaultListModel model = (DefaultListModel) accountList.getModel();
                        model.clear();
                        foundAccountsArray = new ArrayList<>();
                        int i = 0;
                        // add the accounts found to the ArrayList for easy retrieval
                        while (rs.next()) {
                            Person pers = new Person(rs.getInt(1), rs.getString(2), rs.getString(4), rs.getString(6), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12));
                            if (rs.getString(7) != null) {
                                pers.setAddressLine2(rs.getString(7));
                            }
                            if (rs.getString(8) != null) {
                                pers.setAddressLine3(rs.getString(8));
                            }
                            if (rs.getString(13) != null) {
                                pers.setPhone1(rs.getString(13));
                            }

                            foundAccountsArray.add(i, pers);
                            // add the accounts found to accountList for selecting
                            if (rs.getInt(15) == 1) {
                                model.add(i, rs.getString(2) + " " + rs.getString(4) + ", " + rs.getString(12) + " (DELETED)");
                            } else if (rs.getInt(16) == 1) {
                                model.add(i, rs.getString(2) + " " + rs.getString(4) + ", " + rs.getString(12) + " (DISABLED)");
                            } else {
                                model.add(i, rs.getString(2) + " " + rs.getString(4) + ", " + rs.getString(12));
                            }

                        }
                        foundAccountPanel.setVisible(true);
                    } else {
                        personSearchLabel.addWarning("Customer not found.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        personSearchField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        personSearchField.getActionMap().put("pressed", customerPress);
    }

    /**
     * Initializes and places components in newItempanel.
     */
    public void setupNewItemPanel() {
        NewItemPanel newItemP = new NewItemPanel();
        newItemP.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(newItemP);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(null);
        newItemPanel.add(scroll, BorderLayout.CENTER);
    }

    /**
     * Initializes and places components in newDiscountPanel.
     */
    public void setupDiscountPanel() {
        JScrollPane scroll = new JScrollPane(new DiscountPanel());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(null);
        newDiscountPanel.removeAll();
        newDiscountPanel.add(scroll, BorderLayout.CENTER);
    }

    /**
     * Initializes and places components in searchDiscountPanel. Adds event
     * listeners to searchDiscountPanel components.
     */
    public void setupSearchDiscountPanel() {
        //----- SETUP COMPONENTS -----//
        JList discountList = new JList(new DefaultListModel());
        discountList.setFont(new Font("Nunito", Font.PLAIN, 18));
        discountList.setForeground(Colors.RICH_BLACK);

        // add all discounts to discountList (including disabled) unless expired
        try {
            DefaultListModel model = (DefaultListModel) discountList.getModel();
            ResultSet rs = State.getConnection().getAllDiscounts();
            int i = 0;
            while (rs.next()) {
                String str = rs.getString(1) + " | " + rs.getString(2);
                if (rs.getInt(3) == 1) {
                    str += " (DISABLED)";
                }
                model.add(i, str);
                i++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // JScrollPane to display discountList
        JScrollPane discountScroll = new JScrollPane(discountList);
        discountScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        discountScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        discountScroll.getVerticalScrollBar().setUnitIncrement(16);
        discountScroll.getViewport().setBackground(Colors.GHOST_WHITE);
        discountScroll.setPreferredSize(new Dimension(600, 400));

        // edit discount button
        ButtonPrimary editDiscountBttn = new ButtonPrimary("Edit Selected Discount");
        editDiscountBttn.setPreferredSize(new Dimension(350, 54));

        // title
        FormTitle title = new FormTitle("Active Discounts");

        //----- ADD COMPONENTS TO SEARCH DISCOUNT PANEL -----//
        searchDiscountPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        searchDiscountPanel.add(title, gbc);
        gbc.gridy += 1;
        searchDiscountPanel.add(discountScroll, gbc);
        gbc.gridy += 1;
        searchDiscountPanel.add(editDiscountBttn, gbc);

        //----- EVENT LISTENERS -----//
        // EDIT DISCOUNT BUTTON
        AbstractAction editDiscountBttnPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (discountList.getSelectedValue() != null) {
                    editDiscountBttn.mouseReleased();
                    String selected = discountList.getSelectedValue().toString();
                    String[] discount = selected.split(" ");

                    try {
                        ResultSet rs = con.checkDiscountCode(discount[0]);
                        rs.next();
                        int id = rs.getInt(1);
                        String code = rs.getString(2);
                        String desc = rs.getString(3);
                        int level = rs.getInt(4);
                        int item = rs.getInt(5);
                        int type = rs.getInt(6);
                        double percent = rs.getDouble(7);
                        double dollar = rs.getDouble(8);
                        String start = rs.getString(9);
                        LocalDate exp = LocalDate.parse(rs.getString(10));
                        Discount dis = new Discount(code, desc, level, type, exp);
                        dis.setDiscountID(id);
                        if (start != null) {
                            dis.setStartDate(LocalDate.parse(start));
                        }

                        if (type == Discount.PERCENT_TYPE) {
                            dis.setPercentage(percent);
                        } else {
                            dis.setDollarAmount(dollar);
                        }

                        if (level == Discount.ITEM_LEVEL) {
                            dis.setInventoryID(item);
                        }

                        JScrollPane s = new JScrollPane(new DiscountPanel(dis));
                        s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                        s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                        s.getVerticalScrollBar().setUnitIncrement(16);
                        s.getViewport().setBackground(Color.WHITE);
                        s.setBorder(null);
                        editDiscountPanel.removeAll();
                        editDiscountPanel.add(s, BorderLayout.CENTER);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        editDiscountBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        editDiscountBttn.getActionMap().put("pressed", editDiscountBttnPress);

        editDiscountBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                editDiscountBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                editDiscountBttn.mouseReleased();
                editDiscountBttnPress.actionPerformed(new ActionEvent(editDiscountBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });
    }

    /**
     * Initializes and places components in reportPanel. Adds event listeners to
     * reportPanel components.
     */
    public void setupReportPanel() {
        //----- SETUP COMPONENTS -----//
        GridBagConstraints gbc = new GridBagConstraints();
        // TITLE
        FormTitle title = new FormTitle("Generate Reports", 600, 2);

        // LABELS
        // labels for comboboxes
        FieldLabel typeLabel = new FieldLabel("Report Type");
        FieldLabel subTypeLabel = new FieldLabel("Sales Report For");
        //label for date picker
        FieldLabel dateLabel = new FieldLabel("Date");

        // COMBOBOXES
        // specifies the type of report to generate, selected item changes reportSubTypeComboBox items
        JComboBox<String> reportTypeComboBox = new JComboBox<>();
        reportTypeComboBox.addItem("Sales By Date");
        reportTypeComboBox.addItem("Sales By Customer");
        reportTypeComboBox.addItem("Inventory");
        reportTypeComboBox.addItem("Account Data");

        // combobox that specifies the sub type of report to generate
        reportSubTypeComboBox = new JComboBox<>();
        reportSubTypeComboBox.addItem("Day");
        reportSubTypeComboBox.addItem("Week");
        reportSubTypeComboBox.addItem("Month");
        reportSubTypeComboBox.addItem("Year");

        // DATE PICKER
        // to choose a date range for sales reports
        datePicker = new DatePicker();
        DatePickerSettings dps = new DatePickerSettings();
        Font f = new Font("Nunito", Font.PLAIN, 18);
        dps.setFontValidDate(f);
        dps.setFontVetoedDate(f);
        dps.setFontInvalidDate(f);
        datePicker.setSettings(dps);

        // customer field/label to search for a customer to generate a sales report on
        customerField = new InputField("search for customer account...");
        customerLabel = new FieldLabel("Customer");

        // button
        ButtonPrimary createReportBttn = new ButtonPrimary("Create Report");

        //----- GROUP COMPONENTS -----//
        // group typeLabel and reportTypeComboBox vertically
        JPanel typePanel = new JPanel();
        typePanel.setBackground(null);
        typePanel.setLayout(new GridBagLayout());
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        typePanel.add(typeLabel, gbc);
        gbc.gridy = 1;
        typePanel.add(reportTypeComboBox, gbc);

        // group subTypeLabel and reportSubTypeComboBox vertically
        JPanel subTypePanel = new JPanel();
        subTypePanel.setBackground(null);
        // BoxLayout was causing subTypeLabel's text to be cut off when text was changed
        subTypePanel.setLayout(new GridBagLayout());
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        subTypePanel.add(subTypeLabel, gbc);
        gbc.gridy = 1;
        subTypePanel.add(reportSubTypeComboBox, gbc);

        // group typePanel and subTypePanel horizontally
        JPanel reportTypePanel = new JPanel();
        reportTypePanel.setBackground(null);
        reportTypePanel.setLayout(new BoxLayout(reportTypePanel, BoxLayout.X_AXIS));
        reportTypePanel.add(typePanel);
        reportTypePanel.add(Box.createHorizontalStrut(10));
        reportTypePanel.add(subTypePanel);

        // group dateLabel and datePicker vertically
        JPanel datePanel = new JPanel();
        datePanel.setBackground(null);
        datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        datePanel.add(dateLabel);
        datePanel.add(Box.createVerticalStrut(5));
        datePicker.setAlignmentX(Component.LEFT_ALIGNMENT);
        datePanel.add(datePicker);

        // group customerLabel and customerField vertically
        JPanel custPanel = new JPanel();
        custPanel.setBackground(null);
        custPanel.setLayout(new BoxLayout(custPanel, BoxLayout.Y_AXIS));
        customerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        custPanel.add(customerLabel);
        custPanel.add(Box.createVerticalStrut(5));
        customerField.setAlignmentX(Component.LEFT_ALIGNMENT);
        custPanel.add(customerField);
        custPanel.setVisible(false);

        //----- SIZE COMPONENTS -----//
        datePicker.getComponentDateTextField().setPreferredSize(new Dimension(480, 45));

        reportTypeComboBox.setFont(new Font("Nunito", Font.PLAIN, 22));
        reportTypeComboBox.setPreferredSize(new Dimension(225, 45));

        reportSubTypeComboBox.setFont(new Font("Nunito", Font.PLAIN, 22));
        reportSubTypeComboBox.setPreferredSize(new Dimension(275, 45));
        createReportBttn.setPreferredSize(new Dimension(275, 64));

        customerField.setWidth(510);

        //----- ADD COMPONENTS TO REPORT PANEL -----//
        gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 15, 0);
        reportPanel.add(title, gbc);
        gbc.gridy += 1;
        reportPanel.add(reportTypePanel, gbc);
        gbc.gridy += 1;
        reportPanel.add(datePanel, gbc);
        gbc.gridy += 1;
        reportPanel.add(custPanel, gbc);
        gbc.gridy += 1;
        reportPanel.add(createReportBttn, gbc);

        //----- ADD EVENT LISTENERS -----//
        // REPORT TYPE COMBO BOX
        reportTypeComboBox.addItemListener((ItemEvent e) -> {
            // change the form based on the type of report selected
            switch (reportTypeComboBox.getSelectedIndex()) {
                case 0, 1 -> {
                    subTypeLabel.setText("Sales Report For");
                    reportSubTypeComboBox.removeAllItems();
                    reportSubTypeComboBox.addItem("Day");
                    reportSubTypeComboBox.addItem("Week");
                    reportSubTypeComboBox.addItem("Month");
                    reportSubTypeComboBox.addItem("Year");
                    datePanel.setVisible(true);
                    if (reportTypeComboBox.getSelectedIndex() == 1) {
                        custPanel.setVisible(true);
                        createReportBttn.setText("Find Customer");
                    } else {
                        custPanel.setVisible(false);
                        createReportBttn.setText("Create Report");
                    }
                }
                case 2 -> {
                    subTypeLabel.setText("Inventory Report Of");
                    reportSubTypeComboBox.removeAllItems();
                    reportSubTypeComboBox.addItem("Available Items");
                    reportSubTypeComboBox.addItem("Items Needing Restock");
                    reportSubTypeComboBox.addItem("All Items");
                    datePanel.setVisible(false);
                    custPanel.setVisible(false);
                    createReportBttn.setText("Create Report");
                }
                case 3 -> {
                    subTypeLabel.setText("Account Report Of");
                    reportSubTypeComboBox.removeAllItems();
                    reportSubTypeComboBox.addItem("Customers");
                    reportSubTypeComboBox.addItem("Employees");
                    datePanel.setVisible(false);
                    custPanel.setVisible(false);
                    createReportBttn.setText("Create Report");
                }
            }
        });

        // CREATE REPORT BUTTON
        Action createReportPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try {
                    switch (reportTypeComboBox.getSelectedIndex()) {
                        case 0 -> {
                            HTML.createSalesReport(reportSubTypeComboBox.getSelectedIndex(), datePicker.getDate());
                        }
                        case 1 -> {
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
                                    foundCustomersArray = new ArrayList<>();
                                    int i = 0;
                                    while (rs.next()) {
                                        foundCustomersArray.add(i, new Person(rs.getInt(1), rs.getString(2), rs.getString(4), rs.getString(6), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12)));
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
                        case 2 -> {
                            HTML.createInventoryReport(reportSubTypeComboBox.getSelectedIndex());
                        }
                        case 3 -> {
                            HTML.createPersonReport(reportSubTypeComboBox.getSelectedIndex());
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        createReportBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        createReportBttn.getActionMap().put("pressed", createReportPress);

        createReportBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                createReportBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                createReportBttn.mouseReleased();
                createReportPress.actionPerformed(new ActionEvent(createReportBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //CUSTOMER SEARCH FIELD
        Action customerPress = new AbstractAction() {
            // search for customers based on text entered in field
            // populate customerDialog customerList with customers found
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

                    // add customers found to an ArrayList for easy retrieval
                    if (rs.isBeforeFirst()) {
                        customerLabel.removeWarning();
                        DefaultListModel model = (DefaultListModel) customerList.getModel();
                        model.clear();
                        foundCustomersArray = new ArrayList<>();
                        int i = 0;
                        // add customers found to customerList in customerDialog
                        while (rs.next()) {
                            foundCustomersArray.add(i, new Person(rs.getInt(1), rs.getString(2), rs.getString(4), rs.getString(6), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12)));
                            model.add(i, rs.getString(2) + " " + rs.getString(4) + ", " + rs.getString(12));
                        }
                        // show customerDialog
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
    }

    /**
     * Initialize and place components in placeOrderPanel. Add event listeners
     * to placeOrderPanel components.
     */
    private void setupPlaceOrderPanel() {
        //----- SETUP COMPONENTS -----//
        // CART SCROLLPANE
        cartArray = new ArrayList<CartItem>();
        cartScrollPanel = new JPanel();
        cartScrollPanel.setLayout(new BoxLayout(cartScrollPanel, BoxLayout.Y_AXIS));
        cartScrollPanel.setBackground(Colors.SILVER);

        cartScroll = new JScrollPane(cartScrollPanel);
        cartScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        cartScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        cartScroll.getVerticalScrollBar().setUnitIncrement(16);
        cartScroll.setBorder(new MatteBorder(15, 15, 15, 10, Color.WHITE));

        // BUTTONS
        ButtonPrimary checkoutBttn = new ButtonPrimary("Place Order");
        ButtonPrimary cancelOrderBttn = new ButtonPrimary("Cancel Order");
        ButtonPrimary showRestockBttn = new ButtonPrimary("Low Stock Items");

        // FIELDS && LABELS
        skuField = new InputField();
        FieldLabel skuLabel = new FieldLabel("SKU Number");
        orderTotalLabel = new BizmartLabel("Subtotal");
        WarningLabel invalidSkuWarning = new WarningLabel("Invalid SKU number.");

        //----- GROUP COMPONENTS -----//
        // group buttons in panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        buttonPanel.setBackground(null);

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 0);
        buttonPanel.add(skuLabel, gbc);

        gbc.gridy++;
        buttonPanel.add(skuField, gbc);

        gbc.gridy++;
        buttonPanel.add(invalidSkuWarning, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        buttonPanel.add(checkoutBttn, gbc);

        gbc.gridy++;
        buttonPanel.add(cancelOrderBttn, gbc);

        gbc.gridy++;
        buttonPanel.add(showRestockBttn, gbc);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 10, 0);
        buttonPanel.add(orderTotalLabel, gbc);

        //----- SIZE COMPONENTS -----//
        int height = frame.getHeight() - 100;
        checkoutBttn.setPreferredSize(new Dimension(300, 54));
        cancelOrderBttn.setPreferredSize(new Dimension(300, 54));
        showRestockBttn.setPreferredSize(new Dimension(300, 54));
        skuField.setWidth(300);
        // BUTTON PANEL
        buttonPanel.setMaximumSize(new Dimension(buttonPanel.getPreferredSize().width, height));

        //----- ADD COMPONENTS TO PLACE ORDER PANEL -----//
        placeOrderPanel.add(cartScroll);
        placeOrderPanel.add(Box.createHorizontalStrut(10));
        placeOrderPanel.add(buttonPanel);
        placeOrderPanel.add(Box.createHorizontalStrut(12));

        //----- ADD EVENT LISTENERS TO BUTTONS -----//
        // CANCEL ORDER BUTTON
        Action cancelPress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearCart();
            }
        };
        cancelOrderBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        cancelOrderBttn.getActionMap().put("pressed", cancelPress);

        cancelOrderBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                cancelOrderBttn.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                cancelOrderBttn.mouseReleased();
                clearCart();
            }
        });

        // CHECKOUT BUTTON
        Action checkoutPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!cartArray.isEmpty()) {
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

        // SKU SEARCH FIELD
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
                    // get inventory item with an item number equal to text in skuField
                    ResultSet rs = con.getItem(Integer.parseInt(skuField.getText()));

                    if (!rs.isBeforeFirst()) {
                        invalidSkuWarning.setText("Invalid SKU number.");
                        invalidSkuWarning.setVisible(true);
                        return;
                    }

                    invalidSkuWarning.setVisible(false);
                    // get information about the inventory item
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

                    // check if the item has already been ordered
                    boolean isOrdered = false;
                    int qtyOrdered = 0;
                    rs = con.getUnreceivedOrderItems();
                    while (rs.next()) {
                        if (rs.getInt(1) == id) {
                            isOrdered = true;
                            qtyOrdered += rs.getInt(2);
                        }
                    }

                    // if the max quantity - the quantity in stock is 0, the item is fully stocked
                    if (max - quantity == 0) {
                        invalidSkuWarning.setText("Item fully stocked.");
                        invalidSkuWarning.setVisible(true);
                        return;
                    } else if (isOrdered) {
                        // if the item is in an unreceived ordered, the quantity that can be ordered
                        // changes so the  user can't purchase more than the max quantity
                        if (quantity + qtyOrdered == max) {
                            invalidSkuWarning.setText("Item in existing order.");
                            invalidSkuWarning.setVisible(true);
                            return;
                        }
                    }

                    // create a cart item (extended JPanel)
                    CartItem c = new CartItem(new InventoryItem(id, name, desc, categoryID, subcategoryID, price, wholesale, quantity, restock, max, img), 1);
                    if (qtyOrdered > 0) {
                        c.setOrderedQuantity(qtyOrdered);
                    }
                    // check if the item is already in the cart
                    for (CartItem ci : cartArray) {
                        if (ci.getInventoryItem().getItemNumber() == c.getInventoryItem().getItemNumber()) {
                            // add the quantity from the cart with the quantity of the selected item
                            int newQty = c.getQuantity() + ci.getQuantity();
                            // get the maximum stock for this item
                            int inStock = ci.getInventoryItem().getQuantity();

                            // check if the new quantity is greater than whats in stock
                            if (newQty > inStock) {
                                newQty = inStock;
                            }

                            // set the new quantity && recalculate the total
                            ci.setQuantity(newQty);
                            calculateTotal();
                            return;
                        }
                    }
                    // if the item wasn't in the cart, add it && recalculate the total
                    addItemToCart(c);
                    calculateTotal();

                    // add item to cartScrollPanel
                    if (cartArray.size() == 1) {
                        cartScrollPanel.add(Box.createVerticalStrut(10));
                    }

                    // clear the sku field
                    skuField.setText("");
                    c.setAlignmentX(Component.CENTER_ALIGNMENT);
                    cartScrollPanel.add(c);
                    cartScrollPanel.add(Box.createVerticalStrut(10));
                    cartScroll.revalidate();
                    cartScroll.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        skuField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        skuField.getActionMap().put("pressed", skuPress);

        // LOW STOCK ITEMS BUTTON
        Action restockPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                showRestockDialog();
            }
        };
        showRestockBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        showRestockBttn.getActionMap().put("pressed", restockPress);

        showRestockBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showRestockBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                showRestockBttn.mouseReleased();
                showRestockDialog();
            }
        });

    }

    /**
     * Initialize and place components in searchOrderPanel. Add event listeners
     * to searchOrderPanel components.
     * <p>
     * This method is called every time searchOrderPanel is shown in cardPanel
     * because the panel needs to be updated to show new orders and/or remove
     * orders that have been received.
     */
    private void setupSearchOrderPanel() {
        //----- SETUP COMPONENTS -----//
        searchOrderPanel.removeAll();
        storeOrderArray = new ArrayList<String>();

        // BUTTONS
        ButtonPrimary viewBttn = new ButtonPrimary("View Order");
        SortButton sortBttn = new SortButton();

        // SEARCH FIELDS
        // order number field
        InputField orderNumField = new InputField("Enter order number...");
        orderNumField.setWidth(220);

        // order date field
        InputField dateField = new InputField("Enter datePicker...");
        dateField.setWidth(220);

        // LABELS
        // label for order number field
        FieldLabel orderLabel = new FieldLabel("Order Number");
        // label for order date field
        FieldLabel dateLabel = new FieldLabel("Order Date");

        // SEARCH ORDER SCROLL
        searchOrderScroll = new JScrollPane();
        searchOrderScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        searchOrderScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // ORDER LIST
        orderList = new JList(new DefaultListModel());
        orderList.setFont(new Font("Nunito", Font.PLAIN, 18));
        orderList.setForeground(Colors.RICH_BLACK);

        // add orders to the list (unreceived only)
        try {
            ResultSet rs = con.getStoreOrders();
            DefaultListModel model = (DefaultListModel) orderList.getModel();
            int i = 0;
            while (rs.next()) {
                String s = "Order #" + rs.getInt(1) + ", Order Date: " + rs.getString(2);
                model.add(i, s);
                storeOrderArray.add(s);
                i++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //----- GROUP COMPONENTS -----//
        // groups orderLabel and orderNumField vertically
        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new GridBagLayout());
        orderPanel.setBackground(null);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        orderPanel.add(orderLabel, gbc);
        gbc.gridy++;
        orderPanel.add(orderNumField, gbc);
        orderPanel.setMaximumSize(orderPanel.getPreferredSize());

        // groups dateLabel and dateField vertically
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new GridBagLayout());
        datePanel.setBackground(null);
        gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        datePanel.add(dateLabel, gbc);
        gbc.gridy++;
        datePanel.add(dateField, gbc);
        datePanel.setMaximumSize(datePanel.getPreferredSize());

        // groups and sortLabel sortBttn vertically
        JPanel sortPanel = new JPanel();
        BizmartLabel sortLabel = new BizmartLabel("Sort Order");
        sortPanel.setBackground(null);
        sortPanel.setLayout(new BoxLayout(sortPanel, BoxLayout.Y_AXIS));
        sortLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        sortPanel.add(sortLabel);
        sortBttn.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        sortPanel.add(sortBttn);

        // groups orderPanel, datePanel, and sortPanel horizontally
        JPanel searchSortPanel = new JPanel();
        searchSortPanel.setLayout(new BoxLayout(searchSortPanel, BoxLayout.X_AXIS));
        searchSortPanel.setBackground(null);
        searchSortPanel.add(Box.createHorizontalStrut(10));
        orderPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
        searchSortPanel.add(orderPanel);
        searchSortPanel.add(Box.createHorizontalStrut(20));
        datePanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
        searchSortPanel.add(datePanel);
        searchSortPanel.add(Box.createHorizontalGlue());
        sortPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
        searchSortPanel.add(sortPanel);
        searchSortPanel.add(Box.createHorizontalStrut(10));

        // used default gridbagconstraints to center viewBttn in panel
        // boxlayout with horizontal glue didn't center properly
        JPanel viewButtonPanel = new JPanel();
        viewButtonPanel.setLayout(new GridBagLayout());
        viewButtonPanel.setBackground(null);
        viewButtonPanel.add(viewBttn, new GridBagConstraints());

        //----- ADD COMPONENTS TO SEARCH ORDER PANEL -----//
        searchOrderScroll.getViewport().add(orderList);
        searchOrderPanel.add(searchSortPanel, BorderLayout.NORTH);
        searchOrderPanel.add(searchOrderScroll, BorderLayout.CENTER);
        searchOrderPanel.add(viewButtonPanel, BorderLayout.SOUTH);

        //----- ADD EVENT LISTENERS -----//
        // SORT BUTTON
        Action sortPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                sortOrders(sortBttn.getIconState());
            }
        };
        sortBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "Pressed");
        sortBttn.getActionMap().put("pressed", sortPress);

        sortBttn.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                sortOrders(sortBttn.getIconState());
            }
        });

        // SEARCH ORDER NUMBER FIELD
        orderNumField.addKeyListener(new KeyListener() {
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

        Action searchNumber = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // get the order with an order number matching the entered text
                try {
                    int orderNum = Integer.parseInt(orderNumField.getText());
                    ResultSet order = con.getStoreOrderByID(orderNum);
                    order.next();
                    storeOrder = new StoreOrder(orderNum, order.getInt(2), order.getDouble(3), order.getString(1));

                    // check if the order has been received (determines if the order can be received or is view only)
                    if (order.getInt(4) == 1) {
                        orderReceived = true;
                    } else {
                        orderReceived = false;
                    }

                    // get line items in the order
                    ResultSet details = con.getStoreOrderDetails(orderNum);
                    orderDetailsArray = new ArrayList<>();
                    while (details.next()) {
                        OrderDetails od = new OrderDetails(details.getInt(1), details.getInt(2));
                        if (orderReceived) {
                            od.setQuantityReceived(details.getInt(3));
                        }
                        orderDetailsArray.add(od);
                    }
                    orderLabel.removeWarning();
                    showPanel(MANAGE_ORDER);
                } catch (NumberFormatException | SQLException ex) {
                    if (ex.getClass() == NumberFormatException.class) {
                        orderLabel.addWarning("Invalid order number.");
                    }
                }
            }
        };
        orderNumField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        orderNumField.getActionMap().put("pressed", searchNumber);

        // VIEW ORDER BUTTON
        Action viewPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // get the order number from the selected value in the order JList
                    String str = orderList.getSelectedValue().toString().split("#")[1];
                    str = str.split(",")[0];
                    int orderNum = Integer.parseInt(str);
                    ResultSet order = con.getStoreOrderByID(orderNum);
                    order.next();
                    storeOrder = new StoreOrder(orderNum, order.getInt(2), order.getDouble(3), order.getString(1));

                    ResultSet details = con.getStoreOrderDetails(orderNum);
                    orderReceived = false;
                    orderDetailsArray = new ArrayList<>();
                    while (details.next()) {
                        orderDetailsArray.add(new OrderDetails(details.getInt(1), details.getInt(2)));
                    }
                    orderLabel.removeWarning();
                    showPanel(MANAGE_ORDER);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        };
        viewBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        viewBttn.getActionMap().put("pressed", viewPress);

        viewBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                viewBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                viewBttn.mouseReleased();
                viewPress.actionPerformed(new ActionEvent(viewBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        // SEARCH ORDER DATE
        Action searchDate = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // find all orders placed on the typed year, month, or day
                try {
                    String orderDate = dateField.getText();
                    ResultSet rs = con.getStoreOrdersByDate(orderDate);

                    if (!rs.isBeforeFirst()) {
                        dateLabel.addWarning("Invalid datePicker entered.");
                        return;
                    }

                    DefaultListModel model = (DefaultListModel) orderList.getModel();
                    int i = 0;
                    model.clear();
                    storeOrderArray.clear();
                    orderReceived = false;
                    // add orders to orderList
                    while (rs.next()) {
                        String s = "Order #" + rs.getInt(1) + ", Order Date: " + rs.getString(2);
                        model.add(i, s);
                        storeOrderArray.add(s);
                        i++;
                    }
                    dateLabel.removeWarning();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        };
        dateField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        dateField.getActionMap().put("pressed", searchDate);
    }

    /**
     * Initializes and places components in manageOrderPanel. Adds event
     * listeners to manageOrderPanelComponents.
     *
     * @param order the StoreOrder selected from searchOrderPanel
     * @param details the OrderDetails for each item in the StoreOrder
     */
    private void setupManageOrderPanel(StoreOrder order, ArrayList<OrderDetails> details) {
        //----- SETUP COMPONENTS -----//
        // CLEAR MANAGE ORDER PANEL 
        manageOrderPanel.removeAll();
        orderItemArray = new ArrayList<>();

        // TITLE && RECEIVE BUTTON
        FormTitle title;
        ButtonPrimary receiveBttn;
        if (orderReceived) {
            receiveBttn = new ButtonPrimary("Back");
            title = new FormTitle("Order #" + order.getOrderID() + " RECEIVED");
        } else {
            receiveBttn = new ButtonPrimary("Receive Order");
            title = new FormTitle("Order #" + order.getOrderID());
        }
        // LABEL TO DISPLAY TOTAL CHARGED
        totalChargedLabel = new BizmartLabel("Total Charged");

        // JPANEL
        // holds order details to be received
        JPanel orderDetailsPanel = new JPanel();
        orderDetailsPanel.setBackground(null);
        orderDetailsPanel.setLayout(new BoxLayout(orderDetailsPanel, BoxLayout.Y_AXIS));

        // SCROLLPANE
        // displays orderDetailsPanel
        JScrollPane orderScroll = new JScrollPane();
        orderScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        orderScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        orderScroll.getVerticalScrollBar().setUnitIncrement(16);
        orderScroll.getViewport().setBackground(Colors.SILVER);
        orderScroll.setBorder(null);

        // ADD ORDER DETAILS TO orderDetailsPanel
        double orderSubtotal = 0;
        for (OrderDetails od : details) {
            try {
                ResultSet rs = con.getItem(od.getInventoryID());
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
                // create an order item panel
                OrderItem item = new OrderItem(od, new InventoryItem(id, name, desc, categoryID, subcategoryID, price, wholesale, quantity, restock, max, img));
                if (orderReceived) {
                    item.orderIsReceived(od.getQuantityReceived());
                }
                // add the wholesale price of the item * the quantity ordered to the total that will be charged
                orderSubtotal += wholesale * item.getQuantityReceived();
                // when details only contains 1 item, the JPanel touches the top of the ScrollPane
                // adds space above item, between the ScrollPane and the top of the item's JPanel
                if (details.size() == 1) {
                    orderDetailsPanel.add(Box.createVerticalStrut(10));
                }
                item.setAlignmentX(Component.CENTER_ALIGNMENT);
                orderDetailsPanel.add(item);
                orderDetailsPanel.add(Box.createVerticalStrut(10));
                orderItemArray.add(item);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // CALCULATE TOTAL
        orderSubtotal += orderSubtotal * Money.TAX_RATE;
        totalChargedLabel.setText("Total Charged: $" + Money.DF.format(orderSubtotal));
        orderScroll.getViewport().add(orderDetailsPanel);

        //----- GROUP COMPONENTS -----//
        // group receiveBttn && totalChargedLabel vertically
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setBackground(null);
        // default GridBagConstraints() centers button in panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridy = 0;
        buttonPanel.add(totalChargedLabel, gbc);
        gbc.gridy++;
        buttonPanel.add(receiveBttn, gbc);

        //----- ADD COMPONENTS TO MANAGE ORDER PANEL -----//
        manageOrderPanel.add(title, BorderLayout.NORTH);
        manageOrderPanel.add(orderScroll, BorderLayout.CENTER);
        manageOrderPanel.add(buttonPanel, BorderLayout.SOUTH);

        //----- EVENT LISTENERS -----//
        Action receivePress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!orderReceived) {
                    int qty = 0;
                    int itemID = 0;
                    int orderID = 0;
                    boolean success = false;
                    for (OrderItem oi : orderItemArray) {
                        qty = oi.getQuantityReceived();
                        itemID = oi.getInventoryItem().getItemNumber();
                        orderID = storeOrder.getOrderID();
                        try {
                            con.updateReceivedItem(orderID, itemID, qty);
                            con.updateInventoryItemQuantity(itemID, qty);
                            success = true;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    try {
                        con.updateReceivedOrder(storeOrder.getOrderID(), totalCharged);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        success = false;
                    }

                    if (success) {
                        try {
                            HTML.createStoreOrderReceipt(orderItemArray, orderID);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        showPanel(OPTIONS);
                    }
                } else {
                    showPanel(OPTIONS);
                }
            }
        };
        receiveBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        receiveBttn.getActionMap().put("pressed", receivePress);

        receiveBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                receiveBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                receiveBttn.mouseReleased();
                receivePress.actionPerformed(new ActionEvent(receiveBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });

    }

    /**
     * Initialize and place components in customerDialog. Add event listeners to
     * customerDialog components.
     */
    private void setupCustomerDialog() {
        //----- SETUP COMPONENTS -----//
        // DIALOG
        customerDialog = new PopupDialog(State.frame);
        customerDialog.setTitle("Customer Accounts");
        customerDialog.setLayout(new GridBagLayout());
        customerDialog.setResizable(false);
        customerDialog.getContentPane().setBackground(Colors.SILVER);

        // JLIST
        customerList = new JList(new DefaultListModel());
        customerList.setFont(new Font("Nunito", Font.PLAIN, 18));
        customerList.setForeground(Colors.RICH_BLACK);

        // SCROLLPANE
        customerScroll = new JScrollPane(customerList);
        customerScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        customerScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        customerScroll.getVerticalScrollBar().setUnitIncrement(16);
        customerScroll.getViewport().setBackground(Color.WHITE);
        customerScroll.setBorder(null);
        customerScroll.setPreferredSize(new Dimension(600, 200));

        // REPORT BUTTON
        generateCustomerReportBttn = new ButtonPrimary("Generate Customer Report");
        generateCustomerReportBttn.setPreferredSize(new Dimension(400, 54));
        generateCustomerReportBttn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // TITLE
        FormTitle title = new FormTitle("Accounts Found", 600, 4);
        title.setUnderlineColor(Colors.SILVER);

        //----- GROUP COMPONENTS -----//
        // add horizontal space to center generateCustomerReportBttn
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(Color.WHITE);
        panel.add(Box.createHorizontalStrut(100));
        panel.add(generateCustomerReportBttn);
        panel.add(Box.createHorizontalStrut(100));

        // add vertical space on both sides of generateCustomerReport
        // add a top border above button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new MatteBorder(4, 0, 0, 0, Colors.SILVER));
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(panel);
        buttonPanel.add(Box.createVerticalStrut(5));

        //----- ADD COMPONENTS TO CUSTOMER DIALOG-----//
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

        //----- EVENT LISTENERS -----//
        AbstractAction addPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (customerList.getSelectedValue() != null) {
                    try {
                        HTML.createSalesReport(reportSubTypeComboBox.getSelectedIndex(), datePicker.getDate(), foundCustomersArray.get(customerList.getSelectedIndex()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    customerDialog.dispose();
                }
            }
        };
        generateCustomerReportBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        generateCustomerReportBttn.getActionMap().put("pressed", addPress);

        generateCustomerReportBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                generateCustomerReportBttn.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                generateCustomerReportBttn.mouseReleased();
                addPress.actionPerformed(new ActionEvent(generateCustomerReportBttn, ActionEvent.ACTION_PERFORMED, null));
            }
        });
    }

    /**
     * Initialize and place components in helpDialog. Add event listeners to
     * helpDialog components.
     */
    private void setupHelpDialog() {
        //----- SETUP COMPONENTS -----//
        helpDialog = new PopupDialog(State.frame);
        helpDialog.setTitle("Help");
        helpDialog.setLayout(new BorderLayout());

        // card panel that changes based on cardPanel in managerPanel
        helpCardPanel = new JPanel();
        helpCardPanel.setLayout(new CardLayout());

        // scrollpane for helpCardPanel
        JScrollPane scroll = new JScrollPane();
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(null);

        //----- OPTIONS HELP -----//
        JPanel optionsCard = new JPanel();
        optionsCard.setLayout(new GridBagLayout());
        optionsCard.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        BizmartLabel manager = new BizmartLabel("Manager Help");
        manager.setFont(new Font("Nunito", Font.BOLD, 24));
        optionsCard.add(manager, gbc);

        gbc.gridy += 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        optionsCard.add(new BizmartLabel("Click the \"Manage Inventory\" button to edit inventory items."), gbc);

        gbc.gridy += 1;
        optionsCard.add(new BizmartLabel("Click the \"Add New Product\" button to create a new inventory item."), gbc);

        gbc.gridy += 1;
        optionsCard.add(new BizmartLabel("Click the \"Create New Account\" button to create new user accounts."), gbc);

        gbc.gridy += 1;
        optionsCard.add(new BizmartLabel("Click the \"Manager User Data\" button to edit exisiting accounts."), gbc);

        gbc.gridy += 1;
        optionsCard.add(new BizmartLabel("Click the \"Create Promo Code\" button to create new codes."), gbc);

        gbc.gridy += 1;
        optionsCard.add(new BizmartLabel("Click the \"Generate Reports\" button to create a variety of HTML reports."), gbc);

        gbc.gridy += 1;
        optionsCard.add(new BizmartLabel("Click the \"POS System\" button to use it."), gbc);

        helpCardPanel.add(optionsCard, OPTIONS);

        //----- ALTER ITEM HELP -----//
        JPanel editItemsCard = new JPanel();
        editItemsCard.setLayout(new GridBagLayout());
        editItemsCard.setBackground(Color.WHITE);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        BizmartLabel editInv = new BizmartLabel("Edit Inventory Items");
        editInv.setFont(new Font("Nunito", Font.BOLD, 24));
        editItemsCard.add(editInv, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridy += 1;
        editItemsCard.add(new BizmartLabel("Search for an item and click the thumbnail of the one you want to edit."), gbc);

        gbc.gridy += 1;
        editItemsCard.add(new BizmartLabel("Here you can edit the item's information, such as:"), gbc);

        gbc.gridy += 1;
        gbc.insets = new Insets(0, 0, 3, 10);
        editItemsCard.add(new BizmartLabel(" -Product Image"), gbc);

        gbc.gridy += 1;
        editItemsCard.add(new BizmartLabel(" -Name and Description"), gbc);

        gbc.gridy += 1;
        editItemsCard.add(new BizmartLabel(" -Description"), gbc);

        gbc.gridy += 1;
        editItemsCard.add(new BizmartLabel(" -Retail Price"), gbc);

        gbc.gridy += 1;
        editItemsCard.add(new BizmartLabel(" -Quantity in Stock"), gbc);

        gbc.gridy += 1;
        editItemsCard.add(new BizmartLabel(" -Discontinued Status"), gbc);

        helpCardPanel.add(editItemsCard, EDIT_ITEM);

        //----- ADD NEW ITEM -----//
        JPanel addItemsCard = new JPanel();
        addItemsCard.setLayout(new GridBagLayout());
        addItemsCard.setBackground(Color.WHITE);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        BizmartLabel addInv = new BizmartLabel("Add New Inventory Item");
        addInv.setFont(new Font("Nunito", Font.BOLD, 24));
        addItemsCard.add(addInv, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridy += 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        addItemsCard.add(new BizmartLabel("Fill out required fields and click the \"Add Item\" button to save the new item."), gbc);

        helpCardPanel.add(addItemsCard, NEW_ITEM);

        //----- CREATE NEW ACCOUNT -----//
        JPanel newAccountCard = new JPanel();
        newAccountCard.setLayout(new GridBagLayout());
        newAccountCard.setBackground(Color.WHITE);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        BizmartLabel newAcc = new BizmartLabel("Create New Account");
        newAcc.setFont(new Font("Nunito", Font.BOLD, 24));
        newAccountCard.add(newAcc, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridy += 1;
        newAccountCard.add(new BizmartLabel("You can create accounts for customers and employees (including other managers)."), gbc);

        gbc.gridy += 1;
        newAccountCard.add(new BizmartLabel("Fill out required fields and click the \"Create Account\" button to save the new account."), gbc);

        helpCardPanel.add(newAccountCard, NEW_PERSON);

        //----- SEARCH ACCOUNTS -----//
        JPanel searchAccountCard = new JPanel();
        searchAccountCard.setLayout(new GridBagLayout());
        searchAccountCard.setBackground(Color.WHITE);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        BizmartLabel searchAcc = new BizmartLabel("Search Accounts");
        searchAcc.setFont(new Font("Nunito", Font.BOLD, 24));
        searchAccountCard.add(searchAcc, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridy += 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        searchAccountCard.add(new BizmartLabel("Search for an account to edit by the user's name, email, phone number, or account number."), gbc);

        gbc.gridy += 1;
        searchAccountCard.add(new BizmartLabel("To search, hit enter while the cursor is inside the text field."), gbc);

        gbc.gridy += 1;
        searchAccountCard.add(new BizmartLabel("If accounts associated with the entered information are found, they will appear in a list."), gbc);

        gbc.gridy += 1;
        searchAccountCard.add(new BizmartLabel("Select the account you want to edit, and press the \"Edit Selected Account\" button."), gbc);

        helpCardPanel.add(searchAccountCard, SEARCH_PERSON);

        //----- EDIT ACCOUNT -----//
        JPanel editAccountCard = new JPanel();
        editAccountCard.setLayout(new GridBagLayout());
        editAccountCard.setBackground(Color.WHITE);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        BizmartLabel editAcc = new BizmartLabel("Edit Exisitng Accounts");
        editAcc.setFont(new Font("Nunito", Font.BOLD, 24));
        editAccountCard.add(editAcc, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridy += 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        editAccountCard.add(new BizmartLabel("You can change the access level of user accounts to customer, employee, or manager."), gbc);

        gbc.gridy += 1;
        editAccountCard.add(new BizmartLabel("You cannot view or change security questions, passwords, or usernames."), gbc);

        gbc.gridy += 1;
        editAccountCard.add(new BizmartLabel("You can delete, disable, or enable accounts."), gbc);

        gbc.gridy += 1;
        editAccountCard.add(new BizmartLabel("Deleted accounts cannot be recovered, but disabled accounts can be reinstated"), gbc);

        gbc.gridy += 1;
        editAccountCard.add(new BizmartLabel("Click the \"Save Changes\" button to save account changes."), gbc);

        helpCardPanel.add(editAccountCard, EDIT_PERSON);

        //----- CREATE NEW DISCOUNT CODE -----//
        JPanel createDiscountCard = new JPanel();
        createDiscountCard.setLayout(new GridBagLayout());
        createDiscountCard.setBackground(Color.WHITE);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        BizmartLabel newDis = new BizmartLabel("Create New Discount Code");
        newDis.setFont(new Font("Nunito", Font.BOLD, 24));
        createDiscountCard.add(newDis, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridy += 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        createDiscountCard.add(new BizmartLabel("You can create item or cart level promo codes."), gbc);

        gbc.gridy += 1;
        createDiscountCard.add(new BizmartLabel("Promo codes can be dollar amounts or percentages."), gbc);

        gbc.gridy += 1;
        createDiscountCard.add(new BizmartLabel("Start datePicker is optional, but all codes require an expiration datePicker."), gbc);

        gbc.gridy += 1;
        createDiscountCard.add(new BizmartLabel("Fill out required fields and click the \"Create Discount\" button to save the new code."), gbc);

        helpCardPanel.add(createDiscountCard, NEW_DISCOUNT);

        //----- EDIT DISCOUNT CODE -----//
        JPanel editDiscountCard = new JPanel();
        editDiscountCard.setLayout(new GridBagLayout());
        editDiscountCard.setBackground(Color.WHITE);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        BizmartLabel editDis = new BizmartLabel("Edit Existing Discount Code");
        editDis.setFont(new Font("Nunito", Font.BOLD, 24));
        editDiscountCard.add(editDis, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridy += 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        editDiscountCard.add(new BizmartLabel("You can enable or disable discount codes."), gbc);

        gbc.gridy += 1;
        editDiscountCard.add(new BizmartLabel("You can change everything about the discount code except its ID number."), gbc);

        gbc.gridy += 1;
        editDiscountCard.add(new BizmartLabel("Click the \"Save Changes\" button to save code changes."), gbc);

        helpCardPanel.add(editDiscountCard, EDIT_DISCOUNT);

        //----- GENERATE REPORTS -----//
        JPanel reportCard = new JPanel();
        reportCard.setLayout(new GridBagLayout());
        reportCard.setBackground(Color.WHITE);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        BizmartLabel rep = new BizmartLabel("Generate Reports");
        rep.setFont(new Font("Nunito", Font.BOLD, 24));
        reportCard.add(rep, gbc);

        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridy += 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        reportCard.add(new BizmartLabel("You can create four types of HTML reports:"), gbc);

        gbc.gridy += 1;
        gbc.insets = new Insets(0, 0, 3, 10);
        reportCard.add(new BizmartLabel(" -Store-wide sales reports by day, week, month, or year."), gbc);

        gbc.gridy += 1;
        reportCard.add(new BizmartLabel(" -Sales reports by day, week, month, or year for a specific customer."), gbc);

        gbc.gridy += 1;
        reportCard.add(new BizmartLabel(" -Inventory reports for all items, items in need of restock, or available items."), gbc);

        gbc.gridy += 1;
        reportCard.add(new BizmartLabel(" -Reports containing current employee or customer data."), gbc);

        gbc.gridy += 1;
        reportCard.add(new BizmartLabel("If you're generating reports by month or year, just choose any day within that month/year."), gbc);

        helpCardPanel.add(reportCard, REPORT);

        //----- SEARCH DISCOUNTS -----//
        JPanel searchDiscountCard = new JPanel();
        searchDiscountCard.setLayout(new GridBagLayout());
        searchDiscountCard.setBackground(Color.WHITE);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        BizmartLabel searchDis = new BizmartLabel("Search Discounts");
        searchDis.setFont(new Font("Nunito", Font.BOLD, 24));
        searchDiscountCard.add(searchDis, gbc);

        gbc.gridy += 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        searchDiscountCard.add(new BizmartLabel("All active discounts will apear in the list."), gbc);

        gbc.gridy += 1;
        searchDiscountCard.add(new BizmartLabel("Click the one you want to edit, then click the \"Edit Selected Discount\" button."), gbc);

        helpCardPanel.add(searchDiscountCard, SEARCH_DISCOUNT);

        //----- PLACE STORE ORDER -----//
        JPanel storeOrderCard = new JPanel();
        storeOrderCard.setLayout(new GridBagLayout());
        storeOrderCard.setBackground(Color.WHITE);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        BizmartLabel storeOrder = new BizmartLabel("Placing Store Orders");
        storeOrder.setFont(new Font("Nunito", Font.BOLD, 24));
        storeOrderCard.add(storeOrder, gbc);

        gbc.gridy += 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        storeOrderCard.add(new BizmartLabel("Type an items SKU number in the field and press enter to add it to the order."), gbc);

        gbc.gridy += 1;
        storeOrderCard.add(new BizmartLabel("Press the \"Low Stock Items\" button to check what items need to be reordered."), gbc);

        gbc.gridy += 1;
        storeOrderCard.add(new BizmartLabel("You can't order items that are fully stocked."), gbc);

        gbc.gridy += 1;
        storeOrderCard.add(new BizmartLabel("The quantity of an item in an unreceived order is subtracted from the amount you can purchase."), gbc);

        helpCardPanel.add(storeOrderCard, PLACE_ORDER);

        //----- SEARCH STORE ORDER -----//
        JPanel searchOrderCard = new JPanel();
        searchOrderCard.setLayout(new GridBagLayout());
        searchOrderCard.setBackground(Color.WHITE);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        BizmartLabel searchOrders = new BizmartLabel("Search Store Orders");
        searchOrders.setFont(new Font("Nunito", Font.BOLD, 24));
        searchOrderCard.add(searchOrders, gbc);

        gbc.gridy += 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        searchOrderCard.add(new BizmartLabel("Find orders by year (YYYY), month (YYYY-MM), or day (YYYY-MM-DD)."), gbc);

        gbc.gridy += 1;
        searchOrderCard.add(new BizmartLabel("Only unreceived orders will show up in the list."), gbc);

        gbc.gridy += 1;
        searchOrderCard.add(new BizmartLabel("Select an order from the list and press the \"View Order\" button receive it."), gbc);

        gbc.gridy += 1;
        searchOrderCard.add(new BizmartLabel("Type an order number and press enter to receive it."), gbc);

        gbc.gridy += 1;
        searchOrderCard.add(new BizmartLabel("You can view both received and unreceived orders by number."), gbc);

        helpCardPanel.add(searchOrderCard, SEARCH_ORDER);

        //----- MANAGE STORE ORDER -----//
        JPanel manageOrderCard = new JPanel();
        manageOrderCard.setLayout(new GridBagLayout());
        manageOrderCard.setBackground(Color.WHITE);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        BizmartLabel manageOrders = new BizmartLabel("Manage Store Orders");
        manageOrders.setFont(new Font("Nunito", Font.BOLD, 24));
        manageOrderCard.add(searchOrders, gbc);

        gbc.gridy += 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        manageOrderCard.add(new BizmartLabel("Press the receive order button to close the order and add the products to the inventory."), gbc);

        gbc.gridy += 1;
        manageOrderCard.add(new BizmartLabel("If product is missing from the order, change the received quantity."), gbc);

        gbc.gridy += 1;
        manageOrderCard.add(new BizmartLabel("Receiving an order cannot be undone. Already received orders cannot be altered."), gbc);

        helpCardPanel.add(manageOrderCard, MANAGE_ORDER);

        scroll.setViewportView(helpCardPanel);
        helpDialog.add(scroll, BorderLayout.CENTER);
        helpDialog.setSize(new Dimension(900, 450));
        helpDialog.setLocationRelativeTo(null);
    }

    //-------------------------------------------------//
    //---------- OTHER MANAGER STATE METHODS ----------//
    /**
     * Changes the panel displayed in cardLayout.
     *
     * @param str a String constant that identifies the desired card in card
     * layout.
     */
    public void showPanel(String str) {
        if (str.equals(SEARCH_DISCOUNT)) {
            setupSearchDiscountPanel();
        } else if (str.equals(MANAGE_ORDER)) {
            setupManageOrderPanel(storeOrder, orderDetailsArray);
        }
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, str);

        CardLayout cl2 = (CardLayout) helpCardPanel.getLayout();
        cl2.show(helpCardPanel, str);
        helpCardPanel.setPreferredSize(helpCardPanel.getPreferredSize());
    }

    /**
     * Repaints itemScroll to reflect changes made to inventory items in the
     * item state.
     */
    public void repaintScroll() {
        try {
            if (subcategoriesComboBox.isVisible() && subcategoriesComboBox.getSelectedIndex() != 0) {
                // populate the JScrollPane with thumbnails of items from the selected subcategory
                try {
                    ResultSet r = con.searchEntireSubcategory(subcategoriesComboBox.getSelectedItem().toString());
                    itemScroll.sortItems(r);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            } else {
                ResultSet r;
                if (categoriesComboBox.getSelectedIndex() > 0) {
                    r = con.searchEntireCategory(categoriesComboBox.getSelectedItem().toString());
                    itemScroll.sortItems(r);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Shows restockDialog and updates the items listed to reflect changes to
     * item quantity.
     */
    public void showRestockDialog() {
        try {
            // get all items whose quantity in stock is below its restock threshold
            ResultSet rs = con.getRestockItems();
            if (rs.isBeforeFirst()) {
                restockDialog = new JDialog();
                restockDialog.setSize(new Dimension(700, 350));
                restockDialog.setLayout(new BorderLayout());
                restockDialog.setTitle("Item Restock");
                restockDialog.getContentPane().setBackground(Color.WHITE);
                restockDialog.setLocationRelativeTo(null);

                JScrollPane scroll = new JScrollPane();
                scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                JPanel panel = new JPanel();
                panel.setBackground(null);
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                restockDialog.add(new FormTitle("Items Below Restock Threshold"), BorderLayout.NORTH);
                // add a new label for each item below its restock threshold
                while (rs.next()) {
                    panel.add(new BizmartLabel(rs.getString(1) + " | " + rs.getString(2)));
                }

                // add panel to scrollpane
                scroll.setViewportView(panel);

                // add to restock dialog
                restockDialog.add(scroll, BorderLayout.CENTER);
                restockDialog.revalidate();
                restockDialog.repaint();
                restockDialog.setVisible(true);
            } else {
                restockDialog = new JDialog();
                restockDialog.setTitle("Item Restock");
                restockDialog.getContentPane().setBackground(Color.WHITE);
                restockDialog.setSize(100, 100);
                restockDialog.setLocationRelativeTo(null);
                restockDialog.add(new FormTitle("All Items Above Restock Threshold"));
                restockDialog.setVisible(true);
                showRestockLink.setVisible(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Clears the personSearchField in searchPersonPanel and hides
     * foundAccountPanel.
     */
    public void clearAccountSearch() {
        personSearchField.setText("");
        foundAccountPanel.setVisible(false);
    }

    /**
     * Gets subcategory selected in subcategoriesComboBox in editItemPanel for
     * SQL statements to use.
     *
     * @return the String name of the subcategory
     */
    public String getSelectedSubcategory() {
        if (subcategoriesComboBox.getSelectedIndex() > 0) {
            return subcategoriesComboBox.getSelectedItem().toString();
        } else {
            return "";
        }
    }

    /**
     * Gets category selected in categoriesComboBox in editItemPanel for SQL
     * statements to use.
     *
     * @return the String name of the category
     */
    public String getSelectedCategory() {
        return categoriesComboBox.getSelectedItem().toString();
    }

    /**
     * Sorts storeOrderArray in ascending or descending order.
     *
     * @param s the IconState of a SortButton
     */
    public void sortOrders(SortButton.IconState s) {
        if (s == SortButton.IconState.ASCENDING) {
            Collections.sort(storeOrderArray);
        } else {
            Collections.sort(storeOrderArray, Collections.reverseOrder());
        }

        DefaultListModel model = (DefaultListModel) orderList.getModel();
        model.clear();
        int size = storeOrderArray.size();
        for (int i = 0; i < size; i++) {
            model.add(i, storeOrderArray.get(i));
        }
    }

    //----------------------------------------------//
    //---------- POLYMORPHIC CART METHODS ----------//
    @Override
    protected void addItemToCart(CartItem c) {
        cartArray.add(c);
    }

    @Override
    public void deleteItem(int itemNumber) {
        // delete the item with itemNumber
        for (CartItem c : cartArray) {
            if (c.getInventoryItem().getItemNumber() == itemNumber) {
                cartArray.remove(c);
                break;
            }
        }

        // re-add cartArray to cart
        cartScrollPanel.removeAll();
        for (CartItem i : cartArray) {
            cartScrollPanel.add(Box.createVerticalStrut(15));
            cartScrollPanel.add(i);
        }

        // recalulate total
        calculateTotal();

        // repaint the scrollpane
        cartScroll.revalidate();
        cartScroll.repaint();
    }

    @Override
    public void calculateTotal() {
        double subtotal = 0;
        for (CartItem i : cartArray) {
            subtotal += i.getInventoryItem().getRetailPrice() * i.getQuantity();
        }
        // update subtotal label
        orderTotalLabel.setText("Subtotal: $" + Money.DF.format(subtotal));
    }

    /**
     * Calculates the order total of a store order based on the quantity
     * received.
     */
    public void calculateOrderTotal() {
        double subtotal = 0;
        for (OrderItem oi : orderItemArray) {
            subtotal += oi.getInventoryItem().getWholesalePrice() * oi.getQuantityReceived();
        }
        subtotal += subtotal * Money.TAX_RATE;
        // update subtotal label
        totalChargedLabel.setText("Total Charged: $" + Money.DF.format(subtotal));
        totalCharged = subtotal;
    }

    @Override
    public ArrayList<CartItem> getCart() {
        return cartArray;
    }

    @Override
    public void clearCart() {
        skuField.setText("");

        cartArray = new ArrayList<>();

        // remove items
        cartScrollPanel.removeAll();

        // recalulate total
        calculateTotal();

        // repaint the scrollpane
        cartScroll.revalidate();
        cartScroll.repaint();
    }
}
