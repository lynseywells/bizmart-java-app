package util;

import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import statemachine.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.awt.image.*;
import javax.imageio.*;
import java.awt.*;

/**
 * SQL contains methods to retrieve, update, and add data to the Bizmart
 * database. An object must be instantiated to open a connection to the
 * database. This object opens a thread that continuously checks the database
 * connection to ensure it remains open while the app is running.
 */
public class SQL {

    /* NOTE: this is the original connection to a MySQL database.
    ** private static final String username = "lwellsfa24";
    ** private static final String pass = "cpt_Tstc1";
    ** private static final String url = "jdbc:mysql://13.58.236.216/" + username; */
    /**
     * The database connection String for testing.
     * <p>
     * This connection string saves changes to the database when testing, but it
     * prevents the Jar file from running outside of NetBeans IDE.
     */
    private static final String testUrl = "jdbc:sqlite:src/resources/bizmart-database-2.db";

    /**
     * The database connection String for final build.
     * <p>
     * This connection string works, but all changes to the database are
     * reverted on clean and build when testing. Only use this connection String
     * when converting project to EXE file.
     */
    private static final String url = "jdbc:sqlite::resource:bizmart-database-2.db";

    /**
     * The database connection manager.
     */
    private static Connection con;

    /**
     * Formats dates for SQL queries.
     */
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Used to get the week of the year from a date.
     */
    private static WeekFields wf = WeekFields.of(Locale.getDefault());

