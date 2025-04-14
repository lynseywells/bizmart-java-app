package statemachine;

import component.labels.WarningLabel;
import component.labels.FormTitle;
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
import javax.swing.*;
import util.*;

/**
 * Subclass Login is an interface for signing into an account, and is accessible
 * to all users. This state can move into the states employee, manager,
 * customer, password, and register.
 * <p>
 * This is the initial state of the application, and is used to set up the SQL
 * connection manager and JFrame that displays the program.
 */
public class Login extends State {

    /**
     * The root JPanel for the login state that is placed in the JFrame when
     * this state is entered. Uses a GridBagLayout.
     */
    private DropShadowPanel loginPanel;

    /**
     * InputField to enter an accounts username to sign in to the application.
     */
    private InputField usernameField;
    /**
     * PasswordField to enter an accounts password to sign in to the
     * application.
     */
    private PasswordField passwordField;
    /**
     * Button that toggles the visibility of the text in passwordField when
     * pressed.
     */
    private TogglePasswordButton showPasswordBttn;

    /**
     * Takes user to password state when pressed.
     */
    private Link resetPasswordLink;
    /**
     * Takes user to register state when pressed.
     */
    private Link registerLink;
    /**
     * Shows helpDialog when pressed.
     */
    private Link helpLink;
    /**
     * Takes user to customer state without logging in when pressed. User can
     * only view products, and cannot place an order.
     */
    private Link guestLink;

    /**
     * Button that signs into an account and changes the state based on the
     * accounts access level (if username and password are valid).
     */
    private ButtonPrimary loginBttn;

    /**
     * JLabel that notifies the user when the information entered in
     * usernameField and passwordField aren't valid.
     */
    private WarningLabel warning;

    /**
     * JDialog help menu.
     */
    PopupDialog helpDialog;

