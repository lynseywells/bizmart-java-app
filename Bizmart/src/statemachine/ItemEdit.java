package statemachine;

import component.labels.FieldLabel;
import component.labels.FieldRequiredLabel;
import component.Colors;
import component.PopupDialog;
import component.panels.DropShadowPanel;
import component.Sizing;
import component.buttons.ButtonPrimary;
import component.fields.InputField;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import javax.swing.*;
import util.*;
import javax.swing.plaf.metal.MetalCheckBoxIcon;
import javax.swing.filechooser.*;
import java.io.File;
import java.nio.file.Files;

/**
 * Subclass ItemEdit is a form for managers to edit inventory items. Fields are
 * initially filled with the items current details, so users can choose which
 * attributes to change. This state can move into the manager state.
 * <p>
 * ItemEdit is initialized each time the item state is entered to reflect the
 * item selected in the manager state.
 */
public class ItemEdit extends State {

    /**
     * The root JPanel for the item state that is placed in the JFrame when this
     * state is entered. Uses a BoxLayout.
     */
    private DropShadowPanel itemPanel;

    /**
     * JScrollPane for scrollPanel.
     */
    private JScrollPane itemScroll;

    /**
     * JPanel displays item information.
     */
    private JPanel scrollPanel;

    /**
     * Button to make ItemEdit form editable when pressed.
     */
    private ButtonPrimary editBttn;

    /**
     * Button to go back to manager state when pressed.
     */
    private ButtonPrimary backBttn;

    /**
     * Button to save changes to information when pressed.
     */
    private ButtonPrimary saveBttn;

    /**
     * Button to upload a new item image when pressed.
     */
    private ButtonPrimary imageBttn;

    /**
     * JCheckBox to toggle item availability. If checked, the item is
     * discontinued and can't be purchased.
     */
    private JCheckBox discontinueCheck;

    /**
     * Editable JTextArea for item information.
     */
    private JTextArea itemName, description;

    /**
     * Editable InputField for item information.
     */
    private InputField retailPrice, stock;

    /**
     * Label for editable field.
     */
    private FieldRequiredLabel nameLabel, priceLabel, stockLabel, descLabel;

    /**
     * Label for non-editable field.
     */
    private FieldLabel maxQtyLabel, discontinueLabel, wholesaleLabel;

    /**
     * JLabel to display icon.
     */
    private JLabel image;

    /**
     * The items image.
     */
    private ImageIcon icon;

    /**
     * Boolean determines the type of layout ItemEdit will have based on image
     * dimensions. If the image's width is greater than its height, this
     * variable is true and components are displayed below the image. If it's
     * height is greater than it's width this variable is false and components
     * are displayed next to the image.
     */
    private boolean verticalLayout;

    /**
     * The InventoryItem used to create this state.
     */
    private InventoryItem inventoryItem;

