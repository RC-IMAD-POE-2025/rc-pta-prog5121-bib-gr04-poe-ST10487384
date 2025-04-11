package quickchat;


import java.util.ArrayList; // I used it for dynamic success/error messages handling
import java.util.regex.Pattern; // Used for regex validation in checkCellPhoneNumber (Grok 3 assisted)

/**
 * A class responsible for handling user registration in the QuickChat application.
 * Validates and stores user data (username, password, cellphone number, first name, last name)
 * according to POE Part 1 requirements, using setter methods with encapsulated validation logic.
 * 
 * @author Tshedimosetso Wowana
 * @version 1.0.0
 * @since 2025-04-01
 */
public class Registration
{
    /** The stored username, validated to contain an underscore and be max 5 characters. */
    private String storedUserName;
    
    /** The stored password, validated for complexity (8+ chars, capital, number, special char). */
    private String storedPassword;
    
    /** The stored cellphone number, validated to start with "+27" followed by 9 digits. */
    private String storedCellPhoneNumber;
    
    /** The stored first name, validated to contain only letters and be non-empty. */
    private String storedFirstName;
    
    /** The stored last name, validated to contain only letters and be non-empty. */
    private String storedLastName;
    
    /**
     * Registers a user by calling the setter methods which contain the validation logic.
     * 
     * The setter methods for username, password, and cellphone number are responsible for validating the 
     * individual inputs based on the POE criteria. These methods return a Boolean indicating whether the 
     * input is valid or not. If an input is invalid, the corresponding error message is generated in the 
     * `registerUser` method.
     * 
     * The `registerUser` method accepts five arguments: username, password, cellphone number, first name,
     * and last name. It invokes the respective setter methods that check if the inputs meet the required 
     * format. If all inputs are valid, the corresponding success messages are returned. And If any input 
     * is invalid, the corresponding error messages are returned, providing the user with feedback on 
     * what needs to be corrected.
     * 
     * @param newUserName the username to register
     * @param newPassword the password to register
     * @param newCellPhoneNumber the cellphone number to register
     * @param newFirstName the first name to register
     * @param newLastName the last name to register
     * @return an array of 6 Strings: 5 field-specific messages + 1 final status
     */
    public String[] registerUser(String newUserName, String newPassword, String newCellPhoneNumber, String newFirstName, String newLastName)
    {
        ArrayList<String> messages = new ArrayList<>(); // Collect all field messages
        
        // Validate and store username
        if (setUserName(newUserName))
        {
            messages.add("Username successfully captured");
        }
        else
        {
            messages.add("Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.");
        }
        
        // Validate and store password
        if (setPassword(newPassword))
        {
            messages.add("Password successfully captured");
        }
        else
        {
            messages.add("Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
        }
        
        // Validate and store cellphone number
        if (setCellPhoneNumber(newCellPhoneNumber))
        {
            messages.add("Cellphone number successfully captured");
        }
        else
        {
            messages.add("Cellphone number is incorrectly formatted or does not contain an international code, please correct the number and try again.");
        }

        // Validate and store first name
        if (setFirstName(newFirstName))
        {
            messages.add("First name successfully captured");
        }
        else
        {
            messages.add("First name is invalid, please ensure it is not empty and contains only letters.");
        }

        // Validate and store last name
        if (setLastName(newLastName))
        {
            messages.add("Last name successfully captured");
        }
        else
        {
            messages.add("Last name is invalid, please ensure it is not empty and contains only letters.");
        }
        
        // Determine final status using getters (all fields must be non-null for success)
        if ((((
                   this.storedUserName == null 
                || this.storedPassword == null) 
                || this.storedCellPhoneNumber == null) 
                || this.storedFirstName == null) 
                || this.storedLastName == null)
        {
            messages.add("Registration Failed");
        }
        else
        {
            messages.add("Registration Successful");
        }
        
        return messages.toArray(String[]::new);
    }

    /**
     * Retrieves the stored username.
     * 
     * @return the current stored username, or null if not set
     */
    public String getUserName() 
    {
        return this.storedUserName;
    }
    
    /**
     * Retrieves the stored password.
     * 
     * @return the current stored password, or null if not set
     */
    public String getPassword() 
    {
        return this.storedPassword;
    } 
    
    /**
     * Retrieves the stored cellphone number.
     * 
     * @return the current stored cellphone number, or null if not set
     */
    public String getCellPhoneNumber() 
    {
        return this.storedCellPhoneNumber;
    }
    
    /**
     * Sets the username if it is correctly formatted.
     *
     * @param userName the username to set
     * @return true if the username is valid and set, false otherwise
     */
    public boolean setUserName(String userName) 
    {
        if (checkUserName(userName)) 
        {
            this.storedUserName = userName;
            return true;
        } 
        else 
        {
            return false;
        }
    }
    
    /**
     * Sets the password if it meets complexity requirements.
     *
     * @param password the password to set
     * @return true if the password is valid and set, false otherwise
     */
    public boolean setPassword(String password) 
    {
        if (checkPasswordComplexity(password)) 
        {
            this.storedPassword = password;
            return true;
        } 
        else 
        {
            return false;
        }
    }
    
    /**
     * Sets the cellphone number ONLY if it is correctly formatted.
     *
     * @param cellPhoneNumber the cellphone number to set
     * @return true if the cellphone number is valid and set, false otherwise
     */
    public boolean setCellPhoneNumber(String cellPhoneNumber) 
    {
        if (checkCellPhoneNumber(cellPhoneNumber))
        {
            this.storedCellPhoneNumber = cellPhoneNumber;
            return true;
        } 
        else 
        {
            return false;
        }
    }
    
    /**
     * Sets the first name if it contains only letters and is non-empty.
     * 
     * @param firstName the first name to set
     * @return true if the first name is valid and set, false otherwise
     */
    public boolean setFirstName(String firstName)
    {
        if (firstName != null && !firstName.trim().isEmpty() && firstName.matches("^[a-zA-Z]+$"))
        {
            this.storedFirstName = firstName;
            return true;
        }
        return false;
    }

    /**
     * Sets the last name if it contains only letters and is non-empty.
     * 
     * @param lastName the last name to set
     * @return true if the last name is valid and set, false otherwise
     */
    public boolean setLastName(String lastName)
    {
        if (lastName != null && !lastName.trim().isEmpty() && lastName.matches("^[a-zA-Z]+$"))
        {
            this.storedLastName = lastName;
            return true;
        }
        return false;
    }

    /**
     * Retrieves the stored first name.
     * 
     * @return the current stored first name, or null if not set
     */
    public String getFirstName()
    {
        return this.storedFirstName;
    }
    
    /**
     * Retrieves the stored last name.
     * 
     * @return the current stored last name, or null if not set
     */
    public String getLastName()
    {
        return this.storedLastName;
    }
    
    // -------------------------------------------------------------------------------------------------
    // THE FOLLOWING METHODS WERE DECLARED PRIVATE... 
    // I applied the principle of encapsulation (Farrell, 2020) , to prevent the core logic from being tampered
    // with when an instance of this Registration class is invoked.
    // These methods will be called by Public Setter methods above ONLY.
    
    /**
     * Checks if the username contains an underscore and is no more than 5 characters long.
     * Enforces POE requirements for username format.
     *
     * @param userName the username to check
     * @return true if the username is valid, false otherwise
     */
    private boolean checkUserName(String userName)
    {
        if (userName == null || userName.isBlank())
        {
            return false;
        }
        
        final int MAX_USERNAME_LENGTH = 5; // The POE explicitly asked for this but not a minimum length
        
        return userName.contains("_") && userName.length() <= MAX_USERNAME_LENGTH;
        // The underscore character is enforced for all usernames as per the POE instruction
    }
            
    /**
     * Checks if the password meets complexity requirements: 8-32 characters, with at least one capital letter,
     * one digit, and one special character. Uses a for-each loop inspired by C# style for validation.
     *
     * @param password the password to check
     * @return true if the password is valid, false otherwise or null
     */
    private boolean checkPasswordComplexity(String password)
    {
        if (password == null || password.isBlank())
        {
            return false;
        }
        
        // It is good practice to use constants instead of hardcoded literals for readability 
        final int MIN_PASSWORD_LENGTH = 8; // This isn't required by POE but It is good practice to enforce.
        final int MAX_PASSWORD_LENGTH = 32; // 32 is reasonable to prevent memory hog issues
        
        boolean passwordLengthAcceptable 
                = password.length() >= MIN_PASSWORD_LENGTH 
                && password.length() <= MAX_PASSWORD_LENGTH;
        
        boolean passwordHasCapital = false, passwordHasDigit = false, passwordHasSpecialChar = false;
        
        /**
         * This for loop is an elegant approach because it will iterate through every letter inside the string
         * I do not have to worry about the password length here, this is similar to the for-each loop in C#
         * This loop will check for Capital letters, numbers & special characters
         */
        for (char c : password.toCharArray())
        {
            if (Character.isUpperCase(c)) 
            {
                passwordHasCapital = true;
            }
            else if (Character.isDigit(c))
            {
                passwordHasDigit = true;
            }
            else if (!Character.isLetterOrDigit(c))
            {
                passwordHasSpecialChar = true;
            }
        }   
        
        return passwordLengthAcceptable && passwordHasCapital && passwordHasDigit && passwordHasSpecialChar;     
    }
    
    /**
     * Checks if the cellphone number starts with "+27" followed by exactly 9 digits.
     * Uses regex pattern assisted by Grok 3 from xAI.
     * 
     * @param cellPhoneNumber the cellphone number to check
     * @return true if the cellphone number is valid, false otherwise (includes null/blank checks)
     * @see xAI (2025) Grok 3 AI Assistant, xAI, Available at: [https://x.ai/] (Accessed: 7 April 2025).
     */
    private boolean checkCellPhoneNumber(String cellPhoneNumber) 
    {
        // Guard clause: return false for null or blank input to prevent crashes
        if (cellPhoneNumber == null || cellPhoneNumber.isBlank()) 
        {
            return false;
        }
        
        // Regex pattern from Grok 3 ensures +27 prefix and exactly 9 digits
        String regexChecker = "^\\+27[0-9]{9}$"; // Source: xAI (2025)
        return Pattern.matches(regexChecker, cellPhoneNumber);
    }
}