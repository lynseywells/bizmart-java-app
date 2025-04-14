package statemachine;

import component.labels.RequirementLabel;
import component.labels.WarningLabel;
import component.labels.FormTitle;
import component.labels.InstructionLabel;
import component.labels.SmallLabel;
import component.labels.BizmartLabel;
import component.fields.InputField;
import component.fields.PasswordField;
import component.buttons.ButtonPrimary;
import component.buttons.TogglePasswordButton;
import component.buttons.Link;
import component.PopupDialog;
import component.Colors;
import component.panels.DropShadowPanel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import util.*;

/**
 * Subclass ForgotPassword is an interface for resetting an accounts password,
 * and is accessible to all users. This state can move into the register and
 * login.
 */
public class ForgotPassword extends State {

    /**
     * The root JPanel for the password state that is placed in the JFrame when
     * this state is entered. Uses a GridBagLayout.
     */
    private DropShadowPanel forgotPasswordPanel;

    /**
     * A JPanel with a CardLayout used to display JPanels for each interface of
     * the password state.
     */
    private JPanel cards;

    /**
     * A card in cards that contains a search field for the user to enter their
     * username.
     */
    private JPanel usernamePanel;

    /**
     * A card in cards that contains a form for the user to answer the security
     * questions associated with an account.
     */
    private JPanel questionsPanel;

    /**
     * A card in cards that contains a form for the user to reset the password
     * of an account.
     */
    private JPanel passwordPanel;

    //---------- USERNAME PANEL COMPONENTS ----------//
    /**
     * Field to enter the username of the account to reset the password of.
     * <p>
     * usernamePanel component.
     */
    private InputField usernameField;

    /**
     * Button that searches for an account with the username entered in
     * usernameField when pressed.
     * <p>
     * usernamePanel component.
     */
    private ButtonPrimary findAccountBttn;

    /**
     * Warning shown when an invalid username is entered.
     * <p>
     * usernamePanel component.
     */
    private WarningLabel usernameWarning;

    //---------- QUESTIONS PANEL COMPONENTS ----------//
    /**
     * Field to enter the answer to a security question.
     * <p>
     * questionsPanel component.
     */
    private InputField answer1, answer2, answer3;
    /**
     * Button to check the entered answers when pressed.
     * <p>
     * questionsPanel component.
     */
    private ButtonPrimary submitAnswerBttn;
    /**
     * Warning shown when the answers aren't correct.
     * <p>
     * questionsPanel component.
     */
    private WarningLabel answerWarning;
    /**
     * Label displays one of the security questions for the account.
     * <p>
     * questionsPanel component.
     */
    private BizmartLabel question1, question2, question3;

    /**
     * The correct answers to each security question.
     */
    private ArrayList<String> correctAnswers;

    /**
     * Stores the account's username to update the password in the database.
     */
    private String username;

    /**
     * Label displays instructions for questionsPanel form.
     * <p>
     * questionsPanel component.
     */
    private InstructionLabel answerInstructions;

    //----- PASSWORD PANEL COMPONENTS -----//
    /**
     * PasswordField to enter the new password.
     * <p>
     * passwordPanel component.
     */
    private PasswordField passwordField;
    /**
     * PasswordField to enter the new password again.
     * <p>
     * passwordPanel component.
     */
    private PasswordField passwordField2;

    /**
     * Label that indicates when a password requirement is met.
     * <p>
     * passwordPanel component.
     */
    private RequirementLabel specialReq, lengthReq, charReq, numberReq, matchReq;

    /**
     * Button that toggles the visibility of text in passwordField.
     * <p>
     * passwordPanel component.
     */
    private TogglePasswordButton toggle1;

    /**
     * Button that toggles the visibility of text in passwordField2.
     * <p>
     * passwordPanel component.
     */
    private TogglePasswordButton toggle2;

    /**
     * Button that updates the accounts password and changes state to login
     * state.
     * <p>
     * passwordPanel component.
     */
    private ButtonPrimary resetPasswordBttn;

    /**
     * Shows helpDialog when pressed.
     */
    private Link helpLink;
    /**
     * Changes state to register when pressed.
     */
    private Link registerLink;
    /**
     * Changes state to login when pressed.
     */
    private Link loginLink;

    /**
     * A JDialog help menu.
     */
    PopupDialog helpDialog;

