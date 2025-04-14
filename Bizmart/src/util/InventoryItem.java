package util;

import component.Sizing;
import java.io.File;
import javax.swing.ImageIcon;
import java.nio.file.Files;
import java.awt.*;

/**
 * InventoryItem is used by the Bizmart application to store details about
 * inventory items for easy access in the program.
 */
public class InventoryItem {

    /**
     * Item name. Limited to 200 characters.
     */
    private String itemName;

    /**
     * Item description. Limited to 5000 characters.
     */
    private String itemDesc;

    /**
     * Items category.
     */
    private int categoryID;

    /**
     * Items subcategory, if applicable.
     */
    private int subcategoryID;

    /**
     * Items retail value.
     */
    private double retailPrice;

    /**
     * Items wholesale value.
     */
    private double wholesalePrice;

    /**
     * Current quantity in stock.
     */
    private int quantity;

    /**
     * Restock threshold. When quantity falls below this value, the item needs
     * to be restocked.
     */
    private int restock;

    /**
     * Maximum quantity allowed.
     */
    private int maxQuantity;

    /**
     * Item image file to be uploaded to the database.
     */
    private File imageFile;

    /**
     * Item image to be displayed in the program.
     */
    private ImageIcon image;

    /**
     * Item thumbnail to be displayed in the program or uploaded to the
     * database.
     */
    private Image thumbnail;

    /**
     * Item ID (SKU) number.
     */
    private int itemNumber;

    /**
     * False if the item is available, true if its discontinued.
     */
    private boolean discontinued;

    /**
     * InventoryItem constructor used to organize data for uploading a new
     * InventoryItem into the database. Thumbnail is automatically generated
     * with image File.
     *
     * @param name the item's name
     * @param desc the item's description
     * @param categoryID the item's category ID number
     * @param retail the item's retail price
     * @param wholesale the item's wholesale price
     * @param quantity the quantity currently in stock
     * @param restock the restock threshold
     * @param max the maximum quantity
     * @param img an image of the item as a File
     */
    public InventoryItem(String name, String desc, int categoryID, double retail, double wholesale, int quantity, int restock, int max, File img) {
        itemName = name;
        itemDesc = desc;
        this.categoryID = categoryID;
        retailPrice = retail;
        wholesalePrice = wholesale;
        this.quantity = quantity;
        this.restock = restock;
        imageFile = img;
        // set the image icon using the file to generate a thumbnail
        setImage(img);

        // generate a thumbnail
        thumbnail = Sizing.createThumbnail(image);

        maxQuantity = max;
        discontinued = false;
        subcategoryID = 0;
    }

    /**
     * InventoryItem constructor used to organize InventoryItem data retrieved
     * from the database for use in the program.
     *
     * @param itemNum the item's ID (SKU) number
     * @param name the item's name
     * @param desc the item's description
     * @param categoryID the item's category ID number
     * @param subcategory the item's subcategory ID number (set to 0 if there's
     * no subcategory)
     * @param retail the item's retail price
     * @param wholesale the item's wholesale price
     * @param quantity the quantity in stock
     * @param restock the restock threshold
     * @param max the maximum quantity
     * @param img the full-sized item image
     */
    public InventoryItem(int itemNum, String name, String desc, int categoryID, int subcategory, double retail, double wholesale, int quantity, int restock, int max, ImageIcon img) {
        itemName = name;
        itemDesc = desc;
        this.categoryID = categoryID;
        subcategoryID = subcategory;
        retailPrice = retail;
        wholesalePrice = wholesale;
        this.quantity = quantity;
        this.restock = restock;
        image = img;
        itemNumber = itemNum;
        maxQuantity = max;
        discontinued = false;
    }

    //-----------------------------//
    //---------- GETTERS ----------//
    /**
     * Gets the items name.
     *
     * @return the items name
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * Gets the items description.
     *
     * @return the items description
     */
    public String getDescription() {
        return itemDesc;
    }

    /**
     * Gets the items category ID.
     *
     * @return a category ID
     */
    public int getCategory() {
        return categoryID;
    }

    /**
     * Gets the items subcategory ID. Returns 0 if the item has no subcategory.
     *
     * @return a subcategory ID
     */
    public int getSubcategory() {
        return subcategoryID;
    }

