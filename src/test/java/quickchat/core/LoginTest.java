package quickchat.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the Login class in the QuickChat application.
 * Tests the login functionality, including credential validation and status messages,
 * using JUnit5 to ensure compliance with Part 1 POE requirements.
 * * @author Tshedimosetso Wowana
 * @version 1.0.0
 * @since 2025-03-01
 */
public class LoginTest 
{
    /** The Registration instance used to store test user data. */
    private Registration registration;
    
    /** The Login instance under test, linked to the Registration object. */
    private Login login;

    // Test data
    /** Valid username for testing (max 5 chars with underscore). */
    private static final String VALID_USERNAME = "Tsh_1"; // Using student's name convention
    
    /** Valid password for testing (8+ chars, capital, number, special char). */
    private static final String VALID_PASSWORD = "Ch&&sec@ke99!";
    
    /** Valid cellphone number for testing (+27 followed by 9 digits). */
    private static final String VALID_CELL_PHONE_NUMBER = "+27838968976";
    
    /** Valid first name for testing. */
    private static final String VALID_FIRST_NAME = "Tshedimosetso";
    
    /** Valid last name for testing. */
    private static final String VALID_LAST_NAME = "Wowana";
    
    /** Invalid username for testing (exceeds format rules). */
    private static final String INVALID_USERNAME = "kyle!!!!!!!";
    
    /** Invalid password for testing (lacks complexity). */
    private static final String INVALID_PASSWORD = "password";

    /**
     * Sets up the test environment before each test method.
     * Creates a new Registration instance, registers a valid user with test data,
     * and initializes the Login instance with the Registration object.
     */
    @BeforeEach
    public void setUp() 
    {
        registration = new Registration();
        // Register a user with valid data before each test
        registration.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE_NUMBER, VALID_FIRST_NAME, VALID_LAST_NAME);
        login = new Login(registration);
    }

    /**
     * Tests that loginUser returns true when provided with correct credentials.
     * Verifies that the Login class correctly validates a matching username and password.
     */
    @Test
    public void loginUser_CorrectCredentials_ReturnsTrue() 
    {
        boolean loginResult = login.loginUser(VALID_USERNAME, VALID_PASSWORD);
        assertTrue(loginResult);
    }

    /**
     * Tests that loginUser returns false when provided with incorrect credentials (both wrong).
     */
    @Test
    public void loginUser_WrongCredentials_ReturnsFalse() 
    {
        // Corrected Test: Use invalid credentials to test for failure
        boolean loginResult = login.loginUser(INVALID_USERNAME, INVALID_PASSWORD);
        assertFalse(loginResult);
    }

    /**
     * Tests that loginUser returns false when the password is incorrect.
     * Verifies that the Login class rejects a valid username with an invalid password.
     */
    @Test
    public void loginUser_WrongPassword_ReturnsFalse() 
    {
        boolean loginResult = login.loginUser(VALID_USERNAME, INVALID_PASSWORD);
        assertFalse(loginResult);
    }

    /**
     * Tests that loginUser returns false when the username is incorrect.
     * Verifies that the Login class rejects an invalid username with a valid password.
     */
    @Test
    public void loginUser_WrongUsername_ReturnsFalse() 
    {
        boolean loginResult = login.loginUser(INVALID_USERNAME, VALID_PASSWORD);
        assertFalse(loginResult);
    }

    /**
     * Tests that returnLoginStatus provides a welcome message with names after a successful login.
     * Ensures the Login class formats the welcome string correctly using first and last names.
     */
    @Test
    public void returnLoginStatus_ValidCredentials_ReturnsWelcomeMessageWithNames() 
    {
        login.loginUser(VALID_USERNAME, VALID_PASSWORD);
        String status = login.returnLoginStatus();
        assertEquals(String.format("Welcome %s %s,\nit is great to see you.", VALID_FIRST_NAME, VALID_LAST_NAME), status);
    }

    /**
     * Tests that returnLoginStatus returns a failure message after an unsuccessful login.
     * Verifies that the Login class provides the correct error message for invalid credentials.
     */
    @Test
    public void returnLoginStatus_InvalidCredentials_ReturnsFailureMessage() 
    {
        login.loginUser(VALID_USERNAME, INVALID_PASSWORD); // Unsuccessful login attempt
        String status = login.returnLoginStatus();
        assertEquals("Username & Password do not match our records, please try again.", status);
    }
}