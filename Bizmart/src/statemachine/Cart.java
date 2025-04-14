package statemachine;

import component.labels.FormTitle;
import component.labels.BizmartLabel;
import component.PopupDialog;
import component.Colors;
import component.panels.DropShadowPanel;
import component.panels.CartItem;
import component.buttons.ButtonPrimary;
import component.buttons.Link;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.MatteBorder;

/**
 * Subclass Cart can only be entered if a customer is signed in. This state can
 * enter the states customer, and checkout.
 */
public class Cart extends State {

    /**
     * Stores items added to the cart in customer state.
     */
    private ArrayList<CartItem> items;

    /**
     * The root JPanel for the cart state that is placed in the JFrame when this
     * state is entered. Uses a BorderLayout.
     */
    private DropShadowPanel cartPanel;

    /**
     * JPanel displays items added to the cart.
     */
    private JPanel summaryPanel;

    /**
     * JScrollPane for summaryPanel.
     */
    private JScrollPane scroll;

    /**
     * Button enters customer state when pressed.
     */
    private ButtonPrimary backBttn;

    /**
     * Button enters checkout state when pressed.
     */
    private ButtonPrimary checkoutBttn;

    /**
     * Button clears the cart when pressed.
     */
    private ButtonPrimary clearCart;
    /**
     * JPanel that surrounds clearCart button to prevent layout managers from
     * altering the button's hit-box.
     */
    private JPanel clearCartBttn;

    /**
     * Label dynamically displays order subtotal as items are added to the cart.
     */
    private BizmartLabel orderTotal;

    /**
     * JDialog help menu.
     */
    private PopupDialog helpDialog;

    /**
     * Link shows helpDialog when pressed.
     */
    private Link helpLink;

    /**
     * Cart constructor initializes components needed for the cart state.
     */
    public Cart() {
        //----- INITIALIZE MAIN COMPONENTS -----//
        items = new ArrayList<>();
        // CART PANEL
        cartPanel = new DropShadowPanel(8, Color.WHITE, Colors.SILVER, Colors.DARK_BLUE);
        cartPanel.setLayout(new BorderLayout());

        // SCROLLPANE
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        scroll = new JScrollPane(summaryPanel);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);

        // BUTTONS
        // checkout button
        checkoutBttn = new ButtonPrimary("Checkout");
        checkoutBttn.setBorderColor(Colors.GREEN);
        checkoutBttn.setBackground(Colors.MID_GREEN);
        checkoutBttn.setSelectedBorderColor(Colors.GREEN);
        checkoutBttn.setSelectedBackground(Colors.MID_GREEN);
        checkoutBttn.setPreferredSize(new Dimension(175, 54));
        // button nested inside of JPanel b/c when the button was added to the button panel, 
        // Box.createGlue() made the hitbox bigger 
        JPanel c = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        c.add(checkoutBttn);
        c.setMaximumSize(new Dimension((int) c.getPreferredSize().getWidth(), (int) c.getMaximumSize().getHeight()));
        c.setBackground(null);

        // back button
        backBttn = new ButtonPrimary("Go Back");
        backBttn.setBorderColor(Colors.GREEN);
        backBttn.setBackground(Colors.MID_GREEN);
        backBttn.setSelectedBorderColor(Colors.GREEN);
        backBttn.setSelectedBackground(Colors.MID_GREEN);
        backBttn.setPreferredSize(new Dimension(175, 54));
        // button nested inside of JPanel b/c when the button was added to the button panel, 
        // Box.createGlue() made the hitbox bigger 
        JPanel b = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        b.add(backBttn);
        b.setMaximumSize(new Dimension((int) b.getPreferredSize().getWidth(), (int) b.getMaximumSize().getHeight()));
        b.setBackground(null);

        //clear cart button
        clearCart = new ButtonPrimary("Clear Cart");
        clearCart.setBorderColor(Colors.GREEN);
        clearCart.setBackground(Colors.MID_GREEN);
        clearCart.setSelectedBorderColor(Colors.GREEN);
        clearCart.setSelectedBackground(Colors.MID_GREEN);
        clearCart.setPreferredSize(new Dimension(150, 54));
        // button nested inside of JPanel b/c when the button was added to the button panel, 
        // Box.createGlue() made the hitbox bigger 
        clearCartBttn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        clearCartBttn.add(clearCart);
        clearCartBttn.setMaximumSize(new Dimension((int) b.getPreferredSize().getWidth(), (int) b.getMaximumSize().getHeight()));
        clearCartBttn.setBackground(null);
        clearCartBttn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // LABELS
        FormTitle title = new FormTitle("Cart Summary");
        orderTotal = new BizmartLabel("Order Subtotal: $");

        //HELP DIALOG
        helpLink = new Link("Help");
        helpDialogSetup();

