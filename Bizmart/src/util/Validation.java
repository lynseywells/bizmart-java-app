package util;

import java.util.regex.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Validation is utilized by the Bizmart application to ensure data entered by
 * users is valid before allowing the information to be uploaded to the
 * database.
 */
public abstract class Validation {

    /**
     * Checks if the passed string contains only numbers.
     *
     * @param s the string to check
     * @return true if the string is numeric, false otherwise
     */
    public static boolean isNumeric(String s) {
        try {
            @SuppressWarnings("unused")
            long num = Long.parseLong(s);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the passed character is one of the special characters allowed
     * for account passwords.
     * <p>
     * Allowed special characters: ! # $ % ^ &amp; ( ) @ *
     *
     * @param c the character to check
     * @return true if the char is a special char, false otherwise
     */
    public static boolean isSpecialChar(char c) {
        return c == '!' || c == '#' || c == '$' || c == '%' || c == '&'
                || c == ')' || c == '(' || c == '@' || c == '*' || c == '^';
    }

    /**
     * Checks if a passed character array contains at least one of the special
     * characters allowed for account passwords.
     * <p>
     * Allowed special characters: ! # $ % ^ &amp; ( ) @ *
     *
     * @param array the password to check
     * @return true if the password contains a special char, false otherwise
     */
    public static boolean hasSpecialChar(char[] array) {
        for (char c : array) {
            //allowed characters: ! # $ % & ( ) @ * ^
            if (isSpecialChar(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a passed character array contains at least one alphabetic
     * character.
     *
     * @param array the array to check
     * @return true if the array contains an alphabetic char, false otherwise
     */
    public static boolean hasAlphaChar(char[] array) {
        for (char c : array) {
            if (Character.isAlphabetic(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a passed character array contains at least one number.
     *
     * @param array the array to check
     * @return true if the array contains a number, false otherwise
     */
    public static boolean hasDigitChar(char[] array) {
        for (char c : array) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the passed password meets all of the password requirements
     * <p>
     * Password must be between 8 and 20 characters, and it must contain an
     * alphabetic char, a number, and a valid special character.
     *
     * @param array the password to check
     * @return true if the password is valid, false otherwise
     */
    public static boolean isValidPassword(char[] array) {
        if (array.length >= 8 && array.length <= 20) {
            if (hasAlphaChar(array) && hasDigitChar(array) && hasSpecialChar(array)) {
                //search the password for invalid characters
                for (char c : array) {
                    if (!Character.isAlphabetic(c) && !Character.isDigit(c)) {
                        //allowed characters: ! # $ % & ( ) @ * ^
                        if (!isSpecialChar(c)) {
                            return false;
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Checks if the passed string contains only alphabetic characters.
     *
     * @param str the string to check
     * @return true if the string is alphabetic, false otherwise
     */
    public static boolean isAlphabetic(String str) {
        char[] array = str.toCharArray();
        for (char c : array) {
            if (!Character.isAlphabetic(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the passed email is valid based on a regex pattern.
     * <p>
     * Email must contain a username of at least one alphabetic or numeric
     * character, an "@" sign, and a domain. Email can only contain alphabetic
     * and numeric characters, it cannot contain special characters.
     *
     * @param email the email to check
     * @return true if the email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile("^[a-zA-Z[0-9]]+@[a-zA-Z[0-9]]+[.][a-z]+$");
        Matcher matcher = pattern.matcher(email);
        if (email.length() <= 100 && matcher.find()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the passed phone number is valid.
     * <p>
     * Phone number must be between 7 and 15 numeric digits.
     *
     * @param phone the phone number to check
     * @return true if the phone number is valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (Validation.isNumeric(phone) && phone.length() >= 7 && phone.length() <= 15) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the passed US zip code is valid.
     * <p>
     * Zip code must be in the format ##### or #####-####.
     *
     * @param zip the zip to check
     * @return true if the zip is valid, false otherwise
     */
    public static boolean isValidZip(String zip) {
        Pattern pattern = Pattern.compile("[0-9]{5}");
        Matcher matcher = pattern.matcher(zip);
        if (matcher.find() && zip.length() == 5) {
            return true;
        } else {
            pattern = Pattern.compile("[0-9]{5}\u002D[0-9]{4}");
            matcher = pattern.matcher(zip);
            if (matcher.find() && zip.length() == 10) {
                return true;
            }
            return false;
        }
    }

    /**
     * Checks if the passed date string is a valid date.
     * <p>
     * Date should be in the format "MM/YY" or "MM/YYYY".
     *
     * @param date a string date
     * @return true if the date is valid, false otherwise
     */
    public static boolean isValidDate(String date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/yy");
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("MM/yyyy");
        // try to parse the date in one of two formats
        try {
            YearMonth ym = YearMonth.parse(date, dtf);
        } catch (Exception ex) {
            try {
                YearMonth ym2 = YearMonth.parse(date, dtf2);
            } catch (Exception exc) {
                return false;

            }
        }
        // only returns true if the date can be parsed
        return true;
    }

    /**
     * Checks if the passed string is a valid credit card number.
     * <p>
     * Credit card numbers must be 16 digits long.
     *
     * @param num the card number to check
     * @return true if the number is valid, false otherwise
     */
    public static boolean isValidCardNumber(String num) {
        // VISA/MasterCard format
        Pattern p = Pattern.compile("[0-9]{16}");
        Matcher m = p.matcher(num);
        return m.find();
    }

    /**
     * Checks if the passed username is valid.
     * <p>
     * Usernames must be between 8 and 20 characters, cannot start with a
     * number, and cannot contain special characters.
     *
     * @param username the username to check
     * @return true if the username is valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        char[] array = username.toCharArray();
        if (Character.isDigit(array[0])) {
            return false;
        }
        if (hasSpecialChar(array)) {
            return false;
        }
        if (username.length() < 8 || username.length() > 20) {
            return false;
        }
        return true;
    }
}
