package statemachine;

import component.panels.CartItem;
import java.util.ArrayList;
import javax.swing.*;
import util.*;

/**
 * Abstract class State is used to control which features of the application are
 * accessible by dividing functionality into several "states" of a state
 * machine, which can only be entered when certain conditions are met. Each
 * state is a derived class of State.
 * <p>
 * This application is built on the Swing library. When a state is entered, it's
 * interface is displayed in a JFrame.
 */
public abstract class State {

    /* TODO: Add key binding for "ENTER" key to submit forms easier 
    ** Finished Classes: Login, Register */
 /* TODO: Change constants in HTML to enums */
 /* TODO: Add Javadoc to states && add more documentation
    ** Finished Classes: statemachine.*, util.*, component.*, component.buttons.*, component.fields.*, 
    ** component.labels.*
    ** Working on: */
 /* TODO: Update all mouseListeners to utilize Action objects 
    ** Finished Classes: statemachine.*, 
    ** Working on:  */
    /**
     * TODO: DiscountPanel needs key binding
     */
    /**
     * A state of this application. Contains an object that is an instance of a
     * derived class of State. When this object calls the enterState() method,
     * the State displayed in the JFrame changes to this State.
     */
    protected static State customer, cart, item, employee, manager, login, password, register, checkout, account;
    /**
     * The user currently signed into the application. Primarily used to
     * determine which features the user has access to.
     */
    protected static User currentUser;
    /**
     * The customer associated with an order placed by an employee in the
     * employee state.
     */
    protected static Person currentCustomer;
    /**
     * The SQL connection manager, used to retrieve, update, and add information
     * to the database.
     */
    protected static SQL con;
    /**
     * A JLabel that notifies the user if the applications connection to the
     * database is interrupted. Invisible by default.
     */
    protected static JLabel connectionStatus;
    /**
     * Tracks the current state that the application is in. When necessary, this
     * is used to determine which State to change to.
     */
    protected static State currentState;
    /**
     * Tracks the previous state that the application was last in. When
     * necessary, this is used to determine which State to change to.
     */
    protected static State previousState;
    /**
     * The main JFrame window of this application, used to display all States.
     */
    protected static JFrame frame;

    //-----------------------------//
    //---------- SETTERS ----------//
    /**
     * Sets the customer state to a Customer object and calls
     * addEventListeners().
     *
     * @param c An instance of the Customer class
     */
    public static void setCustomerState(Customer c) {
        customer = c;
        customer.addEventListeners();
    }

    /**
     * Sets the register state to a Register object and calls
     * addEventListeners().
     *
     * @param r An instance of the Register class
     */
    public static void setRegisterState(Register r) {
        register = r;
        register.addEventListeners();
    }

    /**
     * Sets the login state to a Login object and calls addEventListeners().
     *
     * @param l An instance of the Register class
     */
    public static void setLoginState(Login l) {
        login = l;
        login.addEventListeners();
    }

    /**
     * Sets the password state to a ForgotPassword object and calls
     * addEventListeners().
     *
     * @param p An instance of the ForgotPassword class
     */
    public static void setPasswordState(ForgotPassword p) {
        password = p;
        password.addEventListeners();
    }

    /**
     * Sets the manager state to a Manager object and calls addEventListeners().
     *
     * @param m An instance of the Manager class
     */
    public static void setManagerState(Manager m) {
        manager = m;
        manager.addEventListeners();
    }

    /**
     * Sets the employee state to an Employee object and calls
     * addEventListeners().
     *
     * @param e An instance of the Employee class
     */
    public static void setEmployeeState(Employee e) {
        employee = e;
        employee.addEventListeners();
    }

    /**
     * Sets the item state based on an InventoryItem, calls addEventListeners,
     * and enters the state.
     * <p>
     * The item state can either be an ItemEdit object or an ItemDetails object,
     * depending on the current state. The ItemEdit state allows users to edit
     * details about an item, while the ItemDetails state shows information
     * about an item and allows users to add it to a cart for purchasing.
     *
     * @param i the InventoryItem object to be displayed in the item state
     */
    public static void createItemState(InventoryItem i) {
        if (currentState instanceof Manager) {
            item = new ItemEdit(i);
        } else {
            item = new ItemDetails(i);
        }
        item.addEventListeners();
        currentState = item;
        currentState.enterState();
    }