    /**
     * Gets the items retail price in USD.
     *
     * @return the items retail price
     */
    public double getRetailPrice() {
        return retailPrice;
    }

    /**
     * Gets the items wholesale price in USD.
     *
     * @return the items wholesale price
     */
    public double getWholesalePrice() {
        return wholesalePrice;
    }

    /**
     * Gets the items quantity in stock.
     *
     * @return the items quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Gets the items restock threshold.
     *
     * @return the items restock threshold
     */
    public int getRestock() {
        return restock;
    }

    /**
     * Gets the items image file to be uploaded to the database.
     *
     * @return the items image File
     */
    public File getImageFile() {
        return imageFile;
    }

    /**
     * Gets the items thumbnail image to be displayed in the program or uploaded
     * to the database.
     *
     * @return the items thumbnail Image
     */
    public Image getThumbnail() {
        return thumbnail;
    }

    /**
     * Gets the items image icon to be displayed in the program.
     *
     * @return the items ImageIcon
     */
    public ImageIcon getImageIcon() {
        return image;
    }

    /**
     * Gets the items ID (SKU) number.
     *
     * @return the items ID number
     */
    public int getItemNumber() {
        return itemNumber;
    }

    /**
     * Gets the items maximum quantity.
     *
     * @return the max quantity
     */
    public int getMaxQuantity() {
        return maxQuantity;
    }

    /**
     * Gets the items availability.
     *
     * @return true if the item is discontinued, false if its available
     */
    public boolean isDiscontinued() {
        return discontinued;
    }

    //-----------------------------//
    //---------- SETTERS ----------//
    /**
     * Sets the items optional subcategory ID.
     *
     * @param id a subcategory ID
     */
    public void setSubcategory(int id) {
        subcategoryID = id;
    }

    /**
     * Sets the items availability.
     *
     * @param b true to discontinue the item, false to make it available
     */
    public void setDiscontinued(boolean b) {
        discontinued = b;
    }

    /**
     * Sets the quantity in stock.
     *
     * @param i the new quantity
     */
    public void setQuantity(int i) {
        quantity = i;
    }

    /**
     * Sets the items description. Description cannot be longer than 5000
     * characters.
     *
     * @param d the new description
     */
    public void setDescription(String d) {
        if (d.length() <= 5000) {
            itemDesc = d;
        } else {
            throw new RuntimeException("Description must be 5000 characters or less");
        }
    }

    /**
     * Sets the items name. Name cannot be longer than 200 characters.
     *
     * @param n the new name
     */
    public void setName(String n) {
        if (n.length() <= 200) {
            itemName = n;
        } else {
            throw new RuntimeException("Name must be 200 characters or less.");
        }

    }

    /**
     * Sets the items retail price in USD.
     *
     * @param d the items price
     */
    public void setRetailPrice(double d) {
        if (d < wholesalePrice) {
            retailPrice = d;
        } else {
            throw new RuntimeException("Retail price must be less than wholesale price $" + Money.DF.format(wholesalePrice));
        }
    }

    /**
     * Sets the item's image File and converts it to an ImageIcon. Used when an
     * image is selected in JFileChooser to replace the current image.
     *
     * @param f the File of the image
     */
    public void setImage(File f) {
        imageFile = f;
        try {
            byte[] bytes = Files.readAllBytes(f.toPath());
            image = new ImageIcon(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    THIS METHOD WAS GIVING java.sql.SQLException: Parameter index out of range (1 > number of parameters, which is 0).
//    public String createInsertStatement() throws IOException{
//        String query =  "INSERT INTO Inventory(ItemName, ItemDescription, CategoryID, SubcategoryID, RetailPrice, WholesalePrice, Quantity, " +
//                        "RestockThreshold, ItemImage) VALUES ('" + itemName + "', '" + itemDesc + "', " + categoryID + ", ";
//        if (subcategoryID > 0) {
//            query += subcategoryID;
//        } else {
//            query += "null";
//        }
//        
//        query += ", " + retailPrice + ", " + wholesalePrice + ", " + quantity + ", " + restock + ", ?)";
//        
//        return query;
//    }
}
