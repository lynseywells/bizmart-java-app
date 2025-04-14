package util;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Order is used by the Bizmart application to store details about orders for
 * uploading to the database and generating a receipt.
 */
public class Order {

    /**
     * The discounts ID, if a discount was applied to the order.
     */
    private int discountID;

    /**
     * The person ID of the customer that purchased the order.
     */
    private int personID;

    /**
     * The person ID of the employee who placed the order, if the order was
     * placed in the employee state.
     */
    private int employeeID;

    /**
     * The date of purchase.
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
     * The total cost of the order.
     */
    private double orderTotal;

    /**
     * Order constructor used to organize data for uploading a new Order into
     * the database.
     *
     * @param personID the ID number of the customer who purchased the order
     * @param orderDate the date of purchase
     * @param ccNumber a 16 digit credit card number
     * @param expDate an expiration date in the format MM/YY or MM/YYYY
     * @param ccv a 3 digit security code
     * @param total the total cost of the order
     */
    public Order(int personID, LocalDate orderDate, long ccNumber, String expDate, int ccv, double total) {
        this.personID = personID;
        this.orderDate = orderDate;
        creditCardNumber = ccNumber;
        expirationDate = expDate;
        this.ccv = ccv;
        discountID = 0;
        employeeID = 0;
        orderTotal = total;
    }

    //-----------------------------//
    //---------- GETTERS ----------// 
    /**
     * Get the ID number of the customer who purchased the order.
     *
     * @return an person ID
     */
    public int getPersonID() {
        return personID;
    }

    /**
     * Get the date the order was placed on in the format "YYYY-MM-DD".
     *
     * @return the order date as a String
     */
    public String getOrderDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return (dtf.format(orderDate));
    }

    //---------- SETTERS ----------//
    /**
     * Sets the employee who placed the order.
     *
     * @param id an employee person ID
     */
    public void setEmployeeID(int id) {
        employeeID = id;
    }

    /**
     * Sets the ID of the discount added to the order.
     *
     * @param discount a discount ID
     */
    public void setDiscountID(int discount) {
        discountID = discount;
    }

    //---------- METHOD TO CREATE SQL UPDATE STATEMENT ----------//
    /**
     * Creates an SQL insert statement for this Order object so it can be
     * uploaded to the database table Orders.
     *
     * @return a String SQL insert statement
     */
    public String createInsertStatement() {
        String columns = "INSERT INTO Orders (";
        String values = " VALUES (";

        if (discountID != 0) {
            columns += "DiscountID, ";
            values += discountID + ", ";
        }
        columns += "PersonID, ";
        values += personID + ", ";

        if (employeeID != 0) {
            columns += "EmployeeID, ";
            values += employeeID + ", ";
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        columns += "OrderDate, CreditCardNumber, ExpirationDate, CCV, OrderTotal)";
        values += "'" + dtf.format(orderDate) + "', " + creditCardNumber + ", '" + expirationDate + "', " + ccv + ", " + Money.DF.format(orderTotal) + ")";
        return columns + values;
    }
}