    /**
     * Login constructor initializes components needed for the login state.
     */
    public Login() {
        //----- INITIALIZE MAIN COMPONENTS -----//
        // CONNECTION LABEL
        connectionStatus = new JLabel("Not Connected");
        connectionStatus.setForeground(Colors.RED);
        connectionStatus.setFont(new Font("Nunito", Font.BOLD, 20));
        connectionStatus.setVisible(false);

        // SQL DATABASE CONNECTION
        con = new SQL();

        // JFRAME
        frame = new JFrame("Bizmart Supply Co");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // size & position window
        frame.setSize(1200, 800);
        frame.setMinimumSize(new Dimension(720, 720));
        frame.setLocationRelativeTo(null); // center frame in window
        // change the window icon
        ImageIcon img = new ImageIcon("src\\main\\java\\com\\bizmart\\images\\bizmart_icon.png", "bizmart logo");
        frame.setIconImage(img.getImage());
        // background color
        frame.getContentPane().setBackground(Colors.SILVER);
        // NOTE: BorderLayout doesn't respect panel sizing and FlowLayout doesn't respect panel location.
        // NOTE: Must use getContentPane() to prevent AWTError: BoxLayout can't be shared. 
        // NOTE: GridBagLayout is used b/c JScrollPane in Register panel wasn't centered with BoxLayout
        frame.getContentPane().setLayout(new GridBagLayout());

        // TERMINATE THE PROGRAM WHEN WINDOW IS CLOSED
        Action closePress = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                con.close();
                System.exit(0);
            }
        };
        ((JPanel) frame.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "pressed");
        ((JPanel) frame.getContentPane()).getActionMap().put("pressed", closePress);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closePress.actionPerformed(new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        // LOGIN PANEL
        loginPanel = new DropShadowPanel(8, Color.WHITE, Colors.SILVER, Colors.DARK_BLUE);
        loginPanel.setBackground(Color.WHITE);

        // LABELS
        FormTitle title = new FormTitle("Login");
        BizmartLabel usernameLabel = new BizmartLabel("Username");
        BizmartLabel passwordLabel = new BizmartLabel("Password");

        // FIELDS
        usernameField = new InputField();
        passwordField = new PasswordField();

        // BUTTONS
        showPasswordBttn = new TogglePasswordButton(passwordField);
        loginBttn = new ButtonPrimary("Login");

        // LINKS
        helpLink = new Link("Help");
        resetPasswordLink = new Link("Forgot Password?");
        SmallLabel registerLabel = new SmallLabel("Don't have an account?");
        registerLink = new Link("Sign Up");
        guestLink = new Link("Continue as Guest");

        // WARNING LABEL
        warning = new WarningLabel("Username or password is incorrect.");

        //HELP DIALOG
        setupHelpDialog();

        //----- GROUP COMPONENTS -----//
        // USERNAME PANEL 
        // group usernameLabel and usernameField vertically
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.Y_AXIS));
        usernamePanel.setBackground(null);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(Box.createVerticalStrut(5));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernamePanel.add(usernameField);

        // PASSWORD PANEL
        // group passwordLabel, showPasswordBttn, passwordField, && resetPasswordLink
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new GridBagLayout());
        passwordPanel.setBackground(null);
        gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 2, 0);
        passwordPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        passwordPanel.add(showPasswordBttn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridwidth = 2;
        passwordPanel.add(passwordField, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        passwordPanel.add(resetPasswordLink, gbc);

        // REGISTER PANEL
        // group registerLabel && registerLink horizontally
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.X_AXIS));
        registerPanel.setBackground(null);
        registerPanel.add(registerLabel);
        registerPanel.add(Box.createHorizontalStrut(5));
        registerPanel.add(registerLink);

        //----- SIZE COMPONENTS -----//
        int width = (int) usernameField.getWidth() + 100;
        title.setUnderlineWidth(width - 80);
        title.setUnderlineThickness(2);
        title.setUnderlineOffset(4);
        loginBttn.setPreferredSize(new Dimension(width - 150, 60));

        width = frame.getWidth() - 100;
        int height = frame.getHeight() - 100;

        loginPanel.setPreferredSize(new Dimension(width, height));
        loginPanel.setMaximumSize(new Dimension(width, height));

        //----- ADD COMPONENTS TO LOGIN PANEL -----//
        loginPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        loginPanel.add(helpLink, gbc);

        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 18, 0);
        loginPanel.add(title, gbc);

        gbc.gridy = 2;
        loginPanel.add(usernamePanel, gbc);

        gbc.gridy = 3;
        loginPanel.add(passwordPanel, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 6, 0);
        loginPanel.add(loginBttn, gbc);

        gbc.gridy = 5;
        loginPanel.add(warning, gbc);

        gbc.gridy = 6;
        loginPanel.add(registerPanel, gbc);
        loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        gbc.gridy = 7;
        loginPanel.add(guestLink, gbc);
    }

    //--------------------------------------------//
    //---------- ABSTRACT STATE METHODS ----------//
    //---------- ENTER STATE ----------//
    @Override
    protected void enterState() {
        // change frame title
        State.frame.setTitle("Bizmart Login");
        // add panel to frame
        State.frame.getContentPane().removeAll();
        resetForm();
        // default GridBagConstraints centers panel in frame & respects sizing
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        State.frame.add(connectionStatus, gbc);
        gbc.gridy = 1;
        State.frame.add(loginPanel, gbc);

        // set focus
        usernameField.requestFocusInWindow();

        // repaint the frame
        State.frame.repaint();
        State.frame.setVisible(true);
    }

    //---------- ADD EVENT LISTENERS TO COMPONENTS ----------//
    @Override
    protected void addEventListeners() {
        //----- USERNAME FIELD -----//
        usernameField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // prevent the user from typing over 20 characters or non-alphanumeric characters
                if (usernameField.getText().length() >= 20
                        || !Character.isAlphabetic(e.getKeyChar()) && !Character.isDigit(e.getKeyChar())) {
                    e.consume();
                }
            }
        });

        //----- LOGIN BUTTON -----//
        Action submit = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                submitForm();
            }
        };
        // key binding for "SPACE" key
        loginBttn.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        loginBttn.getActionMap().put("pressed", submit);
        // key binding for "ENTER" key
        loginBttn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "submit");
        loginBttn.getActionMap().put("submit", submit);

        loginBttn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                loginBttn.mouseReleased();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                loginBttn.mousePressed();
                submitForm();
            }
        });

        //----- REGISTER LINK -----//
        Action registerPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                resetForm();
                changeState(State.register);
            }
        };
        registerLink.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        registerLink.getActionMap().put("pressed", registerPress);

        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                registerPress.actionPerformed(new ActionEvent(registerLink, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        //----- RESET PASSWORD LINK -----//
        Action resetPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                resetForm();
                changeState(State.password);
            }
        };
        resetPasswordLink.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        resetPasswordLink.getActionMap().put("pressed", resetPress);

        resetPasswordLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                resetPress.actionPerformed(new ActionEvent(resetPasswordLink, ActionEvent.ACTION_PERFORMED, null));
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

        //----- GUEST LINK -----//
        Action guestPress = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                State.currentUser = null;
                changeState(State.customer);
            }
        };
        guestLink.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        guestLink.getActionMap().put("pressed", guestPress);

        guestLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                guestPress.actionPerformed(new ActionEvent(guestLink, ActionEvent.ACTION_PERFORMED, null));
            }
        });
    }

    //-----------------------------------------//
    //---------- LOGIN STATE METHODS ----------//
    /**
     * Resets the login form by clearing fields, hiding the warning label, and
     * hiding the passwordField text.
     */
    private void resetForm() {
        usernameField.setText("");
        passwordField.setText("");
        warning.setVisible(false);
        showPasswordBttn.changeState(TogglePasswordButton.IconState.HIDE);
    }

    /**
     * Checks if the entered username and password are valid.
     * <p>
     * If valid, the user's information is retrieved and saved in the variable
     * currentUser and the state is changed based on the user's account type. If
     * invalid, the state is not changed and a warning label is shown.
     */
    private void submitForm() {
        String username = usernameField.getText();
        String password = User.encodePassword(new String(passwordField.getPassword()));
        try {
            ResultSet rs = con.getAccount(username, password);
            User user = null;
            if (rs.next()) {
                warning.setVisible(false);
                user = new User();
                user.setPersonID(rs.getInt(1));
                user.setUsername(rs.getString(2));
                user.setPassword(rs.getString(3));
                user.setQuestion1(rs.getInt(4));
                user.setQuestion2(rs.getInt(5));
                user.setQuestion3(rs.getInt(6));
                user.setAnswer1(rs.getString(7));
                user.setAnswer2(rs.getString(8));
                user.setAnswer3(rs.getString(9));
                user.setPosition(rs.getInt(10));

                State.currentUser = user;
                if (user.getPosition() == User.CUSTOMER) {
                    changeState(State.customer);
                } else if (user.getPosition() == User.EMPLOYEE) {
                    changeState(State.employee);
                } else {
                    changeState(State.manager);
                }
            } else {
                warning.setVisible(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
        BizmartLabel loginHelp = new BizmartLabel("Login Help");
        loginHelp.setFont(new Font("Nunito", Font.BOLD, 24));
        panel.add(loginHelp, gbc);

        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(new BizmartLabel("Enter your username and password to sign in to your account."), gbc);

        gbc.gridy = 2;
        panel.add(new BizmartLabel("If you don't have an account, click the 'Sign Up' link."), gbc);

        gbc.gridy = 3;
        panel.add(new BizmartLabel("If you forgot your password, click the 'Forgot Password?' link."), gbc);

        helpDialog.add(panel, new GridBagConstraints());
        helpDialog.setSize(new Dimension(700, 450));
        panel.setPreferredSize(new Dimension(630, 350));
        helpDialog.setLocationRelativeTo(null);
    }
}
