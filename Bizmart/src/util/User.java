package util;

/**
 * User is utilized by the Bizmart application to store account login
 * information. User is used to upload login information to the database when an
 * account is created, when signing-in to the application, or when editing an
 * account (i.e. account type, password, and disabled/deletion status).
 */
public class User {

    /**
     * Indicates an account is customer type.
     */
    public static final int CUSTOMER = 1;

    /**
     * Indicates an account is employee type.
     */
    public static final int EMPLOYEE = 2;

    /**
     * Indicates an account is manager type.
     */
    public static final int MANAGER = 3;

    /**
     * The account's username. Limited to 50 characters.
     */
    private String username;

    /**
     * The account's password. Limited to 50 characters.
     */
    private String password;

    /**
     * The ID of the account's first security question in the database.
     */
    private int question1;

    /**
     * The ID of the account's second security question in the database.
     */
    private int question2;

    /**
     * The ID of the account's third security question in the database.
     */
    private int question3;

    /**
     * The correct answer to question1.
     */
    private String answer1;

    /**
     * The correct answer to question2.
     */
    private String answer2;

    /**
     * The correct answer to question3.
     */
    private String answer3;

    /**
     * The account's type, which determines the features the user can access
     * once signed in.
     */
    private int position;

    /**
     * True if the account is disabled, false otherwise.
     */
    private boolean isAccountDisabled;

    /**
     * True if the account is deleted, false otherwise.
     */
    private boolean isAccountDeleted;

    /**
     * The Person who owns the account.
     */
    private Person person;

    /**
     * The ID number of the person who owns the account in the database.
     */
    private int personID;

    /**
     * The ID number of the account in the database.
     */
    private int loginID;

    /**
     * The key used to decode the password.
     */
    private static final int key = 3;

    /**
     * Empty User constructor used when a user signs into the Bizmart program to
     * store essential account information.
     */
    public User() {
    }

    /**
     * User constructor used when creating a new account to upload login
     * information to the database.
     *
     * @param username a username
     * @param password a password
     * @param question1 a security question id
     * @param question2 a security question id
     * @param question3 a security question id
     * @param answer1 the answer to question1
     * @param answer2 the answer to question2
     * @param answer3 the answer to question3
     * @param position the accounts type
     * @param person the Person who owns the account
     */
    public User(String username, String password, int question1, int question2, int question3, String answer1,
            String answer2, String answer3, int position, Person person) {
        this.username = username;
        this.password = password;
        this.question1 = question1;
        this.question2 = question2;
        this.question3 = question3;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.position = position;
        this.person = person;
    }

    /**
     * User constructor used to organize data retrieved from the database for
     * editing.
     *
     * @param username the accounts username
     * @param password the password username
     * @param question1 a security question id
     * @param question2 a security question id
     * @param question3 a security question id
     * @param answer1 the answer to question1
     * @param answer2 the answer to question2
     * @param answer3 the answer to question3
     * @param position the accounts type
     * @param person the Person who owns the account
     * @param disabled true if the account is disabled, false otherwise
     * @param deleted true if the account is deleted, false otherwise
     * @param loginID the login ID in the database
     */
    public User(String username, String password, int question1, int question2, int question3, String answer1,
            String answer2, String answer3, int position, Person person, boolean disabled, boolean deleted, int loginID) {
        this.username = username;
        this.password = password;
        this.question1 = question1;
        this.question2 = question2;
        this.question3 = question3;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.position = position;
        this.person = person;
        this.personID = person.getPersonID();
        isAccountDisabled = disabled;
        isAccountDeleted = deleted;
        this.loginID = loginID;
    }

    //-----------------------------//
    //---------- GETTERS ----------//
    /**
     * Gets the person ID of the account holder.
     *
     * @return a person ID
     */
    public int getPersonID() {
        return personID;
    }

    /**
     * Gets the accounts username.
     *
     * @return a username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the accounts password.
     *
     * @return a password
     */
    public char[] getPassword() {
        return decodePassword(password).toCharArray();
    }

    /**
     * Gets the first security question ID.
     *
     * @return a question ID
     */
    public int getQuestion1() {
        return question1;
    }

    /**
     * Gets the second security question ID.
     *
     * @return a question ID
     */
    public int getQuestion2() {
        return question2;
    }

    /**
     * Gets the third security question ID.
     *
     * @return a question ID
     */
    public int getQuestion3() {
        return question3;
    }

