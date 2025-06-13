package quickchat.core;

import java.util.regex.Pattern; // AI Assisted Regex checker

/**
 * A class responsible for handling user registration in the QuickChat application.
 * Validates and stores user data (username, password, cellphone number, first name, last name)
 * according to POE Part 1 requirements, using setter methods with encapsulated validation logic.
 * * @author Tshedimosetso Wowana
 * @version 1.1.0 // Version updated for new registerUser behavior
 * @since 2025-03-01
 */
public class Registration
{
    private String storedUserName;
    private String storedPassword;
    private String storedCellPhoneNumber;
    private String storedFirstName;
    private String storedLastName;

    // Constants for messages as per POE document and common practice
    private static final String USERNAME_SUCCESS_MESSAGE = "Username successfully captured.";
    private static final String USERNAME_ERROR_MESSAGE = "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
    private static final String PASSWORD_SUCCESS_MESSAGE = "Password successfully captured.";
    private static final String PASSWORD_ERROR_MESSAGE = "Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
    private static final String CELLPHONE_SUCCESS_MESSAGE = "Cell phone number successfully added."; // As per POE table
    private static final String CELLPHONE_ERROR_MESSAGE = "Cellphone number is incorrectly formatted or does not contain an international code, please correct the number and try again."; // Adapted from POE
    private static final String FIRST_NAME_SUCCESS_MESSAGE = "First name successfully captured.";
    private static final String FIRST_NAME_ERROR_MESSAGE = "First name is invalid, please ensure it is not empty and contains only letters.";
    private static final String LAST_NAME_SUCCESS_MESSAGE = "Last name successfully captured.";
    private static final String LAST_NAME_ERROR_MESSAGE = "Last name is invalid, please ensure it is not empty and contains only letters.";
    static final String REGISTRATION_SUCCESSFUL_OVERALL = "Registration successful";
    static final String REGISTRATION_ABORTED_OVERALL = "Registration aborted - please check the messages above.";


    /**
     * Registers a user by calling setter methods for validation.
     * Returns a 6-element String array:
     * Index 0: Username validation message (success/error)
     * Index 1: Password validation message (success/error)
     * Index 2: Cellphone validation message (success/error)
     * Index 3: First name validation message (success/error)
     * Index 4: Last name validation message (success/error)
     * Index 5: Overall registration status ("Registration successful" or "Registration aborted...")
     * @param newUserName the username to register
     * @param newPassword the password to register
     * @param newCellPhoneNumber the cellphone number to register
     * @param newFirstName the first name to register
     * @param newLastName the last name to register
     * @return a 6-element array of Strings with detailed feedback and overall status.
     */
    public String[] registerUser(String newUserName, String newPassword, String newCellPhoneNumber, String newFirstName, String newLastName) {
        String[] feedbackMessages = new String[6];
        boolean allFieldsValid = true;

        // Validate Username
        if (setUserName(newUserName)) {
            feedbackMessages[0] = USERNAME_SUCCESS_MESSAGE;
        } else {
            feedbackMessages[0] = USERNAME_ERROR_MESSAGE;
            allFieldsValid = false;
        }

        // Validate Password
        if (setPassword(newPassword)) {
            feedbackMessages[1] = PASSWORD_SUCCESS_MESSAGE;
        } else {
            feedbackMessages[1] = PASSWORD_ERROR_MESSAGE;
            allFieldsValid = false;
        }

        // Validate Cellphone Number
        if (setCellPhoneNumber(newCellPhoneNumber)) {
            feedbackMessages[2] = CELLPHONE_SUCCESS_MESSAGE;
        } else {
            feedbackMessages[2] = CELLPHONE_ERROR_MESSAGE;
            allFieldsValid = false;
        }

        // Validate First Name
        if (setFirstName(newFirstName)) {
            feedbackMessages[3] = FIRST_NAME_SUCCESS_MESSAGE;
        } else {
            feedbackMessages[3] = FIRST_NAME_ERROR_MESSAGE;
            allFieldsValid = false;
        }

        // Validate Last Name
        if (setLastName(newLastName)) {
            feedbackMessages[4] = LAST_NAME_SUCCESS_MESSAGE;
        } else {
            feedbackMessages[4] = LAST_NAME_ERROR_MESSAGE;
            allFieldsValid = false;
        }

        // Set overall registration status message
        if (allFieldsValid) {
            feedbackMessages[5] = REGISTRATION_SUCCESSFUL_OVERALL;
        } else {
            feedbackMessages[5] = REGISTRATION_ABORTED_OVERALL;
        }

        return feedbackMessages;
    }

