package util;

/**
 * OrderDetails is used by the Bizmart application to organize details about a
 * single line item from an Order/StoreOrder for uploading to the database, or
 * for viewing/receiving a line-item in a StoreOrder. Also used to add line-item
 * details to an Order/StoreOrder's receipt.
 */
public class OrderDetails {

    /**
     * The line items ID (SKU) number.
     */
    private int inventoryID;

    /**
     * The discount ID number. Only used if an item-level discount was applied
     * to the line item.
     */
    private int discountID;

    /**
     * The quantity of the line item ordered.
     */
    private int quantity;

    /**
     * The quantity of the line item that was received. Used only when receiving
     * store orders.
     */
    private int quantityReceived;

    /**
     * OrderDetails constructor used to organize data for uploading a new
     * OrderDetails into the database, for creating a line-item in a receipt, or
     * for receiving a line item in a StoreOrder.
     *
     * @param inventoryID the line item ID (SKU) number
     * @param qty the quantity order
     */
    public OrderDetails(int inventoryID, int qty) {
        this.inventoryID = inventoryID;
        quantity = qty;
        discountID = 0;
    }

    //-----------------------------//
    //---------- GETTERS ----------//
    /**
     * Gets the inventory ID (SKU) number for the line item.
     *
     * @return an items id
     */
    public int getInventoryID() {
        return inventoryID;
    }

    /**
     * Gets the quantity of the item ordered.
     *
     * @return the quantity ordered
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Gets the discount ID of the item-level discount applied to this
     * line-item. Returns 0 if their is no discount applied.
     *
     * @return a discount ID
     */
    public int getDiscountID() {
        return discountID;
    }

    /**
     * Gets the quantity of this item that was received in a StoreOrder.
     *
     * @return the quantity received
     */
    public int getQuantityReceived() {
        return quantityReceived;
    }

    //-----------------------------//
    //---------- SETTERS ----------//
    /**
     * Sets the quantity received in a StoreOrder.
     *
     * @param qty the quantity received
     */
    public void setQuantityReceived(int qty) {
        quantityReceived = qty;
    }

    /**
     * Sets the discount ID of the item-level discount applied to this
     * line-item.
     *
     * @param id a discount ID
     */
    public void setDiscountID(int id) {
        discountID = id;
    }
}
