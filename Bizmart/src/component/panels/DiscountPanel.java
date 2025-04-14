package component.panels;

import component.labels.FieldLabel;
import component.labels.FormTitle;
import component.labels.FieldRequiredLabel;
import component.Colors;
import java.awt.*;
import javax.swing.*;
import util.Discount;
import component.fields.InputField;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import component.PopupDialog;
import component.buttons.ButtonPrimary;
import java.awt.event.*;
import statemachine.State;
import statemachine.Manager;
import java.sql.*;
import util.Money;
import java.time.LocalDate;
import javax.swing.plaf.metal.MetalCheckBoxIcon;

/**
 * DiscountPanel provides a form for the manager state of the Bizmart
 * application to create new discounts or edit existing discounts.
 */
public class DiscountPanel extends JPanel {

    /**
     * Label for a required field.
     */
    private FieldRequiredLabel codeLabel, descLabel, levelLabel, itemLabel, typeLabel,
            percentLabel, dollarLabel, expLabel;

    /**
     * Label for an optional field.
     */
    private FieldLabel startLabel, disableLabel;

    /**
     * InputField for discount code text. Limited to 20 characters.
     */
    private InputField codeField;

    /**
     * InputField for the inventory item SKU number this discount is associated
     * with.
     * <p>
     * Only visible when "Item" is selected in levelBox.
     */
    private InputField itemField;

    /**
     * InputField for discount percentage.
     * <p>
     * Only visible when "Percentage" is selected in typeBox.
     */
    private InputField percentField;

    /**
     * InputField for discount dollar amount.
     * <p>
     * Only visible when "Dollar Amount" is selected in typeBox.
     */
    private InputField dollarField;

    /**
     * JComboBox determines discount level, either "Cart" or "Item".
     */
    private JComboBox levelBox;

    /**
     * JComboBox determines discount type, either "Dollar Amount" or
     * "Percentage".
     */
    private JComboBox typeBox;

    /**
     * Optional DatePicker for discount start date.
     */
    private DatePicker startDate;

    /**
     * DatePicker for discount expiration date.
     */
    private DatePicker expDate;

    /**
     * JTextArea for discount description. Limited to 50 characters.
     */
    private JTextArea descField;

    /**
     * JCheckBox disables or enables discount. Only visible when editing an
     * existing discount.
     */
    private JCheckBox disableCheck;

    /**
     * Button uploads new discount code or saves changes to existing discount in
     * the database if the form is valid.
     */
    private ButtonPrimary submitBttn;

    /**
     * JPanel contains dollarLabel and dollarField.
     * <p>
     * Only visible when "Dollar Amount" is selected in typeBox.
     */
    private JPanel dollar;

    /**
     * JPanel contains percentLabel and percentField.
     * <p>
     * Only visible when "Percentage" is selected in typeBox.
     */
    private JPanel percent;

    /**
     * JPanel contains itemLabel and itemField.
     * <p>
     * Only visible when "Item" is selected in levelBox.
     */
    private JPanel item;

    /**
     * The discount being edited in DiscountPanel.
     */
    private Discount currentDiscount;

