package quickchat.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the Registration class in the QuickChat application.
 * Tests the registration functionality, including input validation, error messaging,
 * and setter/getter methods, using JUnit5 to ensure compliance with Part 1 POE requirements.
 * 
 * @author Tshedimosetso Wowana
 * @version 1.0
 * @since 2025-04-09
 */
public class RegistrationTest 
{
    /** The Registration instance under test. */
    private Registration registration;

    // Expected messages as constants
    /** Success message returned when registration is valid. */
    private static final String SUCCESS_REGISTRATION_MESSAGE = "You have been successfully registered";
    
    /** Error message for invalid username format. */
    private static final String USERNAME_ERROR_MESSAGE = "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
    
    /** Error message for invalid password complexity. */
    private static final String PASSWORD_ERROR_MESSAGE = "Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
    
    /** Error message for invalid cellphone number format. */
    private static final String CELL_PHONE_ERROR_MESSAGE = "Cellphone number is incorrectly formatted or does not contain an international code, please correct the number and try again.";
    
    /** Error message for invalid first name format. */
    private static final String FIRST_NAME_ERROR_MESSAGE = "First name is invalid, please ensure it is not empty and contains only letters.";
    
    /** Error message for invalid last name format. */
    private static final String LAST_NAME_ERROR_MESSAGE = "Last name is invalid, please ensure it is not empty and contains only letters.";

    // Test data as constants
    /** Valid username for testing (max 5 chars with underscore). */
    private static final String VALID_USERNAME = "kyl_1";
    
    /** Valid password for testing (8+ chars, capital, number, special char). */
    private static final String VALID_PASSWORD = "Ch&&sec@ke99!";
    
    /** Valid cellphone number for testing (+27 followed by 9 digits). */
    private static final String VALID_CELL_PHONE_NUMBER = "+27838968976";
    
    /** Valid first name for testing (letters only). */
    private static final String VALID_FIRST_NAME = "Kyle";
    
    /** Valid last name for testing (letters only). */
    private static final String VALID_LAST_NAME = "Smith";

    // Invalid test data as constants
    /** Invalid username for testing (exceeds format rules). */
    private static final String INVALID_USERNAME = "kyle!!!!!!!";
    
    /** Username without underscore for testing invalid format. */
    private static final String USERNAME_WITHOUT_UNDERSCORE = "kyl1";
    
    /** Invalid password for testing (lacks complexity). */
    private static final String INVALID_PASSWORD = "password";
    
    /** Invalid cellphone number for testing (wrong format). */
    private static final String INVALID_CELL_PHONE_NUMBER = "123456789";
    
    /** Invalid first name for testing (contains numbers). */
    private static final String INVALID_FIRST_NAME = "Kyle123"; // Numbers not allowed
    
    /** Invalid last name for testing (empty string). */
    private static final String INVALID_LAST_NAME = "";        // Empty not allowed

    /**
     * Sets up the test environment before each test method.
     * Creates a new Registration instance to ensure a clean state for testing.
     */
    @BeforeEach
    public void setUp() 
    {
        registration = new Registration();
    }