    /**
     * ForgotPassword constructor initializes components needed for the password
     * state.
     */
    public ForgotPassword() {
        //----- INITIALIZE MAIN COMPONENTS -----//
        // ARRAYLIST TO STORE CORRECT ANSWERS
        correctAnswers = new ArrayList<>();

        // LINKS
        helpLink = new Link("Help");
        registerLink = new Link("Sign Up");
        loginLink = new Link("Login");

        // JPANELS
        forgotPasswordPanel = new DropShadowPanel(8, Color.WHITE, Colors.SILVER, Colors.DARK_BLUE);
        forgotPasswordPanel.setLayout(new GridBagLayout());
        cards = new JPanel(new CardLayout());
        cards.setBackground(null);
        usernamePanel = new JPanel(new GridBagLayout());
        usernamePanel.setBackground(null);
        questionsPanel = new JPanel(new GridBagLayout());
        questionsPanel.setBackground(null);
        passwordPanel = new JPanel(new GridBagLayout());
        passwordPanel.setBackground(null);

        // FORM TITLES
        FormTitle forgotPasswordTitle = new FormTitle("Forgot Password", 400, 2);
        FormTitle securityTitle = new FormTitle("Security Questions", 400, 2);
        FormTitle resetPasswordTitle = new FormTitle("Reset Password", 400, 2);

        // USERNAME PANEL COMPONENTS
        usernameField = new InputField();
        usernameWarning = new WarningLabel("Username not found.");
        findAccountBttn = new ButtonPrimary("Find Account");

        // ANSWER PANEL COMPONENTS
        question1 = new BizmartLabel("Question 1");
        question2 = new BizmartLabel("Question 2");
        question3 = new BizmartLabel("Question 3");
        answer1 = new InputField();
        answer2 = new InputField();
        answer3 = new InputField();
        submitAnswerBttn = new ButtonPrimary("Submit");
        answerWarning = new WarningLabel("One or more answers are incorrect.");

        // PASSWORD PANEL COMPONENTS
        passwordField = new PasswordField();
        passwordField2 = new PasswordField();
        BizmartLabel passwordRequirements = new BizmartLabel("Password Requirements:");
        toggle1 = new TogglePasswordButton(passwordField);
        toggle2 = new TogglePasswordButton(passwordField2);
        BizmartLabel passwordLabel1 = new BizmartLabel("Password");
        BizmartLabel passwordLabel2 = new BizmartLabel("Repeat Password");
        resetPasswordBttn = new ButtonPrimary("Reset Password");

        // PASSWORD REQUIREMENTS
        specialReq = new RequirementLabel("Must contain a special character.");
        lengthReq = new RequirementLabel("Must be between 8 and 20 characters.");
        charReq = new RequirementLabel("Must contain an alphabetic character.");
        numberReq = new RequirementLabel("Must contain a numeric digit.");
        matchReq = new RequirementLabel("Both passwords must match.");

        // HELP DIALOG
        setupHelpDialog();

        //----- ADD COMPONENTS TO USERNAME PANEL -----//
        GridBagConstraints gbc = new GridBagConstraints();
        InstructionLabel userInstructions = new InstructionLabel("Enter your account's username to reset your password.");
        BizmartLabel usernameLabel = new BizmartLabel("Username");

        // PANEL FOR USERNAME LABEL & FIELD
        JPanel uPanel = new JPanel();
        uPanel.setLayout(new BoxLayout(uPanel, BoxLayout.Y_AXIS));
        uPanel.setBackground(null);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        uPanel.add(usernameLabel);
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        uPanel.add(Box.createVerticalStrut(5));
        uPanel.add(usernameField);

        // ADD TO USERNAME PANEL
        // (0, 0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        usernamePanel.add(forgotPasswordTitle, gbc);
        // (0, 1)
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        usernamePanel.add(userInstructions, gbc);
        // (0, 2)
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 5, 0);
        usernamePanel.add(uPanel, gbc);
        // (0, 3)
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        usernamePanel.add(usernameWarning, gbc);
        // (0, 4)
        gbc.gridy = 4;
        gbc.insets = new Insets(15, 0, 0, 0);
        usernamePanel.add(findAccountBttn, gbc);

        //----- ADD COMPONENTS TO QUESTIONS PANEL -----//
        gbc = new GridBagConstraints();
        answerInstructions = new InstructionLabel("Answer these security questions to prove it's you.");

