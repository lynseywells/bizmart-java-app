package component.panels;

import component.labels.RequirementLabel;
import component.labels.WarningLabel;
import component.labels.FieldLabel;
import component.labels.FormTitle;
import component.labels.FieldRequiredLabel;
import component.labels.BizmartLabel;
import component.fields.InputField;
import component.fields.PasswordField;
import component.buttons.ButtonPrimary;
import component.buttons.TogglePasswordButton;
import component.buttons.Link;
import component.Colors;
import component.PopupDialog;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.util.*;
import javax.swing.plaf.metal.MetalCheckBoxIcon;
import util.*;
import statemachine.*;

/**
 * AccountPanel provides a form for the manager state of the Bizmart application
 * to create new accounts or edit existing account information.
 */
public class AccountPanel extends JScrollPane {

    /**
     * Shows helpDialog when pressed.
     */
    private Link helpLink;

    /**
     * A JDialog help menu.
     */
    private PopupDialog helpDialog;

    /**
     * Button that creates a new account and changes the displayed panel in the
     * manager state to OPTIONS (if the form is valid) when pressed.
     */
    private ButtonPrimary registerBttn;

    /**
     * Button that saves changing to an existing account and changes the
     * displayed panel in the manager state to OPTIONS (if the form is valid)
     * when pressed.
     */
    private ButtonPrimary saveBttn;

    /**
     * Button that deletes an existing account permanently, then changes the
     * displayed panel in the manager state to OPTIONS when pressed.
     */
    private ButtonPrimary deleteBttn;

    //---------- CONTACT INFORMATION SECTION ----------//
    /**
     * Label for a required field in the contact information section of the
     * form.
     */
    FieldRequiredLabel firstNameLabel, lastNameLabel, address1Label, cityLabel, stateLabel, zipLabel, emailLabel;

    /**
     * Label for an optional field in the contact information section of the
     * form.
     */
    FieldLabel address2Label, address3Label, phoneLabel;

    /**
     * Field in the contact information section of the form.
     */
    private InputField firstNameField, lastNameField, address1Field, address2Field, address3Field, cityField,
            zipField, phoneField, emailField;

    /**
     * JComboBox containing all 50 states in the contact information section of
     * the form.
     */
    private JComboBox<String> stateComboBox;

    //---------- ACCOUNT SETUP SECTION ----------//
    /**
     * JComboBox to indicate account type in the account setup section of the
     * form.
     */
    private JComboBox accountType;

    /**
     * JCheckBox to enable or disable an account. Only available when editing an
     * existing account. Found in the account setup section of the form.
     */
    private JCheckBox disableCheck;

    /**
     * Label for a field in the account setup section of the form.
     */
    private FieldLabel usernameLabel, passwordLabel, passwordLabel2, accountLabel, disableLabel;

    /**
     * InputField to enter the new accounts username.
     */
    private InputField usernameField;

    /**
     * PasswordField to enter a new password.
     */
    private PasswordField passwordField;

    /**
     * PasswordField to enter the new password again.
     */
    private PasswordField passwordField2;

    /**
     * Button that toggles the visibility of text in passwordField.
     */
    private TogglePasswordButton showPasswordBttn;

    /**
     * Button that toggles the visibility of text in passwordField2.
     */
    private TogglePasswordButton showPasswordBttn2;

    /**
     * Label that indicates when a password requirement is met.
     */
    private RequirementLabel specialReq, lengthReq, charReq, numberReq, matchReq;

    //---------- SECURITY QUESTION SECTION ----------//
    /**
     * Label for a required field in the security question section of the form.
     */
    private FieldRequiredLabel question1, question2, question3;

    /**
     * Field to enter a security question answer.
     */
    private InputField answer1, answer2, answer3;

    /**
     * ArrayList contains all question set IDs from the database. Used to cycle
     * through question sets.
     */
    private ArrayList<Integer> questionSets;

    /**
     * Button that changes the current question set when pressed.
     */
    private ButtonPrimary changeQuestionSetBttn;

    /**
     * The ID of the current question set.
     */
    private int currentQuestionSet;

    /**
     * The ID of a security question in currentQuestionSet.
     */
    private int firstQuestion, secondQuestion, thirdQuestion;

    /**
     * A JLabel that notifies the user when the form isn't complete.
     */
    WarningLabel fieldWarning;

    /**
     * When editing an existing account, the Person who owns the account.
     */
    private Person person;

    /**
     * When editing an existing account, the Users information.
     */
    private User user;

    /**
     * When editing an existing account, the original email.
     * <p>
     * Email must be unique in the database, so if the email doesn't change it
     * the SQL update statement will fail if it tries to update the email. This
     * is used to check if the email was changed when submitting the form. If
     * the email is the same, the email is not updated when changes are saved.
     */
    String originalEmail;