    // Registration tests via registerUser method
    /**
     * Tests that registerUser returns a success message when all input values are valid.
     * Verifies that the Registration class accepts correct data and returns a single success message.
     */
    @Test
    public void registerUser_ValidValues_ReturnsSuccessMessage() 
    {
        String[] result = registration.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE_NUMBER, VALID_FIRST_NAME, VALID_LAST_NAME);
        assertEquals(1, result.length);
        assertEquals(SUCCESS_REGISTRATION_MESSAGE, result[0]);
    }

    /**
     * Tests that registerUser returns an error message for an invalid username.
     * Verifies that the Registration class rejects usernames exceeding format rules.
     */
    @Test
    public void registerUser_InvalidUsername_ReturnsErrorMessage() 
    {
        String[] result = registration.registerUser(INVALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE_NUMBER, VALID_FIRST_NAME, VALID_LAST_NAME);
        assertEquals(1, result.length);
        assertEquals(USERNAME_ERROR_MESSAGE, result[0]);
    }

    /**
     * Tests that registerUser returns an error message for an invalid password.
     * Verifies that the Registration class rejects passwords lacking required complexity.
     */
    @Test
    public void registerUser_InvalidPassword_ReturnsErrorMessage() 
    {
        String[] result = registration.registerUser(VALID_USERNAME, INVALID_PASSWORD, VALID_CELL_PHONE_NUMBER, VALID_FIRST_NAME, VALID_LAST_NAME);
        assertEquals(1, result.length);
        assertEquals(PASSWORD_ERROR_MESSAGE, result[0]);
    }

    /**
     * Tests that registerUser returns an error message for an invalid cellphone number.
     * Verifies that the Registration class rejects cellphone numbers without proper format.
     */
    @Test
    public void registerUser_InvalidCellPhoneNumber_ReturnsErrorMessage() 
    {
        String[] result = registration.registerUser(VALID_USERNAME, VALID_PASSWORD, INVALID_CELL_PHONE_NUMBER, VALID_FIRST_NAME, VALID_LAST_NAME);
        assertEquals(1, result.length);
        assertEquals(CELL_PHONE_ERROR_MESSAGE, result[0]);
    }

    /**
     * Tests that registerUser returns an error message for an invalid first name.
     * Verifies that the Registration class rejects first names with numbers or invalid format.
     */
    @Test
    public void registerUser_InvalidFirstName_ReturnsErrorMessage() 
    {
        String[] result = registration.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE_NUMBER, INVALID_FIRST_NAME, VALID_LAST_NAME);
        assertEquals(1, result.length);
        assertEquals(FIRST_NAME_ERROR_MESSAGE, result[0]);
    }

    /**
     * Tests that registerUser returns an error message for an invalid last name.
     * Verifies that the Registration class rejects empty or invalid last names.
     */
    @Test
    public void registerUser_InvalidLastName_ReturnsErrorMessage() 
    {
        String[] result = registration.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE_NUMBER, VALID_FIRST_NAME, INVALID_LAST_NAME);
        assertEquals(1, result.length);
        assertEquals(LAST_NAME_ERROR_MESSAGE, result[0]);
    }

    /**
     * Tests that registerUser returns all error messages when all inputs are invalid.
     * Verifies that the Registration class correctly identifies and reports multiple validation failures.
     */
    @Test
    public void registerUser_AllInvalid_ReturnsAllErrorMessages() 
    {
        String[] result = registration.registerUser(INVALID_USERNAME, INVALID_PASSWORD, INVALID_CELL_PHONE_NUMBER, INVALID_FIRST_NAME, INVALID_LAST_NAME);
        assertEquals(5, result.length);
        assertTrue(contains(result, USERNAME_ERROR_MESSAGE));
        assertTrue(contains(result, PASSWORD_ERROR_MESSAGE));
        assertTrue(contains(result, CELL_PHONE_ERROR_MESSAGE));
        assertTrue(contains(result, FIRST_NAME_ERROR_MESSAGE));
        assertTrue(contains(result, LAST_NAME_ERROR_MESSAGE));
    }

    // Setter and Getter tests
    /**
     * Tests that setUserName returns true and stores a valid username.
     * Verifies that the getter retrieves the stored value correctly.
     */
    @Test
    public void setUserName_Valid_ReturnsTrueAndStoresValue() 
    {
        boolean setResult = registration.setUserName(VALID_USERNAME);
        assertTrue(setResult);
        assertEquals(VALID_USERNAME, registration.getUserName());
    }

    /**
     * Tests that setUserName returns false for an invalid username.
     * Verifies that the method rejects usernames without an underscore.
     */
    @Test
    public void setUserName_Invalid_ReturnsFalse() 
    {
        boolean setResult = registration.setUserName(USERNAME_WITHOUT_UNDERSCORE);
        assertFalse(setResult);
    }

    /**
     * Tests that setPassword returns true and stores a valid password.
     * Verifies that the getter retrieves the stored value correctly.
     */
    @Test
    public void setPassword_Valid_ReturnsTrueAndStoresValue() 
    {
        boolean setResult = registration.setPassword(VALID_PASSWORD);
        assertTrue(setResult);
        assertEquals(VALID_PASSWORD, registration.getPassword());
    }

    /**
     * Tests that setPassword returns false for an invalid password.
     * Verifies that the method rejects passwords lacking complexity.
     */
    @Test
    public void setPassword_Invalid_ReturnsFalse() 
    {
        boolean setResult = registration.setPassword(INVALID_PASSWORD);
        assertFalse(setResult);
    }

    /**
     * Tests that setCellPhoneNumber returns true and stores a valid cellphone number.
     * Verifies that the getter retrieves the stored value correctly.
     */
    @Test
    public void setCellPhoneNumber_Valid_ReturnsTrueAndStoresValue() 
    {
        boolean setResult = registration.setCellPhoneNumber(VALID_CELL_PHONE_NUMBER);
        assertTrue(setResult);
        assertEquals(VALID_CELL_PHONE_NUMBER, registration.getCellPhoneNumber());
    }

    /**
     * Tests that setCellPhoneNumber returns false for an invalid cellphone number.
     * Verifies that the method rejects numbers without proper format.
     */
    @Test
    public void setCellPhoneNumber_Invalid_ReturnsFalse() 
    {
        boolean setResult = registration.setCellPhoneNumber(INVALID_CELL_PHONE_NUMBER);
        assertFalse(setResult);
    }

    /**
     * Tests that setFirstName returns true and stores a valid first name.
     * Verifies that the getter retrieves the stored value correctly.
     */
    @Test
    public void setFirstName_Valid_ReturnsTrueAndStoresValue() 
    {
        boolean setResult = registration.setFirstName(VALID_FIRST_NAME);
        assertTrue(setResult);
        assertEquals(VALID_FIRST_NAME, registration.getFirstName());
    }

    /**
     * Tests that setFirstName returns false for an invalid first name.
     * Verifies that the method rejects names with numbers or invalid format.
     */
    @Test
    public void setFirstName_Invalid_ReturnsFalse() 
    {
        boolean setResult = registration.setFirstName(INVALID_FIRST_NAME);
        assertFalse(setResult);
    }

    /**
     * Tests that setLastName returns true and stores a valid last name.
     * Verifies that the getter retrieves the stored value correctly.
     */
    @Test
    public void setLastName_Valid_ReturnsTrueAndStoresValue() 
    {
        boolean setResult = registration.setLastName(VALID_LAST_NAME);
        assertTrue(setResult);
        assertEquals(VALID_LAST_NAME, registration.getLastName());
    }

    /**
     * Tests that setLastName returns false for an invalid last name.
     * Verifies that the method rejects empty or invalid last names.
     */
    @Test
    public void setLastName_Invalid_ReturnsFalse() 
    {
        boolean setResult = registration.setLastName(INVALID_LAST_NAME);
        assertFalse(setResult);
    }

    /**
     * Helper method to check if a String array contains a specific value.
     * Used in tests to verify multiple error messages in the result array.
     * 
     * @param array the String array to search
     * @param value the value to find in the array
     * @return true if the value is found in the array, false otherwise
     */
    private boolean contains(String[] array, String value) 
    {
        for (String item : array) 
        {
            if (item.equals(value)) 
            {
                return true;
            }
        }
        return false;
    }
}