    /**
     * DiscountPanel constructor sets up form to create a new discount.
     */
    public DiscountPanel() {
        //----- INITIALIZE COMPONENTS -----//
        super();
        setBackground(null);

        // LABELS
        codeLabel = new FieldRequiredLabel("Discount Code");
        descLabel = new FieldRequiredLabel("Description");
        levelLabel = new FieldRequiredLabel("Discount Level");
        itemLabel = new FieldRequiredLabel("Inventory Item ID");
        typeLabel = new FieldRequiredLabel("Discount Type");
        percentLabel = new FieldRequiredLabel("Discount Percentage");
        dollarLabel = new FieldRequiredLabel("Discount Dollar Amount");
        startLabel = new FieldLabel("Start Date");
        expLabel = new FieldRequiredLabel("Expiration Date");

        // FIELDS
        codeField = new InputField();
        itemField = new InputField();
        percentField = new InputField();
        dollarField = new InputField();

        // DESCRIPTION TEXT AREA
        descField = new JTextArea();
        descField.setFont(new Font("Nunito", Font.PLAIN, 18));
        descField.setForeground(Colors.DARK_BLUE);
        descField.setWrapStyleWord(true);
        descField.setBackground(Colors.GHOST_WHITE);
        descField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        descField.setLineWrap(true);

        // COMBO BOXES
        levelBox = new JComboBox<String>();
        levelBox.addItem("Cart");
        levelBox.addItem("Item");
        typeBox = new JComboBox<String>();
        typeBox.addItem("Percentage");
        typeBox.addItem("Dollar Amount");

        // DATE PICKERS
        Font f = new Font("Nunito", Font.PLAIN, 18);
        DatePickerSettings startDps = new DatePickerSettings();
        // start date picker
        startDps.setFontValidDate(f);
        startDps.setFontVetoedDate(f);
        startDps.setFontInvalidDate(f);
        startDate = new DatePicker(startDps);
        // expiration date picker
        DatePickerSettings expDps = new DatePickerSettings();
        expDps.setFontValidDate(f);
        expDps.setFontVetoedDate(f);
        expDps.setFontInvalidDate(f);
        expDate = new DatePicker(expDps);

        // BUTTONS
        submitBttn = new ButtonPrimary("Create Discount");

        //----- GROUP COMPONENTS -----//
        // CODE && DISCOUNT LEVEL
        // align discount code label && field vertically
        JPanel code = new JPanel();
        code.setLayout(new BoxLayout(code, BoxLayout.Y_AXIS));
        code.setBackground(null);
        codeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        code.add(codeLabel);
        code.add(Box.createVerticalStrut(5));
        codeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        code.add(codeField);

        // align discount level label && field vertically
        JPanel level = new JPanel();
        level.setLayout(new BoxLayout(level, BoxLayout.Y_AXIS));
        level.setBackground(null);
        levelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        level.add(levelLabel);
        level.add(Box.createVerticalStrut(5));
        levelBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        level.add(levelBox);

        // align discount code && discount label panels horizontally
        JPanel codeLevel = new JPanel();
        codeLevel.setLayout(new BoxLayout(codeLevel, BoxLayout.X_AXIS));
        codeLevel.setBackground(null);
        codeLevel.add(code);
        codeLevel.add(Box.createHorizontalStrut(10));
        codeLevel.add(level);

        // DESCRIPTION
        JPanel desc = new JPanel();
        desc.setLayout(new BoxLayout(desc, BoxLayout.Y_AXIS));
        desc.setBackground(null);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        desc.add(descLabel);
        desc.add(Box.createVerticalStrut(5));
        descField.setAlignmentX(Component.LEFT_ALIGNMENT);
        desc.add(descField);

        // DISCOUNT && PERCENTAGE || DOLLAR AMOUNT
        // align discount type label && combobox vertically
        JPanel type = new JPanel();
        type.setLayout(new BoxLayout(type, BoxLayout.Y_AXIS));
        type.setBackground(null);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        type.add(typeLabel);
        type.add(Box.createVerticalStrut(5));
        typeBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        type.add(typeBox);

        // align discount percent label && field vertically
        percent = new JPanel();
        percent.setLayout(new BoxLayout(percent, BoxLayout.Y_AXIS));
        percent.setBackground(null);
        percentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        percent.add(percentLabel);
        percent.add(Box.createVerticalStrut(5));
        percentField.setAlignmentX(Component.LEFT_ALIGNMENT);
        percent.add(percentField);

        // align discount dollar amount label && field vertically
        // set invisible by default
        dollar = new JPanel();
        dollar.setLayout(new BoxLayout(dollar, BoxLayout.Y_AXIS));
        dollar.setBackground(null);
        dollarLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dollar.add(dollarLabel);
        dollar.add(Box.createVerticalStrut(5));
        dollarField.setAlignmentX(Component.LEFT_ALIGNMENT);
        dollar.add(dollarField);
        dollar.setVisible(false);

        // align discount type && percent && dollar amount panels horizontally
        // only percent || dollar amount is shown at a time
        JPanel typeAmount = new JPanel();
        typeAmount.setLayout(new BoxLayout(typeAmount, BoxLayout.X_AXIS));
        typeAmount.setBackground(null);
        typeAmount.add(type);
        typeAmount.add(Box.createHorizontalStrut(10));
        typeAmount.add(percent);
        typeAmount.add(dollar);

        // INVENTORY ITEM
        item = new JPanel();
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.setBackground(null);
        itemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.add(itemLabel);
        item.add(Box.createVerticalStrut(5));
        itemField.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.add(itemField);
        item.setVisible(false);

        // PROMO CODE DATES
        // align start date label && field vertically
        JPanel start = new JPanel();
        start.setLayout(new BoxLayout(start, BoxLayout.Y_AXIS));
        start.setBackground(null);
        startLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        start.add(startLabel);
        start.add(Box.createVerticalStrut(5));
        startDate.setAlignmentX(Component.LEFT_ALIGNMENT);
        start.add(startDate);

        // align expiration date label && field vertically
        JPanel exp = new JPanel();
        exp.setLayout(new BoxLayout(exp, BoxLayout.Y_AXIS));
        exp.setBackground(null);
        expLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        exp.add(expLabel);
        exp.add(Box.createVerticalStrut(5));
        expDate.setAlignmentX(Component.LEFT_ALIGNMENT);
        exp.add(expDate);

        // align start && expiration date horizontally 
        JPanel dates = new JPanel();
        dates.setLayout(new BoxLayout(dates, BoxLayout.X_AXIS));
        dates.setBackground(null);
        dates.add(start);
        dates.add(Box.createHorizontalStrut(10));
        dates.add(exp);

        //----- SIZE COMPONENTS -----//
        // FIELDS
        codeField.setWidth(300);
        percentField.setWidth(260);
        dollarField.setWidth(260);
        descField.setPreferredSize(new Dimension(480, 100));
        itemField.setWidth(480);

        // COMBOBOXES
        levelBox.setFont(new Font("Nunito", Font.PLAIN, 22));
        levelBox.setPreferredSize(new Dimension(170, 45));

        typeBox.setFont(new Font("Nunito", Font.PLAIN, 22));
        typeBox.setPreferredSize(new Dimension(210, 45));

        // DATE FIELDS
        startDate.getComponentDateTextField().setPreferredSize(new Dimension(205, 45));
        expDate.getComponentDateTextField().setPreferredSize(new Dimension(205, 45));

        // BUTTONS
        submitBttn.setPreferredSize(new Dimension(250, 64));

        //----- ADD COMPONENTS TO THIS PANEL -----//
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        add(new FormTitle("Create Discount", 500, 2), gbc);
        gbc.gridy += 1;
        add(dates, gbc);
        gbc.gridy += 1;
        add(codeLevel, gbc);
        gbc.gridy += 1;
        add(item, gbc);
        gbc.gridy += 1;
        add(typeAmount, gbc);
        gbc.gridy += 1;
        add(desc, gbc);
        gbc.gridy += 1;
        add(submitBttn, gbc);

        addEventListeners();
    }