        // PANEL FOR QUESTION LABELS & ANSWER FIELDS
        JPanel qPanel = new JPanel(new GridBagLayout());
        qPanel.setBackground(null);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        qPanel.add(question1, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        qPanel.add(answer1, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 5, 0);
        qPanel.add(question2, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 15, 0);
        qPanel.add(answer2, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 5, 0);
        qPanel.add(question3, gbc);

        gbc.gridy = 5;
        qPanel.add(answer3, gbc);

        // ADD TO QUESTIONS PANEL
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 5, 0);
        questionsPanel.add(securityTitle, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        questionsPanel.add(answerInstructions, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        questionsPanel.add(qPanel, gbc);

        gbc.gridy = 3;
        questionsPanel.add(answerWarning, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(15, 0, 0, 0);
        questionsPanel.add(submitAnswerBttn, gbc);

        //----- ADD COMPONENTS TO PASSWORD PANEL -----//
        // PASSWORD 1 PANEL
        JPanel password1Panel = new JPanel(new GridBagLayout());
        password1Panel.setBackground(null);
        gbc = new GridBagConstraints();
        // (0, 0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        password1Panel.add(passwordLabel1, gbc);
        // (1, 0)
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        password1Panel.add(toggle1, gbc);
        // (0, 1)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        password1Panel.add(passwordField, gbc);

        // PASSWORD 2 PANEL
        JPanel password2Panel = new JPanel(new GridBagLayout());
        password2Panel.setBackground(null);
        gbc = new GridBagConstraints();
        // (0, 0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        password2Panel.add(passwordLabel2, gbc);
        // (1, 0)
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        password2Panel.add(toggle2, gbc);
        // (0, 1)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        password2Panel.add(passwordField2, gbc);

        // REQUIREMENTS PANEL
        gbc = new GridBagConstraints();
        InstructionLabel passwordInstructions = new InstructionLabel("Enter a new password.");
        JPanel reqPanel = new JPanel(new GridBagLayout());
        reqPanel.setBackground(null);
        // (0, 0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        reqPanel.add(passwordRequirements, gbc);
        // (0, 1)
        gbc.gridy = 1;
        reqPanel.add(charReq, gbc);
        // (0, 2)
        gbc.gridy = 2;
        reqPanel.add(numberReq, gbc);
        // (0, 3)
        gbc.gridy = 3;
        reqPanel.add(specialReq, gbc);
        // (0, 4)
        gbc.gridy = 4;
        reqPanel.add(lengthReq, gbc);
        // (0, 5)
        gbc.gridy = 5;
        reqPanel.add(matchReq, gbc);

        // ADD TO PASSWORD PANEL
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 5, 0);
        passwordPanel.add(resetPasswordTitle, gbc);

        gbc.gridy = 1;
        passwordPanel.add(passwordInstructions, gbc);

        gbc.gridy = 2;
        passwordPanel.add(password1Panel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 5, 0);
        passwordPanel.add(password2Panel, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 0, 0);
        passwordPanel.add(reqPanel, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 0, 0);
        passwordPanel.add(resetPasswordBttn, gbc);

        //----- SIZE COMPONENTS -----//
        // BUTTONS
        resetPasswordBttn.setPreferredSize(new Dimension(274, 64));
        findAccountBttn.setPreferredSize(new Dimension(254, 64));
        submitAnswerBttn.setPreferredSize(new Dimension(254, 64));

        // INSTRUCTION TEXTPANES
        Dimension d = new Dimension(350, 60);
        userInstructions.setSize(350, 2);
        passwordInstructions.setSize(350, 1);
        answerInstructions.setSize(350, 2);

        // FIELDS
        usernameField.setWidth(375);
        passwordField.setWidth(375);
        passwordField2.setWidth(375);
        answer1.setWidth(375);
        answer2.setWidth(375);
        answer3.setWidth(375);

        // FORGOT PASSWORD PANEL SIZE
        int width = frame.getWidth() - 100;
        int height = frame.getHeight() - 100;
        d = new Dimension(width, height);
        forgotPasswordPanel.setPreferredSize(d);
        forgotPasswordPanel.setMaximumSize(d);

        //----- ADD PANELS TO CARDLAYOUT PANEL -----//
        cards.add(usernamePanel, "username");
        cards.add(questionsPanel, "questions");
        cards.add(passwordPanel, "password");

        //----- ADD CARDLAYOUT & LINKS TO FORGOT PASSWORD PANEL -----//
        // LINK PANELS
        JPanel linkPanel = new JPanel();
        linkPanel.setBackground(null);
        linkPanel.setLayout(new BoxLayout(linkPanel, BoxLayout.X_AXIS));
        SmallLabel label = new SmallLabel("Don't have an account?");
        linkPanel.add(label);
        linkPanel.add(Box.createHorizontalStrut(5));
        linkPanel.add(registerLink);

        JPanel linkPanel2 = new JPanel();
        linkPanel2.setBackground(null);
        linkPanel2.setLayout(new BoxLayout(linkPanel2, BoxLayout.X_AXIS));
        SmallLabel label2 = new SmallLabel("Already know your password?");
        linkPanel2.add(label2);
        linkPanel2.add(Box.createHorizontalStrut(5));
        linkPanel2.add(loginLink);

        // ADD TO FORGOT PASSWORD PANEL
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        forgotPasswordPanel.add(helpLink, gbc);

        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        forgotPasswordPanel.add(cards, gbc);

        gbc.gridy = 2;
        forgotPasswordPanel.add(linkPanel, gbc);

        gbc.gridy = 3;
        forgotPasswordPanel.add(linkPanel2, gbc);
    }

    //--------------------------------------------//
    //---------- ABSTRACT STATE METHODS ----------//
    //---------- ENTER STATE ----------//
    @Override
    protected void enterState() {
        // change frame title
        State.frame.setTitle("Bizmart Forgot Password");

        // add panel to frame
        State.frame.getContentPane().removeAll();
        // default GridBagConstraints centers panel in frame & respects sizing
        State.frame.add(forgotPasswordPanel, new GridBagConstraints());

        // show the first card
        CardLayout cl = (CardLayout) cards.getLayout();
        cl.show(cards, "username");

        // set focus
        usernameField.requestFocusInWindow();

        // repaint the frame
        State.frame.repaint();
        State.frame.setVisible(true);
    }

    //---------- ADD EVENT LISTENERS TO COMPONENTS ----------//
    @Override
    protected void addEventListeners() {
        //----- FIND ACCOUNT BUTTON -----//
        Action findPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                showQuestionsCard();
            }
        };
        findAccountBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        findAccountBttn.getActionMap().put("pressed", findPress);

        findAccountBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                findAccountBttn.mouseReleased();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                findAccountBttn.mousePressed();
                showQuestionsCard();
            }
        });

        //----- SUBMIT ANSWER BUTTON -----//
        Action passwordPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                showPasswordCard();
            }
        };
        submitAnswerBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        submitAnswerBttn.getActionMap().put("pressed", passwordPress);

        submitAnswerBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                submitAnswerBttn.mouseReleased();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                submitAnswerBttn.mousePressed();
                showPasswordCard();
            }
        });

        //----- RESET PASSWORD BUTTON -----//
        Action resetPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                savePassword();
            }
        };
        resetPasswordBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        resetPasswordBttn.getActionMap().put("pressed", resetPress);

        resetPasswordBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                resetPasswordBttn.mouseReleased();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                resetPasswordBttn.mousePressed();
                savePassword();
            }
        });

        //----- REGISTER LINK -----//
        Action registerPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                resetForm();
                changeState(State.register);
            }
        };

        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                registerPress.actionPerformed(new ActionEvent(registerLink, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        registerLink.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        registerLink.getActionMap().put("pressed", registerPress);

        //----- LOGIN LINK -----//
        Action loginPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                resetForm();
                changeState(State.login);
            }
        };
        loginLink.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        loginLink.getActionMap().put("pressed", loginPress);

        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                loginPress.actionPerformed(new ActionEvent(loginLink, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- HELP LINK -----//
        Action helpPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                helpDialog.setVisible(true);
            }
        };
        helpLink.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        helpLink.getActionMap().put("pressed", helpPress);

        helpLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                helpDialog.setVisible(true);
            }
        });

        //----- USERNAME -----//
        usernameField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // prevent the user from typing over 20 characters or non-alphanumeric
                // characters
                if (usernameField.getText().length() >= 20
                        || !Character.isAlphabetic(e.getKeyChar()) && !Character.isDigit(e.getKeyChar())) {
                    e.consume();
                }
            }
        });

        //----- PASSWORD 1 -----//
        passwordField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // if password field 2 is not empty, check if both passwords match
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

                    if (password.length + 1 < 8) {
                        lengthReq.requirementMet(false);
                    }
                }

                if (passwordField.getPassword().length + 1 >= 8) {
                    lengthReq.requirementMet(true);
                }
            }
        });

        //----- PASSWORD 2 -----//
        passwordField2.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
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
    }

    //---------------------------------------------------//
    //---------- FORGOT PASSWORD STATE METHODS ----------//
    /**
     * Resets the form by clearing fields, resetting password requirements, and
     * hiding text in password fields.
     */
    private void resetForm() {
        usernameField.setText("");
        answer1.setText("");
        answer2.setText("");
        answer3.setText("");
        passwordField.setText("");
        toggle1.changeState(TogglePasswordButton.IconState.HIDE);
        toggle2.changeState(TogglePasswordButton.IconState.HIDE);
        passwordField2.setText("");
        answerWarning.setVisible(false);
        usernameWarning.setVisible(false);
        charReq.requirementMet(false);
        lengthReq.requirementMet(false);
        matchReq.requirementMet(false);
        numberReq.requirementMet(false);
        specialReq.requirementMet(false);
    }

    /**
     * Checks the username entered in usernamePanel. If username is valid,
     * changes displayed panel in cards to questionsPanel.
     */
    private void showQuestionsCard() {
        // check username
        String u = usernameField.getText();
        try {
            // try to get the security questions
            ResultSet rs = con.getAccountSecurityQuestions(u);

            // if the username was found, get the security questions
            if (rs.next()) {
                usernameWarning.setVisible(false);

                // add the username to answerInstructions
                username = u;
                answerInstructions.setText("Hi " + username + ", please answer these security questions to prove it's you.");
                // add another row to the jtextpane if the username is too long
                if (username.length() > 11) {
                    answerInstructions.setRows(3);
                }

                // add the questions
                question1.setText(rs.getString(1));
                rs.next();
                question2.setText(rs.getString(1));
                rs.next();
                question3.setText(rs.getString(1));
                // get the answers
                rs = con.getAccountSecurityAnswers(u);
                rs.next();
                correctAnswers.add(rs.getString(1));
                correctAnswers.add(rs.getString(2));
                correctAnswers.add(rs.getString(3));
                // show the answer panel
                CardLayout cl = (CardLayout) cards.getLayout();
                cl.show(cards, "questions");
            } else {
                usernameWarning.setVisible(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Checks the answers entered in questionsPanel. If answers are correct, the
     * panel displayed in cards is changed to passwordPanel.
     */
    private void showPasswordCard() {
        // check the answers
        String a1 = answer1.getText();
        String a2 = answer2.getText();
        String a3 = answer3.getText();
        if (a1.equalsIgnoreCase(correctAnswers.get(0)) && a2.equalsIgnoreCase(correctAnswers.get(1))
                && a3.equalsIgnoreCase(correctAnswers.get(2))) {
            answerWarning.setVisible(false);
            CardLayout cl = (CardLayout) cards.getLayout();
            cl.show(cards, "password");
        } else {
            answerWarning.setVisible(true);
        }
    }

    /**
     * Updates the account's password and changes state to login state.
     */
    private void savePassword() {
        // if all requirements are met, save the new password and take user back to login
        if (matchReq.getIsMet() && specialReq.getIsMet() && charReq.getIsMet()
                && lengthReq.getIsMet() && numberReq.getIsMet()) {
            try {
                con.updatePassword(username, new String(passwordField.getPassword()));
                resetForm();
                changeState(State.login);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Initialize and place components in helpDialog. Add event listeners to
     * helpDialog components.
     */
    private void setupHelpDialog() {
        helpDialog = new PopupDialog(State.frame);
        helpDialog.setTitle("Help");
        helpDialog.setLayout(new GridBagLayout());
        helpDialog.setResizable(false);
        helpDialog.getContentPane().setBackground(Color.WHITE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        BizmartLabel helpLabel = new BizmartLabel("Forgot Password Help");
        helpLabel.setFont(new Font("Nunito", Font.BOLD, 24));
        panel.add(helpLabel, gbc);

        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(new BizmartLabel("To reset your password, type your username in the text field."), gbc);

        gbc.gridy = 2;
        panel.add(new BizmartLabel("Answer the security questions correctly to reset your password."), gbc);

        gbc.gridy = 3;
        panel.add(new BizmartLabel("Answers are not case-sensitive."), gbc);

        gbc.gridy = 4;
        panel.add(new BizmartLabel("Valid special characters for password include: ()!@#$%^&*"), gbc);

        gbc.gridy = 5;
        panel.add(new BizmartLabel("Click the 'Login' or 'Sign Up' links to quit."), gbc);

        helpDialog.add(panel, new GridBagConstraints());
        helpDialog.setSize(new Dimension(720, 450));
        panel.setPreferredSize(new Dimension(650, 350));
        helpDialog.setLocationRelativeTo(null);
    }
}
