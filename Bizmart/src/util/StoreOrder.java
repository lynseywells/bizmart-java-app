package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * StoreOrder is used by the Bizmart application to store details about store
 * orders for uploading to the database and generating a receipt.
 */
public class StoreOrder {

    /**
     * Indicates whether the order has been received.
     */
    private boolean received;

    /**
     * The person ID of the employee that placed the order.
     */
    private int employeeID;

    /**
     * The order's ID number in the database.
     */
    private int orderID;

    /**
     * The total cost of the order.
     */
    private double orderTotal;

    /**
     * The date the order was placed.
     */
    private LocalDate orderDate;

    /**
     * The 16 digit credit card number of the card used to purchase the order.
     */
    private long creditCardNumber;

    /**
     * The expiration date of the credit card used to purchase the order, in the
     * format MM/YY or MM/YYYY.
     */
    private String expirationDate;

    /**
     * The 3 digit security code of the credit card used to purchase the order.
     */
    private int ccv;

    /**
     * StoreOrder constructor used to organize data for uploading a new
     * StoreOrder into the database.
     *
     * @param empID person ID of the employee placing the order
     * @param total estimated total cost
     * @param date date of order
     * @param cardNum 16 digit credit card number
     * @param expDate credit card expiration date
     * @param ccv credit card CCV code
     */
    public StoreOrder(int empID, double total, LocalDate date, long cardNum, String expDate, int ccv) {
        employeeID = empID;
        orderTotal = total;
        orderDate = date;
        creditCardNumber = cardNum;
        expirationDate = expDate;
        this.ccv = ccv;
    }

    /**
     * StoreOrder constructor used to organize StoreOrder data retrieved from
     * the database to view and receive orders.
     *
     * @param orderID the orders ID number
     * @param empID person ID of the employee that placed the order
     * @param total estimated or actual order total
     * @param date date of order
     */
    public StoreOrder(int orderID, int empID, double total, String date) {
        this.orderID = orderID;
        employeeID = empID;
        orderTotal = total;
        orderDate = LocalDate.parse(date);
    }

    //-----------------------------//
    //---------- GETTERS ----------//
    /**
     * Gets the orders ID number.
     *
     * @return an order ID
     */
    public int getOrderID() {
        return orderID;
    }

    /**
     * Gets the received status of the order.
     *
     * @return true if the order is received, false if not
     */
    public boolean isReceived() {
        return received;
    }

    /**
     * Gets the date the order was placed as a String in the format
     * "yyyy-MM-dd".
     *
     * @return the order date
     */
    public String getOrderDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return (dtf.format(orderDate));
    }

    //-----------------------------//
    //---------- SETTERS ----------//    
    /**
     * Sets the received status of the order.
     *
     * @param b true to received the order, false otherwise
     */
    public void setReceived(boolean b) {
        received = b;
    }

    /**
     * Sets the person ID of the employee who placed the order.
     *
     * @param id a person ID
     */
    public void setEmployeeID(int id) {
        employeeID = id;
    }

    /**
     * Sets the orders ID number.
     *
     * @param num an order ID
     */
    public void setOrderID(int num) {
        orderID = num;
    }

    /**
     * Creates an SQL insert statement for this StoreOrder object so it can be
     * uploaded to the database table StoreOrders.
     *
     * @return a String SQL insert statement
     */
    public String createInsertStatement() {
        String columns = "INSERT INTO StoreOrders (";
        String values = " VALUES (";

        columns += "EmployeeID, ";
        values += employeeID + ", ";

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        columns += "OrderDate, CreditCardNumber, ExpirationDate, CCV, OrderTotal)";
        values += "'" + dtf.format(orderDate) + "', '" + creditCardNumber + "', '" + expirationDate + "', '" + ccv + "', " + Money.DF.format(orderTotal) + ")";
        return columns + values;
    }
}
