package component.panels;

import component.labels.RequirementLabel;
import component.labels.FieldLabel;
import component.labels.FieldRequiredLabel;
import component.Colors;
import component.PopupDialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import component.fields.InputField;
import component.buttons.ButtonPrimary;
import java.io.File;
import java.sql.ResultSet;
import javax.swing.filechooser.FileNameExtensionFilter;
import statemachine.State;
import util.*;

/**
 * NewItemPanel is used by the manager state of the Bizmart application to add
 * new inventory items to the database.
 */
public class NewItemPanel extends JPanel {

    /**
     * Label for a required field in the NewItemPanel form.
     */
    FieldRequiredLabel nameLabel, descLabel, categoryLabel, retailLabel, wholesaleLabel,
            qtyLabel, imageLabel, thresholdLabel, maxQtyLabel;

    /**
     * Label for the optional JComboBox subcategories.
     */
    FieldLabel subcategoryLabel;

    /**
     * Field for information about the new item.
     */
    InputField itemName, retailPrice, wholesalePrice, quantity, threshold, maxQty;

    /**
     * Multi-line text field for item description.
     */
    JTextArea description;

    /**
     * JComboBox to set the items category.
     */
    JComboBox categories;

    /**
     * JComboBox to set the items optional subcategory. Only visible if the
     * selected category in categories has a subcategory.
     */
    JComboBox subcategories;

    /**
     * Button validates form and uploads the new item to the database if valid
     * when pressed.
     */
    ButtonPrimary addItemBttn;

    /**
     * Button opens JFileChooser and saves the selected item image when pressed.
     */
    ButtonPrimary imageBttn;

    /**
     * Label indicates whether an image has been selected for the item or not.
     */
    RequirementLabel imageReq;

    /**
     * Item image to be uploaded to the database.
     */
    File imageFile;