    //-----------------------------//
    //---------- GETTERS ----------//
    /**
     * Gets the manager state
     *
     * @return a Manager object
     */
    public static State getManagerState() {
        return manager;
    }

    /**
     * Gets the cart state
     *
     * @return a Cart object
     */
    public static State getCartState() {
        return cart;
    }

    /**
     * Gets the customer state
     *
     * @return a Customer object
     */
    public static State getCustomerState() {
        return customer;
    }

    /**
     * Gets the checkout state
     *
     * @return a Checkout object
     */
    public static State getCheckoutState() {
        return checkout;
    }

    /**
     * Gets the employee state
     *
     * @return an Employee object
     */
    public static State getEmployeeState() {
        return employee;
    }

    /**
     * Gets the JFrame window that displays all states.
     *
     * @return a JFrame
     */
    public static JFrame getFrame() {
        return frame;
    }

    /**
     * Gets the current state of the application.
     *
     * @return a State object
     */
    public static State getCurrentState() {
        return currentState;
    }

    /**
     * Gets the previous state of the application.
     *
     * @return a State object
     */
    public static State getPreviousState() {
        return previousState;
    }

    /**
     * Gets the user currently signed into the application.
     *
     * @return a User object
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Gets the customer associated with an order placed by an employee in the
     * employee state.
     *
     * @return a Person object
     */
    public static Person getCurrentCustomer() {
        return currentCustomer;
    }

    /**
     * Gets the SQL connection manager to perform queries and updates on the
     * database.
     *
     * @return an SQL object
     */
    public static SQL getConnection() {
        return con;
    }

    //-------------------------------------------//
    //---------- STATE CONTROL METHODS ----------//
    /**
     * Changes the previous state to the current state, and the current state to
     * the passed State object, then enters the state.
     *
     * @param s a State object
     */
    protected void changeState(State s) {
        previousState = currentState;
        currentState = s;
        currentState.enterState();
    }

    /**
     * Starts the application by setting the current state to the initial state
     * login, then enters the state.
     */
    public static void startApp() {
        currentState = login;
        login.enterState();
    }

    /**
     * Toggles the visibility of the connection status warning.
     *
     * @param b true (show) or false (hide)
     */
    public static void setConnectionStatusVisible(boolean b) {
        connectionStatus.setVisible(b);
    }

    //--------------------------------------//
    //---------- ABSTRACT METHODS ----------//
    /**
     * Prepares and displays the State object calling this method in the
     * application's static JFrame.
     * <p>
     * Must be implemented by all State subclasses.
     *
     */
    protected abstract void enterState();

    /**
     * Adds event listeners to the components of the State object calling this
     * method.
     * <p>
     * Must be implemented by all State subclasses.
     *
     */
    protected abstract void addEventListeners();

    //-----------------------------------------//
    //---------- POLYMORPHIC METHODS ----------//
    /**
     * Adds the specified item to the cart of the overriding State subclass. The
     * cart is represented by an ArrayList, each item of which is displayed in a
     * JScrollPane.
     *
     * @param c a CartItem object
     */
    protected void addItemToCart(CartItem c) {
    }

    /**
     * Deletes the specified item from the cart of the overriding State
     * subclass. The cart is represented by an ArrayList, each item of which is
     * displayed in a JScrollPane.
     *
     * @param itemNumber an integer representing the items number in the
     * database
     */
    public void deleteItem(int itemNumber) {
    }

    /**
     * Calculates and displays the total of all items in the cart of the
     * overriding State subclass. Called every time an item is added or removed
     * from the cart, or when it's quantity is changed.
     */
    public void calculateTotal() {
    }

    /**
     * Gets the cart from the overriding State subclass.
     *
     * @return an ArrayList containing all items added to the cart
     */
    public ArrayList<CartItem> getCart() {
        return new ArrayList<CartItem>();
    }

    /**
     * Removes all items from the cart of the overriding State subclass. Clears
     * the ArrayList and JScrollPane representing the cart.
     *
     */
    public void clearCart() {
    }
}
