package util;

import statemachine.Checkout;
import java.io.*;
import java.awt.Desktop;
import java.util.ArrayList;
import javax.swing.filechooser.FileSystemView;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import statemachine.State;
import java.sql.*;
import component.panels.*;

/**
 * HTML contains static methods to create HTML receipts and reports. Each
 * report is saved in one of several folders found at
 * "C:\Users\XXX\Documents\Bizmart Supply Co", where "XXX" is the name of the
 * user logged into the computer.
 * <p>
 * When a file is created, it's automatically opened in the users default web
 * browsers.
 */
public abstract class HTML {

    /**
     * Indicates inventory report should be of available items.
     */
    public static final int AVAILABLE_ITEMS = 0;
    /**
     * Indicates inventory report should be of items that need to be restocked.
     */
    public static final int RESTOCK_ITEMS = 1;

    /**
     * Indicates inventory report should be of all items.
     */
    public static final int ALL_ITEMS = 2;

    /**
     * Indicates sales report should be by day.
     */
    public static final int DAY = 0;

    /**
     * Indicates sales report should be by week of the year.
     */
    public static final int WEEK = 1;

    /**
     * Indicates sales report should be by month.
     */
    public static final int MONTH = 2;

    /**
     * Indicates sales report should be by year.
     */
    public static final int YEAR = 3;

    /**
     * Indicates to tableRows that the HTML file should be formatted for a
     * receipt.
     */
    public static final int RECEIPT = 0;

    /**
     * Indicates to tableRows that the HTML file should be formatted for a
     * report.
     */
    public static final int REPORT = 1;

    /**
     * LocalDate formatter of pattern MM-dd-yyyy.
     */
    private static final DateTimeFormatter mdy = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    /**
     * LocalDate formatter of pattern MM-yyyy.
     */
    private static final DateTimeFormatter my = DateTimeFormatter.ofPattern("MM-yyyy");

    /**
     * LocalDate formatter of pattern yyyy.
     */
    private static final DateTimeFormatter y = DateTimeFormatter.ofPattern("yyyy");