    /**
     * AccountPanel constructor sets up form to create a new account.
     */
    public AccountPanel() {
        //---- INITIALIZE MAIN COMPONENTS -----//
        super();

        // TITLES
        FormTitle accountTitle = new FormTitle("Account Setup", 350, 2);
        FormTitle informationTitle = new FormTitle("Contact Information", 350, 2);
        FormTitle securityTitle = new FormTitle("Security Questions", 350, 2);

        // LINKS
        helpLink = new Link("Help");

        // INFORMATION LABELS
        firstNameLabel = new FieldRequiredLabel("First Name");
        lastNameLabel = new FieldRequiredLabel("Last Name");
        address1Label = new FieldRequiredLabel("Address Line 1");
        address2Label = new FieldLabel("Address Line 2");
        address3Label = new FieldLabel("Address Line 3");
        cityLabel = new FieldRequiredLabel("City");
        stateLabel = new FieldRequiredLabel("State");
        zipLabel = new FieldRequiredLabel("Zip Code");
        phoneLabel = new FieldLabel("Phone Number");
        emailLabel = new FieldRequiredLabel("Email Address");

        // INFORMATION FIELDS
        firstNameField = new InputField();
        lastNameField = new InputField();
        address1Field = new InputField();
        address2Field = new InputField();
        address3Field = new InputField();
        cityField = new InputField();
        zipField = new InputField();
        phoneField = new InputField();
        emailField = new InputField("email@company.com");
        stateComboBox = new JComboBox<String>();
        String[] validStates = {"--", "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI",
            "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS",
            "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR",
            "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"};
        for (String state : validStates) {
            stateComboBox.addItem(state);
        }

        accountType = new JComboBox<String>();
        accountType.addItem("Customer");
        accountType.addItem("Employee");
        accountType.addItem("Manager");

        // ACCOUNT LABELS
        usernameLabel = new FieldLabel("Username");
        passwordLabel = new FieldLabel("Password");
        passwordLabel2 = new FieldLabel("Repeat Password");
        accountLabel = new FieldLabel("Account Type");
        accountLabel.setLabelFont(new Font("Nunito", Font.PLAIN, 20));

        // ACCOUNT FIELDS
        usernameField = new InputField();
        passwordField = new PasswordField();
        passwordField2 = new PasswordField();
        showPasswordBttn = new TogglePasswordButton(passwordField);
        showPasswordBttn2 = new TogglePasswordButton(passwordField2);

        // PASSWORD REQUIREMENTS
        BizmartLabel passwordRequirements = new BizmartLabel("Password Requirements:");
        specialReq = new RequirementLabel("Must contain a special character.");
        lengthReq = new RequirementLabel("Must be between 8 and 20 characters.");
        charReq = new RequirementLabel("Must contain an alphabetic character.");
        numberReq = new RequirementLabel("Must contain a numeric digit.");
        matchReq = new RequirementLabel("Both passwords must match.");

        // BUTTONS
        registerBttn = new ButtonPrimary("Create Account");

        // WARNING LABELS
        fieldWarning = new WarningLabel("Please fill out all required fields.");

        // SECURITY QUESTIONS
        questionSets = new ArrayList<>();
        question1 = new FieldRequiredLabel("Question 1");
        question2 = new FieldRequiredLabel("Question 2");
        question3 = new FieldRequiredLabel("Question 3");
        answer1 = new InputField();
        answer2 = new InputField();
        answer3 = new InputField();

        // QUESTION SET BUTTON
        changeQuestionSetBttn = new ButtonPrimary("Change Question Set");
        changeQuestionSetBttn.setBorderColor(Colors.GREEN);
        changeQuestionSetBttn.setBackground(Colors.MID_GREEN);
        changeQuestionSetBttn.setSelectedBorderColor(Colors.GREEN);
        changeQuestionSetBttn.setSelectedBackground(Colors.MID_GREEN);

        // get question set ID's from the database - initialized to 0 so when
        // nextQuestionSet() is called, QuestionSetID == 1
        currentQuestionSet = 0;
        try {
            ResultSet r = State.getConnection().getUniqueQuestionSetIDs();
            while (r.next()) {
                questionSets.add(Integer.valueOf(r.getString(1)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // get the first question set and add each question to a label
        nextQuestionSet();

        // SETUP HELP DIALOG
        setupHelpDialog();

        //----- GROUP INFORMATION COMPONENTS -----//
        // NAME PANEL
        // group firstNameLabel && firstNameField vertically
        JPanel firstPanel = new JPanel();
        firstPanel.setLayout(new BoxLayout(firstPanel, BoxLayout.Y_AXIS));
        firstPanel.setBackground(null);
        firstNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        firstPanel.add(firstNameLabel);
        firstPanel.add(Box.createVerticalStrut(5));
        firstNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        firstPanel.add(firstNameField);
        firstPanel.add(Box.createVerticalStrut(5));

        // group lastNameLabel && lastNameField vertically
        JPanel lastPanel = new JPanel();
        lastPanel.setLayout(new BoxLayout(lastPanel, BoxLayout.Y_AXIS));
        lastPanel.setBackground(null);
        lastNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        lastPanel.add(lastNameLabel);
        lastPanel.add(Box.createVerticalStrut(5));
        lastNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        lastPanel.add(lastNameField);

        // group firstPanel && lastPanel horizontally 
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        namePanel.setBackground(null);
        namePanel.add(firstPanel);
        namePanel.add(Box.createHorizontalStrut(20));
        namePanel.add(lastPanel);

        // ADDRESS PANELS
        // groups address1Label && address1Field vertically
        JPanel address1Panel = new JPanel();
        address1Panel.setLayout(new BoxLayout(address1Panel, BoxLayout.Y_AXIS));
        address1Panel.setBackground(null);
        address1Label.setAlignmentX(Component.LEFT_ALIGNMENT);
        address1Panel.add(address1Label);
        address1Panel.add(Box.createVerticalStrut(5));
        address1Field.setAlignmentX(Component.LEFT_ALIGNMENT);
        address1Panel.add(address1Field);
        address1Panel.add(Box.createVerticalStrut(5));

        // groups address2Label && address2Field vertically
        JPanel address2Panel = new JPanel();
        address2Panel.setLayout(new BoxLayout(address2Panel, BoxLayout.Y_AXIS));
        address2Panel.setBackground(null);
        address2Label.setAlignmentX(Component.LEFT_ALIGNMENT);
        address2Panel.add(address2Label);
        address2Panel.add(Box.createVerticalStrut(5));
        address2Field.setAlignmentX(Component.LEFT_ALIGNMENT);
        address2Panel.add(address2Field);
        address2Panel.add(Box.createVerticalStrut(5));

        // groups address3Label && address3Field vertically
        JPanel address3Panel = new JPanel();
        address3Panel.setLayout(new BoxLayout(address3Panel, BoxLayout.Y_AXIS));
        address3Panel.setBackground(null);
        address3Label.setAlignmentX(Component.LEFT_ALIGNMENT);
        address3Panel.add(address3Label);
        address3Panel.add(Box.createVerticalStrut(5));
        address3Field.setAlignmentX(Component.LEFT_ALIGNMENT);
        address3Panel.add(address3Field);
        address3Panel.add(Box.createVerticalStrut(5));

        // groups cityLabel && cityField vertically
        JPanel cityPanel = new JPanel();
        cityPanel.setLayout(new BoxLayout(cityPanel, BoxLayout.Y_AXIS));
        cityPanel.setBackground(null);
        cityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cityPanel.add(cityLabel);
        cityPanel.add(Box.createVerticalStrut(5));
        cityField.setAlignmentX(Component.LEFT_ALIGNMENT);
        cityPanel.add(cityField);
        cityPanel.add(Box.createVerticalStrut(5));

        // groups stateLabel && stateComboBox vertically
        JPanel statePanel = new JPanel();
        statePanel.setLayout(new BoxLayout(statePanel, BoxLayout.Y_AXIS));
        statePanel.setBackground(null);
        stateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statePanel.add(stateLabel);
        statePanel.add(Box.createVerticalStrut(5));
        stateComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        statePanel.add(stateComboBox);
        statePanel.add(Box.createVerticalStrut(5));

        // groups zipLabel && zipField vertically
        JPanel zipPanel = new JPanel();
        zipPanel.setLayout(new BoxLayout(zipPanel, BoxLayout.Y_AXIS));
        zipPanel.setBackground(null);
        zipLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        zipPanel.add(zipLabel);
        zipPanel.add(Box.createVerticalStrut(5));
        zipField.setAlignmentX(Component.LEFT_ALIGNMENT);
        zipPanel.add(zipField);
        zipPanel.add(Box.createVerticalStrut(5));

        // groups cityPanel, statePanel, && zipPanel horizontally
        JPanel address4Panel = new JPanel();
        address4Panel.setLayout(new BoxLayout(address4Panel, BoxLayout.X_AXIS));
        address4Panel.setBackground(null);
        cityPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        address4Panel.add(cityPanel);
        address4Panel.add(Box.createHorizontalStrut(20));
        statePanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        address4Panel.add(statePanel);
        address4Panel.add(Box.createHorizontalStrut(20));
        zipPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        address4Panel.add(zipPanel);

        // PHONE PANEL
        // groups phoneLabel && phoneField vertically
        JPanel phonePanel = new JPanel();
        phonePanel.setLayout(new BoxLayout(phonePanel, BoxLayout.Y_AXIS));
        phonePanel.setBackground(null);
        phoneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        phonePanel.add(phoneLabel);
        phonePanel.add(Box.createVerticalStrut(5));
        phoneField.setAlignmentX(Component.LEFT_ALIGNMENT);
        phonePanel.add(phoneField);
        phonePanel.add(Box.createVerticalStrut(5));

        // EMAIL PANEL
        // groups emailLabel && emailField vertically
        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.setBackground(null);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailPanel.add(emailLabel);
        emailPanel.add(Box.createVerticalStrut(5));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailPanel.add(emailField);
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        //----- ADD PANELS TO INFORMATION PANEL -----//
        JPanel informationPanel = new JPanel();
        informationPanel.setLayout(new GridBagLayout());
        informationPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        // (0, 0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        informationPanel.add(informationTitle, gbc);
        // (0, 1)
        gbc.gridy = 1;
        informationPanel.add(namePanel, gbc);
        // (0, 2)
        gbc.gridy = 2;
        informationPanel.add(address1Panel, gbc);
        // (0, 3)
        gbc.gridy = 3;
        informationPanel.add(address2Panel, gbc);
        // (0, 4)
        gbc.gridy = 4;
        informationPanel.add(address3Panel, gbc);
        // (0, 5)
        gbc.gridy = 5;
        informationPanel.add(address4Panel, gbc);
        // (0, 6)
        gbc.gridy = 6;
        informationPanel.add(emailPanel, gbc);
        // (0, 7)
        gbc.gridy = 7;
        informationPanel.add(phonePanel, gbc);

        //----- GROUP ACCOUNT COMPONENTS -----//
        // USERNAME PANEL
        // groups usernameLabel && usernameField vertically
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.Y_AXIS));
        usernamePanel.setBackground(null);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(Box.createVerticalStrut(5));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernamePanel.add(usernameField);

        // ACCOUNT LEVEL PANEL
        // groups accountLaben && accountType vertically
        JPanel levelPanel = new JPanel();
        levelPanel.setLayout(new BoxLayout(levelPanel, BoxLayout.X_AXIS));
        levelPanel.setBackground(null);
        levelPanel.add(Box.createHorizontalStrut(12));
        levelPanel.add(accountLabel);
        levelPanel.add(Box.createHorizontalStrut(5));
        levelPanel.add(accountType);

        // PASSWORD PANEL 1
        // groups passwordLabel, showPasswordBttn, && passwordField 
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new GridBagLayout());
        passwordPanel.setBackground(null);
        gbc = new GridBagConstraints();
        // (0, 0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 2, 0);
        passwordPanel.add(passwordLabel, gbc);
        // (1, 0)
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        passwordPanel.add(showPasswordBttn, gbc);
        // (0, 1)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridwidth = 2;
        passwordPanel.add(passwordField, gbc);

        // PASSWORD PANEL 2
        // groups passwordLabel2, showPasswordBttn2, && passwordField2 
        JPanel passwordPanel2 = new JPanel();
        passwordPanel2.setLayout(new GridBagLayout());
        passwordPanel2.setBackground(null);
        gbc = new GridBagConstraints();
        // (0, 0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 2, 0);
        passwordPanel2.add(passwordLabel2, gbc);
        // (1, 0)
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        passwordPanel2.add(showPasswordBttn2, gbc);
        // (0, 1)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridwidth = 2;
        passwordPanel2.add(passwordField2, gbc);

        // PASSWORD REQUIREMENTS PANEL
        // groups all password requirement labels vertically
        JPanel requirementsPanel = new JPanel();
        requirementsPanel.setBackground(null);
        requirementsPanel.setLayout(new BoxLayout(requirementsPanel, BoxLayout.Y_AXIS));
        passwordRequirements.setAlignmentX(Component.LEFT_ALIGNMENT);
        requirementsPanel.add(passwordRequirements);
        charReq.setAlignmentX(Component.LEFT_ALIGNMENT);
        requirementsPanel.add(Box.createVerticalStrut(5));
        requirementsPanel.add(charReq);
        numberReq.setAlignmentX(Component.LEFT_ALIGNMENT);
        requirementsPanel.add(numberReq);
        specialReq.setAlignmentX(Component.LEFT_ALIGNMENT);
        requirementsPanel.add(specialReq);
        lengthReq.setAlignmentX(Component.LEFT_ALIGNMENT);
        requirementsPanel.add(lengthReq);
        matchReq.setAlignmentX(Component.LEFT_ALIGNMENT);
        requirementsPanel.add(matchReq);

        //----- GROUP SECURITY COMPONENTS -----//
        // QUESTION && ANSWER PANEL
        // groups question labels && answer fields
        JPanel securityPanel = new JPanel();
        securityPanel.setBackground(null);
        securityPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        // (0, 0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        securityPanel.add(question1, gbc);
        // (0, 1)
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        securityPanel.add(answer1, gbc);
        // (0, 2)
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 5, 0);
        securityPanel.add(question2, gbc);
        // (0, 3)
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 15, 0);
        securityPanel.add(answer2, gbc);
        // (0, 4)
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 5, 0);
        securityPanel.add(question3, gbc);
        // (0, 5)
        gbc.gridy = 5;
        securityPanel.add(answer3, gbc);

        //----- ADD PANELS TO THE ACCOUNT PANEL -----//
        JPanel account = new JPanel();
        account.setLayout(new GridBagLayout());
        account.setBackground(Color.WHITE);
        gbc = new GridBagConstraints();
        // (0, 0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 15, 0);
        account.add(accountTitle, gbc);
        // (0, 1)
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridy = 1;
        account.add(usernamePanel, gbc);
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        account.add(levelPanel, gbc);
        // (0, 2)
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        account.add(passwordPanel, gbc);
        // (0, 3)
        gbc.gridy = 4;
        account.add(passwordPanel2, gbc);
        // (0, 4)
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 0, 0, 0);
        account.add(requirementsPanel, gbc);
        // (0, 5)
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 0, 15, 0);
        account.add(securityTitle, gbc);
        // (0, 6)
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 5, 0);
        account.add(securityPanel, gbc);
        // (0, 7)
        gbc.gridy = 8;
        account.add(changeQuestionSetBttn, gbc);
        // (0, 8)
        gbc.gridy = 9;
        gbc.insets = new Insets(20, 0, 5, 0);
        account.add(registerBttn, gbc);

        //----- SIZE COMPONENTS -----//
        // ACCOUNT PANEL COMPONENTS
        accountTitle.setUnderlineWidth(460);
        usernameField.setWidth(440);
        passwordField.setWidth(440);
        passwordField2.setWidth(440);
        registerBttn.setPreferredSize(new Dimension(350, 75));

        securityTitle.setUnderlineWidth(460);
        changeQuestionSetBttn.setPreferredSize(new Dimension(300, 55));
        answer1.setWidth(440);
        answer2.setWidth(440);
        answer3.setWidth(440);

        // INFORMATION PANEL COMPONENTS
        informationTitle.setUnderlineWidth(460);
        firstNameField.setWidth(210);
        lastNameField.setWidth(210);
        address1Field.setWidth(440);
        address2Field.setWidth(440);
        address3Field.setWidth(440);
        emailField.setWidth(440);
        phoneField.setWidth(440);
        zipField.setWidth(125);
        cityField.setWidth(190);
        stateComboBox.setFont(new Font("Nunito", Font.PLAIN, 18));
        stateComboBox.setPreferredSize(new Dimension(85, (int) stateComboBox.getPreferredSize().getHeight()));
        accountType.setFont(new Font("Nunito", Font.PLAIN, 18));

        //----- ADD COMPONENTS TO REGISTER PANEL -----//
        // add all jpanels to one jpanel
        JPanel container = new JPanel();
        container.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        container.setBackground(Color.WHITE);
        // (0, 0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(50, 0, 30, 0);
        container.add(helpLink, gbc);
        // (0, 1)
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        container.add(informationPanel, gbc);
        // (0, 2)
        gbc.gridy = 2;
        container.add(account, gbc);
        // (0, 3)
        gbc.gridy = 3;
        container.add(fieldWarning, gbc);

        // add the jpanel to a scrollpane
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getVerticalScrollBar().setUnitIncrement(16);
        setBorder(null);
        getViewport().add(container);

        addEventListeners();
    }

    /**
     * AccountPanel constructor sets up form to edit an existing account.
     *
     * @param u account information
     * @param p account holder contact information
     */
    public AccountPanel(User u, Person p) {
        //----- INITIALIZE MAIN COMPONENTS -----//
        super();
        person = p;
        user = u;

        // TITLES
        FormTitle informationTitle = new FormTitle("Update Information", 350, 2);

        // LINKS
        helpLink = new Link("Help");

        // INFORMATION LABELS
        firstNameLabel = new FieldRequiredLabel("First Name");
        lastNameLabel = new FieldRequiredLabel("Last Name");
        address1Label = new FieldRequiredLabel("Address Line 1");
        address2Label = new FieldLabel("Address Line 2");
        address3Label = new FieldLabel("Address Line 3");
        cityLabel = new FieldRequiredLabel("City");
        stateLabel = new FieldRequiredLabel("State");
        zipLabel = new FieldRequiredLabel("Zip Code");
        phoneLabel = new FieldLabel("Phone Number");
        emailLabel = new FieldRequiredLabel("Email Address");
        usernameLabel = new FieldLabel("Username:  " + user.getUsername());
        usernameLabel.setLabelFont(new Font("Nunito", Font.PLAIN, 24));

        // INFORMATION FIELDS
        firstNameField = new InputField();
        lastNameField = new InputField();
        address1Field = new InputField();
        address2Field = new InputField();
        address3Field = new InputField();
        cityField = new InputField();
        zipField = new InputField();
        phoneField = new InputField();
        emailField = new InputField();
        stateComboBox = new JComboBox<String>();
        String[] validStates = {"--", "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI",
            "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS",
            "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR",
            "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"};
        for (String state : validStates) {
            stateComboBox.addItem(state);
        }
        accountType = new JComboBox<String>();
        accountType.addItem("Customer");
        accountType.addItem("Employee");
        accountType.addItem("Manager");

        accountLabel = new FieldLabel("Account Type");
        accountLabel.setLabelFont(new Font("Nunito", Font.PLAIN, 20));

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

        // ADD TEXT TO FIELDS
        firstNameField.setText(person.getFirstName());
        lastNameField.setText(person.getLastName());
        address1Field.setText(person.getAddressLine1());
        if (person.getAddressLine2() != null) {
            address2Field.setText(person.getAddressLine2());
        }
        if (person.getAddressLine3() != null) {
            address3Field.setText(person.getAddressLine3());
        }
        cityField.setText(person.getCity());
        zipField.setText(person.getZip());
        stateComboBox.setSelectedItem(person.getState());
        emailField.setText(person.getEmail());
        originalEmail = person.getEmail();
        if (person.getPhone1() != null) {
            phoneField.setText(person.getPhone1());
        }

        if (user.getPosition() == User.CUSTOMER) {
            accountType.setSelectedIndex(0);
        } else if (user.getPosition() == User.EMPLOYEE) {
            accountType.setSelectedIndex(1);
        } else {
            accountType.setSelectedIndex(2);
        }

        if (user.isDisabled()) {
            disableCheck.setSelected(true);
            disableCheck.setText("Yes");
        } else {
            disableCheck.setSelected(false);
            disableCheck.setText("No");
        }

        // BUTTONS
        saveBttn = new ButtonPrimary("Save Changes");
        deleteBttn = new ButtonPrimary("Delete Account");

        // WARNING LABELS
        fieldWarning = new WarningLabel("Please fill out all required fields.");

        // SETUP HELP DIALOG 
        setupHelpDialog();

        //----- GROUP COMPONENTS -----//
        // ACCOUNT DISABLED PANEL
        // groups disableLabel && disableCheck horizontally
        JPanel disablePanel = new JPanel();
        disablePanel.setLayout(new BoxLayout(disablePanel, BoxLayout.X_AXIS));
        disablePanel.setBackground(null);
        disablePanel.add(disableLabel);
        disablePanel.add(Box.createHorizontalStrut(10));
        disablePanel.add(disableCheck);

        // NAME PANEL
        // group firstNameLabel && firstNameField vertically
        JPanel firstPanel = new JPanel();
        firstPanel.setLayout(new BoxLayout(firstPanel, BoxLayout.Y_AXIS));
        firstPanel.setBackground(null);
        firstNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        firstPanel.add(firstNameLabel);
        firstPanel.add(Box.createVerticalStrut(5));
        firstNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        firstPanel.add(firstNameField);
        firstPanel.add(Box.createVerticalStrut(5));

        // group lastNameLabel && lastNameField vertically
        JPanel lastPanel = new JPanel();
        lastPanel.setLayout(new BoxLayout(lastPanel, BoxLayout.Y_AXIS));
        lastPanel.setBackground(null);
        lastNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        lastPanel.add(lastNameLabel);
        lastPanel.add(Box.createVerticalStrut(5));
        lastNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        lastPanel.add(lastNameField);

        // group firstPanel && lastPanel horizontally
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        namePanel.setBackground(null);
        namePanel.add(firstPanel);
        namePanel.add(Box.createHorizontalStrut(20));
        namePanel.add(lastPanel);

        // ADDRESS PANELS
        // groups address labels && address fields vertically
        JPanel addressPanel = new JPanel();
        addressPanel.setLayout(new BoxLayout(addressPanel, BoxLayout.Y_AXIS));
        addressPanel.setBackground(null);
        address1Label.setAlignmentX(Component.LEFT_ALIGNMENT);
        addressPanel.add(address1Label);
        addressPanel.add(Box.createVerticalStrut(5));
        address1Field.setAlignmentX(Component.LEFT_ALIGNMENT);
        addressPanel.add(address1Field);
        addressPanel.add(Box.createVerticalStrut(20));
        address2Label.setAlignmentX(Component.LEFT_ALIGNMENT);
        addressPanel.add(address2Label);
        addressPanel.add(Box.createVerticalStrut(5));
        address2Field.setAlignmentX(Component.LEFT_ALIGNMENT);
        addressPanel.add(address2Field);
        addressPanel.add(Box.createVerticalStrut(20));
        address3Label.setAlignmentX(Component.LEFT_ALIGNMENT);
        addressPanel.add(address3Label);
        addressPanel.add(Box.createVerticalStrut(5));
        address3Field.setAlignmentX(Component.LEFT_ALIGNMENT);
        addressPanel.add(address3Field);
        addressPanel.add(Box.createVerticalStrut(5));

        // groups cityLabel && cityField vertically
        JPanel cityPanel = new JPanel();
        cityPanel.setLayout(new BoxLayout(cityPanel, BoxLayout.Y_AXIS));
        cityPanel.setBackground(null);
        cityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cityPanel.add(cityLabel);
        cityPanel.add(Box.createVerticalStrut(5));
        cityField.setAlignmentX(Component.LEFT_ALIGNMENT);
        cityPanel.add(cityField);
        cityPanel.add(Box.createVerticalStrut(5));

        // groups stateLabel && stateComboBox vertically
        JPanel statePanel = new JPanel();
        statePanel.setLayout(new BoxLayout(statePanel, BoxLayout.Y_AXIS));
        statePanel.setBackground(null);
        stateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statePanel.add(stateLabel);
        statePanel.add(Box.createVerticalStrut(5));
        stateComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        statePanel.add(stateComboBox);
        statePanel.add(Box.createVerticalStrut(5));

        // groups zipLabel && zipField vertically
        JPanel zipPanel = new JPanel();
        zipPanel.setLayout(new BoxLayout(zipPanel, BoxLayout.Y_AXIS));
        zipPanel.setBackground(null);
        zipLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        zipPanel.add(zipLabel);
        zipPanel.add(Box.createVerticalStrut(5));
        zipField.setAlignmentX(Component.LEFT_ALIGNMENT);
        zipPanel.add(zipField);
        zipPanel.add(Box.createVerticalStrut(5));

        // groups cityPanel, statePanel, && zipPanel horizontally
        JPanel address4Panel = new JPanel();
        address4Panel.setLayout(new BoxLayout(address4Panel, BoxLayout.X_AXIS));
        address4Panel.setBackground(null);
        cityPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        address4Panel.add(cityPanel);
        address4Panel.add(Box.createHorizontalStrut(20));
        statePanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        address4Panel.add(statePanel);
        address4Panel.add(Box.createHorizontalStrut(20));
        zipPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        address4Panel.add(zipPanel);

        // PHONE NUMBER
        // groups phoneLabel && phoneField vertically
        JPanel phonePanel = new JPanel();
        phonePanel.setLayout(new BoxLayout(phonePanel, BoxLayout.Y_AXIS));
        phonePanel.setBackground(null);
        phoneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        phonePanel.add(phoneLabel);
        phonePanel.add(Box.createVerticalStrut(5));
        phoneField.setAlignmentX(Component.LEFT_ALIGNMENT);
        phonePanel.add(phoneField);
        phonePanel.add(Box.createVerticalStrut(5));

        // EMAIL ADDRESS
        // groups emailLabel && emailField vertially
        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.setBackground(null);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailPanel.add(emailLabel);
        emailPanel.add(Box.createVerticalStrut(5));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailPanel.add(emailField);
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ACCOUNT LEVEL PANEL
        // groups accountLabel && accountType vertically
        JPanel levelPanel = new JPanel();
        levelPanel.setLayout(new BoxLayout(levelPanel, BoxLayout.X_AXIS));
        levelPanel.setBackground(null);
        levelPanel.add(Box.createHorizontalStrut(12));
        levelPanel.add(accountLabel);
        levelPanel.add(Box.createHorizontalStrut(10));
        levelPanel.add(accountType);

        //----- ADD PANELS TO INFORMATION PANEL -----//
        JPanel informationPanel = new JPanel();
        informationPanel.setLayout(new GridBagLayout());
        informationPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.gridwidth = 2;
        informationPanel.add(informationTitle, gbc);
        gbc.gridy += 1;
        informationPanel.add(usernameLabel, gbc);
        gbc.gridy += 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        informationPanel.add(levelPanel, gbc);
        gbc.gridx = 1;
        informationPanel.add(disablePanel, gbc);
        gbc.gridx = 0;
        gbc.gridy += 1;
        gbc.gridwidth = 2;
        informationPanel.add(namePanel, gbc);
        gbc.gridy += 1;
        informationPanel.add(addressPanel, gbc);
        gbc.gridy += 1;
        informationPanel.add(address4Panel, gbc);
        gbc.gridy += 1;
        informationPanel.add(emailPanel, gbc);
        gbc.gridy += 1;
        informationPanel.add(phonePanel, gbc);
        gbc.gridy += 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        informationPanel.add(saveBttn, gbc);
        gbc.gridy += 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        informationPanel.add(deleteBttn, gbc);

        //----- SIZE COMPONENTS -----//
        // INFORMATION PANEL COMPONENTS
        informationTitle.setUnderlineWidth(460);
        firstNameField.setWidth(210);
        lastNameField.setWidth(210);
        address1Field.setWidth(440);
        address2Field.setWidth(440);
        address3Field.setWidth(440);
        emailField.setWidth(440);
        phoneField.setWidth(440);
        zipField.setWidth(125);
        cityField.setWidth(190);
        stateComboBox.setFont(new Font("Nunito", Font.PLAIN, 18));
        stateComboBox.setPreferredSize(new Dimension(85, (int) stateComboBox.getPreferredSize().getHeight()));
        accountType.setFont(new Font("Nunito", Font.PLAIN, 18));
        deleteBttn.setPreferredSize(new Dimension(250, 64));
        saveBttn.setPreferredSize(new Dimension(250, 64));

        //----- ADD COMPONENTS TO REGISTER PANEL -----//
        // add all jpanels to one jpanel
        JPanel container = new JPanel();
        container.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        container.setBackground(Color.WHITE);
        // (0, 0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(50, 0, 30, 0);
        container.add(helpLink, gbc);
        // (0, 1)
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        container.add(informationPanel, gbc);
        // (0, 2)
        gbc.gridy = 2;
        container.add(fieldWarning, gbc);

        // add the jpanel to a scrollpane
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getVerticalScrollBar().setUnitIncrement(16);
        setBorder(null);
        getViewport().add(container);

        addEventListeners();
    }

    //-------------------------------------------//
    //---------- ACCOUNT PANEL METHODS ----------//
    /**
     * Adds event listeners to components in this AccountPanel.
     */
    protected void addEventListeners() {
        //----- LISTENERS FOR BOTH FORMS -----//
        // FIRST NAME
        firstNameField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // prevent the user from typing non-alphabetic characters or over 50 characters
                if (!Character.isAlphabetic(e.getKeyChar()) && e.getKeyChar() != KeyEvent.VK_SPACE
                        || firstNameField.getText().length() >= 50) {
                    e.consume();
                }
            }
        });

        // LAST NAME
        lastNameField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // prevent the user from typing non-alphabetic characters or over 50 characters
                if (!Character.isAlphabetic(e.getKeyChar()) && e.getKeyChar() != KeyEvent.VK_SPACE && e.getKeyChar() != '-'
                        || lastNameField.getText().length() >= 50) {
                    e.consume();
                }
            }
        });

        // ADDRESS 1
        address1Field.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // prevent the user from typing over 100 characters, or non-alphanumeric
                // characters other than "SPACE"
                if (!Character.isAlphabetic(e.getKeyChar()) && !Character.isDigit(e.getKeyChar())
                        && e.getKeyChar() != KeyEvent.VK_SPACE
                        || address1Field.getText().length() >= 100) {
                    e.consume();
                }
            }
        });

        // ADDRESS 2
        address2Field.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // prevent the user from typing over 100 characters, or non-alphanumeric
                // characters other than "SPACE"
                if (!Character.isAlphabetic(e.getKeyChar()) && !Character.isDigit(e.getKeyChar())
                        && e.getKeyChar() != KeyEvent.VK_SPACE
                        || address2Field.getText().length() >= 100) {
                    e.consume();
                }
            }
        });

        // ADDRESS 2
        address3Field.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // prevent the user from typing over 100 characters, or non-alphanumeric
                // characters other than "SPACE"
                if (!Character.isAlphabetic(e.getKeyChar()) && !Character.isDigit(e.getKeyChar())
                        && e.getKeyChar() != KeyEvent.VK_SPACE
                        || address3Field.getText().length() >= 100) {
                    e.consume();
                }
            }
        });

        // CITY
        cityField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // prevent the user from typing non-alphabetic characters other than "SPACE", or
                // more than 50 characters
                if (!Character.isAlphabetic(e.getKeyChar()) && e.getKeyChar() != KeyEvent.VK_SPACE
                        || cityField.getText().length() >= 50) {
                    e.consume();
                }
            }
        });

        // ZIP CODE
        zipField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // prevent the user from typing non-digit characters (except for '-') or more
                // than 10 characters
                if (!Character.isDigit(e.getKeyChar()) && e.getKeyChar() != '-' || zipField.getText().length() >= 10) {
                    e.consume();
                }
            }
        });

        // EMAIL ADDRESS
        emailField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // prevent the user from typing over 100 characters or whitespace characters
                if (emailField.getText().length() >= 100 || Character.isWhitespace(e.getKeyChar())) {
                    e.consume();
                }
            }
        });

        // PHONE NUMBER
        phoneField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // prevent the user from typing non-digit characters or more than 15 characters
                if (!Character.isDigit(e.getKeyChar()) || phoneField.getText().length() >= 15) {
                    e.consume();
                }
            }
        });

        // HELP LINK MOUSE LISTENER
        helpLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                helpDialog.setVisible(true);
            }
        });

        // HELP LINK KEY BINDING
        Action helpPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                helpDialog.setVisible(true);
            }
        };
        helpLink.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        helpLink.getActionMap().put("pressed", helpPress);

        // ZIP CODE
        zipField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                String zip = zipField.getText();
                if (!zip.equals("")) {
                    if (Validation.isValidZip(zip)) {
                        zipLabel.removeWarning();
                    } else {
                        zipLabel.addWarning("Invalid zip code entered. Please use the format '#####' or '#####-####'.");
                    }
                } else {
                    zipLabel.removeWarning();
                }
            }
        });

        // EMAIL ADDRESS
        emailField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                String email = emailField.getText();
                if (!email.equals("") && !emailField.isPlaceholderActive()) {
                    if (Validation.isValidEmail(email)) {
                        try {
                            // get all emails from the database
                            ResultSet rs = State.getConnection().getAllEmails();
                            boolean found = false;
                            while (rs.next()) {
                                // check each email to see if it matches the entered username
                                if (rs.getString(1).equals(email)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                emailLabel.removeWarning();
                            } else {
                                emailLabel.addWarning("Email address already in use.");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        emailLabel.addWarning("Invalid email address entered.");
                    }
                } else {
                    emailLabel.removeWarning();
                }
            }
        });

        // PHONE NUMBER
        phoneField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                String phone = phoneField.getText();
                if (!phone.equals("")) {
                    if (!Validation.isValidPhone(phone)) {
                        phoneLabel.addWarning("Invalid phone number entered. Enter only numbers.");
                    } else {
                        phoneLabel.removeWarning();
                    }
                } else {
                    phoneLabel.removeWarning();
                }
            }
        });

        //----- NEW ACCOUNT FORM LISTENERS -----//
        if (user == null) {
            // USERNAME
            usernameField.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }

                @Override
                public void keyTyped(KeyEvent e) {
                    // stop user from typing more than 20 characters or non-alphanumeric characters
                    if (usernameField.getText().length() >= 20
                            || !Character.isAlphabetic(e.getKeyChar()) && !Character.isDigit(e.getKeyChar())) {
                        e.consume();
                    }
                }
            });

            // PASSWORD 1
            passwordField.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    // if passwordField2 has text, check if the two fields match
                    if (passwordField2.getPassword().length > 0) {
                        if (Arrays.equals(passwordField.getPassword(), passwordField2.getPassword())
                                && passwordField.getPassword().length > 0 && passwordField2.getPassword().length > 0) {
                            matchReq.requirementMet(true);
                        } else {
                            matchReq.requirementMet(false);
                        }
                    }
                }

                @Override
                public void keyTyped(KeyEvent e) {
                    // check if the character meets any of the password requirements
                    if (Character.isAlphabetic(e.getKeyChar())) {
                        charReq.requirementMet(true);
                    } else if (Character.isDigit(e.getKeyChar())) {
                        numberReq.requirementMet(true);
                    } else if (Validation.isSpecialChar(e.getKeyChar())) {
                        specialReq.requirementMet(true);
                    } else if (Character.isWhitespace(e.getKeyChar())) {
                        // consumes whitespace characters like "space" so they can't be typed
                        e.consume();
                    } else {
                        // when backspace is pressed, all requirements must be checked again
                        char[] password = passwordField.getPassword();
                        if (!Validation.hasAlphaChar(password)) {
                            charReq.requirementMet(false);
                        }

                        if (!Validation.hasDigitChar(password)) {
                            numberReq.requirementMet(false);
                        }

                        if (!Validation.hasSpecialChar(password)) {
                            specialReq.requirementMet(false);
                        }

                        if (password.length + 1 <= 8 && password.length + 1 <= 20) {
                            lengthReq.requirementMet(false);
                        }
                    }

                    if (passwordField.getPassword().length + 1 >= 8 && passwordField.getPassword().length + 1 <= 20) {
                        lengthReq.requirementMet(true);
                    } else {
                        lengthReq.requirementMet(false);
                    }
                }
            });

            // PASSWORD 2
            passwordField2.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    // check if both password fields match
                    if (Arrays.equals(passwordField.getPassword(), passwordField2.getPassword())
                            && passwordField.getPassword().length > 0 && passwordField2.getPassword().length > 0) {
                        matchReq.requirementMet(true);
                    } else {
                        matchReq.requirementMet(false);
                    }
                }

                @Override
                public void keyTyped(KeyEvent e) {
                    if (Character.isWhitespace(e.getKeyChar())) {
                        // consumes whitespace characters like "space" so they can't be typed
                        e.consume();
                    }
                }
            });

            // ANSWER 1
            answer1.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyTyped(KeyEvent e) {
                    if (!Character.isAlphabetic(e.getKeyChar()) && e.getKeyChar() != KeyEvent.VK_SPACE
                            || answer1.getText().length() >= 50) {
                        e.consume();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }
            });

            // ANSWER 2
            answer2.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyTyped(KeyEvent e) {
                    if (!Character.isAlphabetic(e.getKeyChar()) && e.getKeyChar() != KeyEvent.VK_SPACE
                            || answer2.getText().length() >= 50) {
                        e.consume();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }
            });

            // ANSWER 3
            answer3.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyTyped(KeyEvent e) {
                    if (!Character.isAlphabetic(e.getKeyChar()) && e.getKeyChar() != KeyEvent.VK_SPACE
                            || answer3.getText().length() >= 50) {
                        e.consume();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }
            });
            // CHANGE QUESTION BUTTON MOUSE LISTENER
            changeQuestionSetBttn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    changeQuestionSetBttn.mouseReleased();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    changeQuestionSetBttn.mousePressed();
                    nextQuestionSet();
                }
            });

            // CHANGE QUESTION SET KEY BINDING
            Action changePress = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    nextQuestionSet();
                }
            };
            changeQuestionSetBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
            changeQuestionSetBttn.getActionMap().put("pressed", changePress);

            // REGISTER BUTTON KEY BINDING
            Action registerPress = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    submitForm();
                }
            };
            Action submit = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    submitForm();
                }
            };
            registerBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
            registerBttn.getActionMap().put("pressed", registerPress);
            registerBttn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "submit");
            registerBttn.getActionMap().put("submit", submit);

            // REGISTER BUTTON MOUSE LISTENER
            registerBttn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    registerBttn.mouseReleased();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    registerBttn.mousePressed();
                    submitForm();
                }
            });

            // PASSWORD 1
            passwordField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                }

                @Override
                public void focusLost(FocusEvent e) {
                    char[] p = passwordField.getPassword();
                    char[] p2 = passwordField2.getPassword();

                    if (Arrays.equals(p, p2) && p.length > 0 && p2.length > 0) {
                        // if the password is still not valid, invalid characters were entered.
                        if (!Validation.isValidPassword(p)) {
                            passwordLabel.addWarning("Password contains invalid special characters.");
                        } else {
                            passwordLabel.removeWarning();
                        }
                    } else {
                        passwordLabel.removeWarning();
                    }
                }
            });

            // PASSWORD 2
            passwordField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                }

                @Override
                public void focusLost(FocusEvent e) {
                    char[] p = passwordField.getPassword();
                    char[] p2 = passwordField2.getPassword();

                    if (Arrays.equals(p, p2) && p.length > 0 && p2.length > 0) {
                        // if the password is still not valid, invalid characters were entered.
                        if (!Validation.isValidPassword(p)) {
                            passwordLabel2.addWarning("Password contains invalid special characters.");
                        } else {
                            passwordLabel2.removeWarning();
                        }
                    } else {
                        passwordLabel2.removeWarning();
                    }
                }
            });

            // USERNAME
            usernameField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                }

                @Override
                public void focusLost(FocusEvent e) {
                    String username = usernameField.getText();
                    if (!username.equals("")) {
                        char[] array = username.toCharArray();
                        if (username.length() < 8) {
                            usernameLabel.addWarning("Username must be longer than 8 characters.");
                        } else if (Character.isDigit(array[0])) {
                            usernameLabel.addWarning("Username cannot start with a number.");
                        } else if (Validation.hasSpecialChar(array)) {
                            usernameLabel.addWarning("Username cannot contain special characters.");
                        }
                        if (Validation.isValidUsername(username)) {
                            try {
                                // get all usernames from the database
                                ResultSet rs = State.getConnection().getAllUsernames();
                                boolean found = false;
                                while (rs.next()) {
                                    // check each username to see if it matches the entered username
                                    if (rs.getString(1).equalsIgnoreCase(username)) {
                                        found = true;
                                        break;
                                    }
                                }
                                // if the username was not found in the database
                                if (!found) {
                                    usernameLabel.removeWarning();
                                } else {
                                    usernameLabel.addWarning("Username already exists.");
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    } else {
                        usernameLabel.removeWarning();
                    }
                }
            });
        } else {
            //----- EDIT ACCOUNT FORM LISTENERS -----//
            // DISABLE CHECKBOX
            disableCheck.addItemListener((ItemEvent e) -> {
                if (disableCheck.isSelected()) {
                    disableCheck.setText("Yes");
                } else {
                    disableCheck.setText("No");
                }
            });
            // SAVE BUTTON KEY BINDING
            Action savePress = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    submitForm();
                }
            };
            Action submit = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    submitForm();
                }
            };
            saveBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
            saveBttn.getActionMap().put("pressed", savePress);
            saveBttn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "submit");
            saveBttn.getActionMap().put("submit", submit);

            // SAVE BUTTON MOUSE LISTENER
            saveBttn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    saveBttn.mouseReleased();
                    submitForm();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    saveBttn.mousePressed();
                }
            });

            // SAVE BUTTON KEY BINDING
            Action delPress = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        State.getConnection().deleteUser(user);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            deleteBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
            deleteBttn.getActionMap().put("pressed", delPress);

            // SAVE BUTTON MOUSE LISTENER
            deleteBttn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    deleteBttn.mouseReleased();
                    try {
                        State.getConnection().deleteUser(user);

                        // show success dialog
                        PopupDialog successDialog = new PopupDialog(State.getFrame());
                        successDialog.setTitle("Account Deleted");
                        successDialog.setLayout(new GridBagLayout());
                        successDialog.setSize(new Dimension(450, 100));
                        successDialog.setLocationRelativeTo(null);
                        successDialog.add(new FieldLabel("Account has been deleted."), new GridBagConstraints());
                        successDialog.setVisible(true);

                        ((Manager) State.getManagerState()).showPanel(Manager.OPTIONS);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    deleteBttn.mousePressed();
                }
            });

        }
    }

    /**
     * Resets this AccountPanel by clearing all fields, resetting password
     * requirement labels, and hiding warnings.
     */
    private void resetForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        address1Field.setText("");
        address2Field.setText("");
        address3Field.setText("");
        cityField.setText("");
        stateComboBox.setSelectedItem("--");
        zipField.setText("");
        emailField.setText("");
        phoneField.setText("");
        if (user == null) {
            usernameField.setText("");
            passwordField.setText("");
            passwordField2.setText("");
            showPasswordBttn.changeState(TogglePasswordButton.IconState.HIDE);
            showPasswordBttn2.changeState(TogglePasswordButton.IconState.HIDE);
            answer1.setText("");
            answer2.setText("");
            answer3.setText("");
            // set all requirements to not met
            charReq.requirementMet(false);
            lengthReq.requirementMet(false);
            matchReq.requirementMet(false);
            numberReq.requirementMet(false);
            specialReq.requirementMet(false);
            // hide warnings
            usernameLabel.removeWarning();
            passwordLabel.removeWarning();
            passwordLabel2.removeWarning();
        }
        phoneLabel.removeWarning();
        fieldWarning.setVisible(false);
        emailLabel.removeWarning();
        zipLabel.removeWarning();
    }

    /**
     * Validates all fields in this AccountPanel to determine if the new account
     * can be created.
     *
     * @return True if the form is valid, false otherwise
     */
    private boolean validateFields() {
        // get the text from every field
        String first = firstNameField.getText();
        String last = lastNameField.getText();
        String add = address1Field.getText();
        String add2 = address2Field.getText();
        String add3 = address3Field.getText();
        String city = cityField.getText();
        String zip = zipField.getText();
        String state = stateComboBox.getSelectedItem().toString();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String username = usernameField.getText();
        char[] p = passwordField.getPassword();
        char[] p2 = passwordField2.getPassword();
        String a1 = answer1.getText();
        String a2 = answer2.getText();
        String a3 = answer3.getText();

        // int to track the fields with valid data
        int validFields = 0;
        // the total fields that must be valid (both password fields are counted as one)
        int requiredValid = 12;

        // if the optional fields are filled, add them to the total required fields
        if (!add2.equals("") && !phone.equals("") && !add3.equals("")) {
            requiredValid += 3;
        } else if (!add2.equals("") && !phone.equals("") || !add2.equals("") && !add3.equals("")
                || !phone.equals("") && !add3.equals("")) {
            requiredValid += 2;
        } else if (!add2.equals("") || !phone.equals("") || !add3.equals("")) {
            requiredValid++;
        }

        // CHECK EACH FIELD FOR VALID DATA - SHOW WARNINGS IF INVALID
        if (!first.equals("")) {
            validFields++;
        }
        if (!last.equals("")) {
            validFields++;
        }
        if (!add.equals("")) {
            validFields++;
        }
        if (!add2.equals("")) {
            validFields++;
        }
        if (!add3.equals("")) {
            validFields++;
        }
        if (!city.equals("")) {
            validFields++;
        }
        if (!state.equals("--")) {
            validFields++;
        }
        if (!zip.equals("")) {
            if (Validation.isValidZip(zip)) {
                validFields++;
                zipLabel.removeWarning();
            } else {
                zipLabel.addWarning("Invalid zip code entered. Please use the format '#####' or '#####-####'.");
            }
        } else {
            zipLabel.removeWarning();
        }
        if (!email.equals("") && !emailField.isPlaceholderActive()) {
            if (Validation.isValidEmail(email)) {
                try {
                    // get all emails from the database
                    ResultSet rs = State.getConnection().getAllEmails();
                    boolean found = false;
                    while (rs.next()) {
                        // check each email to see if it matches the entered username
                        if (rs.getString(1).equals(email)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        validFields++;
                        emailLabel.removeWarning();
                    } else {
                        emailLabel.addWarning("Email address already in use.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                emailLabel.addWarning("Invalid email address entered.");
            }
        } else {
            emailLabel.removeWarning();
        }
        if (!phone.equals("")) {
            if (!Validation.isValidPhone(phone)) {
                phoneLabel.addWarning("Invalid phone number entered. Enter only numbers");
            } else {
                phoneLabel.removeWarning();
                validFields++;
            }
        } else {
            phoneLabel.removeWarning();
        }
        if (Arrays.equals(p, p2) && p.length > 0 && p2.length > 0) {
            // if the password is still not valid, invalid characters were entered.
            if (!Validation.isValidPassword(p)) {
                passwordLabel.addWarning("Password contains invalid special characters.");
            } else if (!Validation.isValidPassword(p)) {
                passwordLabel2.addWarning("Password contains invalid special characters.");
            } else {
                passwordLabel.removeWarning();
                passwordLabel2.removeWarning();
                validFields++;
            }
        } else {
            passwordLabel.removeWarning();
            passwordLabel2.removeWarning();
        }
        if (!username.equals("")) {
            char[] array = username.toCharArray();
            if (username.length() < 8) {
                usernameLabel.addWarning("Username must be longer than 8 characters.");
            } else if (Character.isDigit(array[0])) {
                usernameLabel.addWarning("Username cannot start with a number.");
            } else if (Validation.hasSpecialChar(array)) {
                usernameLabel.addWarning("Username cannot contain special characters.");
            }
            if (Validation.isValidUsername(username)) {
                try {
                    // get all usernames from the database
                    ResultSet rs = State.getConnection().getAllUsernames();
                    boolean found = false;
                    while (rs.next()) {
                        // check each username to see if it matches the entered username
                        if (rs.getString(1).equalsIgnoreCase(username)) {
                            found = true;
                            break;
                        }
                    }
                    // if the username was not found in the database
                    if (!found) {
                        validFields++;
                        usernameLabel.removeWarning();
                    } else {
                        usernameLabel.addWarning("Username already exists.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            usernameLabel.removeWarning();
        }
        if (!a1.equals("")) {
            validFields++;
        }
        if (!a2.equals("")) {
            validFields++;
        }
        if (!a3.equals("")) {
            validFields++;
        }
        if (validFields == requiredValid) {
            return true;
        } else {
            if (first.equals("") || last.equals("") || add.equals("") || city.equals("") || state.equals("--")
                    || zip.equals("") || email.equals("") || user.equals("") || p.length == 0 || p2.length == 0
                    || a1.equals("") || a2.equals("") || a3.equals("")) {
                fieldWarning.setVisible(true);
            } else {
                fieldWarning.setVisible(false);
            }
            return false;
        }
    }

    /**
     * Validates all fields in this AccountPanel to determine if the changes
     * made to an existing account can be saved.
     *
     * @return True if the form is valid, false otherwise
     */
    private boolean checkFields() {
        // get the text from every field
        String first = firstNameField.getText();
        String last = lastNameField.getText();
        String add = address1Field.getText();
        String add2 = address2Field.getText();
        String add3 = address3Field.getText();
        String city = cityField.getText();
        String zip = zipField.getText();
        String state = stateComboBox.getSelectedItem().toString();
        String email = emailField.getText();
        String phone = phoneField.getText();

        // int to track the fields with valid data
        int validFields = 0;
        // the total fields that must be valid (both password fields are counted as one)
        int requiredValid = 7;

        // if the optional fields are filled, add them to the total required fields
        if (!add2.equals("")) {
            requiredValid++;
        }
        if (!add3.equals("")) {
            requiredValid++;
        }
        if (!phone.equals("")) {
            requiredValid++;
        }

        // CHECK EACH FIELD FOR VALID DATA - SHOW WARNINGS IF INVALID
        if (!first.equals("")) {
            validFields++;
        }
        if (!last.equals("")) {
            validFields++;
        }
        if (!add.equals("")) {
            validFields++;
        }
        if (!add2.equals("")) {
            validFields++;
        }
        if (!add3.equals("")) {
            validFields++;
        }
        if (!city.equals("")) {
            validFields++;
        }
        if (!state.equals("--")) {
            validFields++;
        }
        if (!zip.equals("")) {
            if (Validation.isValidZip(zip)) {
                validFields++;
                zipLabel.removeWarning();
            } else {
                zipLabel.addWarning("Invalid zip code entered. Please use the format '#####' or '#####-####'.");
            }
        } else {
            zipLabel.removeWarning();
        }
        if (!email.equals("")) {
            if (Validation.isValidEmail(email)) {
                try {
                    // get all emails from the database
                    boolean found = false;
                    if (!email.equals(originalEmail)) {
                        ResultSet rs = State.getConnection().getAllEmails();
                        while (rs.next()) {
                            // check each email to see if it matches the entered username
                            if (rs.getString(1).equals(email)) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        validFields++;
                        emailLabel.removeWarning();
                    } else {
                        emailLabel.addWarning("Email address already in use.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                emailLabel.addWarning("Invalid email address entered.");
            }
        } else {
            emailLabel.removeWarning();
        }
        if (!phone.equals("")) {
            if (!Validation.isValidPhone(phone)) {
                phoneLabel.addWarning("Invalid phone number entered. Enter only numbers");
            } else {
                phoneLabel.removeWarning();
                validFields++;
            }
        } else {
            phoneLabel.removeWarning();
        }

        if (validFields == requiredValid) {
            return true;
        } else {
            if (first.equals("") || last.equals("") || add.equals("") || city.equals("") || state.equals("--")
                    || zip.equals("") || email.equals("")) {
                fieldWarning.setVisible(true);
            } else {
                fieldWarning.setVisible(false);
            }
            return false;
        }
    }

    /**
     * Changes the current question set to and changes the displayed questions.
     */
    private void nextQuestionSet() {
        currentQuestionSet++;
        int last = questionSets.size() - 1;
        if (currentQuestionSet > questionSets.get(last)) {
            currentQuestionSet = 1;
        }
        try {
            ResultSet r = State.getConnection().getSecurityQuestionSet(currentQuestionSet);
            r.next();
            question1.setText(r.getString(1));
            firstQuestion = Integer.parseInt(r.getString(2));
            r.next();
            question2.setText(r.getString(1));
            secondQuestion = Integer.parseInt(r.getString(2));
            r.next();
            question3.setText(r.getString(1));
            thirdQuestion = Integer.parseInt(r.getString(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * If the form is valid, creates a new account OR saves changes in the
     * database, and shows the OPTIONS panel in the manager state.
     */
    private void submitForm() {
        // if the form is valid
        if (user == null && validateFields()) {
            // get all information from input fields
            String first = firstNameField.getText();
            String last = lastNameField.getText();
            String add = address1Field.getText();
            String add2 = address2Field.getText();
            String add3 = address3Field.getText();
            String city = cityField.getText();
            String zip = zipField.getText();
            String state = stateComboBox.getSelectedItem().toString();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String username = usernameField.getText();
            String p = User.encodePassword(new String(passwordField.getPassword()));
            String a1 = answer1.getText();
            String a2 = answer2.getText();
            String a3 = answer3.getText();
            int position = accountType.getSelectedIndex() + 1;

            // create a Person with the required fields
            Person person = new Person(first, last, add, city, zip, state, email);
            // add the optional fields if they were filled out
            if (!phone.equals("")) {
                person.setPhone1(phone);
            }
            if (!add2.equals("")) {
                person.setAddressLine2(add2);
            }
            if (!add3.equals("")) {
                person.setAddressLine3(add3);
            }

            // create a User
            User newUser = new User(username, p, firstQuestion, secondQuestion, thirdQuestion, a1, a2, a3,
                    position, person);

            // insert the person and user into the database
            boolean success;
            try {
                State.getConnection().insertAccount(newUser, person);
                success = true;
            } catch (Exception ex) {
                ex.printStackTrace();
                success = false;
            }

            // if the insert was successful, reset this form and go back to previous page
            if (success) {
                resetForm();
                PopupDialog successDialog = new PopupDialog(State.getFrame());
                successDialog.setTitle("Success");
                successDialog.setLayout(new GridBagLayout());
                successDialog.setSize(new Dimension(400, 100));
                successDialog.setLocationRelativeTo(null);
                successDialog.add(new FieldLabel("Account created successfully."), new GridBagConstraints());
                successDialog.setVisible(true);
                ((Manager) State.getManagerState()).showPanel(Manager.OPTIONS);
            }
        } else if (user != null && checkFields()) {
            // get all information from input fields
            String first = firstNameField.getText();
            String last = lastNameField.getText();
            String add = address1Field.getText();
            String add2 = address2Field.getText();
            String add3 = address3Field.getText();
            String city = cityField.getText();
            String zip = zipField.getText();
            String state = stateComboBox.getSelectedItem().toString();
            String email = emailField.getText();
            String phone = phoneField.getText();
            int position = accountType.getSelectedIndex() + 1;
            boolean disabled = disableCheck.isSelected();

            // create a Person with the required fields
            person.setFirstName(first);
            person.setLastName(last);
            person.setAddressLine1(add);
            person.setCity(city);
            person.setZip(zip);
            person.setState(state);
            person.setEmail(email);
            user.setPosition(position);

            // add the optional fields if they were filled out
            if (!phone.equals("")) {
                person.setPhone1(phone);
            }
            if (!add2.equals("")) {
                person.setAddressLine2(add2);
            }
            if (!add3.equals("")) {
                person.setAddressLine3(add3);
            }

            // insert the person and user into the database
            boolean success;
            try {
                State.getConnection().updatePerson(person, originalEmail);
                State.getConnection().updatePosition(position, person.getPersonID());
                State.getConnection().disableUser(user, disabled);
                success = true;
            } catch (Exception ex) {
                ex.printStackTrace();
                success = false;
            }

            // if the insert was successful, reset this form and go back to previous page
            if (success) {
                resetForm();
                PopupDialog successDialog = new PopupDialog(State.getFrame());
                successDialog.setTitle("Success");
                successDialog.setLayout(new GridBagLayout());
                successDialog.setSize(new Dimension(450, 100));
                successDialog.setLocationRelativeTo(null);
                successDialog.add(new FieldLabel("Account information successfully updated."), new GridBagConstraints());
                successDialog.setVisible(true);
                ((Manager) State.getManagerState()).clearAccountSearch();
                ((Manager) State.getManagerState()).showPanel(Manager.OPTIONS);
            }
        }
    }

    /**
     * Initialize and place components in helpDialog. Add event listeners to
     * helpDialog components.
     */
    private void setupHelpDialog() {
        helpDialog = new PopupDialog(State.getFrame());
        helpDialog.setTitle("Help");
        helpDialog.setLayout(new GridBagLayout());
        helpDialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        // (0, 0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(new BizmartLabel("Register Help"), gbc);
        // (0, 1)
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(new BizmartLabel("All fields marked with an icon are required."), gbc);
        // (0, 2)
        gbc.gridy = 2;
        panel.add(new BizmartLabel("Username must be between 8-20 characters."), gbc);
        // (0, 3)
        gbc.gridy = 3;
        panel.add(new BizmartLabel("Username cannot begin with a number."), gbc);
        // (0, 4)
        gbc.gridy = 4;
        panel.add(new BizmartLabel("Username cannot contain spaces."), gbc);
        // (0, 5)
        gbc.gridy = 5;
        panel.add(new BizmartLabel("Valid special characters for password include: ()!@#$%^&*"), gbc);
        // (0, 6)
        helpDialog.add(panel, new GridBagConstraints());
        helpDialog.setSize(new Dimension(660, 450));
        panel.setPreferredSize(new Dimension(590, 350));
        helpDialog.setLocationRelativeTo(null);
    }
}