    /**
     * ItemEdit constructor initializes components needed for the item state.
     * Should be called each time the item state is entered.
     *
     * @param item item to edit
     */
    public ItemEdit(InventoryItem item) {
        //----- INITIALIZE COMPONENTS -----//
        inventoryItem = item;
        // ITEM PANEL
        itemPanel = new DropShadowPanel(8, Color.WHITE, Colors.SILVER, Colors.DARK_BLUE);
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));

        // INPUT FIELDS
        retailPrice = new InputField();
        retailPrice.setText(Money.DF.format(item.getRetailPrice()));
        retailPrice.setEditable(false);
        stock = new InputField();
        stock.setText(Integer.toString(item.getQuantity()));
        stock.setEditable(false);

        // ITEM IMAGE LABEL
        icon = item.getImageIcon();
        image = new JLabel(icon);
        sizeImage();

        // TEXT AREAS
        // item name
        itemName = new JTextArea(item.getItemName());
        itemName.setFont(new Font("Nunito", Font.PLAIN, 20));
        itemName.setLineWrap(true);
        itemName.setWrapStyleWord(true);
        itemName.setForeground(Colors.DARK_BLUE);
        itemName.setEditable(false);
        itemName.setBackground(Colors.GHOST_WHITE);
        itemName.setBorder(BorderFactory.createLineBorder(Colors.SILVER, 2));
        // setting height to MAX_VALUE forces JPanel to pack it down to minimum height needed to display all rows
        itemName.setSize(new Dimension(590, Integer.MAX_VALUE));

        // item description
        description = new JTextArea(item.getDescription());
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setFont(new Font("Nunito", Font.PLAIN, 20));
        description.setForeground(Colors.DARK_BLUE);
        description.setBackground(Colors.GHOST_WHITE);
        description.setEditable(false);
        description.setBorder(BorderFactory.createLineBorder(Colors.SILVER, 2));

        // BUTTONS
        // edit item button
        editBttn = new ButtonPrimary("Edit Details");
        editBttn.setBorderColor(Colors.GREEN);
        editBttn.setBackground(Colors.MID_GREEN);
        editBttn.setSelectedBorderColor(Colors.GREEN);
        editBttn.setSelectedBackground(Colors.MID_GREEN);
        // save changes button
        saveBttn = new ButtonPrimary("Save Changes");
        // back button
        backBttn = new ButtonPrimary("Go Back");
        backBttn.setBorderColor(Colors.GREEN);
        backBttn.setBackground(Colors.MID_GREEN);
        backBttn.setSelectedBorderColor(Colors.GREEN);
        backBttn.setSelectedBackground(Colors.MID_GREEN);
        // change item image button
        imageBttn = new ButtonPrimary("Change Image");
        imageBttn.setBorderColor(Colors.GREEN);
        imageBttn.setBackground(Colors.MID_GREEN);
        imageBttn.setSelectedBorderColor(Colors.GREEN);
        imageBttn.setSelectedBackground(Colors.MID_GREEN);
        //discontinue checkbox
        discontinueCheck = new JCheckBox();
        discontinueCheck.setFont(new Font("Nunito", Font.PLAIN, 22));
        discontinueCheck.setIcon(new MetalCheckBoxIcon() {
            protected int getControlSize() {
                return 20;
            }
        });
        discontinueCheck.setBackground(null);
        discontinueCheck.setForeground(Colors.RICH_BLACK);

        //LABELS 
        nameLabel = new FieldRequiredLabel("Product Name");
        priceLabel = new FieldRequiredLabel("Retail Price");
        stockLabel = new FieldRequiredLabel("In Stock");
        descLabel = new FieldRequiredLabel("Product Description");
        maxQtyLabel = new FieldLabel("Quantity Limit: " + inventoryItem.getMaxQuantity());
        discontinueLabel = new FieldLabel("Discontinued:");
        if (inventoryItem.isDiscontinued()) {
            discontinueCheck.setSelected(true);
            discontinueCheck.setText("Yes");
        } else {
            discontinueCheck.setSelected(false);
            discontinueCheck.setText("No");
        }
        discontinueCheck.setEnabled(false);
        wholesaleLabel = new FieldLabel("Wholesale Price: $" + Money.DF.format(inventoryItem.getWholesalePrice()));

        //----- GROUP COMPONENTS -----//
        // BUTTONS PANEL
        JPanel buttons = new JPanel();
        buttons.setBackground(null);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(backBttn);
        buttons.add(Box.createGlue());

        // create JPanel with no space around the edges so buttons appear flush with right side of outer panel
        JPanel editButtons = new JPanel(new FlowLayout(SwingConstants.LEFT, 0, 0));
        editButtons.setBackground(null);
        editButtons.add(editBttn);
        editButtons.add(Box.createHorizontalStrut(10));
        editButtons.add(saveBttn);

        buttons.add(editButtons);

        // NAME PANEL
        JPanel name = new JPanel();
        name.setBackground(null);
        name.setLayout(new BoxLayout(name, BoxLayout.Y_AXIS));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        name.add(nameLabel);
        itemName.setAlignmentX(Component.LEFT_ALIGNMENT);
        name.add(itemName);

        // PRICE PANEL
        JPanel price = new JPanel();
        price.setBackground(null);
        price.setLayout(new BoxLayout(price, BoxLayout.Y_AXIS));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        price.add(priceLabel);
        retailPrice.setAlignmentX(Component.LEFT_ALIGNMENT);
        price.add(retailPrice);

        // STOCK PANEL
        JPanel stockPanel = new JPanel();
        stockPanel.setBackground(null);
        stockPanel.setLayout(new BoxLayout(stockPanel, BoxLayout.Y_AXIS));
        stockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        stockPanel.add(stockLabel);
        stock.setAlignmentX(Component.LEFT_ALIGNMENT);
        stockPanel.add(stock);

        // DESCRIPTION PANEL
        JPanel desc = new JPanel();
        desc.setBackground(null);
        desc.setLayout(new BoxLayout(desc, BoxLayout.Y_AXIS));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        desc.add(descLabel);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);
        desc.add(description);

        // DISCONTINUE PANEL
        JPanel discontinue = new JPanel();
        discontinue.setBackground(null);
        discontinue.setLayout(new BoxLayout(discontinue, BoxLayout.X_AXIS));
        discontinue.add(discontinueLabel);
        discontinue.add(Box.createHorizontalStrut(10));
        discontinue.add(discontinueCheck);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(null);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(name, gbc);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        form.add(desc, gbc);
        gbc.gridy = 2;
        form.add(price, gbc);
        gbc.gridy = 3;
        form.add(stockPanel, gbc);
        gbc.gridy = 4;
        form.add(maxQtyLabel, gbc);
        gbc.gridy = 5;
        form.add(discontinue, gbc);
        gbc.gridy = 6;
        form.add(wholesaleLabel, gbc);

        //----- SIZE COMPONENTS -----//
        int width = State.getFrame().getWidth() - 100;
        int height = State.getFrame().getHeight() - 100;
        Dimension d = new Dimension(width, height);
        itemPanel.setPreferredSize(d);
        itemPanel.setMaximumSize(d);
        sizeImage();

        saveBttn.setPreferredSize(new Dimension(200, 54));
        editBttn.setPreferredSize(new Dimension(200, 54));
        imageBttn.setPreferredSize(new Dimension(225, 54));
        backBttn.setPreferredSize(new Dimension(150, 54));

        // setting height to MAX_VALUE forces JPanel to pack it down to minimum height needed to display all rows
        description.setSize(new Dimension(590, Integer.MAX_VALUE));

        //----- ADD COMPONENTS TO SCROLLPANE -----//
        // layout changes based on image dimensions
        scrollPanel = new JPanel();
        scrollPanel.setLayout(new GridBagLayout());
        scrollPanel.setBackground(null);
        gbc = new GridBagConstraints();

        if (!verticalLayout) {
            gbc.gridx = 0;
            gbc.insets = new Insets(0, 5, 8, 5);
        } else {
            gbc.insets = new Insets(0, 0, 8, 0);
        }
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        image.setMaximumSize(image.getMaximumSize());
        scrollPanel.add(image, gbc);

        gbc.gridy = 1;
        scrollPanel.add(imageBttn, gbc);

        if (!verticalLayout) {
            gbc.gridy = 0;
            gbc.gridx = 1;
            gbc.gridheight = 2;
            gbc.anchor = GridBagConstraints.NORTHWEST;
        } else {
            gbc.gridy = 2;
        }
        scrollPanel.add(form, gbc);

        gbc.gridy += 1;
        if (!verticalLayout) {
            gbc.gridwidth = 2;
            gbc.gridx = 0;
            gbc.gridheight = 1;
            gbc.gridy += 1;
        }
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);
        scrollPanel.add(buttons, gbc);
        scrollPanel.setPreferredSize(scrollPanel.getPreferredSize());

        // CREATE SCROLLPANE
        itemScroll = new JScrollPane(scrollPanel);
        itemScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        itemScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        itemScroll.getVerticalScrollBar().setUnitIncrement(16);
        itemScroll.setBorder(null);
        itemScroll.getViewport().setBackground(Color.WHITE);

        // ADD SCROLLPANE TO THIS PANEL
        itemPanel.add(itemScroll);
    }

    //--------------------------------------------//
    //---------- ABSTRACT STATE METHODS ----------//
    //---------- ENTER STATE ----------//
    @Override
    protected void enterState() {
        State.frame.setTitle("Edit Item");
        State.frame.getContentPane().removeAll();

        // add item panel to frame
        // default GridBagConstraints centers panel in frame & respects sizing
        State.frame.add(itemPanel, new GridBagConstraints());

        // repaint the frame
        State.frame.repaint();
        State.frame.setVisible(true);

        // request focus
        itemScroll.requestFocusInWindow();
    }

    //---------- ADD EVENT LISTENERS TO COMPONENTS ----------//
    @Override
    protected void addEventListeners() {
        //----- BACK BUTTON -----//
        AbstractAction backPress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeState(State.manager);
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
                changeState(State.manager);
            }
        });

        //----- EDIT BUTTON -----//
        AbstractAction editPress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setEditable(true);
            }
        };
        editBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        editBttn.getActionMap().put("pressed", editPress);

        editBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                editBttn.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                editBttn.mouseReleased();
                setEditable(true);
            }
        });

        //----- SAVE BUTTON -----//
        AbstractAction savePress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitForm();
            }
        };
        saveBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        saveBttn.getActionMap().put("pressed", savePress);

        saveBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                saveBttn.mousePressed();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                saveBttn.mouseReleased();
                submitForm();
            }
        });

        //----- IMAGE BUTTON -----//
        AbstractAction imagePress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadImage();
            }
        };
        imageBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        imageBttn.getActionMap().put("pressed", imagePress);

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

        //----- DISCONTINUED CHECKBOX -----//
        discontinueCheck.addItemListener((ItemEvent e) -> {
            if (discontinueCheck.isSelected()) {
                discontinueCheck.setText("Yes");
            } else {
                discontinueCheck.setText("No");
            }
        });

        //----- NAME FIELD -----//
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

        //----- DESCRIPTION FIELD -----//
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

        //----- RETAIL PRICE FIELD -----//
        retailPrice.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar()) && e.getKeyChar() != '.' || retailPrice.getText().length() >= 12) {
                    e.consume();
                }
            }
        });

        //----- STOCK FIELD -----//
        stock.addKeyListener(new KeyListener() {
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
    }

    //----------------------------------------//
    //---------- ITEM STATE METHODS ----------//
    /**
     * Scales the item image to fit inside itemPanel. Also determines the type
     * of layout itemPanel will have based on image dimensions.
     */
    protected void sizeImage() {
        // get size of this panel
        int height = (int) itemPanel.getPreferredSize().getHeight();

        // get image size
        int imgWidth = icon.getIconWidth();
        int imgHeight = icon.getIconHeight();
        int newWidth, newHeight;
        if (imgWidth > imgHeight) {
            // calculate scale from width to new width
            newWidth = 400;
            double scale = (double) newWidth / imgWidth;

            // use scale to set the new width while maintaining aspect ratio
            newHeight = (int) Math.round(imgHeight * scale);

            // if image is too tall, subtract from size until image fits in jpanel
            while (newHeight >= height - 100) {
                newWidth -= 10;
                newHeight -= 10;
            }
            verticalLayout = true;
        } else {
            // calculate scale from height to new height
            newHeight = 500;
            double scale = (double) newHeight / imgHeight;

            // use scale to set the new width while maintaining aspect ratio
            newWidth = (int) Math.round(imgWidth * scale);

            // if image is too wide, subtract from size until image fits in jpanel
            while (newWidth >= 400) {
                newWidth -= 10;
                newHeight -= 10;
            }
            verticalLayout = false;
        }

        // create a new icon to preserve original icons sizing
        ImageIcon newIcon = Sizing.resizeIcon(icon, newWidth, newHeight);
        image.setIcon(newIcon);
    }

    /**
     * Disables or enables the editable fields in the form.
     *
     * @param b True makes form editable, false disables form.
     */
    protected void setEditable(boolean b) {
        itemName.setEditable(b);
        description.setEditable(b);
        stock.setEditable(b);
        retailPrice.setEditable(b);
        discontinueCheck.setEnabled(b);
    }

    /**
     * Validates the information entered and uploads changes to the database.
     */
    private void submitForm() {
        String name = itemName.getText();
        String desc = description.getText();
        String price = retailPrice.getText();
        String qty = stock.getText();

        int validFields = 0;

        if (name.equals("")) {
            nameLabel.addWarning("Product name is required.");
        } else {
            nameLabel.removeWarning();
            validFields++;
        }

        if (desc.equals("")) {
            descLabel.addWarning("Description is required.");
        } else {
            descLabel.removeWarning();
            validFields++;
        }

        if (price.equals("")) {
            priceLabel.addWarning("Price is required.");
        } else {
            double p;
            try {
                p = Double.parseDouble(price);
                if (p <= inventoryItem.getWholesalePrice()) {
                    priceLabel.addWarning("Price cannot be below wholesale price.");
                } else {
                    priceLabel.removeWarning();
                    validFields++;
                }
            } catch (Exception ex) {
                priceLabel.addWarning("Price is not valid.");
            }
        }

        if (qty.equals("")) {
            stockLabel.addWarning("Stock is required.");
        } else {
            int i = Integer.parseInt(qty);
            if (i > inventoryItem.getMaxQuantity()) {
                stockLabel.addWarning("Stock cannot go above max quantity.");
            } else if (i < inventoryItem.getQuantity()) {
                stockLabel.addWarning("Cannot subtract from current quantity.");
            } else {
                stockLabel.removeWarning();
                validFields++;
            }
        }

        if (validFields == 4) {
            try {
                boolean discontinued = discontinueCheck.isSelected();
                inventoryItem.setQuantity(Integer.parseInt(qty));
                inventoryItem.setName(name);
                inventoryItem.setDescription(desc);
                inventoryItem.setRetailPrice(Double.parseDouble(price));
                inventoryItem.setDiscontinued(discontinued);
                // save changes to the database
                con.updateInventoryItem(inventoryItem);
                // update this state to reflect changes
                State.currentState = manager;
                State.createItemState(inventoryItem);
                // show success dialog
                PopupDialog successDialog = new PopupDialog(State.getFrame());
                successDialog.setTitle("Success");
                successDialog.setLayout(new GridBagLayout());
                successDialog.setSize(new Dimension(450, 100));
                successDialog.setLocationRelativeTo(null);
                successDialog.add(new FieldLabel("Item information successfully updated."), new GridBagConstraints());
                successDialog.setVisible(true);

                // repaint scroll in manager state to reflect changes
                ((Manager) State.getManagerState()).repaintScroll();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Opens a file chooser, resizes the selected image down to a thumbnail, and
     * uploads the thumbnail and original image to the database.
     */
    private void uploadImage() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "png", "jpg", "jpeg");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(filter);
        chooser.setMultiSelectionEnabled(false);

        // opens an "Open File" file chooser dialog
        int val = chooser.showOpenDialog(frame);

        if (val == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                Image thumbnail = Sizing.createThumbnail(new ImageIcon(Files.readAllBytes(f.toPath())));
                con.updateImage(inventoryItem.getItemNumber(), f, thumbnail);
                ((Manager) State.getManagerState()).repaintScroll();
                State.currentState = State.manager;
                // update this inventory item & create a new state so changes are reflected and more edits can be done
                inventoryItem.setImage(f);
                State.createItemState(inventoryItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
