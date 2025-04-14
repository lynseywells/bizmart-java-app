package util;

import java.util.regex.*;

/**
 * Person is utilized by the Bizmart application to store contact information
 * about customers and employees. Person is used to upload contact information
 * to the database when an account is created, or to edit an existing accounts
 * contact information. It is also used when placing an order to add a customer
 * and (if applicable) an employees information to the order/receipt.
 */
public class Person {

    /**
     * A persons ID number in the database.
     */
    private int personID;

    /**
     * First name. Limited to 50 characters.
     */
    private String firstName;

    /**
     * Optional middle name. Limited to 50 characters.
     */
    private String middleName;

    /**
     * Last name. Limited to 50 characters.
     */
    private String lastName;

    /**
     * Optional suffix such as "Jr", "Sr", "II", "M.D.", or "PhD". Limited to 10
     * characters.
     */
    private String suffix;

    /**
     * Address line 1 for the person's street address. Limited to 100
     * characters.
     */
    private String addressLine1;

    /**
     * Optional address line 2 for a PO box or apartment/unit number. Limited to
     * 100 characters.
     */
    private String addressLine2;

    /**
     * Optional address line 3 for additional information. Limited to 100
     * characters.
     */
    private String addressLine3;

    /**
     * City. Limited to 50 characters.
     */
    private String city;

    /**
     * Zip code. Limited to 10 characters. Must be in the format ##### or
     * #####-####.
     */
    private String zip;

    /**
     * Two character state.
     */
    private String state;

    /**
     * Email address. Limited to 100 characters.
     */
    private String email;

    /**
     * Optional primary phone number. Limited to 15 characters.
     */
    private String phone1;

    /**
     * Optional secondary phone number. Limited to 15 characters.
     */
    private String phone2;

    /**
     * Person constructor used to organize data for uploading a new Person into
     * the database.
     *
     * @param firstName a first name
     * @param lastName a last name
     * @param addressLine1 a street address
     * @param city a city
     * @param zip a zip code
     * @param state a 2 character state
     * @param email an email address
     */
    public Person(String firstName, String lastName, String addressLine1, String city, String zip, String state, String email) {
        setFirstName(firstName);
        setLastName(lastName);
        setAddressLine1(addressLine1);
        setCity(city);
        setZip(zip);
        setState(state);
        setEmail(email);
    }

    /**
     * Person constructor used to organize Person data retrieved from the
     * database for use in the Bizmart program.
     *
     * @param personID an id number in the database
     * @param firstName a first name
     * @param lastName a last name
     * @param addressLine1 a street address
     * @param city a city
     * @param zip a zip code
     * @param state a 2 character state
     * @param email an email address
     */
    public Person(int personID, String firstName, String lastName, String addressLine1, String city, String zip, String state, String email) {
        setFirstName(firstName);
        setLastName(lastName);
        setAddressLine1(addressLine1);
        setCity(city);
        setZip(zip);
        setState(state);
        setEmail(email);
        this.personID = personID;
    }

    //-----------------------------//
    //---------- GETTERS ----------//
    /**
     * Gets the person's ID number.
     *
     * @return an ID number
     */
    public int getPersonID() {
        return personID;
    }

    /**
     * Gets the person's first name.
     *
     * @return a first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the person's middle name. Returns null if no middle name is set.
     *
     * @return a middle name
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Gets the person's last name.
     *
     * @return a last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the person's name suffix. Returns null if no suffix is set.
     *
     * @return a suffix
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Gets the person's street address.
     *
     * @return a street address
     */
    public String getAddressLine1() {
        return addressLine1;
    }

    /**
     * Gets the person's address line 2. Returns null if address line 2 wasn't
     * set.
     *
     * @return additional address details
     */
    public String getAddressLine2() {
        return addressLine2;
    }

    /**
     * Gets the person's address line 3. Returns null if address line 3 wasn't
     * set.
     *
     * @return additional address details
     */
    public String getAddressLine3() {
        return addressLine3;
    }

    /**
     * Gets the person's city of residence.
     *
     * @return a city
     */
    public String getCity() {
        return city;
    }

    /**
     * Gets the person's zip code.
     *
     * @return a zip code
     */
    public String getZip() {
        return zip;
    }

    /**
     * Gets the state the person lives in.
     *
     * @return an abbreviated US state
     */
    public String getState() {
        return state;
    }

    /**
     * Gets the person's email address.
     *
     * @return an email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the person's primary phone number. Returns null if phone 1 wasn't
     * set.
     *
     * @return a phone number
     */
    public String getPhone1() {
        return phone1;
    }

    /**
     * Gets the person's secondary phone number. Returns null if phone 2 wasn't
     * set.
     *
     * @return a phone number
     */
    public String getPhone2() {
        return phone2;
    }

    //-----------------------------//
    //---------- SETTERS ----------//
    /**
     * Sets the person's first name. Name cannot be longer than 50 characters.
     *
     * @param name a first name
     */
    public void setFirstName(String name) {
        if (name.length() <= 50) {
            firstName = name.toUpperCase();
        }
    }