    /**
     * DiscountPanel constructor sets up form to edit an existing discount.
     *
     * @param d the discount code to edit
     */
    public DiscountPanel(Discount d) {
        //----- INITIALIZE COMPONENTS -----//
        super();
        setBackground(null);
        currentDiscount = d;

        // DISABLED CHECKBOX 
        disableLabel = new FieldLabel("Disabled");
        disableLabel.setLabelFont(new Font("Nunito", Font.PLAIN, 20));
        disableCheck = new JCheckBox();
        disableCheck.setFont(new Font("Nunito", Font.PLAIN, 22));
        disableCheck.setIcon(new MetalCheckBoxIcon() {
            protected int getControlSize() {
                return 20;
            }
        });
        disableCheck.setBackground(null);
        disableCheck.setForeground(Colors.RICH_BLACK);

        // LABELS
        codeLabel = new FieldRequiredLabel("Discount Code");
        descLabel = new FieldRequiredLabel("Description");
        levelLabel = new FieldRequiredLabel("Discount Level");
        itemLabel = new FieldRequiredLabel("Inventory Item ID");
        typeLabel = new FieldRequiredLabel("Discount Type");
        percentLabel = new FieldRequiredLabel("Discount Percentage");
        dollarLabel = new FieldRequiredLabel("Discount Dollar Amount");
        startLabel = new FieldLabel("Start Date");
        expLabel = new FieldRequiredLabel("Expiration Date");

        // FIELDS
        codeField = new InputField();
        itemField = new InputField();
        percentField = new InputField();
        dollarField = new InputField();
        descField = new JTextArea();
        descField.setFont(new Font("Nunito", Font.PLAIN, 18));
        descField.setForeground(Colors.DARK_BLUE);
        descField.setWrapStyleWord(true);
        descField.setBackground(Colors.GHOST_WHITE);
        descField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        descField.setLineWrap(true);

        // COMBO BOXES
        levelBox = new JComboBox<String>();
        levelBox.addItem("Cart");
        levelBox.addItem("Item");
        typeBox = new JComboBox<String>();
        typeBox.addItem("Percentage");
        typeBox.addItem("Dollar Amount");

        // DATE PICKER
        DatePickerSettings startDps = new DatePickerSettings();
        DatePickerSettings expDps = new DatePickerSettings();
        Font f = new Font("Nunito", Font.PLAIN, 18);
        startDps.setFontValidDate(f);
        startDps.setFontVetoedDate(f);
        startDps.setFontInvalidDate(f);
        expDps.setFontValidDate(f);
        expDps.setFontVetoedDate(f);
        expDps.setFontInvalidDate(f);
        startDate = new DatePicker(startDps);
        expDate = new DatePicker(expDps);

        // BUTTONS
        submitBttn = new ButtonPrimary("Save Changes");

        //----- GROUP COMPONENTS -----//
        // CODE && DISCOUNT LEVEL
        JPanel code = new JPanel();
        code.setLayout(new BoxLayout(code, BoxLayout.Y_AXIS));
        code.setBackground(null);
        codeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        code.add(codeLabel);
        code.add(Box.createVerticalStrut(5));
        codeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        code.add(codeField);

        JPanel level = new JPanel();
        level.setLayout(new BoxLayout(level, BoxLayout.Y_AXIS));
        level.setBackground(null);
        levelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        level.add(levelLabel);
        level.add(Box.createVerticalStrut(5));
        levelBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        level.add(levelBox);

        JPanel codeLevel = new JPanel();
        codeLevel.setLayout(new BoxLayout(codeLevel, BoxLayout.X_AXIS));
        codeLevel.setBackground(null);
        codeLevel.add(code);
        codeLevel.add(Box.createHorizontalStrut(10));
        codeLevel.add(level);

        // DESCRIPTION
        JPanel desc = new JPanel();
        desc.setLayout(new BoxLayout(desc, BoxLayout.Y_AXIS));
        desc.setBackground(null);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        desc.add(descLabel);
        desc.add(Box.createVerticalStrut(5));
        descField.setAlignmentX(Component.LEFT_ALIGNMENT);
        desc.add(descField);

        // DISCOUNT && PERCENTAGE || DOLLAR AMOUNT
        JPanel type = new JPanel();
        type.setLayout(new BoxLayout(type, BoxLayout.Y_AXIS));
        type.setBackground(null);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        type.add(typeLabel);
        type.add(Box.createVerticalStrut(5));
        typeBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        type.add(typeBox);

        percent = new JPanel();
        percent.setLayout(new BoxLayout(percent, BoxLayout.Y_AXIS));
        percent.setBackground(null);
        percentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        percent.add(percentLabel);
        percent.add(Box.createVerticalStrut(5));
        percentField.setAlignmentX(Component.LEFT_ALIGNMENT);
        percent.add(percentField);

        dollar = new JPanel();
        dollar.setLayout(new BoxLayout(dollar, BoxLayout.Y_AXIS));
        dollar.setBackground(null);
        dollarLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dollar.add(dollarLabel);
        dollar.add(Box.createVerticalStrut(5));
        dollarField.setAlignmentX(Component.LEFT_ALIGNMENT);
        dollar.add(dollarField);
        dollar.setVisible(false);

        JPanel typeAmount = new JPanel();
        typeAmount.setLayout(new BoxLayout(typeAmount, BoxLayout.X_AXIS));
        typeAmount.setBackground(null);
        typeAmount.add(type);
        typeAmount.add(Box.createHorizontalStrut(10));
        typeAmount.add(percent);
        typeAmount.add(dollar);

        // INVENTORY ITEM
        item = new JPanel();
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.setBackground(null);
        itemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.add(itemLabel);
        item.add(Box.createVerticalStrut(5));
        itemField.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.add(itemField);
        item.setVisible(false);

        // PROMO CODE DATES
        JPanel start = new JPanel();
        start.setLayout(new BoxLayout(start, BoxLayout.Y_AXIS));
        start.setBackground(null);
        startLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        start.add(startLabel);
        start.add(Box.createVerticalStrut(5));
        startDate.setAlignmentX(Component.LEFT_ALIGNMENT);
        start.add(startDate);

        JPanel exp = new JPanel();
        exp.setLayout(new BoxLayout(exp, BoxLayout.Y_AXIS));
        exp.setBackground(null);
        expLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        exp.add(expLabel);
        exp.add(Box.createVerticalStrut(5));
        expDate.setAlignmentX(Component.LEFT_ALIGNMENT);
        exp.add(expDate);

        JPanel dates = new JPanel();
        dates.setLayout(new BoxLayout(dates, BoxLayout.X_AXIS));
        dates.setBackground(null);
        dates.add(start);
        dates.add(Box.createHorizontalStrut(10));
        dates.add(exp);

        // DISABLED CHECKBOX
        JPanel disablePanel = new JPanel();
        disablePanel.setLayout(new BoxLayout(disablePanel, BoxLayout.X_AXIS));
        disablePanel.setBackground(null);
        disablePanel.add(disableLabel);
        disablePanel.add(Box.createHorizontalStrut(10));
        disablePanel.add(disableCheck);

        //----- SET FIELD TEXT -----//
        codeField.setText(d.getDiscountCode());
        descField.setText(d.getDescription());
        levelBox.setSelectedIndex(d.getDiscountLevel());
        typeBox.setSelectedIndex(d.getDiscountType());
        if (d.getDiscountLevel() == Discount.ITEM_LEVEL) {
            itemField.setText(Integer.toString(d.getInventoryID()));
        }
        if (d.getDiscountType() == Discount.PERCENT_TYPE) {
            percent.setVisible(true);
            dollar.setVisible(false);
            percentField.setText(Integer.toString((int) (d.getPercentage() * 100)));
        } else {
            percent.setVisible(false);
            dollar.setVisible(true);
            dollarField.setText(Money.DF.format(d.getDollarAmount()));
        }

        if (d.getStartDate() != null) {
            startDate.setDate(d.getStartDate());
        }
        expDate.setDate(d.getExpirationDate());

        if (d.isDisabled()) {
            disableCheck.setSelected(true);
            disableCheck.setText("Yes");
        } else {
            disableCheck.setSelected(false);
            disableCheck.setText("No");
        }

        //----- SIZE COMPONENTS -----//
        codeField.setWidth(300);
        percentField.setWidth(260);
        dollarField.setWidth(260);
        descField.setPreferredSize(new Dimension(480, 100));
        itemField.setWidth(480);

        levelBox.setFont(new Font("Nunito", Font.PLAIN, 22));
        levelBox.setPreferredSize(new Dimension(170, 45));

        typeBox.setFont(new Font("Nunito", Font.PLAIN, 22));
        typeBox.setPreferredSize(new Dimension(210, 45));

        startDate.getComponentDateTextField().setPreferredSize(new Dimension(205, 45));
        expDate.getComponentDateTextField().setPreferredSize(new Dimension(205, 45));
        submitBttn.setPreferredSize(new Dimension(250, 64));

        //----- ADD COMPONENTS TO THIS PANEL -----//
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        add(new FormTitle("Edit Discount", 500, 2), gbc);
        gbc.gridy += 1;
        add(dates, gbc);
        gbc.gridy += 1;
        add(codeLevel, gbc);
        gbc.gridy += 1;
        add(item, gbc);
        gbc.gridy += 1;
        add(typeAmount, gbc);
        gbc.gridy += 1;
        add(desc, gbc);
        gbc.gridy += 1;
        add(disablePanel, gbc);
        gbc.gridy += 1;
        add(submitBttn, gbc);

        addEventListeners();
    }