    /**
     * SQL constructor opens database connection and starts a thread to
     * continuously check the connection's status while the program is running.
     * If connection is lost, the thread continuously tries to reconnect until
     * the connection is fixed or the program is closed.
     */
    public SQL() {
        try {
            // get the connection
            con = DriverManager.getConnection(url);
            con.setAutoCommit(true);

            // start a new thread to check the connection continuously
            java.util.Timer timer = new java.util.Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    // if the connection is lost, attempt to restart it and set warning visible
                    try {
                        if (!con.isValid(10)) {
                            State.setConnectionStatusVisible(true);
                            con = DriverManager.getConnection(url);
                            con.close();
                        } else {
                            State.setConnectionStatusVisible(false);
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            timer.scheduleAtFixedRate(task, 0, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the connection manager.
     */
    public void close() {
        try {
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-------------------------------------------------------//
    //---------- METHODS FOR FORGOT PASSWORD STATE ----------//
    /**
     * Gets the security question answers for an account by its username.
     * <p>
     * ResultSet contains the columns Answer1 (text), Answer2 (text), Answer3
     * (text).
     *
     * @param username an accounts username
     * @return a ResultSet containing 3 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getAccountSecurityAnswers(String username) throws SQLException {
        String u = username.toLowerCase();
        String query = "SELECT Answer1, Answer2, Answer3 "
                + "FROM Login WHERE Username = '" + u + "' COLLATE NOCASE";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets the security questions for an account by its username.
     * <p>
     * ResultSet contains the column QuestionPrompt (text).
     *
     * @param username an accounts username
     * @return a ResultSet containing 1 column.
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getAccountSecurityQuestions(String username) throws SQLException {
        String u = username.toLowerCase();
        //get the question ids
        String query = "SELECT Question1, Question3 "
                + "FROM Login WHERE Username = '" + u + "' COLLATE NOCASE";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        int first = 0;
        int last = 0;
        if (rs.next()) {
            first = Integer.parseInt(rs.getString(1));
            last = Integer.parseInt(rs.getString(2));
        }

        //get the question prompts
        query = "SELECT QuestionPrompt FROM SecurityQuestions WHERE QuestionID BETWEEN "
                + first + " AND " + last;
        ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Updates an accounts password by its username. Password must be between 8
     * and 20 characters (inclusive).
     *
     * @param username the accounts username
     * @param password the accounts new password
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void updatePassword(String username, String password) throws SQLException {
        String u = username.toLowerCase();
        String query = "UPDATE Login SET Password = '" + User.encodePassword(password)
                + "' WHERE Username = '" + u + "' COLLATE NOCASE";
        PreparedStatement ps = con.prepareStatement(query);
        ps.executeUpdate();
    }

    //---------------------------------------------//
    //---------- METHODS FOR LOGIN STATE ----------//
    /**
     * Gets an account's information by entered username and password. If the
     * returned ResultSet contains a row, then the username and password are
     * correct.
     * <p>
     * ResultSet contains the columns PersonID (int), Username (text), Password
     * (text), Question1 (int), Question2 (int), Question3 (int), Answer1 (int),
     * Answer2 (int), Answer3 (int), PositionID (int).
     *
     * @param username the accounts username
     * @param password the accounts password
     * @return a ResultSet containing 10 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getAccount(String username, String password) throws SQLException {
        String u = username.toLowerCase();
        String query = "SELECT PersonID, Username, Password, Question1, Question2, Question3, "
                + "Answer1, Answer2, Answer3, PositionID FROM Login WHERE Username = '" + u
                + "' COLLATE NOCASE AND Password = '" + password + "' AND (AccountDisabled = 0 OR AccountDisabled IS NULL) "
                + "AND (AccountDeleted = 0 OR AccountDeleted IS NULL)";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    //------------------------------------------------//
    //---------- METHODS FOR REGISTER STATE ----------//
    /**
     * Gets all unique question set IDs.
     * <p>
     * ResultSEt contains the column QuestionSetID (int).
     *
     * @return a ResultSet with 1 column
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getUniqueQuestionSetIDs() throws SQLException {
        String query = "SELECT DISTINCT QuestionSetID FROM SecurityQuestions";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets the 3 question prompts from a set by the question set's ID number.
     * Use getUniqueQuestionSetIDs() to get ID numbers.
     * <p>
     * ResultSet contains the columns QuestionPrompt (text), QuestionID (int).
     *
     * @param questionsetid the integer ID for a set of 3 security questions
     * @return a ResultSet with 2 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getSecurityQuestionSet(int questionsetid) throws SQLException {
        String query = "SELECT QuestionPrompt, QuestionID FROM SecurityQuestions WHERE QuestionSetID = " + questionsetid;
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets all existing usernames.
     * <p>
     * ResultSet contains the column Username (text).
     *
     * @return a ResultSet with 1 column
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getAllUsernames() throws SQLException {
        PreparedStatement ps = con.prepareStatement("SELECT Username FROM Login");
        return ps.executeQuery();
    }

    /**
     * Gets all existing emails.
     * <p>
     * ResultSet contains the column Email (text).
     *
     * @return a ResultSet with 1 column
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getAllEmails() throws SQLException {
        PreparedStatement ps = con.prepareStatement("SELECT Email FROM Person");
        return ps.executeQuery();
    }

    /**
     * Creates a new account by inserting rows into the Person and Login tables
     * in the database.
     *
     * @param p a Person object
     * @param u a User object
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     *
     */
    public void insertAccount(User u, Person p) throws SQLException {
        PreparedStatement ps = con.prepareStatement(p.createInsertStatement());
        ps.executeUpdate();

        //statement to get the PersonID of p
        String query = "SELECT PersonID FROM Person WHERE Email = '" + p.getEmail() + "' COLLATE NOCASE";
        ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        //set the person id of the user
        rs.next();
        u.setPersonID(Integer.parseInt(rs.getString(1)));

        //add the user to the table
        ps = con.prepareStatement(u.createInsertStatement());
        ps.executeUpdate();
    }

    //-------------------------------------------------------//
    //---------- METHODS TO SEARCH INVENTORY ITEMS ----------//
    /**
     * Gets all inventory item categories.
     * <p>
     * ResultSet contains columns CategoryID (int), CategoryName (text),
     * CategoryDescription (text).
     *
     * @return a ResultSet with 3 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getItemCategories() throws SQLException {
        // CategoryID, CategoryName, CategoryDescription
        String query = "SELECT * FROM Categories";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets all subcategories for the passed category.
     * <p>
     * ResultSet contains column SubcategoryName (text).
     *
     * @param category the categories name
     * @return a ResultSet with 1 column
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getSubcategory(String category) throws SQLException {
        String query = "SELECT CategoryID FROM Categories WHERE CategoryName = '" + category + "'";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        int id = 0;

        if (rs.isBeforeFirst()) {
            rs.next();
            id = Integer.parseInt(rs.getString(1));
        }

        query = "SELECT SubcategoryName FROM Subcategories WHERE CategoryID = " + id;
        ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets a subcategory's ID number.
     * <p>
     * ResultSet contains column SubcategoryID (int).
     *
     * @param sub the subcategories name
     * @return a ResultSet with 1 column
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getSubcategoryID(String sub) throws SQLException {
        String query = "SELECT SubcategoryID FROM Subcategories WHERE SubcategoryName = '" + sub + "'";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets all items that contain a keyword somewhere in their name. If a
     * category or subcategory is selected, the search is restricted to that
     * group.
     * <p>
     * ResultSet contains the columns ItemName (text), RetailPrice (double),
     * Quantity (int), ItemThumbnail (blob), InventoryID (int). ResultSet used
     * to create item thumbnails.
     *
     * @param keyword the word to search
     * @param s the current State of the state machine
     * @return a ResultSet with 5 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet searchItems(String keyword, State s) throws SQLException {
        String query;
        String category;
        String subcategory;
        if (State.getCurrentUser() == null || State.getCurrentUser().getPosition() == User.CUSTOMER) {
            category = ((Customer) s).getSelectedCategory();
            subcategory = ((Customer) s).getSelectedSubcategory();
        } else {
            category = ((Employee) s).getSelectedCategory();
            subcategory = ((Employee) s).getSelectedSubcategory();
        }

        if (category.equals("Shop by Category")) {
            // if no category is selected, search all categories
            query = "SELECT ItemName, RetailPrice, Quantity, ItemThumbnail, InventoryID FROM Inventory WHERE ItemName LIKE '%" + keyword + "%' LIMIT 100";
        } else {
            // if a category is selected but no subcategory is selected, search by category
            if (subcategory.equals("") || subcategory.equals("Choose a Subcategory")) {
                query = "SELECT CategoryID FROM Categories WHERE CategoryName = '" + category + "'";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int id = Integer.parseInt(rs.getString(1));
                query = "SELECT ItemName, RetailPrice, Quantity, ItemThumbnail, InventoryID FROM Inventory WHERE ItemName LIKE '%"
                        + keyword + "%' AND CategoryID = " + id + " AND (Discontinued = 0 OR Discontinued IS NULL) LIMIT 100";
            } else {
                // if a subcategory is selected, search through subcategory
                query = "SELECT SubcategoryID FROM Subcategories WHERE SubcategoryName = '" + subcategory + "'";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int id = Integer.parseInt(rs.getString(1));
                query = "SELECT ItemName, RetailPrice, Quantity, ItemThumbnail, InventoryID FROM Inventory WHERE ItemName LIKE '%"
                        + keyword + "%' AND SubcategoryID = " + id + " AND (Discontinued = 0 OR Discontinued IS NULL) LIMIT 100";
            }
        }

        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets all items that contain the keywords somewhere in their name or
     * description. If a category or subcategory is selected, the search is
     * restricted to that group.
     * <p>
     * ResultSet contains the columns ItemName (text), RetailPrice (double),
     * Quantity (int), ItemThumbnail (blob), InventoryID (int). ResultSet used
     * to create item thumbnails.
     *
     * @param keywords the words to search
     * @param s the current State of the state machine
     * @return a ResultSet with 5 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet searchItems(String[] keywords, State s) throws SQLException {
        String query;
        String category;
        String subcategory;
        if (State.getCurrentUser() == null || State.getCurrentUser().getPosition() == User.CUSTOMER) {
            category = ((Customer) s).getSelectedCategory();
            subcategory = ((Customer) s).getSelectedSubcategory();
        } else {
            category = ((Employee) s).getSelectedCategory();
            subcategory = ((Employee) s).getSelectedSubcategory();
        }

        if (category.equals("Shop by Category")) {
            // if no category is selected, search all categories
            query = "SELECT ItemName, RetailPrice, Quantity, ItemThumbnail, InventoryID FROM Inventory WHERE ItemName LIKE '%"
                    + keywords[0] + "%'";
            for (int i = 1; i < keywords.length; i++) {
                query += " AND ItemName LIKE '%" + keywords[i] + "%'";
            }
            query += " OR ItemDescription LIKE '%";
            for (int i = 0; i < keywords.length; i++) {
                if (i == keywords.length - 1) {
                    query += keywords[i] + "%'";
                } else {
                    query += keywords[i] + " ";
                }
            }
        } else {
            // if a category is selected but no subcategory is selected, search by category
            if (subcategory.equals("") || subcategory.equals("Choose a Subcategory")) {
                query = "SELECT CategoryID FROM Categories WHERE CategoryName = '" + category + "'";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int id = Integer.parseInt(rs.getString(1));

                query = "SELECT ItemName, RetailPrice, Quantity, ItemThumbnail, InventoryID FROM Inventory WHERE ItemName LIKE '%"
                        + keywords[0] + "%'";
                for (int i = 1; i < keywords.length; i++) {
                    query += " AND ItemName LIKE '%" + keywords[i] + "%'";
                }
                query += " AND CategoryID = " + id + " OR ItemDescription LIKE '%";
                for (int i = 0; i < keywords.length; i++) {
                    if (i == keywords.length - 1) {
                        query += keywords[i] + "%'";
                    } else {
                        query += keywords[i] + " ";
                    }
                }
                query += " AND CategoryID = " + id;
            } else {
                // if a subcategory is selected, search through subcategory
                query = "SELECT SubcategoryID FROM Subcategories WHERE SubcategoryName = '" + subcategory + "'";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int id = Integer.parseInt(rs.getString(1));
                query = "SELECT ItemName, RetailPrice, Quantity, ItemThumbnail, InventoryID FROM Inventory WHERE ItemName LIKE '%"
                        + keywords[0] + "%'";
                for (int i = 1; i < keywords.length; i++) {
                    query += " AND ItemName LIKE '%" + keywords[i] + "%'";
                }
                query += " AND SubcategoryID = " + id + " OR ItemDescription LIKE '%";
                for (int i = 0; i < keywords.length; i++) {
                    if (i == keywords.length - 1) {
                        query += keywords[i] + "%'";
                    } else {
                        query += keywords[i] + " ";
                    }
                }
                query += " AND SubcategoryID = " + id;
            }
        }
        query += " AND (Discontinued = 0 OR Discontinued IS NULL) LIMIT 100";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets 100 inventory items from a category.
     * <p>
     * ResultSet contains the columns ItemName (text), RetailPrice (double),
     * Quantity (int), ItemThumbnail (blob), InventoryID (int). ResultSet used
     * to create item thumbnails.
     *
     * @param category the category's name
     * @return a ResultSet with 5 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet searchCategory(String category) throws SQLException {
        String query = "SELECT CategoryID FROM Categories WHERE CategoryName = '" + category + "'";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int id = Integer.parseInt(rs.getString(1));

        query = "SELECT ItemName, RetailPrice, Quantity, ItemThumbnail, InventoryID FROM Inventory WHERE CategoryID = " + id
                + " AND (Discontinued = 0 OR Discontinued IS NULL) LIMIT 100";
        ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets 100 inventory items from a subcategory.
     * <p>
     * ResultSet contains the columns ItemName (text), RetailPrice (double),
     * Quantity (int), ItemThumbnail (blob), InventoryID (int). ResultSet used
     * to create item thumbnails.
     *
     * @param subcategory the subcategory's name
     * @return a ResultSet with 5 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet searchSubcategory(String subcategory) throws SQLException {
        String query = "SELECT SubcategoryID FROM Subcategories WHERE SubcategoryName = '" + subcategory + "'";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int id = Integer.parseInt(rs.getString(1));

        query = "SELECT ItemName, RetailPrice, Quantity, ItemThumbnail, InventoryID FROM Inventory WHERE SubcategoryID = " + id
                + " AND (Discontinued = 0 OR Discontinued IS NULL) LIMIT 100";
        ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets the top 100 items that are not discontinued. Used when no category
     * is selected, or when the Customer state is entered.
     * <p>
     * Contains columns ItemName (text), RetailPrice (double), Quantity (int),
     * ItemThumbnail (blob), InventoryID (int). ResultSet used to create item
     * thumbnails.
     *
     * @return a ResultSet with 5 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet searchTop100() throws SQLException {
        String query = "SELECT ItemName, RetailPrice, Quantity, ItemThumbnail, InventoryID FROM Inventory WHERE Discontinued = 0 OR Discontinued IS NULL LIMIT 100";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    //---------------------------------------------------------------------------//
    //---------- METHODS TO CREATE, UPDATE, OR VIEW DETAILS OF AN ITEM ----------//
    /**
     * Gets an item by it's InventoryID.
     * <p>
     * ResultSet contains the columns InventoryID (int), ItemName (text),
     * ItemDescription (text), CategoryID (int), SubcategoryID (int),
     * RetailPrice (double), WholesalePrice (double), Quantity (int),
     * RestockThreshold (int), ItemImage (blob), Discontinued (int), MaxQuantity
     * (int).
     *
     * @param itemNumber item ID (SKU) number
     * @return a ResultSet with 12 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getItem(int itemNumber) throws SQLException {
        String query = "SELECT InventoryID, ItemName, ItemDescription, CategoryID, SubcategoryID, RetailPrice, "
                + "WholesalePrice, Quantity, RestockThreshold, ItemImage, Discontinued, MaxQuantity "
                + "FROM Inventory WHERE InventoryID = " + itemNumber;
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Inserts a new inventory item into the database.
     *
     * @param i the InventoryItem to upload
     * @throws IOException indicates a failed or interrupted I/O operation
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void insertInventoryItem(InventoryItem i) throws IOException, SQLException {
        // convert the image file into a byte array to add it to the database
        byte[] imageBytes = Files.readAllBytes(i.getImageFile().toPath());

        // convert the thumbnail image into a byte array
        // needs to be done differently because this is resized in an ImageIcon, and cannot be converted into a File
        Image thumb = i.getThumbnail();
        // create a new buffered image with transparancy (TYPE_INT_ARGB) at the same width && height of the thumbnail
        BufferedImage thumbnail = new BufferedImage(thumb.getWidth(null), thumb.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // draw the image onto the buffered image
        Graphics2D g2d = thumbnail.createGraphics();
        g2d.drawImage(thumb, 0, 0, null);
        g2d.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(thumbnail, "jpg", out);
        byte[] thumbnailBytes = out.toByteArray();

        String query;
        int subcategory = i.getSubcategory();

        if (subcategory > 0) {
            query = "INSERT INTO Inventory(ItemName, ItemDescription, CategoryID, SubcategoryID, RetailPrice, WholesalePrice, Quantity, "
                    + "RestockThreshold, MaxQuantity, ItemImage, ItemThumbnail) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            query = "INSERT INTO Inventory(ItemName, ItemDescription, CategoryID, RetailPrice, WholesalePrice, Quantity, "
                    + "RestockThreshold, MaxQuantity, ItemImage, ItemThumbnail) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }

        PreparedStatement ps = con.prepareStatement(query);
        int col = 4;
        ps.setString(1, i.getItemName());
        ps.setString(2, i.getDescription());
        ps.setInt(3, i.getCategory());

        if (subcategory > 0) {
            ps.setInt(4, i.getSubcategory());
            col++;
        }

        ps.setDouble(col, i.getRetailPrice());
        col++;
        ps.setDouble(col, i.getWholesalePrice());
        col++;
        ps.setInt(col, i.getQuantity());
        col++;
        ps.setInt(col, i.getRestock());
        col += 1;
        ps.setInt(col, i.getMaxQuantity());
        // create a binary input stream for the byte array & add it to the SQL statement
        col += 1;
        ps.setBytes(col, imageBytes);

        col += 1;
        ps.setBytes(col, thumbnailBytes);
        ps.executeUpdate();
    }

    /**
     * Updates the columns ItemName, ItemDescription, RetailPrice, Quantity, and
     * Discontinued of an inventory item.
     *
     * @param i an InventoryItem with updated information
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void updateInventoryItem(InventoryItem i) throws SQLException {
        String query = "UPDATE Inventory SET ItemName = ?, ItemDescription = ?, RetailPrice = ?, Quantity = ?, Discontinued = ?"
                + " WHERE InventoryID = " + i.getItemNumber();
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, i.getItemName());
        ps.setString(2, i.getDescription());
        ps.setDouble(3, i.getRetailPrice());
        ps.setInt(4, i.getQuantity());
        int d = 0;
        if (i.isDiscontinued()) {
            d = 1;
        }
        ps.setInt(5, d);
        ps.executeUpdate();
    }

    //------------------------------------------------//
    //---------- METHODS FOR CHECKOUT STATE ----------//
    /**
     * Returns columns needed to determine if a discount code is valid for the
     * current transaction. If the ResultSet is empty, the discount code doesn't
     * exist or is disabled.
     * <p>
     * ResultSet contains the columns DiscountID (int), DiscountCode (text),
     * DiscountLevel (int), InventoryID (int), DiscountType (int),
     * DiscountPercentage (double), DiscountDollarAmount (double), StartDate
     * (text), ExpirationDate (text).
     *
     * @param code the discount code to retrieve
     * @return a ResultSet with 10 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet checkDiscountCode(String code) throws SQLException {
        String query = "SELECT DiscountID, DiscountCode, DiscountLevel, InventoryID, DiscountType, "
                + "DiscountPercentage, DiscountDollarAmount, StartDate, ExpirationDate "
                + "FROM Discounts WHERE DiscountCode = '" + code + "' AND (Disabled = 0 OR Disabled IS NULL)";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets an item's retail price by it's InventoryID.
     * <p>
     * ResultSet contains the column RetailPrice (double).
     *
     * @param itemNumber item ID (SKU) number
     * @return a ResultSet with 1 column
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getItemPrice(int itemNumber) throws SQLException {
        String query = "SELECT RetailPrice FROM Inventory WHERE InventoryID = " + itemNumber;
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Inserts a customer order into the database.
     *
     * @param o the Order object to upload
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void insertOrder(Order o) throws SQLException {
        String query = o.createInsertStatement();
        PreparedStatement ps = con.prepareStatement(query);
        ps.executeUpdate();
    }

    /**
     * Inserts a store order into the database.
     *
     * @param o the StoreOrder object to upload
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void insertOrder(StoreOrder o) throws SQLException {
        String query = o.createInsertStatement();
        PreparedStatement ps = con.prepareStatement(query);
        ps.executeUpdate();
    }

    /**
     * Inserts details about one item in a customer order to the database.
     *
     * @param od the OrderDetails about an item
     * @param o the Order the OrderDetails are for
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void insertOrderDetails(OrderDetails od, Order o) throws SQLException {
        // get order id
        String query = "SELECT MAX(OrderID) FROM Orders";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int orderID = rs.getInt(1);

        String columns = "INSERT INTO OrderDetails (OrderID, InventoryID, ";
        String values = "VALUES (" + orderID + ", " + od.getInventoryID() + ", ";

        if (od.getDiscountID() != 0) {
            columns += "DiscountID, ";
            values += od.getDiscountID() + ", ";
        }

        columns += "Quantity)";
        values += od.getQuantity() + ")";

        ps = con.prepareStatement(columns + values);
        ps.executeUpdate();
    }

    /**
     * Inserts details about one item in a store order to the database.
     *
     * @param od the OrderDetails about an item
     * @param o the StoreOrder the OrderDetails are for
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void insertOrderDetails(OrderDetails od, StoreOrder o) throws SQLException {
        // get order id
        String query = "SELECT MAX(OrderID) FROM StoreOrders";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int orderID = rs.getInt(1);

        String columns = "INSERT INTO StoreOrderDetails (OrderID, InventoryID, ";
        String values = "VALUES (" + orderID + ", " + od.getInventoryID() + ", ";

        columns += "Quantity)";
        values += od.getQuantity() + ")";

        ps = con.prepareStatement(columns + values);
        ps.executeUpdate();
    }

    /**
     * Subtracts the ordered quantity of an item from the quantity in stock when
     * a customer order is placed.
     *
     * @param od the OrderDetails containing the inventory item and quantity
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void subtractInventory(OrderDetails od) throws SQLException {
        String query = "UPDATE Inventory SET Quantity = Quantity - " + od.getQuantity() + " WHERE InventoryID = " + od.getInventoryID();
        PreparedStatement ps = con.prepareStatement(query);
        ps.executeUpdate();
    }

    /**
     * Gets the order ID number of the newest order.
     *
     * @return an order number
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public int getOrderNumber() throws SQLException {
        String query = "SELECT MAX(OrderID) FROM Orders";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    /**
     * Gets the store order ID number of the newest order.
     *
     * @return a store order number
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public int getStoreOrderNumber() throws SQLException {
        String query = "SELECT MAX(OrderID) FROM StoreOrders";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    /**
     * Gets the first and last name of a person. Used when generating an order
     * receipt to display the customer's name, or the employee who placed the
     * orders name if applicable.
     *
     * @param personID person ID number in the database
     * @return a String containing a first and last name.
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public String getPersonName(int personID) throws SQLException {
        String query = "SELECT FirstName, LastName FROM Person WHERE PersonID = " + personID;
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString(1) + " " + rs.getString(2);
    }

    //-------------------------------------------------------------------------------//
    //---------- METHODS TO ADD CUSTOMERS AND DISCOUNTS TO POS TRANSACTION ----------//
    /**
     * Gets customers based on a phone number, id number, email address, first
     * name, or last name.
     * <p>
     * ResultSet contains the columns PersonID (int), FirstName (text),
     * MiddleName (text), LastName (text), Suffix (text), AddressLine1 (text),
     * AddressLine2 (text), AddressLine3 (text), City (text), Zip (text), State
     * (text), Email (text), Phone1 (text), Phone2 (text), PersonDeleted (int),
     * AccountDisabled (int)
     *
     * @param identifier the search criteria
     * @return a ResultSet with 16 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getCustomer(String identifier) throws SQLException {
        String query;
        if (Validation.isNumeric(identifier)) {
            query = "SELECT DISTINCT Person.*, Login.AccountDisabled FROM Person INNER JOIN Login ON Person.PersonID = Login.PersonID WHERE (Phone1 = '" + identifier + "'"
                    + " OR PersonID = " + identifier + ") AND (PersonDeleted = 0 OR PersonDeleted IS NULL) AND "
                    + "(AccountDisabled = 0 OR AccountDisabled IS NULL)";
        } else {
            query = "SELECT DISTINCT Person.*, Login.AccountDisabled FROM Person INNER JOIN Login ON Person.PersonID = Login.PersonID WHERE (Email = '"
                    + identifier + "' COLLATE NOCASE OR FirstName = '" + identifier + "' COLLATE NOCASE OR LastName = '" + identifier + "' COLLATE NOCASE) "
                    + "AND (PersonDeleted = 0 OR PersonDeleted IS NULL) AND (AccountDisabled = 0 OR AccountDisabled IS NULL)";
        }

        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets customers based on first and last name.
     * <p>
     * ResultSet contains the columns PersonID (int), FirstName (text),
     * MiddleName (text), LastName (text), Suffix (text), AddressLine1 (text),
     * AddressLine2 (text), AddressLine3 (text), City (text), Zip (text), State
     * (text), Email (text), Phone1 (text), Phone2 (text), PersonDeleted (int),
     * AccountDisabled (int)
     *
     * @param first first name
     * @param last last name
     * @return a ResultSet with 16 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getCustomer(String first, String last) throws SQLException {
        String query = "SELECT DISTINCT Person.*, Login.AccountDisabled FROM Person INNER JOIN Login ON Person.PersonID = Login.PersonID  WHERE FirstName = '" + first + "' COLLATE NOCASE AND LastName = '" + last
                + "' COLLATE NOCASE AND (PersonDeleted = 0 OR PersonDeleted IS NULL) AND (AccountDisabled = 0 OR AccountDisabled IS NULL)";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        return rs;
    }

    /**
     * Gets all currently active discounts.
     * <p>
     * ResultSet contains the columns DiscountCode (text), Description (text)
     *
     * @return a ResultSet with 2 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getActiveDiscounts() throws SQLException {
        String query = "SELECT DiscountCode, Description FROM Discounts WHERE (StartDate <= '" + LocalDate.now() + "' OR StartDate IS NULL)"
                + " AND ExpirationDate >= '" + LocalDate.now() + "' AND (Disabled IS NULL OR Disabled = 0)";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets all discount codes that aren't expired, including disabled
     * discounts.
     *
     * @return a ResultSet with 3 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getAllDiscounts() throws SQLException {
        String query = "SELECT DiscountCode, Description, Disabled FROM Discounts WHERE (StartDate <= '" + LocalDate.now() + "' OR StartDate IS NULL)"
                + " AND ExpirationDate >= '" + LocalDate.now() + "'";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    //----------------------------------------------------------------------------//
    //---------- METHODS TO UPDATE ACCOUNT INFORMATION IN MANAGER STATE ----------//
    /**
     * Gets a users account.
     * <p>
     * ResultSet contains the columns LoginID (int), PersonID (int), Username
     * (text), Password (text), Question1 (int), Question2 (int), Question3
     * (int), Answer1 (text), Answer2 (text), Answer3 (text), PositionID (int),
     * AccountDisabled (int), AccountDeleted (int)
     *
     * @param personID the ID number of the account holder
     * @return a ResultSet with 13 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getUser(int personID) throws SQLException {
        String query = "SELECT * FROM Login WHERE PersonID = " + personID;
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Updates a row in the Person database.
     *
     * @param p the Person to update
     * @param ogEmail the original email before changes
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void updatePerson(Person p, String ogEmail) throws SQLException {
        String query = "UPDATE Person SET FirstName = ?, LastName = ?, AddressLine1 = ?, Zip = ?, City = ?";
        if (p.getAddressLine2() != null && !p.getAddressLine2().equals("")) {
            query += ", AddressLine2 = '" + p.getAddressLine2() + "'";
        }
        if (p.getAddressLine3() != null && !p.getAddressLine3().equals("")) {
            query += ", AddressLine3 = '" + p.getAddressLine3() + "'";
        }
        if (p.getPhone1() != null && !p.getPhone1().equals("")) {
            query += ", Phone1 = '" + p.getPhone1() + "'";
        }

        // only update the email if it was changed
        // (Email column must be unique, attempting to update with the same email fails)
        if (!p.getEmail().equals(ogEmail)) {
            query += ", Email = '" + p.getEmail() + "'";
        }

        query += " WHERE PersonID = " + p.getPersonID();
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, p.getFirstName());
        ps.setString(2, p.getLastName());
        ps.setString(3, p.getAddressLine1());
        ps.setString(4, p.getZip());
        ps.setString(5, p.getCity());
        ps.executeUpdate();

    }

    /**
     * Change an accounts type.
     *
     * @param id the ID number of the account type
     * @param personID the ID number of the person who owns the account
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void updatePosition(int id, int personID) throws SQLException {
        String query = "UPDATE Login SET PositionID = " + id + " WHERE PersonID = " + personID;
        PreparedStatement ps = con.prepareStatement(query);
        ps.executeUpdate();

    }

    /**
     * Disables or enables a user account. Disabled accounts cannot be signed
     * into or added to transactions.
     *
     * @param u the account to disable or enable
     * @param disabled represents whether account should be disabled or enabled
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void disableUser(User u, boolean disabled) throws SQLException {
        int bit;
        if (disabled) {
            bit = 1;
        } else {
            bit = 0;
        }
        String query = "UPDATE Login SET AccountDisabled = " + bit + " WHERE LoginID = " + u.getLoginID();
        PreparedStatement ps = con.prepareStatement(query);
        ps.executeUpdate();
    }

    /**
     * Deletes an account permanently.
     *
     * @param u the account to delete
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void deleteUser(User u) throws SQLException {
        String query = "UPDATE Login SET AccountDeleted = 1 WHERE LoginID = " + u.getLoginID();
        PreparedStatement ps = con.prepareStatement(query);
        ps.executeUpdate();
        query = "UPDATE Person SET PersonDeleted = 1 WHERE PersonID = " + u.getPersonID();
        ps = con.prepareStatement(query);
        ps.executeUpdate();
    }

    //------------------------------------------------------------------------//
    //---------- METHODS TO UPDATE INVENTORY ITEMS IN MANAGER STATE ----------//
    /**
     * Updates an InventoryItems image and thumbnail image.
     *
     * @param itemNumber the ID number of the inventory item
     * @param image the new image
     * @param thumb the new image sized down to a thumbnail
     * @throws IOException indicates a failed or interrupted I/O operation
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void updateImage(int itemNumber, File image, Image thumb) throws IOException, SQLException {
        byte[] b = Files.readAllBytes(image.toPath());

        // convert the thumbnail image into a byte array
        // needs to be done differently because this is resized in an ImageIcon, and cannot be converted into a File
        // force image to load - without this, Image.getHeight() && .getWidth were returning -1
        new javax.swing.ImageIcon(thumb);

        // create a new buffered image with transparancy (TYPE_INT_ARGB) at the same width && height of the thumbnail
        BufferedImage thumbnail = new BufferedImage(thumb.getWidth(null), thumb.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // draw the image onto the buffered image
        Graphics2D g2d = thumbnail.createGraphics();
        g2d.drawImage(thumb, 0, 0, null);
        g2d.dispose();

        byte[] thumbnailBytes = null;
        try {
            File outputFile = new File("saved.png");
            ImageIO.write(thumbnail, "png", outputFile);
            thumbnailBytes = Files.readAllBytes(outputFile.toPath());
            outputFile.delete();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String query = "UPDATE Inventory SET ItemImage = ?, ItemThumbnail = ? WHERE InventoryID = " + itemNumber;
        PreparedStatement ps = con.prepareStatement(query);
        ps.setBytes(1, b);
        ps.setBytes(2, thumbnailBytes);
        ps.executeUpdate();
    }

    //--------------------------------------------------//
    //---------- METHODS TO SEARCH FOR ITEMS ----------//
    /**
     * Searches the database for items that contain a keyword in the ItemName
     * column. If a category or subcategory is selected in the current state,
     * the search is restricted to that category/subcategory.
     * <p>
     * ResultSet contains the columns ItemName (text), RetailPrice (double),
     * Quantity (int), ItemThumbnail (blob), InventoryID (int).
     *
     * @param keyword the keyword to find in an items name
     * @param s the current state
     * @return a ResultSet with 5 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet searchAllItems(String keyword, State s) throws SQLException {
        String query;
        String category;
        String subcategory;
        if (State.getCurrentUser().getPosition() == User.CUSTOMER) {
            category = ((Customer) s).getSelectedCategory();
            subcategory = ((Customer) s).getSelectedSubcategory();
        } else if (State.getCurrentUser().getPosition() == User.EMPLOYEE) {
            category = ((Employee) s).getSelectedCategory();
            subcategory = ((Employee) s).getSelectedSubcategory();
        } else {
            category = ((Manager) s).getSelectedCategory();
            subcategory = ((Manager) s).getSelectedSubcategory();
        }

        if (category.equals("Shop by Category")) {
            // if no category is selected, search all categories
            query = "SELECT ItemName, RetailPrice, Quantity, ItemThumbnail, InventoryID FROM Inventory WHERE ItemName LIKE '%" + keyword + "%' LIMIT 100";
        } else {
            // if a category is selected but no subcategory is selected, search by category
            if (subcategory.equals("") || subcategory.equals("Choose a Subcategory")) {
                query = "SELECT CategoryID FROM Categories WHERE CategoryName = '" + category + "'";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int id = Integer.parseInt(rs.getString(1));
                query = "SELECT ItemName, RetailPrice, Quantity, ItemThumbnail, InventoryID FROM Inventory WHERE ItemName LIKE '%"
                        + keyword + "%' AND CategoryID = " + id + " LIMIT 100";
            } else {
                // if a subcategory is selected, search through subcategory
                query = "SELECT SubcategoryID FROM Subcategories WHERE SubcategoryName = '" + subcategory + "'";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int id = Integer.parseInt(rs.getString(1));
                query = "SELECT ItemName, RetailPrice, Quantity, ItemThumbnail, InventoryID FROM Inventory WHERE ItemName LIKE '%"
                        + keyword + "%' AND SubcategoryID = " + id + " LIMIT 100";
            }
        }

        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Searches the database for items that contain multiple keywords in the
     * ItemName or Description columns. If a category or subcategory is selected
     * in the current state, the search is restricted to that
     * category/subcategory.
     * <p>
     * ResultSet contains the columns ItemName (text), RetailPrice (double),
     * Quantity (int), ItemThumbnail (blob), InventoryID (int).
     *
     * @param keywords the keywords to search
     * @param s the current state
     * @return a ResultSet with 5 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet searchAllItems(String[] keywords, State s) throws SQLException {
        String query;
        String category;
        String subcategory;
        if (State.getCurrentUser().getPosition() == User.CUSTOMER) {
            category = ((Customer) s).getSelectedCategory();
            subcategory = ((Customer) s).getSelectedSubcategory();
        } else {
            category = ((Employee) s).getSelectedCategory();
            subcategory = ((Employee) s).getSelectedSubcategory();
        }

        if (category.equals("Shop by Category")) {
            // if no category is selected, search all categories
            query = "SELECT ItemName, RetailPrice, Quantity, ItemThumbnail, InventoryID FROM Inventory WHERE ItemName LIKE '%"
                    + keywords[0] + "%'";
            for (int i = 1; i < keywords.length; i++) {
                query += " AND ItemName LIKE '%" + keywords[i] + "%'";
            }
            query += " OR ItemDescription LIKE '%";
            for (int i = 0; i < keywords.length; i++) {
                if (i == keywords.length - 1) {
                    query += keywords[i] + "%'";
                } else {
                    query += keywords[i] + " ";
                }
            }
        } else {
            // if a category is selected but no subcategory is selected, search by category
            if (subcategory.equals("") || subcategory.equals("Choose a Subcategory")) {
                query = "SELECT CategoryID FROM Categories WHERE CategoryName = '" + category + "'";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int id = Integer.parseInt(rs.getString(1));

                query = "SELECT ItemName, RetailPrice, Quantity, ItemThumbnail, InventoryID FROM Inventory WHERE ItemName LIKE '%"
                        + keywords[0] + "%'";
                for (int i = 1; i < keywords.length; i++) {
                    query += " AND ItemName LIKE '%" + keywords[i] + "%'";
                }
                query += " AND CategoryID = " + id + " OR ItemDescription LIKE '%";
                for (int i = 0; i < keywords.length; i++) {
                    if (i == keywords.length - 1) {
                        query += keywords[i] + "%'";
                    } else {
                        query += keywords[i] + " ";
                    }
                }
                query += " AND CategoryID = " + id;
            } else {
                // if a subcategory is selected, search through subcategory
                query = "SELECT SubcategoryID FROM Subcategories WHERE SubcategoryName = '" + subcategory + "'";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int id = Integer.parseInt(rs.getString(1));
                query = "SELECT ItemName, RetailPrice, Quantity, ItemThumbnail, InventoryID FROM Inventory WHERE ItemName LIKE '%"
                        + keywords[0] + "%'";
                for (int i = 1; i < keywords.length; i++) {
                    query += " AND ItemName LIKE '%" + keywords[i] + "%'";
                }
                query += " AND SubcategoryID = " + id + " OR ItemDescription LIKE '%";
                for (int i = 0; i < keywords.length; i++) {
                    if (i == keywords.length - 1) {
                        query += keywords[i] + "%'";
                    } else {
                        query += keywords[i] + " ";
                    }
                }
                query += " AND SubcategoryID = " + id;
            }
        }
        query += " LIMIT 100";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets the first 100 items in the specified category.
     * <p>
     * ResultSet contains the columns ItemName (text), RetailPrice (double),
     * Quantity (int), ItemThumbnail (blob), InventoryID (int).
     *
     * @param category the category to search
     * @return a ResultSet with 5 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet searchEntireCategory(String category) throws SQLException {
        String query = "SELECT CategoryID FROM Categories WHERE CategoryName = '" + category + "'";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int id = Integer.parseInt(rs.getString(1));

        query = "SELECT ItemName, RetailPrice, Quantity, ItemThumbnail, InventoryID FROM Inventory WHERE CategoryID = " + id
                + " LIMIT 100";
        ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets the first 100 items in the specified subcategory.
     * <p>
     * ResultSet contains the columns ItemName (text), RetailPrice (double),
     * Quantity (int), ItemThumbnail (blob), InventoryID (int).
     *
     * @param subcategory the subcategory to search
     * @return a ResultSet with 5 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet searchEntireSubcategory(String subcategory) throws SQLException {
        String query = "SELECT SubcategoryID FROM Subcategories WHERE SubcategoryName = '" + subcategory + "'";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int id = Integer.parseInt(rs.getString(1));

        query = "SELECT ItemName, RetailPrice, Quantity, ItemThumbnail, InventoryID FROM Inventory WHERE SubcategoryID = " + id
                + " LIMIT 100";
        ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    //-----------------------------------------------------------------------//
    //---------- METHODS TO CREATE/EDIT DISCOUNTS IN MANAGER STATE ----------//
    /**
     * Uploads a new discount code to the database.
     *
     * @param d the Discount to upload
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void insertDiscount(Discount d) throws SQLException {
        String query = "INSERT INTO Discounts(DiscountCode, Description, DiscountLevel";
        String values = "VALUES('" + d.getDiscountCode() + "', '" + d.getDescription() + "', " + d.getDiscountLevel();

        if (d.getDiscountLevel() == Discount.ITEM_LEVEL) {
            query += ", InventoryID";
            values += ", " + d.getInventoryID();
        }

        query += ", DiscountType";
        values += ", " + d.getDiscountType();
        if (d.getDiscountType() == Discount.PERCENT_TYPE) {
            query += ", DiscountPercentage";
            values += ", " + d.getPercentage();
        } else {
            query += ", DiscountDollarAmount";
            values += ", " + d.getDollarAmount();
        }

        if (d.getStartDate() != null) {
            query += ", StartDate";
            values += ", '" + d.getStartDate().toString() + "'";
        }

        query += ", ExpirationDate) ";
        values += ", '" + d.getExpirationDate().toString() + "')";

        PreparedStatement ps = con.prepareStatement(query + values);
        ps.executeUpdate();
    }

    /**
     * Updates an existing discount in the database.
     *
     * @param d the Discount to update
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void updateDiscount(Discount d) throws SQLException {
        String query = "UPDATE Discounts SET DiscountCode = '" + d.getDiscountCode() + "', Description = '" + d.getDescription()
                + "', DiscountLevel = " + d.getDiscountLevel();

        if (d.getDiscountLevel() == Discount.ITEM_LEVEL) {
            query += ", InventoryID = " + d.getInventoryID();
        }

        query += ", DiscountType = " + d.getDiscountType();
        if (d.getDiscountType() == Discount.PERCENT_TYPE) {
            query += ", DiscountPercentage = " + d.getPercentage();
        } else {
            query += ", DiscountDollarAmount =" + d.getDollarAmount();
        }

        if (d.getStartDate() != null) {
            query += ", StartDate = '" + d.getStartDate().toString() + "'";
        }

        int dis = 0;
        if (d.isDisabled()) {
            dis = 1;
        }
        query += ", ExpirationDate = '" + d.getExpirationDate().toString() + "', Disabled = " + dis
                + " WHERE DiscountID = " + d.getDiscountID();

        PreparedStatement ps = con.prepareStatement(query);
        ps.executeUpdate();
    }

    //-------------------------------------------------//
    //---------- METHODS FOR REPORT CREATION ----------//
    /**
     * Gets all available (not discontinued) items from the database to create
     * an inventory report.
     * <p>
     * ResultSet contains the columns InventoryID (int), ItemName (text),
     * RetailPrice (double), Quantity (int), RestockThreshold (int),
     * Discontinued (int).
     *
     * @return a ResultSet with 6 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getAllAvailableItems() throws SQLException {
        String query = "SELECT InventoryID, ItemName, RetailPrice, Quantity, RestockThreshold, Discontinued FROM Inventory WHERE Discontinued = 0 OR Discontinued IS NULL";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets all items from the database to create an inventory report.
     * <p>
     * ResultSet contains the columns InventoryID (int), ItemName (text),
     * RetailPrice (double), Quantity (int), RestockThreshold (int),
     * Discontinued (int).
     *
     * @return a ResultSet with 6 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getAllItems() throws SQLException {
        String query = "SELECT InventoryID, ItemName, RetailPrice, Quantity, RestockThreshold, Discontinued FROM Inventory";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets all available (not discontinued) items from the database that need
     * to be restocked to create an inventory report.
     * <p>
     * ResultSet contains the columns InventoryID (int), ItemName (text),
     * RetailPrice (double), Quantity (int), RestockThreshold (int),
     * Discontinued (int).
     *
     * @return a ResultSet with 6 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getRestockItems() throws SQLException {
        String query = "SELECT InventoryID, ItemName, RetailPrice, Quantity, RestockThreshold, Discontinued FROM Inventory WHERE Quantity <= RestockThreshold AND "
                + "(Discontinued = 0 OR Discontinued IS NULL)";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Retrieves all orders placed on a date to create a sales report.
     * <p>
     * ResultSet contains the columns OrderID (int), PersonID (int), EmployeeID
     * (int), OrderDate (text), OrderTotal (double).
     *
     * @param date the date of an order
     * @return a ResultSet with 5 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getDailySales(LocalDate date) throws SQLException {
        String query = "SELECT OrderID, PersonID, EmployeeID, OrderDate, OrderTotal FROM Orders WHERE OrderDate = '" + dtf.format(date) + "'"
                + " UNION SELECT NULL, NULL, NULL, NULL, SUM(OrderTotal) AS Summary FROM Orders WHERE OrderDate = '" + dtf.format(date) + "'";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Retrieves all orders placed during a week of the year to create a sales
     * report.
     * <p>
     * ResultSet contains the columns OrderID (int), PersonID (int), EmployeeID
     * (int), OrderDate (text), OrderTotal (double).
     *
     * @param date the week of the year an order was placed
     * @return a ResultSet with 5 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getWeeklySales(LocalDate date) throws SQLException {
        //subtracts one b/c sqlite starts week counting at 00
        int weekInt = date.get(wf.weekOfWeekBasedYear()) - 1;

        String week = Integer.toString(weekInt);
        if (weekInt < 10) {
            week = "0" + weekInt;
        }

        System.out.println(week);

        String query = "SELECT OrderID, PersonID, EmployeeID, OrderDate, OrderTotal FROM Orders WHERE strftime('%Y', OrderDate) = '" + date.getYear()
                + "' AND strftime('%W', OrderDate) = '" + week + "'"
                + " UNION SELECT Null, Null, Null, Null, SUM(OrderTotal) AS Summary FROM Orders WHERE strftime('%Y', OrderDate) = '" + date.getYear()
                + "' AND strftime('%W', OrderDate) = '" + week + "'";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Retrieves all orders placed in a month to create a sales report.
     * <p>
     * ResultSet contains the columns OrderID (int), PersonID (int), EmployeeID
     * (int), OrderDate (text), OrderTotal (double).
     *
     * @param date the month an order was placed
     * @return a ResultSet with 5 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getMonthlySales(LocalDate date) throws SQLException {
        int monthInt = date.getMonthValue();
        String month = Integer.toString(monthInt);
        if (monthInt < 10) {
            month = "0" + monthInt;
        }
        String query = "SELECT OrderID, PersonID, EmployeeID, OrderDate, OrderTotal FROM Orders WHERE strftime('%m', OrderDate) = '" + month
                + "' AND strftime('%Y', OrderDate) = '" + date.getYear() + "'"
                + " UNION SELECT Null, Null, Null, Null, SUM(OrderTotal) AS Summary FROM Orders WHERE strftime('%m', OrderDate) = '" + month
                + "' AND strftime('%Y', OrderDate) = '" + date.getYear() + "'";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Retrieves all orders placed in a year to create a sales report.
     * <p>
     * ResultSet contains the columns OrderID (int), PersonID (int), EmployeeID
     * (int), OrderDate (text), OrderTotal (double).
     *
     * @param date the year an order was placed
     * @return a ResultSet with 5 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getYearlySales(LocalDate date) throws SQLException {
        String query = "SELECT OrderID, PersonID, EmployeeID, OrderDate, OrderTotal FROM Orders WHERE strftime('%Y', OrderDate) = '" + date.getYear() + "'"
                + " UNION SELECT Null, Null, Null, Null, SUM(OrderTotal) AS Summary FROM Orders WHERE strftime('%Y', OrderDate) = '" + date.getYear() + "'";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Retrieves all orders placed on a date by a specific customer to create a
     * sales report.
     * <p>
     * ResultSet contains the columns OrderID (int), PersonID (int), EmployeeID
     * (int), OrderDate (text), OrderTotal (double).
     *
     * @param date the date an order was placed
     * @param id the ID number of the customer
     * @return a ResultSet with 5 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getDailySales(LocalDate date, int id) throws SQLException {
        String query = "SELECT OrderID, PersonID, EmployeeID, OrderDate, OrderTotal FROM Orders WHERE OrderDate = '" + dtf.format(date) + "' AND PersonID = " + id
                + " UNION SELECT NULL, NULL, NULL, NULL, SUM(OrderTotal) AS Summary FROM Orders WHERE OrderDate = '" + dtf.format(date) + "' AND PersonID = " + id;
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Retrieves all orders placed during a week of the year by a specific
     * customer to create a sales report.
     * <p>
     * ResultSet contains the columns OrderID (int), PersonID (int), EmployeeID
     * (int), OrderDate (text), OrderTotal (double).
     *
     * @param date the week an order was placed
     * @param id the ID number of the customer
     * @return a ResultSet with 5 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getWeeklySales(LocalDate date, int id) throws SQLException {
        //subtracts one b/c sqlite starts week counting at 00
        int weekInt = date.get(wf.weekOfWeekBasedYear()) - 1;

        String week = Integer.toString(weekInt);
        if (weekInt < 10) {
            week = "0" + weekInt;
        }

        System.out.println(week);

        String query = "SELECT OrderID, PersonID, EmployeeID, OrderDate, OrderTotal FROM Orders WHERE strftime('%Y', OrderDate) = '" + date.getYear()
                + "' AND strftime('%W', OrderDate) = '" + week + "' AND PersonID = " + id
                + " UNION SELECT Null, Null, Null, Null, SUM(OrderTotal) AS Summary FROM Orders WHERE strftime('%Y', OrderDate) = '" + date.getYear()
                + "' AND strftime('%W', OrderDate) = '" + week + "' AND PersonID = " + id;
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Retrieves all orders placed in a month by a specific customer to create a
     * sales report.
     * <p>
     * ResultSet contains the columns OrderID (int), PersonID (int), EmployeeID
     * (int), OrderDate (text), OrderTotal (double).
     *
     * @param date the month an order was placed
     * @param id the ID number of the customer
     * @return a ResultSet with 5 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getMonthlySales(LocalDate date, int id) throws SQLException {
        int monthInt = date.getMonthValue();
        String month = Integer.toString(monthInt);
        if (monthInt < 10) {
            month = "0" + monthInt;
        }
        String query = "SELECT OrderID, PersonID, EmployeeID, OrderDate, OrderTotal FROM Orders WHERE strftime('%m', OrderDate) = '" + month
                + "' AND strftime('%Y', OrderDate) = '" + date.getYear() + "' AND PersonID = " + id
                + " UNION SELECT Null, Null, Null, Null, SUM(OrderTotal) AS Summary FROM Orders WHERE strftime('%m', OrderDate) = '" + month
                + "' AND strftime('%Y', OrderDate) = '" + date.getYear() + "' AND PersonID = " + id;
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Retrieves all orders placed in a year by a specific customer to create a
     * sales report.
     * <p>
     * ResultSet contains the columns OrderID (int), PersonID (int), EmployeeID
     * (int), OrderDate (text), OrderTotal (double).
     *
     * @param date the year an order was placed
     * @param id the ID number of the customer
     * @return a ResultSet with 5 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getYearlySales(LocalDate date, int id) throws SQLException {
        String query = "SELECT OrderID, PersonID, EmployeeID, OrderDate, OrderTotal FROM Orders WHERE strftime('%Y', OrderDate) = '" + date.getYear() + "'"
                + " AND PersonID = " + id
                + " UNION SELECT Null, Null, Null, Null, SUM(OrderTotal) AS Summary FROM Orders WHERE strftime('%Y', OrderDate) = '" + date.getYear() + "'"
                + " AND PersonID = " + id;
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets employee information to create a report.
     * <p>
     * ResultSet contains the columns PersonID (int), FirstName (text), LastName
     * (text), AddressLine1 (text), City (text), Zip (text), Email (text),
     * Phone1 (text), Username (text), AccountDisabled (int), AccountDeleted
     * (int), PositionID (int).
     *
     * @return a ResultSet with 12 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getEmployeeData() throws SQLException {
        String query = "SELECT Person.PersonID, FirstName, LastName, AddressLine1, City, Zip, Email, Phone1, Username, AccountDisabled, AccountDeleted, PositionID"
                + " FROM Person INNER JOIN Login ON Person.PersonID = Login.PersonID WHERE PositionID = 2 OR PositionID = 3";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets customer information to create a report.
     * <p>
     * ResultSet contains the columns PersonID (int), FirstName (text), LastName
     * (text), AddressLine1 (text), City (text), Zip (text), Email (text),
     * Phone1 (text), Username (text), AccountDisabled (int), AccountDeleted
     * (int).
     *
     * @return a ResultSet with 11 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getCustomerData() throws SQLException {
        String query = "SELECT Person.PersonID, FirstName, LastName, AddressLine1, City, Zip, Email, Phone1, Username, AccountDisabled, AccountDeleted"
                + " FROM Person INNER JOIN Login ON Person.PersonID = Login.PersonID WHERE PositionID = 1";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    //----------------------------------------------------------------------//
    //---------- METHODS TO RECEIVE STORE ORDERS IN MANAGER STATE ----------//
    /**
     * Gets all unreceived store orders.
     * <p>
     * ResultSet contains the columns OrderID (int), OrderDate (text).
     *
     * @return a ResultSet with 2 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getStoreOrders() throws SQLException {
        String query = "SELECT OrderID, OrderDate FROM StoreOrders WHERE Received IS NULL OR Received = 0";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets all unreceived store orders by date.
     * <p>
     * ResultSet contains the columns OrderID (int), OrderDate (text).
     *
     * @param date the date an order was placed
     * @return a ResultSet with 2 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getStoreOrdersByDate(String date) throws SQLException {
        String[] dateParts = date.split("-");
        int count = dateParts.length;
        String query = "SELECT OrderID, OrderDate FROM StoreOrders WHERE OrderDate ";

        switch (count) {
            case 1:
            case 2:
                query += "LIKE '" + date + "%'";
                break;
            case 3:
                query += "= '" + date + "'";
                break;
        }

        query += " AND (Received IS NULL OR Received = 0)";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets a store order.
     * <p>
     * ResultSet contains the columns OrderDate (text), EmployeeID (int),
     * OrderTotal (double), Received (int).
     *
     * @param id the store orders ID number
     * @return a ResultSet with 4 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getStoreOrderByID(int id) throws SQLException {
        String query = "SELECT OrderDate, EmployeeID, OrderTotal, Received FROM StoreOrders WHERE OrderID = " + id;
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets store order details.
     * <p>
     * ResultSet contains the columns InventoryID (int), Quantity (int),
     * QuantityReceived (int), ItemName (text).
     *
     * @param id the store orders ID number
     * @return a ResultSet with 4 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getStoreOrderDetails(int id) throws SQLException {
        String query = "SELECT StoreOrderDetails.InventoryID, StoreOrderDetails.Quantity, QuantityReceived, ItemName "
                + "FROM StoreOrderDetails INNER JOIN Inventory ON Inventory.InventoryID = StoreOrderDetails.InventoryID"
                + " WHERE OrderID = " + id;
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Gets unreceived order items so Managers can received individual items.
     * <p>
     * ResultSet contains the columns InventoryID (int), Quantity (int).
     *
     * @return a ResultSet with 2 columns
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public ResultSet getUnreceivedOrderItems() throws SQLException {
        String query = "SELECT StoreOrderDetails.InventoryID, StoreOrderDetails.Quantity FROM StoreOrderDetails INNER JOIN StoreOrders "
                + " ON StoreOrders.OrderID = StoreOrderDetails.OrderID WHERE StoreOrders.Received IS NULL OR StoreOrders.Received = 0";
        PreparedStatement ps = con.prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * Receives the specified quantity of an inventory item from a store order.
     *
     * @param orderID the store order being received
     * @param inventoryID the inventory item being received
     * @param qtyReceived the quantity of the inventory item received
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void updateReceivedItem(int orderID, int inventoryID, int qtyReceived) throws SQLException {
        String query = "UPDATE StoreOrderDetails SET QuantityReceived = " + qtyReceived + " WHERE InventoryID = " + inventoryID + " AND OrderID = " + orderID;
        PreparedStatement ps = con.prepareStatement(query);
        ps.executeUpdate();
    }

    /**
     * Receives an entire store order and charges the store based on the
     * quantity of product received.
     *
     * @param orderID the store order being received
     * @param charged the cost of the order
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void updateReceivedOrder(int orderID, double charged) throws SQLException {
        String query = "UPDATE StoreOrders SET Received = 1, AmountCharged = " + charged + " WHERE OrderID = " + orderID;
        PreparedStatement ps = con.prepareStatement(query);
        ps.executeUpdate();
    }

    /**
     * Adds quantity to the existing stock of an inventory item. Used when
     * receiving an order.
     *
     * @param itemID the inventory item to update
     * @param qty the quantity to add
     * @throws SQLException indicates a database access error or other SQL
     * statement error
     */
    public void updateInventoryItemQuantity(int itemID, int qty) throws SQLException {
        String query = "Update Inventory SET Quantity = Quantity + " + qty + " WHERE InventoryID = " + itemID;
        PreparedStatement ps = con.prepareStatement(query);
        ps.executeUpdate();
    }
}