    /**
     * Sets the person's middle name. Name cannot be longer than 50 characters.
     *
     * @param name a middle name
     */
    public void setMiddleName(String name) {
        if (name.length() <= 50) {
            middleName = name.toUpperCase();
        }
    }

    /**
     * Sets the person's last name. Name cannot be longer than 50 characters.
     *
     * @param name a last name
     */
    public void setLastName(String name) {
        if (name.length() <= 50) {
            lastName = name.toUpperCase();
        }
    }

    /**
     * Sets the person's name suffix (i.e "Jr", "Sr", "II", "M.D.", or "PhD").
     * Suffix cannot be longer than 10 characters.
     *
     * @param name a suffix
     */
    public void setSuffix(String name) {
        if (name.length() <= 10) {
            lastName = name.toUpperCase();
        }
    }

    /**
     * Sets the person's address line 1, or street address. Address line 1
     * cannot be longer than 100 characters.
     *
     * @param address a street address
     */
    public void setAddressLine1(String address) {
        if (address.length() <= 100) {
            addressLine1 = address.toUpperCase();
        }
    }

    /**
     * Sets the person's address line 2 for additional address details (i.e. a
     * PO box or apartment number). Address line 2 cannot be longer than 100
     * characters.
     *
     * @param address additional address details
     */
    public void setAddressLine2(String address) {
        if (address.length() <= 100) {
            addressLine2 = address.toUpperCase();
        }
    }

    /**
     * Sets the person's address line 3 for any additional information. Address
     * line 3 cannot be longer than 100 characters.
     *
     * @param address additional address details
     */
    public void setAddressLine3(String address) {
        if (address.length() <= 100) {
            addressLine3 = address.toUpperCase();
        }
    }

    /**
     * Sets the person's city of residence. City cannot be longer than 50
     * characters.
     *
     * @param city a city name
     */
    public void setCity(String city) {
        if (city.length() <= 50) {
            this.city = city.toUpperCase();
        }
    }

    /**
     * Sets the person's zip code. Zip code cannot be longer than 10 characters,
     * and must be in the format ##### or #####-####.
     *
     * @param zip a zip code
     */
    public void setZip(String zip) {
        if (Validation.isValidZip(zip)) {
            this.zip = zip;
        }
    }

    /**
     * Sets the state the person lives in. State must be a 2 character
     * abbreviation.
     *
     * @param state a US state
     */
    public void setState(String state) {
        if (state.length() == 2) {
            String validStates = "AL|AK|AZ|AR|CA|CO|CT|DE|FL|GA|HI|ID|IL|IN|IA|KS|KY|LA|ME|MD|MA|MI|"
                    + "MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|OH|OK|OR|PA|RI|SC|SD|TN|TX|UT|"
                    + "VT|VA|WA|WV|WI|WY";
            Pattern pattern = Pattern.compile(validStates, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(state);

            if (matcher.find()) {
                String st = state.toUpperCase();
                this.state = st;
            }
        }
    }

    /**
     * Sets the person's email address. Email cannot be longer than 100
     * characters, and must be in a valid email format.
     *
     * @param email an email address
     */
    public void setEmail(String email) {
        if (Validation.isValidEmail(email)) {
            this.email = email;
        }
    }

    /**
     * Sets the person's primary phone number. Phone 1 cannot be longer than 15
     * characters.
     *
     * @param phone a phone number
     */
    public void setPhone1(String phone) {
        if (Validation.isValidPhone(phone)) {
            phone1 = phone;
        }
    }

    /**
     * Sets the person's secondary phone number. Phone 2 cannot be longer than
     * 15 characters.
     *
     * @param phone a phone number
     */
    public void setPhone2(String phone) {
        if (Validation.isValidPhone(phone)) {
            phone2 = phone;
        }
    }

    /**
     * Creates an SQL insert statement for this Person object so it can be
     * uploaded to the database table Person.
     *
     * @return a String SQL insert statement
     */
    public String createInsertStatement() {
        String columns = "INSERT INTO Person (FirstName";
        String values = " VALUES ('" + firstName + "'";

        if (middleName != null) {
            columns += ", MiddleName";
            values += ", '" + middleName + "'";
        }
        columns += ", LastName";
        values += ", '" + lastName + "'";

        if (suffix != null) {
            columns += ", Suffix";
            values += ", '" + suffix + "'";
        }
        columns += ", AddressLine1";
        values += ", '" + addressLine1 + "'";

        if (addressLine2 != null) {
            columns += ", AddressLine2";
            values += ", '" + addressLine2 + "'";
        }

        if (addressLine3 != null) {
            columns += ", AddressLine3";
            values += ", '" + addressLine3 + "'";
        }

        columns += ", City, State, Zip, Email";
        values += ", '" + city + "', '" + state + "', '" + zip + "', '" + email + "'";

        if (phone1 != null) {
            columns += ", Phone1";
            values += ", '" + phone1 + "'";
        }

        if (phone2 != null) {
            columns += ", Phone2";
            values += ", '" + phone2 + "'";
        }

        columns += ")";
        values += ")";

        return columns + values;
    }
}
