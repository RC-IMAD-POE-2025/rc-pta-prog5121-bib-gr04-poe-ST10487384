package quickchat;

import javax.swing.JFrame;

/**
 * The main class for the QuickChat application, managing the flow between 
 * registration and login forms. This class initializes core logic and GUI 
 * instances, and controls the visibility of forms for the entire POE.
 * 
 * @author Tshedimosetso Wowana
 * @version 1.0.0
 * @since 2025-04-01
 */
public class QuickChat 
{
    /** The Registration instance for handling user registration logic. */
    private final Registration REGISTRATION;
    
    /** The Login instance for verifying user credentials. */
    private final Login LOGIN;
    
    /** The RegistrationForm GUI for user input during registration. */
    private final RegistrationForm REGISTRATION_FORM;
    
    /** The LoginForm GUI for user input during login. */
    private final LoginForm LOGIN_FORM;

    /**
     * Constructs a new QuickChat instance, initializing the core logic and GUI components.
     * Creates instances of Registration, Login, RegistrationForm, and LoginForm, linking them
     * for application flow control.
     */
    public QuickChat() 
    {
        REGISTRATION = new Registration(); // Create core logic instances
        LOGIN = new Login(REGISTRATION);// Pass registration to logins
        REGISTRATION_FORM = new RegistrationForm(REGISTRATION, this);
        LOGIN_FORM = new LoginForm(LOGIN, this);
    }

    /**
     * Starts the QuickChat application by displaying the RegistrationForm.
     * Centers the form on the screen and sets it to exit the application when closed.
     */
    public void start() 
    {
        REGISTRATION_FORM.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        REGISTRATION_FORM.setLocationRelativeTo(null); // Center the form
        REGISTRATION_FORM.setVisible(true); // Show registration first
    }

    /**
     * Displays the LoginForm after successful registration.
     * Centers the form on the screen and sets it to exit the application when closed.
     */
    public void showLoginForm() 
    {
        LOGIN_FORM.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        LOGIN_FORM.setLocationRelativeTo(null); // Center the form
        LOGIN_FORM.setVisible(true);                   
    }
    
    /**
     * Displays a placeholder message for the messaging feature to be implemented in Part 2.
     * Uses a JOptionPane dialog to inform the user of upcoming functionality.
     */
    public void showMessageFeature() 
    {
        // Placeholder for future messaging feature in Part2
        javax.swing.JOptionPane.showMessageDialog(null, "Messaging feature coming soon.");
    }

    /**
     * The main entry point for the QuickChat application.
     * Creates a QuickChat instance and starts the application.
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) 
    {
        QuickChat quickChat = new QuickChat();
        quickChat.start();
    }
}