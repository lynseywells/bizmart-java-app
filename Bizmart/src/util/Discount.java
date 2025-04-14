package util;

import java.time.LocalDate;

/**
 * Discount is used by the Bizmart application to store details about discount
 * codes for easy access in the program.
 */
public class Discount {

    /**
     * Indicates discount is cart level.
     */
    public static final int CART_LEVEL = 0;

    /**
     * Indicates discount is item level.
     */
    public static final int ITEM_LEVEL = 1;

    /**
     * Indicates discount is a percentage.
     */
    public static final int PERCENT_TYPE = 0;

    /**
     * Indicates discount is a dollar amount.
     */
    public static final int DOLLAR_TYPE = 1;

    /**
     * ID number of the discount in the database.
     */
    private int discountID;

    /**
     * Discount code name. Limited to 20 characters.
     */
    private String discountCode;

    /**
     * Discount code description. Limited to 50 characters.
     */
    private String description;

    /**
     * Discount level, either cart or item.
     */
    private int discountLevel;

    /**
     * For item level discounts, the SKU number of the item to be discounted.
     */
    private int inventoryID;

    /**
     * Discount type, either percentage or dollar.
     */
    private int discountType;

    /**
     * For percentage type discounts, the percentage as a decimal.
     */
    private double percentage;

    /**
     * For dollar type discounts, the dollar amount.
     */
    private double dollarAmount;

    /**
     * Optional start date of discount.
     */
    private LocalDate startDate;

    /**
     * Expiration date of the discount.
     */
    private LocalDate expirationDate;

    /**
     * False if the discount is enabled, true if its disabled.
     */
    private boolean isDisabled;

    /**
     * Discount constructor used to organize discount data for uploading to the
     * database or for use in the program.
     *
     * @param code the discount code
     * @param desc the discount description
     * @param level CART_LEVEL or ITEM_LEVEL
     * @param type PERCENT_TYPE or DOLLAR_TYPE
     * @param exp the discounts expiration date
     */
    public Discount(String code, String desc, int level, int type, LocalDate exp) {
        discountCode = code;
        description = desc;
        discountLevel = level;
        discountType = type;
        expirationDate = exp;
    }

    //-----------------------------//
    //---------- SETTERS ----------//
    /**
     * Sets percentage for percentage type discounts.
     *
     * @param i an integer representing a percentage
     * @throws RuntimeException if percentage is invalid or discount is not the
     * correct type
     */
    public void setPercentage(int i) throws RuntimeException {
        if (discountType == PERCENT_TYPE) {
            if (i <= 100) {
                percentage = i * .01;
            } else {
                throw new RuntimeException("Percentage cannot be over 100%.");
            }
        } else {
            throw new RuntimeException("Discount type is not PERCENT_TYPE. Cannot set percentage.");
        }
    }

    /**
     * Sets percentage for percentage type discounts.
     *
     * @param d a percentage as a decimal
     * @throws RuntimeException if percentage is invalid or discount is not the
     * correct type
     */
    public void setPercentage(double d) throws RuntimeException {
        if (discountType == PERCENT_TYPE) {
            if (d <= 1) {
                percentage = d;
            } else {
                throw new RuntimeException("Percentage cannot be over 100%.");
            }
        } else {
            throw new RuntimeException("Discount type is not PERCENT_TYPE. Cannot set percentage.");
        }
    }

    /**
     * Sets dollar amount for dollar type discounts.
     *
     * @param d the dollar amount
     * @throws RuntimeException if dollar amount is invalid or discount is not
     * the correct type
     */
    public void setDollarAmount(double d) throws RuntimeException {
        if (discountType == DOLLAR_TYPE) {
            if (d > 0) {
                dollarAmount = d;
            } else {
                throw new RuntimeException("Dollar amount must be greater than 0.");
            }
        } else {
            throw new RuntimeException("Discount type is not DOLLAR_TYPE. Cannot set dollar amount.");
        }
    }

    /**
     * Sets start date for the discount.
     *
     * @param date the discounts start date
     */
    public void setStartDate(LocalDate date) {
        startDate = date;
    }

    /**
     * Sets the inventory id for item level discounts.
     *
     * @param id the items SKU number
     * @throws RuntimeException if discount is not ITEM_LEVEL
     */
    public void setInventoryID(int id) throws RuntimeException {
        if (discountLevel == ITEM_LEVEL) {
            inventoryID = id;
        } else {
            throw new RuntimeException("Discount level is not ITEM_LEVEL. Cannot add an inventory ID.");
        }
    }

    /**
     * Sets the discount ID.
     * <p>
     * Discount ID's are automatically assigned when a discount is uploaded to
     * the database. This value should only be set when retrieving a discount
     * from the database for editing or applying it to a transaction.
     *
     * @param id the discounts ID in the database
     */
    public void setDiscountID(int id) {
        discountID = id;
    }

    /**
     * Sets the discount code.
     *
     * @param discountCode the discount code
     */
    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    /**
     * Sets the discounts description.
     *
     * @param description the discounts description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the discounts level.
     *
     * @param discountLevel CART_LEVEL or ITEM_LEVEL
     */
    public void setDiscountLevel(int discountLevel) {
        this.discountLevel = discountLevel;
    }

    /**
     * Sets the discounts type.
     *
     * @param discountType PERCENT_TYPE or DOLLAR_TYPE
     */
    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }

    /**
     * Sets the discounts expiration date.
     *
     * @param expirationDate the expiration date
     */
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Sets the discounts availability.
     *
     * @param b true to disable the discount, false to enable
     */
    public void setDisabled(boolean b) {
        isDisabled = b;
    }

    //-----------------------------//
    //---------- GETTERS ----------//
    /**
     * Gets the discount's percentage amount. Returns 0 if no percentage is set.
     *
     * @return a percentage as a decimal
     */
    public double getPercentage() {
        return percentage;
    }

    /**
     * Gets the discount's dollar amount. Returns 0 if no dollar amount is set.
     *
     * @return a dollar amount as a double
     */
    public double getDollarAmount() {
        return dollarAmount;
    }

    /**
     * Gets the discounts start date. Returns null is discount has no start
     * date.
     *
     * @return the discounts start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Gets the inventory id of the discounted item. Returns 0 if no item is
     * set.
     *
     * @return the items SKU number
     */
    public int getInventoryID() {
        return inventoryID;
    }

    /**
     * Gets the discount ID. Returns 0 if no discount ID is set.
     *
     * @return the discounts ID in the database
     */
    public int getDiscountID() {
        return discountID;
    }

    /**
     * Gets the discount code.
     *
     * @return the discount code
     */
    public String getDiscountCode() {
        return discountCode;
    }

    /**
     * Gets the discounts description.
     *
     * @return the discounts description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the discounts level.
     *
     * @return an integer equal to CART_LEVEL or ITEM_LEVEL
     */
    public int getDiscountLevel() {
        return discountLevel;
    }

    /**
     * Gets the discounts type.
     *
     * @return an integer equal to PERCENT_TYPE or DOLLAR_TYPE
     */
    public int getDiscountType() {
        return discountType;
    }

    /**
     * Gets the discounts expiration date.
     *
     * @return the expiration date
     */
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    /**
     * Gets the discounts availability.
     *
     * @return true if the discount is disabled, false if enabled
     */
    public boolean isDisabled() {
        return isDisabled;
    }
}