    // Getter methods remain unchanged
    public String getUserName() {
        return this.storedUserName;
    }

    public String getPassword() {
        return this.storedPassword;
    }

    public String getCellPhoneNumber() {
        return this.storedCellPhoneNumber;
    }

    public String getFirstName() {
        return this.storedFirstName;
    }

    public String getLastName() {
        return this.storedLastName;
    }

    // Setter and validation methods remain unchanged
    public boolean setUserName(String userName) {
        if (checkUserName(userName)) {
            this.storedUserName = userName;
            return true;
        } else {
            this.storedUserName = null; // Clear if invalid
            return false;
        }
    }

    public boolean setPassword(String password) {
        if (checkPasswordComplexity(password)) {
            this.storedPassword = password;
            return true;
        } else {
            this.storedPassword = null; // Clear if invalid
            return false;
        }
    }

    public boolean setCellPhoneNumber(String cellPhoneNumber) {
        if (checkCellPhoneNumber(cellPhoneNumber)) {
            this.storedCellPhoneNumber = cellPhoneNumber;
            return true;
        } else {
            this.storedCellPhoneNumber = null; // Clear if invalid
            return false;
        }
    }

    public boolean setFirstName(String firstName) {
        if (firstName != null && !firstName.trim().isEmpty() && firstName.matches("^[a-zA-Z]+$")) {
            this.storedFirstName = firstName;
            return true;
        }
        this.storedFirstName = null; // Clear if invalid
        return false;
    }

    public boolean setLastName(String lastName) {
        if (lastName != null && !lastName.trim().isEmpty() && lastName.matches("^[a-zA-Z]+$")) {
            this.storedLastName = lastName;
            return true;
        }
        this.storedLastName = null; // Clear if invalid
        return false;
    }
    
    // Private validation helper methods remain unchanged
    private boolean checkUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            return false;
        }
        final int MAX_USERNAME_LENGTH = 5;
        return userName.contains("_") && userName.length() <= MAX_USERNAME_LENGTH;
    }

    private boolean checkPasswordComplexity(String password) {
        if (password == null || password.isBlank()) {
            return false;
        }
        final int MIN_PASSWORD_LENGTH = 8;
        // final int MAX_PASSWORD_LENGTH = 32; // Max length not strictly required by POE but good practice
        // boolean passwordLengthAcceptable = password.length() >= MIN_PASSWORD_LENGTH && password.length() <= MAX_PASSWORD_LENGTH;
        // For POE, just min length
        boolean passwordLengthAcceptable = password.length() >= MIN_PASSWORD_LENGTH;


        boolean passwordHasCapital = false, passwordHasDigit = false, passwordHasSpecialChar = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                passwordHasCapital = true;
            } else if (Character.isDigit(c)) {
                passwordHasDigit = true;
            } else if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c)) { // Consider !Character.isWhitespace if spaces are not special chars
                passwordHasSpecialChar = true;
            }
        }
        return passwordLengthAcceptable && passwordHasCapital && passwordHasDigit && passwordHasSpecialChar;
    }

    private boolean checkCellPhoneNumber(String cellPhoneNumber) {
        if (cellPhoneNumber == null || cellPhoneNumber.isBlank()) {
            return false;
        }
        // Regex from POE Part 1 requirement (page 7)
        // "international country code followed by the number, which is no more than ten characters long."
        // The example +27838968976 has +27 (3 chars) + 9 digits = 12 chars total.
        // The regex "^\\+27[0-9]{9}$" reflects this.
        String regexChecker = "^\\+27[0-9]{9}$";
        return Pattern.matches(regexChecker, cellPhoneNumber);
    }
}