    /**
     * NewItemPanel constructor sets up form to create a new inventory item.
     */
    public NewItemPanel() {
        super();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        nameLabel = new FieldRequiredLabel("Name");
        descLabel = new FieldRequiredLabel("Description");
        categoryLabel = new FieldRequiredLabel("Category");
        subcategoryLabel = new FieldLabel("Subcategory");
        retailLabel = new FieldRequiredLabel("Retail Price");
        wholesaleLabel = new FieldRequiredLabel("Wholesale Price");
        qtyLabel = new FieldRequiredLabel("In Stock");
        imageLabel = new FieldRequiredLabel("Product Image");
        thresholdLabel = new FieldRequiredLabel("Restock Threshold");
        maxQtyLabel = new FieldRequiredLabel("Max Quantity");
        imageReq = new RequirementLabel("Image Uploaded");

        itemName = new InputField();
        retailPrice = new InputField();
        wholesalePrice = new InputField();
        quantity = new InputField();
        threshold = new InputField();
        maxQty = new InputField();
        description = new JTextArea();
        description.setFont(new Font("Nunito", Font.PLAIN, 18));
        description.setForeground(Colors.DARK_BLUE);
        description.setBackground(Colors.GHOST_WHITE);
        description.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        description.setWrapStyleWord(true);
        description.setLineWrap(true);

        categories = new JComboBox<String>();
        categories.setFont(new Font("Nunito", Font.PLAIN, 20));
        categories.setForeground(Colors.RICH_BLACK);
        try {
            ResultSet rs = State.getConnection().getItemCategories();
            while (rs.next()) {
                categories.addItem(rs.getString(2));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        subcategories = new JComboBox<String>();
        subcategories.setFont(new Font("Nunito", Font.PLAIN, 20));
        subcategories.setForeground(Colors.RICH_BLACK);
        subcategories.addItem("No subcategory");
        try {
            ResultSet rs = State.getConnection().getSubcategory(categories.getSelectedItem().toString());
            while (rs.next()) {
                subcategories.addItem(rs.getString(1));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        addItemBttn = new ButtonPrimary("Add Item");
        imageBttn = new ButtonPrimary("Upload Image");

        //----- GROUP COMPONENTS -----//
        // ITEM NAME
        JPanel name = new JPanel();
        name.setLayout(new BoxLayout(name, BoxLayout.Y_AXIS));
        name.setBackground(null);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        name.add(nameLabel);
        name.add(Box.createVerticalStrut(5));
        itemName.setAlignmentX(Component.LEFT_ALIGNMENT);
        name.add(itemName);

        // IMAGE BUTTON
        JPanel img = new JPanel();
        img.setLayout(new BoxLayout(img, BoxLayout.Y_AXIS));
        img.setBackground(null);
        imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        img.add(imageLabel);
        img.add(Box.createVerticalStrut(5));
        imageReq.setAlignmentX(Component.LEFT_ALIGNMENT);
        img.add(imageReq);

        JPanel imgPanel = new JPanel();
        imgPanel.setLayout(new BoxLayout(imgPanel, BoxLayout.X_AXIS));
        imgPanel.setBackground(null);
        imgPanel.add(img);
        imgPanel.add(Box.createHorizontalStrut(10));
        imgPanel.add(imageBttn);

        // DESCRIPTION
        JPanel desc = new JPanel();
        desc.setLayout(new BoxLayout(desc, BoxLayout.Y_AXIS));
        desc.setBackground(null);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        desc.add(descLabel);
        desc.add(Box.createVerticalStrut(5));
        description.setAlignmentX(Component.LEFT_ALIGNMENT);
        desc.add(description);

        // RETAIL PRICE
        JPanel retail = new JPanel();
        retail.setLayout(new BoxLayout(retail, BoxLayout.Y_AXIS));
        retail.setBackground(null);
        retailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        retail.add(retailLabel);
        retail.add(Box.createVerticalStrut(5));
        retailPrice.setAlignmentX(Component.LEFT_ALIGNMENT);
        retail.add(retailPrice);

        // WHOLESALE PRICE
        JPanel whole = new JPanel();
        whole.setLayout(new BoxLayout(whole, BoxLayout.Y_AXIS));
        whole.setBackground(null);
        wholesaleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        whole.add(wholesaleLabel);
        whole.add(Box.createVerticalStrut(5));
        wholesalePrice.setAlignmentX(Component.LEFT_ALIGNMENT);
        whole.add(wholesalePrice);

        JPanel prices = new JPanel();
        prices.setLayout(new BoxLayout(prices, BoxLayout.X_AXIS));
        prices.setBackground(null);
        prices.add(retail);
        prices.add(Box.createHorizontalStrut(20));
        prices.add(whole);

        // QUANTITY 
        JPanel qty = new JPanel();
        qty.setLayout(new BoxLayout(qty, BoxLayout.Y_AXIS));
        qty.setBackground(null);
        qtyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        qty.add(qtyLabel);
        qty.add(Box.createVerticalStrut(5));
        quantity.setAlignmentX(Component.LEFT_ALIGNMENT);
        qty.add(quantity);

        // THRESHOLD
        JPanel thresh = new JPanel();
        thresh.setLayout(new BoxLayout(thresh, BoxLayout.Y_AXIS));
        thresh.setBackground(null);
        thresholdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        thresh.add(thresholdLabel);
        thresh.add(Box.createVerticalStrut(5));
        threshold.setAlignmentX(Component.LEFT_ALIGNMENT);
        thresh.add(threshold);

        // MAX QUANTITY
        JPanel max = new JPanel();
        max.setLayout(new BoxLayout(max, BoxLayout.Y_AXIS));
        max.setBackground(null);
        maxQtyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        max.add(maxQtyLabel);
        max.add(Box.createVerticalStrut(5));
        maxQty.setAlignmentX(Component.LEFT_ALIGNMENT);
        max.add(maxQty);

        // CATEGORY && SUBCATEGORY
        JPanel cat = new JPanel();
        cat.setLayout(new BoxLayout(cat, BoxLayout.Y_AXIS));
        cat.setBackground(null);
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cat.add(categoryLabel);
        cat.add(Box.createVerticalStrut(5));
        categories.setAlignmentX(Component.LEFT_ALIGNMENT);
        cat.add(categories);

        JPanel sub = new JPanel();
        sub.setLayout(new BoxLayout(sub, BoxLayout.Y_AXIS));
        sub.setBackground(null);
        subcategoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.add(subcategoryLabel);
        sub.add(Box.createVerticalStrut(5));
        subcategories.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.add(subcategories);

        JPanel subcat = new JPanel();
        subcat.setLayout(new BoxLayout(subcat, BoxLayout.X_AXIS));
        subcat.setBackground(null);
        subcat.add(cat);
        subcat.add(Box.createHorizontalStrut(20));
        subcat.add(sub);

        JPanel qtyPanel = new JPanel();
        qtyPanel.setLayout(new BoxLayout(qtyPanel, BoxLayout.X_AXIS));
        qtyPanel.setBackground(null);
        qtyPanel.add(qty);
        qtyPanel.add(Box.createHorizontalStrut(10));
        qtyPanel.add(max);
        qtyPanel.add(Box.createHorizontalStrut(10));
        qtyPanel.add(thresh);

        //----- SIZE COMPONENTS -----//
        itemName.setWidth(680);

        retailPrice.setWidth(330);
        wholesalePrice.setWidth(330);
        quantity.setWidth(220);
        maxQty.setWidth(220);
        threshold.setWidth(220);
        subcategories.setPreferredSize(new Dimension(330, 50));
        categories.setPreferredSize(new Dimension(330, 50));
        description.setPreferredSize(new Dimension(680, 150));

        imageBttn.setPreferredSize(new Dimension(204, 64));
        imageBttn.setBorderColor(Colors.GREEN);
        imageBttn.setBackground(Colors.MID_GREEN);
        imageBttn.setSelectedBorderColor(Colors.GREEN);
        imageBttn.setSelectedBackground(Colors.MID_GREEN);

        //----- ADD COMPONENTS TO THIS JPANEL -----//
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(name, gbc);

        gbc.gridy += 1;
        add(desc, gbc);

        gbc.gridy += 1;
        add(subcat, gbc);

        gbc.gridy += 1;
        add(prices, gbc);

        gbc.gridy += 1;
        add(qtyPanel, gbc);

        gbc.gridy += 1;
        add(imgPanel, gbc);

        gbc.gridy += 1;
        add(addItemBttn, gbc);
        addEventListeners();
    }

    //--------------------------------------------//
    //---------- NEW ITEM PANEL METHODS ----------//
    /**
     * Adds event listeners to components.
     */
    public void addEventListeners() {
        categories.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                subcategories.removeAllItems();
                subcategories.addItem("No subcategory");
                try {
                    ResultSet rs = State.getConnection().getSubcategory(categories.getSelectedItem().toString());
                    while (rs.next()) {
                        subcategories.addItem(rs.getString(1));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        itemName.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (itemName.getText().length() >= 200) {
                    e.consume();
                }
            }
        });
        description.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (description.getText().length() >= 5000) {
                    e.consume();
                }
            }
        });
        retailPrice.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                String str = retailPrice.getText();
                if (str.length() >= 12 || !Character.isDigit(e.getKeyChar()) && e.getKeyChar() != '.') {
                    e.consume();
                }
            }
        });
        wholesalePrice.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                String str = wholesalePrice.getText();
                if (str.length() >= 12 || !Character.isDigit(e.getKeyChar()) && e.getKeyChar() != '.') {
                    e.consume();
                }
            }
        });
        quantity.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) {
                    e.consume();
                }
            }
        });
        threshold.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) {
                    e.consume();
                }
            }
        });
        maxQty.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) {
                    e.consume();
                }
            }
        });

        imageBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                imageBttn.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                imageBttn.mouseReleased();
                uploadImage();
            }
        });

        Action imgPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                uploadImage();
            }
        };
        imageBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        imageBttn.getActionMap().put("pressed", imgPress);

        addItemBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                addItemBttn.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                addItemBttn.mouseReleased();
                submitForm();
            }
        });

        Action addPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                submitForm();
            }
        };
        addItemBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        addItemBttn.getActionMap().put("pressed", addPress);
    }

    /**
     * If the form is valid, creates a new inventory item in the database, then
     * clears the form. If the form is invalid, warnings are shown where needed.
     */
    private void submitForm() {
        String name = itemName.getText();
        String d = description.getText();
        int cat = categories.getSelectedIndex() + 1;
        String sub = subcategories.getSelectedItem().toString();
        String stock = quantity.getText();
        String maxStock = maxQty.getText();
        String stockThresh = threshold.getText();
        String price = retailPrice.getText();
        String wholesale = wholesalePrice.getText();

        int stockInt = 0;
        int maxInt = 0;
        int threshInt = 0;
        int validFields = 0;
        int requiredValid = 8;
        double priceDouble = 0;
        double wholeDouble = 0;

        if (!name.equals("")) {
            validFields++;
            nameLabel.removeWarning();
        } else {
            nameLabel.addWarning("Item name is required.");
        }

        if (!d.equals("")) {
            validFields++;
            descLabel.removeWarning();
        } else {
            descLabel.addWarning("Description is required.");
        }

        if (!stock.equals("")) {
            stockInt = Integer.parseInt(stock);
            validFields++;
            qtyLabel.removeWarning();
        } else {
            qtyLabel.addWarning("Quantity is required.");
        }

        if (!maxStock.equals("")) {
            maxInt = Integer.parseInt(maxStock);
            if (maxInt < stockInt) {
                maxQtyLabel.addWarning("Max quantity must be greater than or equal to current stock.");
            } else {
                validFields++;
                maxQtyLabel.removeWarning();
            }
        } else {
            maxQtyLabel.addWarning("Max quantity is required.");
        }

        if (!stockThresh.equals("")) {
            threshInt = Integer.parseInt(stockThresh);
            if (threshInt >= maxInt) {
                thresholdLabel.addWarning("Restock threshold must be less than max quantity.");
            } else {
                validFields++;
                thresholdLabel.removeWarning();
            }
        } else {
            thresholdLabel.addWarning("Restock threshold is required");
        }

        if (!price.equals("")) {
            try {
                priceDouble = Double.parseDouble(price);
                validFields++;
                retailLabel.removeWarning();
            } catch (Exception ex) {
                retailLabel.addWarning("Invalid price entered.");
            }
        } else {
            retailLabel.addWarning("Retail price is required.");
        }

        if (!wholesale.equals("")) {
            try {
                wholeDouble = Double.parseDouble(wholesale);
                if (wholeDouble >= priceDouble) {
                    wholesaleLabel.addWarning("Wholesale price must be less than retail price.");
                } else {
                    validFields++;
                    wholesaleLabel.removeWarning();
                }
            } catch (Exception ex) {
                wholesaleLabel.addWarning("Invalid price entered.");
            }
        } else {
            wholesaleLabel.addWarning("Wholesale price is required.");
        }

        if (imageFile == null) {
            imageLabel.addWarning("Upload an image.");
        } else {
            validFields++;
            imageLabel.removeWarning();
        }

        if (validFields == requiredValid) {
            InventoryItem item = new InventoryItem(name, d, cat, priceDouble, wholeDouble, stockInt, threshInt, maxInt, imageFile);
            if (sub != "") {
                try {
                    ResultSet rs = State.getConnection().getSubcategoryID(sub);
                    rs.next();
                    int s = rs.getInt(1);
                    item.setSubcategory(s);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            try {
                State.getConnection().insertInventoryItem(item);
                PopupDialog successDialog = new PopupDialog(State.getFrame());
                successDialog.setTitle("Success");
                successDialog.setLayout(new GridBagLayout());
                successDialog.setSize(new Dimension(400, 100));
                successDialog.setLocationRelativeTo(null);
                successDialog.add(new FieldLabel("Item successfully uploaded."), new GridBagConstraints());
                successDialog.setVisible(true);

                // clear the form
                itemName.setText("");
                retailPrice.setText("");
                wholesalePrice.setText("");
                quantity.setText("");
                threshold.setText("");
                maxQty.setText("");
                description.setText("");
                categories.setSelectedIndex(0);
                subcategories.removeAllItems();
                subcategories.addItem("No subcategory");

                // if the category at index 0 has subcategories, add them to the subcategories combobox
                try {
                    ResultSet rs = State.getConnection().getSubcategory(categories.getSelectedItem().toString());
                    while (rs.next()) {
                        subcategories.addItem(rs.getString(1));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (Exception io) {
                io.printStackTrace();
            }
        }
    }

    /**
     * Opens a JFileChooser and saves the selected image, then sets imageReq to
     * met.
     */
    private void uploadImage() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "png", "jpg", "jpeg");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(filter);
        chooser.setMultiSelectionEnabled(false);

        // opens an "Open File" file chooser dialog
        int val = chooser.showOpenDialog(State.getFrame());

        if (val == JFileChooser.APPROVE_OPTION) {
            imageFile = chooser.getSelectedFile();
            imageReq.requirementMet(true);
        }
    }
}
