package quickchat.core;

/**
 * A class responsible for handling user login functionality in the QuickChat application.
 * Verifies user credentials against a Registration instance and provides login status feedback.
 * Designed for Part 1 of the POE, with no persistent storage, relying on in-memory data.
 * 
 * @author Tshedimosetso Wowana
 * @version 1.0
 * @since 2025-03-01
 */
public class Login 
{
    /** The Registration instance containing the stored user credentials for verification. */
    private final Registration registration;
    
    /** A flag indicating whether the last login attempt was successful. */
    private boolean accessGranted;
    
    /**
     * Constructs a new Login instance with the specified Registration object.
     * Initializes the login process by linking to an existing Registration instance.
     * 
     * @param registration the Registration object holding the user data to verify against
     */
    public Login(Registration registration) 
    {
        this.registration = registration;
    }
    
    /**
     * Attempts to log in a user by comparing the provided username and password 
     * with the stored credentials in the Registration instance.
     * Updates the accessGranted flag based on the result.
     * 
     * @param userNameAttempt the username entered by the user
     * @param passwordAttempt the password entered by the user
     * @return true if the credentials match the stored values, false otherwise
     */
    public boolean loginUser(String userNameAttempt, String passwordAttempt) 
    {
        String storedUserName = registration.getUserName();
        String storedPassword = registration.getPassword();
        
        accessGranted = (userNameAttempt != null && userNameAttempt.equals(storedUserName)) &&
                        (passwordAttempt != null && passwordAttempt.equals(storedPassword));
        
        return accessGranted;
    }

    /**
     * Returns a status message based on the result of the last login attempt.
     * If login was successful, provides a personalized welcome message using the 
     * user's first and last names; otherwise, indicates a credentials mismatch.
     * 
     * @return a String with the login status message
     */
    public String returnLoginStatus() 
    {
        if (accessGranted) 
        {
            return String.format("Welcome %s %s,\nit is great to see you.", 
                registration.getFirstName(), registration.getLastName());
        } else 
        {
            return "Username & Password do not match our records, please try again.";
        }
    }

    /**
     * Retrieves the username from the associated Registration instance.
     * Added as a utility method for future features, such as a MessagePanel in Part 2.
     * 
     * @return the stored username from the Registration object
     */
    public String getUsername() 
    {
        return registration.getUserName();
    }
}