    //--------------------------------------------//
    //---------- DISCOUNT PANEL METHODS ----------//
    /**
     * Adds event listeners to components.
     */
    protected void addEventListeners() {
        codeField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (codeField.getText().length() >= 20) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        descField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (descField.getText().length() >= 50) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        itemField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        percentField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar()) || percentField.getText().length() >= 3) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        dollarField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar()) && e.getKeyChar() != '.' || dollarField.getText().length() >= 15) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        levelBox.addItemListener((ItemEvent e) -> {
            if (levelBox.getSelectedIndex() == Discount.CART_LEVEL) {
                item.setVisible(false);
            } else {
                item.setVisible(true);
            }
        });

        typeBox.addItemListener((ItemEvent e) -> {
            if (typeBox.getSelectedIndex() == Discount.PERCENT_TYPE) {
                percent.setVisible(true);
                dollar.setVisible(false);
            } else {
                percent.setVisible(false);
                dollar.setVisible(true);
            }
        });

        submitBttn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                submitBttn.mousePressed();
            }

            public void mouseReleased(MouseEvent e) {
                submitBttn.mouseReleased();
                submitForm();
            }
        });

        if (currentDiscount != null) {
            disableCheck.addItemListener((ItemEvent e) -> {
                if (disableCheck.isSelected()) {
                    disableCheck.setText("Yes");
                } else {
                    disableCheck.setText("No");
                }
            });
        }
    }

    /**
     * If the form is valid, either creates a new discount in the database or
     * saves changes to an existing discount, then clears the form. If the form
     * is invalid, warnings are shown where needed.
     */
    protected void submitForm() {
        String code = codeField.getText();
        String desc = descField.getText();
        int level = levelBox.getSelectedIndex();
        int type = typeBox.getSelectedIndex();
        int percent = 0;
        double dollar = 0;
        int item = 0;
        String exp = expDate.getText();
        String sta = startDate.getText();
        LocalDate today = LocalDate.now();
        LocalDate expiration = null;
        LocalDate start = null;
        int requiredValid = 4;

        if (level == Discount.ITEM_LEVEL) {
            requiredValid++;
        }

        if (!sta.equals("")) {
            requiredValid++;
        }

        int validFields = 0;
        if (type == Discount.PERCENT_TYPE) {
            if (!percentField.getText().equals("")) {
                percent = Integer.parseInt(percentField.getText());
                if (percent < 5) {
                    percentLabel.addWarning("Percentage cannot be less than 5.");
                } else if (percent > 75) {
                    percentLabel.addWarning("Percentage cannot be greater than 75.");
                } else {
                    validFields++;
                    percentLabel.removeWarning();
                }
            } else {
                percentLabel.addWarning("Percentage is required for percent type discounts.");
            }
        } else {
            if (!dollarField.getText().equals("")) {
                try {
                    dollar = Double.parseDouble(dollarField.getText());
                    if (dollar <= 0) {
                        dollarLabel.addWarning("Dollar amount must be above 0.");
                    } else {
                        validFields++;
                    }
                } catch (Exception e) {
                    dollarLabel.addWarning("Invalid dollar amount entered.");
                }
            } else {
                dollarLabel.addWarning("Dollar amount is required for dollar amount type discounts.");
            }
        }

        if (level == Discount.ITEM_LEVEL) {
            if (!itemField.getText().equals("")) {
                item = Integer.parseInt(itemField.getText());
                try {
                    ResultSet rs = State.getConnection().getItem(item);
                    if (rs.isBeforeFirst()) {
                        rs.next();
                        if (type == Discount.DOLLAR_TYPE) {
                            double price = rs.getDouble(6);
                            if ((price * .75) < dollar) {
                                dollarLabel.addWarning("Dollar amount cannot be greater than $" + Money.DF.format(price * .75) + " (75% of item price).");
                            } else {
                                dollarLabel.removeWarning();
                                validFields++;
                            }
                            itemLabel.removeWarning();
                        } else {
                            validFields++;
                            itemLabel.removeWarning();
                        }
                    } else {
                        itemLabel.addWarning("Item not found.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                itemLabel.addWarning("Inventory item ID is required for item level discounts.");
            }
        }

        if (!code.equals("")) {
            try {
                ResultSet rs = State.getConnection().checkDiscountCode(code);
                if (rs.isBeforeFirst() && currentDiscount == null) {
                    codeLabel.addWarning("Discount code already exists.");
                } else {
                    validFields++;
                    codeLabel.removeWarning();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            codeLabel.addWarning("Discount code is required.");
        }

        if (!desc.equals("")) {
            descLabel.removeWarning();
            validFields++;
        } else {
            descLabel.addWarning("Description is required.");
        }

        if (!exp.equals("")) {
            expiration = expDate.getDate();
            if (expiration.isBefore(today) || expiration.isEqual(today)) {
                expLabel.addWarning("Expiration date must be after today.");
            } else {
                validFields++;
                expLabel.removeWarning();
            }
        } else {
            expLabel.addWarning("Expiration date is required.");
        }

        if (!sta.equals("")) {
            start = startDate.getDate();
            if (start.isBefore(today)) {
                startLabel.addWarning("Start date cannot be before today.");
            } else if (!exp.equals("") && (start.isAfter(expiration))) {
                startLabel.addWarning("Start date cannot be after expiration date.");
            } else {
                startLabel.removeWarning();
                validFields++;
            }
        }

        if (validFields == requiredValid) {
            Discount discount = new Discount(code, desc, level, type, expiration);

            if (start != null) {
                discount.setStartDate(start);
            }

            if (type == Discount.PERCENT_TYPE) {
                discount.setPercentage(percent);
            } else {
                discount.setDollarAmount(dollar);
            }

            if (level == Discount.ITEM_LEVEL) {
                discount.setInventoryID(item);
            }

            try {
                PopupDialog success = new PopupDialog(State.getFrame());
                success.setTitle("Success");

                success.setLayout(new GridBagLayout());
                success.setSize(new Dimension(400, 100));
                success.setLocationRelativeTo(null);

                if (currentDiscount == null) {
                    State.getConnection().insertDiscount(discount);
                    clearForm();
                    success.add(new FieldLabel("Discount code created successfully."), new GridBagConstraints());
                    success.setVisible(true);

                } else {
                    discount.setDisabled(disableCheck.isSelected());
                    discount.setDiscountID(currentDiscount.getDiscountID());
                    State.getConnection().updateDiscount(discount);
                    ((Manager) State.getManagerState()).showPanel(Manager.SEARCH_DISCOUNT);
                    success.add(new FieldLabel("Discount code successfully updated."), new GridBagConstraints());
                    success.setVisible(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Clears all fields and hides warnings.
     */
    protected void clearForm() {
        // set to default value (cart level, percent type)
        levelBox.setSelectedIndex(0);
        typeBox.setSelectedIndex(0);

        // show correct labels/fields to reflect discount level and type
        dollar.setVisible(false);
        percent.setVisible(true);
        item.setVisible(false);

        // clear fields
        codeField.setText("");
        dollarField.setText("");
        percentField.setText("");
        itemField.setText("");
        descField.setText("");
        startDate.setText("");
        expDate.setText("");

        // remove warnings
        codeLabel.removeWarning();
        descLabel.removeWarning();
        levelLabel.removeWarning();
        itemLabel.removeWarning();
        typeLabel.removeWarning();
        percentLabel.removeWarning();
        dollarLabel.removeWarning();
        expLabel.removeWarning();
        startLabel.removeWarning();
    }
}