    /**
     * Gets the answer to the first security question.
     *
     * @return a security question answer
     */
    public String getAnswer1() {
        return answer1;
    }

    /**
     * Gets the answer to the second security question.
     *
     * @return a security question answer
     */
    public String getAnswer2() {
        return answer2;
    }

    /**
     * Gets the answer to the third security question.
     *
     * @return a security question answer
     */
    public String getAnswer3() {
        return answer3;
    }

    /**
     * Gets the accounts position (type) as an ID.
     *
     * @return a position ID
     */
    public int getPosition() {
        return position;
    }

    /**
     * Gets the accounts disabled status.
     *
     * @return true if the account is disabled, false if enabled
     */
    public boolean isDisabled() {
        return isAccountDisabled;
    }

    /**
     * Gets the accounts deletion status.
     *
     * @return true if the account is deleted, false otherwise
     */
    public boolean isDeleted() {
        return isAccountDeleted;
    }

    /**
     * Gets the Person object that stores the contact information of the
     * account's holder.
     *
     * @return the Person who owns the account
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Gets the login ID of the account in the database.
     *
     * @return a login ID
     */
    public int getLoginID() {
        return loginID;
    }

    //-----------------------------//
    //---------- SETTERS ----------//
    /**
     * Sets the person ID of the account.
     *
     * @param id a person ID
     */
    public void setPersonID(int id) {
        personID = id;
    }

    /**
     * Sets the accounts username.
     *
     * @param str a username
     */
    public void setUsername(String str) {
        username = str;
    }

    /**
     * Sets the accounts password.
     *
     * @param password a password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the ID number of the accounts first security question.
     *
     * @param id a question ID
     */
    public void setQuestion1(int id) {
        question1 = id;
    }

    /**
     * Sets the ID number of the accounts second security question.
     *
     * @param id a question ID
     */
    public void setQuestion2(int id) {
        question2 = id;
    }

    /**
     * Sets the ID number of the accounts third security question.
     *
     * @param id a question ID
     */
    public void setQuestion3(int id) {
        question3 = id;
    }

    /**
     * Sets the answer to the first security question.
     *
     * @param str an answer
     */
    public void setAnswer1(String str) {
        answer1 = str;
    }

    /**
     * Sets the answer to the second security question.
     *
     * @param str an answer
     */
    public void setAnswer2(String str) {
        answer2 = str;

    }

    /**
     * Sets the answer to the third security question.
     *
     * @param str an answer
     */
    public void setAnswer3(String str) {
        answer3 = str;
    }

    /**
     * Sets the accounts position (type).
     *
     * @param p a position ID
     */
    public void setPosition(int p) {
        position = p;
    }

    /**
     * Disables or enables the account.
     *
     * @param bool true to disable the account, false to enable
     */
    public void setAccountDisabled(boolean bool) {
        isAccountDisabled = bool;
    }

    /**
     * Sets the Person object containing contact information of the account
     * holder.
     *
     * @param p the Person who owns the account
     */
    public void setPerson(Person p) {
        person = p;
    }

    /**
     * Encodes the password for security.
     *
     * @param s the password
     * @return the password encoded
     */
    public static String encodePassword(String s) {
        char[] array = s.toCharArray();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < key; j++) {
                array[i]++;
                //allows ASCII characters 33 - 126
                if (array[i] > 126) {
                    array[i] = '!';
                }
            }
        }
        return new String(array);
    }

    /**
     * Decodes the password.
     *
     * @param s the password
     * @return the decoded password
     */
    private String decodePassword(String s) {
        char[] array = s.toCharArray();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < key; j++) {
                array[i]--;
                //allows ASCII characters 33 - 116
                if (array[i] < 33) {
                    array[i] = '~';
                }
            }
        }
        return new String(array);
    }

    /**
     * Creates an SQL insert statement for this User object so it can be
     * uploaded to the database table User.
     *
     * @return a String SQL insert statement
     */
    public String createInsertStatement() {
        return "INSERT INTO Login (PersonID, Username, Password, Question1, Question2, Question3, "
                + "Answer1, Answer2, Answer3, PositionID) VALUES ("
                + personID + ", '" + username + "', '" + password + "', " + question1 + ", "
                + question2 + ", " + question3 + ", '" + answer1 + "', '" + answer2 + "', '" + answer3 + "', "
                + position + ")";
    }

}