        //----- GROUP COMPONENTS -----//
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

        // ADD BUTTONS & ORDER TOTAL LABEL TO PANEL
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(b);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(orderTotal);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(c);
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new MatteBorder(3, 0, 0, 0, Colors.SILVER));

        //----- SIZE COMPONENTS -----//
        int width = frame.getWidth() - 100;
        int height = frame.getHeight() - 100;
        Dimension d = new Dimension(width, height);
        cartPanel.setPreferredSize(d);
        cartPanel.setMaximumSize(d);

        //----- ADD TO CART PANEL -----//
        cartPanel.add(titlePanel, BorderLayout.NORTH);
        cartPanel.add(scroll, BorderLayout.CENTER);
        cartPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    //--------------------------------------------//
    //---------- ABSTRACT STATE METHODS ----------//
    //---------- ENTER STATE ----------//
    @Override
    public void enterState() {
        State.frame.setTitle("Bizmart Cart");
        State.frame.getContentPane().removeAll();

        // add items to cart
        if (!items.isEmpty()) {
            summaryPanel.removeAll();
            for (CartItem i : items) {
                summaryPanel.add(Box.createVerticalStrut(10));
                i.setAlignmentX(Component.CENTER_ALIGNMENT);
                summaryPanel.add(i);
            }
            summaryPanel.add(Box.createVerticalStrut(10));
            summaryPanel.add(clearCartBttn);
        }

        // calculate subtotal
        calculateTotal();

        // add cart panel to frame
        // default GridBagConstraints centers panel in frame & respects sizing
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        State.frame.add(connectionStatus, gbc);
        gbc.gridy = 1;
        State.frame.add(cartPanel, gbc);

        // repaint the frame
        scroll.revalidate();
        scroll.repaint();
        State.frame.repaint();
        State.frame.setVisible(true);

        // request focus
        scroll.requestFocusInWindow();
    }

    //---------- ADD EVENT LISTENERS TO COMPONENTS ----------//
    @Override
    public void addEventListeners() {
        //----- BACK BUTTON -----//
        AbstractAction backPress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeState(State.customer);

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
                changeState(State.customer);

            }
        });

        //----- CHECKOUT BUTTON -----//
        AbstractAction checkoutPress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (items.size() > 0) {
                    State.checkout = new Checkout();
                    State.checkout.addEventListeners();
                    changeState(State.checkout);
                } else {
                    orderTotal.setText("Add item to cart.");
                    orderTotal.setForeground(Colors.RED);
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

        //----- CLEAR CART BUTTON -----//
        AbstractAction clearPress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearCart();
            }
        };
        clearCart.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        clearCart.getActionMap().put("pressed", clearPress);

        clearCart.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                clearCart.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                clearCart.mouseReleased();
                clearCart();
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
        summaryPanel.removeAll();
        for (CartItem i : items) {
            summaryPanel.add(Box.createVerticalStrut(10));
            summaryPanel.add(i);
        }
        if (items.size() > 0) {
            summaryPanel.add(clearCartBttn);
        }

        // recalulate total
        calculateTotal();

        // repaint the scrollpane
        scroll.revalidate();
        scroll.repaint();
    }

    @Override
    public void calculateTotal() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        double subtotal = 0;
        for (CartItem i : items) {
            subtotal += i.getInventoryItem().getRetailPrice() * i.getQuantity();
        }
        orderTotal.setText("Order Subtotal: $" + df.format(subtotal));
        orderTotal.setForeground(Colors.RICH_BLACK);
    }

    @Override
    public ArrayList<CartItem> getCart() {
        return items;
    }

    @Override
    public void clearCart() {
        items = new ArrayList<>();

        // remove items
        summaryPanel.removeAll();

        // recalulate total
        calculateTotal();

        // repaint the scrollpane
        scroll.revalidate();
        scroll.repaint();
    }

    //----------------------------------------//
    //---------- CART STATE METHODS ----------//
    /**
     * Initialize and place components in helpDialog. Add event listeners to
     * helpDialog components.
     */
    private void helpDialogSetup() {
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
        panel.add(new BizmartLabel("Cart Help"), gbc);
        // (0, 1)
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(new BizmartLabel("Remove items from the cart with the trash-can button."), gbc);
        // (0, 2)
        gbc.gridy = 2;
        panel.add(new BizmartLabel("Change item quantity with the dropdown for each product."), gbc);
        // (0, 3)
        gbc.gridy = 3;
        panel.add(new BizmartLabel("If you sign-out, your cart won't be saved."), gbc);

        helpDialog.add(panel, new GridBagConstraints());
        helpDialog.setSize(new Dimension(700, 450));
        panel.setPreferredSize(new Dimension(630, 350));
        helpDialog.setLocationRelativeTo(null);
    }
}