    /**
     * Creates an HTML receipt for an order placed in the customer or employee
     * state, or an HTML order confirmation for store orders placed in the
     * manager state.
     *
     * @param c the Checkout state to get line items from
     * @param orderNumber the order number
     * @throws IOException indicates a failed or interrupted I/O operation
     */
    public static void createReceipt(Checkout c, int orderNumber) throws IOException {
        ArrayList<String[]> items = c.getCartItems();
        ArrayList<String[]> totals = c.getCartTotals();

        // check to see if the directory "Receipts" exists in the users Documents/Bizmart Supply Co folder
        File dir;
        if (State.getCurrentUser().getPosition() == User.CUSTOMER || State.getPreviousState().equals(State.getEmployeeState())) {
            dir = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/Bizmart Supply Co/Receipts");
        } else {
            dir = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/Bizmart Supply Co/Store Order Confirmations");
        }

        // if it doesn't, make the directory
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // create an html file 
        File receipt;
        if (State.getCurrentUser().getPosition() == User.CUSTOMER || State.getPreviousState().equals(State.getEmployeeState())) {
            receipt = new File(dir + "/receipt_order_" + orderNumber + ".html");
        } else {
            receipt = new File(dir + "/confirmation_store_order_" + orderNumber + ".html");
        }

        // write to the file
        FileWriter fw = new FileWriter(receipt);
        BufferedWriter bw = new BufferedWriter(fw);

        String customer = null;
        String employee = null;
        try {
            if (State.getCurrentUser().getPosition() == User.CUSTOMER) {
                customer = State.getConnection().getPersonName(State.getCurrentUser().getPersonID()).toUpperCase();
            } else if (State.getPreviousState().equals(State.getEmployeeState())) {
                employee = State.getConnection().getPersonName(State.getCurrentUser().getPersonID()).toUpperCase();
                customer = State.getCurrentCustomer().getFirstName() + " " + State.getCurrentCustomer().getLastName();
            } else {
                employee = State.getConnection().getPersonName(State.getCurrentUser().getPersonID()).toUpperCase();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder html = new StringBuilder("<!DOCTYPE html><html><head>");

        if (State.getPreviousState().equals(State.getManagerState())) {
            html.append("<title>Order Confirmation for Order #");
            html.append(orderNumber);
            html.append("</title>");
        } else {
            html.append("<title>Receipt for Order #");
            html.append(orderNumber);
            html.append("</title>");
        }
        html.append("<meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append(styleTag());
        html.append("</head><div><body><h1>BIZMART SUPPLY CO.</h1><p>ORDER NUMBER: ");
        html.append(orderNumber);
        html.append("</p><p>ORDER DATE: ");
        html.append(mdy.format(LocalDate.now()));
        html.append("</p>");

        if (customer != null) {
            html.append("<p>CUSTOMER: ");
            html.append(customer);
            html.append("</p>");
        }

        if (employee != null) {
            html.append("<p>EMPLOYEE: ");
            html.append(employee);
            html.append("</p>");
        }

        html.append("<table><tr><th>Item</th><th>Item Price</th><th>Quantity</th><th>Total</th></tr>");
        html.append(tableRows(items, RECEIPT));
        html.append(tableRows(totals, RECEIPT));
        html.append("</table>");

        if (State.getPreviousState().equals(State.getManagerState())) {
            html.append("<h2>Your Order is Confirmed.</h2></div></body></html>");
        } else {
            html.append("<h2>Thank you for your order!</h2></div></body></html>");
        }

        bw.write(html.toString());
        bw.close();

        //open file in users default browser
        Desktop.getDesktop().browse(receipt.toURI());
    }

    /**
     * Creates an HTML receipt for a received store order.
     *
     * @param items the items received in the order
     * @param orderNumber the order number
     * @throws IOException indicates a failed or interrupted I/O operation
     */
    public static void createStoreOrderReceipt(ArrayList<OrderItem> items, int orderNumber) throws IOException {
        // check to see if the directory "Receipts" exists in the users Documents/Bizmart Supply Co folder
        File dir = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/Bizmart Supply Co/Store Order Receipts");

        // if it doesn't, make the directory
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // create an html file 
        File receipt = new File(dir + "/receipt_store_order_" + orderNumber + ".html");

        // write to the file
        FileWriter fw = new FileWriter(receipt);
        BufferedWriter bw = new BufferedWriter(fw);

        String employee = "";
        try {
            employee = State.getConnection().getPersonName(State.getCurrentUser().getPersonID()).toUpperCase();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // create table rows arraylist
        ArrayList<String[]> rows = new ArrayList<>();
        double subtotal = 0;
        for (OrderItem i : items) {
            String item = i.getInventoryItem().getItemName();
            String price = "$" + Money.DF.format(i.getInventoryItem().getWholesalePrice());
            String ordered = Integer.toString(i.getOrderDetails().getQuantity());
            String received = Integer.toString(i.getQuantityReceived());
            String total = "$" + Money.DF.format(i.getQuantityReceived() * i.getInventoryItem().getWholesalePrice());
            subtotal += i.getInventoryItem().getWholesalePrice() * i.getQuantityReceived();
            rows.add(new String[]{item, price, ordered, received, total});

        }
        rows.add(new String[]{"", "", "", "Tax (8.25%)", "$" + Money.DF.format(subtotal * Money.TAX_RATE)});
        rows.add(new String[]{"", "", "", "Total Charged", "$" + Money.DF.format(subtotal + (subtotal * Money.TAX_RATE))});

        StringBuilder html = new StringBuilder("<!DOCTYPE html><html><head>");

        if (State.getPreviousState().equals(State.getManagerState())) {
            html.append("<title>Order Confirmation for Order #");
            html.append(orderNumber);
            html.append("</title>");
        } else {
            html.append("<title>Receipt for Order #");
            html.append(orderNumber);
            html.append("</title>");
        }
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append(styleTag());
        html.append("</head><div><body><h1>BIZMART SUPPLY CO.</h1><p>ORDER NUMBER: ");
        html.append(orderNumber);
        html.append("</p><p>ORDER DATE: ");
        html.append(mdy.format(LocalDate.now()));
        html.append("</p>");

        if (employee != null) {
            html.append("<p>EMPLOYEE: ");
            html.append(employee);
            html.append("</p>");
        }

        html.append("<table><tr><th>Item</th><th>Wholesale Price</th><th>Quantity Ordered</th><th>Received</th><th>Total</th></tr>");
        int size = rows.size();
        for (int i = 0; i < size; i++) {
            html.append("<tr>");
            for (String str : rows.get(i)) {
                html.append("<td>");
                html.append(str);
                html.append("</td>");
            }
            html.append("</tr>");
        }

        html.append("</table><h2>Order Received. Item(s) in stock quantity updated.</h2></div></body></html>");

        bw.write(html.toString());
        bw.close();

        //open file in users default browser
        Desktop.getDesktop().browse(receipt.toURI());
    }

    /**
     * Creates a style tag for receipts and reports.
     *
     * @return an HTML style tag as a string
     */
    private static StringBuilder styleTag() {
        StringBuilder style = new StringBuilder("<style> :root {");
        style.append("--ghostWhite: rgb(240, 245, 250);--silver: rgb(230, 230, 230);");
        style.append("--deepBlue: rgb(4, 6, 37);--emerald: rgb(105, 201, 137);}");
        style.append("h1, h2 {text-align: center;}body {color: var(--deepBlue); font-family: \"Nunito\", Verdana, sans-serif;}");
        style.append("table {margin: 0 auto;border-collapse: collapse;}");
        style.append("td, th {border: 1px solid var(--deepBlue);box-sizing: border-box; padding: 10px;}");
        style.append("th {background-color: var(--emerald);}tr:nth-child(even){background-color: var(--silver);}");
        style.append("div {margin: 0 auto; width: max-content;}");
        style.append("p {font-size: 18px;}</style>");
        return style;
    }

    /**
     * Creates HTML table elements for reports and receipts.
     *
     * @param arr the data to add to the table
     * @param type REPORT or RECEIPT determines how arr is read
     * @return
     */
    private static StringBuilder tableRows(ArrayList<String[]> arr, int type) {
        StringBuilder row = new StringBuilder();
        if (type == REPORT) {
            //NOTE: Because the MySQL database was converted to SQLite, the UNION row for the total of all orders is at the top of the result set
            int size = arr.size();
            for (int i = 1; i < size; i++) {
                row.append("<tr>");
                for (String str : arr.get(i)) {
                    row.append("<td>");
                    row.append(str);
                    row.append("</td>");
                }
                row.append("</tr>");
            }
            // add summary row with total
            row.append("<tr>");
            for (String str : arr.get(0)) {
                row.append("<td>");
                row.append(str);
                row.append("</td>");
            }
            row.append("</tr>");
        } else {
            int size = arr.size();
            for (int i = 0; i < size; i++) {
                row.append("<tr>");
                for (String str : arr.get(i)) {
                    row.append("<td>");
                    row.append(str);
                    row.append("</td>");
                }
                row.append("</tr>");
            }
        }
        return row;
    }

    /**
     * Creates an HTML inventory report.
     *
     * @param type determines what data is in the report
     * @throws IOException indicates a failed or interrupted I/O operation
     */
    public static void createInventoryReport(int type) throws IOException {
        ArrayList<String[]> items = new ArrayList<>();
        try {
            ResultSet rs;
            if (type == RESTOCK_ITEMS) {
                rs = State.getConnection().getRestockItems();
            } else if (type == AVAILABLE_ITEMS) {
                rs = State.getConnection().getAllAvailableItems();
            } else {
                rs = State.getConnection().getAllItems();
            }
            while (rs.next()) {
                //SELECT InventoryID, ItemName, RetailPrice, Quantity, RestockThreshold, Discontinued
                String id = rs.getString(1);
                String name = rs.getString(2);
                String retail = "$" + rs.getString(3);
                String quantity = rs.getString(4);
                String thresh = rs.getString(5);
                int dis = rs.getInt(6);
                String available;
                if (dis == 0) {
                    available = "Yes";
                } else {
                    available = "No";
                }

                items.add(new String[]{id, name, retail, quantity, thresh, available});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // check to see if the directory "Receipts" exists in the users Documents/Bizmart Supply Co folder
        File dir = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/Bizmart Supply Co/Reports/Inventory");
        // if it doesn't, make the directory
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File report;
        if (type == RESTOCK_ITEMS) {
            report = new File(dir + "/item_restock_report" + mdy.format(LocalDate.now()) + ".html");
        } else if (type == AVAILABLE_ITEMS) {
            report = new File(dir + "/available_inventory_report" + mdy.format(LocalDate.now()) + ".html");
        } else {
            report = new File(dir + "/entire_inventory_report" + mdy.format(LocalDate.now()) + ".html");
        }

        // write to the file
        FileWriter fw = new FileWriter(report);
        BufferedWriter bw = new BufferedWriter(fw);

        StringBuilder html = new StringBuilder("<!DOCTYPE html><html><head>");
        html.append("<title>Inventory Report");
        html.append(mdy.format(LocalDate.now()));
        html.append("</title><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append(styleTag());
        html.append("<div></head><body><h1>BIZMART SUPPLY CO.</h1>");

        if (type == RESTOCK_ITEMS) {
            html.append("<p>ITEM RESTOCK REPORT ");
            html.append(mdy.format(LocalDate.now()));
            html.append("</p>");
        } else if (type == AVAILABLE_ITEMS) {
            html.append("<p>AVAILABLE INVENTORY REPORT ");
            html.append(mdy.format(LocalDate.now()));
            html.append("</p>");
        } else {
            html.append("<p>ENTIRE INVENTORY REPORT ");
            html.append(mdy.format(LocalDate.now()));
            html.append("</p>");
        }

        html.append("<table><tr><th>InventoryID</th><th>Item Name</th><th>Item Price</th><th>Quantity On Hand</th><th>Restock Threshold</th><th>Availability</th></tr>");
        html.append(tableRows(items, RECEIPT));
        html.append("</table>");

        bw.write(html.toString());
        bw.close();

        //open file in users default browser
        Desktop.getDesktop().browse(report.toURI());
    }

    /**
     * Creates an HTML sales report.
     *
     * @param type determines the date range to get reports from
     * @param date the date to get sales from
     * @throws IOException indicates a failed or interrupted I/O operation
     */
    public static void createSalesReport(int type, LocalDate date) throws IOException {
        WeekFields wf = WeekFields.of(DayOfWeek.SUNDAY, 1);
        TemporalField tf = wf.weekOfYear();

        ArrayList<String[]> items = new ArrayList<>();
        try {
            ResultSet rs = null;
            switch (type) {
                case DAY -> {
                    rs = State.getConnection().getDailySales(date);
                }
                case WEEK -> {
                    rs = State.getConnection().getWeeklySales(date);
                }
                case MONTH -> {
                    rs = State.getConnection().getMonthlySales(date);
                }
                case YEAR -> {
                    rs = State.getConnection().getYearlySales(date);
                }
            }

            while (rs.next()) {
                if (rs.getRow() > 1) {
                    String id = rs.getString(1);
                    String person = rs.getString(2);
                    String emp = rs.getString(3);
                    String orderDate = rs.getString(4);
                    String total = "$" + rs.getString(5);
                    items.add(new String[]{id, person, emp, orderDate, total});
                } else {
                    String total = "$" + rs.getString(5);
                    items.add(new String[]{"", "", "", "Total Sales:", total});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // check to see if the directory "Receipts" exists in the users Documents/Bizmart Supply Co folder
        File dir = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/Bizmart Supply Co/Reports/Sales");
        // if it doesn't, make the directory

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File report = null;
        switch (type) {
            case DAY -> {
                report = new File(dir + "/sales_report_day_" + mdy.format(date) + ".html");
            }
            case WEEK -> {
                report = new File(dir + "/sales_report_week_" + date.get(tf) + "_of_" + my.format(date) + ".html");
            }
            case MONTH -> {
                report = new File(dir + "/sales_report_month_" + my.format(date) + ".html");
            }
            case YEAR -> {
                report = new File(dir + "/sales_report_year_" + y.format(date) + ".html");
            }
        }

        // write to the file
        FileWriter fw = new FileWriter(report);
        BufferedWriter bw = new BufferedWriter(fw);

        StringBuilder html = new StringBuilder("<!DOCTYPE html><html><head>");
        html.append("<title>Sales Report");
        html.append(mdy.format(date));
        html.append("</title><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append(styleTag());
        html.append("<div></head><body><h1>BIZMART SUPPLY CO.</h1>");

        switch (type) {
            case DAY -> {
                html.append("<p>SALES REPORT FOR ");
                html.append(mdy.format(date));
                html.append("</p>");
            }
            case WEEK -> {
                html.append("<p>SALES REPORT FOR ");
                html.append(my.format(date));
                html.append(", WEEK ");
                html.append(date.get(tf));
                html.append("</p>");
            }
            case MONTH -> {
                html.append("<p>SALES REPORT FOR ");
                html.append(my.format(date));
                html.append("</p>");
            }
            case YEAR -> {
                html.append("<p>SALES REPORT FOR ");
                html.append(y.format(date));
                html.append("</p>");
            }
        }

        html.append("<table><tr><th>OrderID</th><th>PersonID</th><th>EmployeeID</th><th>Order Date</th><th>Order Total</th></tr>");
        html.append(tableRows(items, REPORT));
        html.append("</table>");
        bw.write(html.toString());
        bw.close();

        //open file in users default browser
        Desktop.getDesktop().browse(report.toURI());
    }

    /**
     * Creates an HTML sales report of a specific customer's orders.
     *
     * @param type determines the date range to get reports from
     * @param date the date to get sales from
     * @param p the customer who placed the orders
     * @throws IOException indicates a failed or interrupted I/O operation
     */
    public static void createSalesReport(int type, LocalDate date, Person p) throws IOException {
        WeekFields wf = WeekFields.of(DayOfWeek.SUNDAY, 1);
        TemporalField tf = wf.weekOfYear();

        ArrayList<String[]> items = new ArrayList<>();
        try {
            ResultSet rs = null;
            switch (type) {
                case DAY -> {
                    rs = State.getConnection().getDailySales(date, p.getPersonID());
                }
                case WEEK -> {
                    rs = State.getConnection().getWeeklySales(date, p.getPersonID());
                }
                case MONTH -> {
                    rs = State.getConnection().getMonthlySales(date, p.getPersonID());
                }
                case YEAR -> {
                    rs = State.getConnection().getYearlySales(date, p.getPersonID());
                }
            }
            while (rs.next()) {
                if (rs.getRow() > 1) {
                    String id = rs.getString(1);
                    String person = rs.getString(2);
                    String emp = rs.getString(3);
                    String orderDate = rs.getString(4);
                    String total = "$" + rs.getString(5);
                    items.add(new String[]{id, person, emp, orderDate, total});
                } else {
                    String total = "$" + rs.getString(5);
                    items.add(new String[]{"", "", "", "Total Sales:", total});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // check to see if the directory "Receipts" exists in the users Documents/Bizmart Supply Co folder
        File dir = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/Bizmart Supply Co/Reports/Sales");
        // if it doesn't, make the directory

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File report = null;
        switch (type) {
            case DAY -> {
                report = new File(dir + "/sales_report_day_" + mdy.format(date) + "_customer_" + p.getPersonID() + ".html");
            }
            case WEEK -> {
                report = new File(dir + "/sales_report_week_" + date.get(tf) + "_of_" + my.format(date) + "_customer_" + p.getPersonID() + ".html");
            }
            case MONTH -> {
                report = new File(dir + "/sales_report_month_" + my.format(date) + "_customer_" + p.getPersonID() + ".html");
            }
            case YEAR -> {
                report = new File(dir + "/sales_report_year_" + y.format(date) + "_customer_" + p.getPersonID() + ".html");
            }
        }

        // write to the file
        FileWriter fw = new FileWriter(report);
        BufferedWriter bw = new BufferedWriter(fw);

        StringBuilder html = new StringBuilder("<!DOCTYPE html><html><head>");
        html.append("<title>Sales Report");
        html.append(mdy.format(date));
        html.append("</title><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append(styleTag());
        html.append("<div></head><body><h1>BIZMART SUPPLY CO.</h1>");

        switch (type) {
            case DAY -> {
                html.append("<p>SALES REPORT FOR ");
                html.append(mdy.format(date));
                html.append("</p>");
            }
            case WEEK -> {
                html.append("<p>SALES REPORT FOR ");
                html.append(my.format(date));
                html.append(", WEEK ");
                html.append(date.get(tf));
                html.append("</p>");
            }
            case MONTH -> {
                html.append("<p>SALES REPORT FOR ");
                html.append(my.format(date));
                html.append("</p>");
            }
            case YEAR -> {
                html.append("<p>SALES REPORT FOR ");
                html.append(y.format(date));
                html.append("</p>");
            }
        }

        String name = null;
        try {
            name = State.getConnection().getPersonName(p.getPersonID());
        } catch (Exception e) {
            e.printStackTrace();
        }

        html.append("<p>CUSTOMER ");
        html.append(name);
        html.append("</p><table><tr><th>OrderID</th><th>PersonID</th><th>EmployeeID</th><th>Order Date</th><th>Order Total</th></tr>");
        html.append(tableRows(items, REPORT));
        html.append("</table>");
        bw.write(html.toString());
        bw.close();

        //open file in users default browser
        Desktop.getDesktop().browse(report.toURI());
    }

    /**
     * Creates an HTML report of customer or employee (including manager)
     * information.
     *
     * @param type determines if report will be of customers or employees
     * @throws IOException indicates a failed or interrupted I/O operation
     */
    public static void createPersonReport(int type) throws IOException {
        ArrayList<String[]> items = new ArrayList<>();
        try {
            ResultSet rs = null;
            switch (type) {
                case 0 -> {
                    rs = State.getConnection().getCustomerData();
                }
                case 1 -> {
                    rs = State.getConnection().getEmployeeData();
                }
            }
            while (rs.next()) {
                String id = rs.getString(1);
                String first = rs.getString(2);
                String last = rs.getString(3);
                String add = rs.getString(4);
                String city = rs.getString(5);
                String zip = rs.getString(6);
                String email = rs.getString(7);
                String phone1 = rs.getString(8);
                String username = rs.getString(9);
                int dis = rs.getInt(10);
                String disabled;
                String deleted;
                int del = rs.getInt(11);

                if (dis == 0) {
                    disabled = "No";
                } else {
                    disabled = "Yes";
                }

                if (del == 0) {
                    deleted = "No";
                } else {
                    deleted = "Yes";
                }

                if (phone1 == null) {
                    phone1 = "None";
                }

                if (type == 0) {
                    items.add(new String[]{id, first, last, add, city, zip, email, phone1, username, disabled, deleted});
                } else {
                    int pos = rs.getInt(12);
                    String position = null;
                    if (pos == 2) {
                        position = "Employee";
                    } else {
                        position = "Manager";
                    }
                    items.add(new String[]{id, first, last, add, city, zip, email, phone1, username, disabled, deleted, position});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // check to see if the directory "Receipts" exists in the users Documents/Bizmart Supply Co folder
        File dir = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/Bizmart Supply Co/Reports/Accounts");
        // if it doesn't, make the directory

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File report = null;
        switch (type) {
            case 0 -> {
                report = new File(dir + "/customer_data_report_" + mdy.format(LocalDate.now()) + ".html");
            }
            case 1 -> {
                report = new File(dir + "/employee_data_report_" + mdy.format(LocalDate.now()) + ".html");
            }
        }

        // write to the file
        FileWriter fw = new FileWriter(report);
        BufferedWriter bw = new BufferedWriter(fw);

        StringBuilder html = new StringBuilder("<!DOCTYPE html><html><head>");
        html.append("<title>Account Data Report");
        html.append(mdy.format(LocalDate.now()));
        html.append("</title><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append(styleTag());
        html.append("<div></head><body><h1>BIZMART SUPPLY CO.</h1>");

        switch (type) {
            case 0 -> {
                html.append("<p>CUSTOMER DATA REPORT ");
                html.append(mdy.format(LocalDate.now()));
                html.append("</p>");
            }
            case 1 -> {
                html.append("<p>EMPLOYEE DATA REPORT ");
                html.append(my.format(LocalDate.now()));
                html.append("</p>");
            }
        }

        html.append("<table><tr><th>PersonID</th><th>First Name</th><th>Last Name</th><th>Street Address</th><th>City</th>");
        html.append("<th>Zip Code</th><th>Email Address</th><th>Phone Number</th><th>Username</th><th>Account Disabled</th><th>Account Deleted</th>");

        if (type == 1) {
            html.append("<th>Position</th>");
        }
        html.append("</tr>");
        html.append(tableRows(items, REPORT));
        html.append("</table>");

        bw.write(html.toString());
        bw.close();

        //open file in users default browser
        Desktop.getDesktop().browse(report.toURI());
    }
}
