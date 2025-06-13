package quickchat.gui;

import quickchat.core.Registration;
import quickchat.core.Login;
import quickchat.core.Message;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * The main class for the QuickChat application, managing the flow between
 * registration, login, and messaging features.
 *
 * @author Tshedimosetso Wowana
 * @version 3.0.0
 */
public class QuickChat {
    private final Registration REGISTRATION;
    private final Login LOGIN;
    private final RegistrationForm REGISTRATION_FORM;
    private final LoginForm LOGIN_FORM;
    private String currentLoggedInUser; // To store the username after successful login

    /**
     * Constructs a new QuickChat instance, initializing the core logic and GUI components.
     */
    public QuickChat() {
        REGISTRATION = new Registration();
        LOGIN = new Login(REGISTRATION); // LOGIN depends on REGISTRATION
        REGISTRATION_FORM = new RegistrationForm(REGISTRATION, this);
        LOGIN_FORM = new LoginForm(LOGIN, this); // LOGIN_FORM depends on LOGIN and QuickChat
    }

    /**
     * Starts the QuickChat application by displaying the RegistrationForm.
     */
    public void start() {
        REGISTRATION_FORM.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        REGISTRATION_FORM.setLocationRelativeTo(null);
        REGISTRATION_FORM.setVisible(true);
    }

    /**
     * Displays the LoginForm.
     * This is typically called after a successful registration or if skipping registration.
     */
    public void showLoginForm() {
        LOGIN_FORM.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        LOGIN_FORM.setLocationRelativeTo(null);
        LOGIN_FORM.setVisible(true);
    }

    /**
     * Sets the currently logged-in user.
     * This should be called after a successful login.
     * @param username The username of the logged-in user.
     */
    public void setCurrentLoggedInUser(String username) {
        this.currentLoggedInUser = username;
    }

    /**
     * Handles the messaging feature interaction using JOptionPane dialogs.
     * Relies on the Message class for validation and Part 3 array logic.
     */
    public void showMessageFeature() {
        if (this.currentLoggedInUser == null || this.currentLoggedInUser.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Error: No user is logged in. Cannot access messaging features.", "Login Required", JOptionPane.ERROR_MESSAGE);
            // Optionally, redirect to login form or exit
            showLoginForm(); // Redirect to login
            return;
        }

        // Populate with Part 3 Test Data for testing purposes
        Message.populateWithPart3TestData();
        // Load any existing messages from JSON files from previous sessions
        // The directory path "." means the current working directory where the app is run.
        Message.loadStoredMessagesFromJSON(".", this.currentLoggedInUser);


        boolean continueMessaging = true;
        while (continueMessaging) {
            String menu = "Messaging Menu (Logged in as: " + this.currentLoggedInUser + "):\n" +
                          "1. Create New Messages (Part 2 Style)\n" +
                          "2. Display Sent Messages Report (Part 3)\n" + // Was "Show Last Sent Message Details"
                          "3. Display Sender and Recipient of Sent Messages (Part 3)\n" +
                          "4. Display Longest Message (Overall - Part 3)\n" +
                          "5. Search Message by ID (Part 3)\n" +
                          "6. Search Messages by Recipient (Part 3)\n" +
                          "7. Delete Message by Hash (Part 3)\n" +
                          "8. Reload Stored Messages from JSON (Part 3)\n" + // Changed from Display Report
                          "9. Exit Messaging";

            String choice = JOptionPane.showInputDialog(null, menu, "Main Menu", JOptionPane.QUESTION_MESSAGE);

            if (choice == null) { // User cancelled main menu
                continueMessaging = false;
                break;
            }

            switch (choice) {
                case "1": // Process Messages (Part 2 style, adapted for Part 3 arrays)
                    String numMessagesStr = JOptionPane.showInputDialog(null,
                            "How many messages would you like to process?",
                            "Number of Messages", JOptionPane.QUESTION_MESSAGE);

                    if (numMessagesStr == null || numMessagesStr.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Number of messages not provided. Returning to menu.", "Input Missing", JOptionPane.WARNING_MESSAGE);
                        continue;
                    }
                    try {
                        int numMessages = Integer.parseInt(numMessagesStr);
                        if (numMessages <= 0) {
                            JOptionPane.showMessageDialog(null, "Please enter a positive number of messages.", "Input Error", JOptionPane.ERROR_MESSAGE);
                            continue;
                        }

                        for (int i = 0; i < numMessages; i++) {
                            JOptionPane.showMessageDialog(null, "Processing Message " + (i + 1) + " of " + numMessages, "Message Progress", JOptionPane.INFORMATION_MESSAGE);

                            String recipient = JOptionPane.showInputDialog(null, "Enter recipient's cell number (e.g., +27718693002):", "Message " + (i + 1) + " - Recipient", JOptionPane.PLAIN_MESSAGE);
                            if (recipient == null) {
                                JOptionPane.showMessageDialog(null, "Recipient input cancelled for Message " + (i + 1) + ". Skipping.", "Cancelled", JOptionPane.WARNING_MESSAGE);
                                continue;
                            }

                            String payload = JOptionPane.showInputDialog(null, "Enter message payload:", "Message " + (i + 1) + " - Payload", JOptionPane.PLAIN_MESSAGE);
                            if (payload == null) {
                                JOptionPane.showMessageDialog(null, "Payload input cancelled for Message " + (i + 1) + ". Skipping.", "Cancelled", JOptionPane.WARNING_MESSAGE);
                                continue;
                            }

                            Message currentMessage = new Message(recipient, payload, this.currentLoggedInUser); // Pass current user as sender
                            JOptionPane.showMessageDialog(null, currentMessage.getGeneratedIdNotification(), "Message ID", JOptionPane.INFORMATION_MESSAGE);

                            // Validate recipient and payload
                            String recipientValidationMsg = currentMessage.validateRecipientNumber(recipient);
                            if (!recipientValidationMsg.equals("Cell phone number successfully captured.")) {
                                JOptionPane.showMessageDialog(null, "Validation Failed for Message " + (i + 1) + ":\n" + recipientValidationMsg, "Recipient Error", JOptionPane.ERROR_MESSAGE);
                                continue;
                            }

                            String payloadValidationMsg = currentMessage.validatePayloadLength(payload);
                            if (!payloadValidationMsg.equals("Message ready to send.")) {
                                JOptionPane.showMessageDialog(null, "Validation Failed for Message " + (i + 1) + ":\n" + payloadValidationMsg, "Payload Error", JOptionPane.ERROR_MESSAGE);
                                continue;
                            }

                            String[] options = {"Send Message", "Store Message for Later", "Disregard Message"};
                            int actionChoice = JOptionPane.showOptionDialog(null,
                                    "Choose an action for this message:\nTo: " + currentMessage.getMessageRecipient() + "\nMessage: " + currentMessage.getMessagePayload().substring(0, Math.min(currentMessage.getMessagePayload().length(), 50)) + "...",
                                    "Message " + (i + 1) + " - Action",
                                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

                            String resultMessage = "";
                            switch (actionChoice) {
                                case 0: // Send Message
                                    resultMessage = currentMessage.sentMessage(); // This method now also calls addMessageToList
                                    JOptionPane.showMessageDialog(null, resultMessage, "Send Status", JOptionPane.INFORMATION_MESSAGE);
                                    if (resultMessage.equals("Message successfully sent.")) {
                                        // Displaying details of sent message is part of the report now.
                                        // Storing the sent message (as JSON) is also an option, could be implicit or explicit.
                                        // For now, sentMessage() adds to sentMessages list. If JSON persistence is also desired for sent messages:
                                        // currentMessage.storeMessage(); // This would store it as message_INDEX.json
                                    }
                                    break;
                                case 1: // Store Message for Later
                                    resultMessage = currentMessage.storeMessage(); // This method adds to storedMessagesList if it's a draft
                                    JOptionPane.showMessageDialog(null, resultMessage, "Store Status", JOptionPane.INFORMATION_MESSAGE);
                                    break;
                                case 2: // Disregard Message
                                    Message.addMessageToList(currentMessage, "Disregarded");
                                    JOptionPane.showMessageDialog(null, "Message disregarded by user.", "Disregarded", JOptionPane.INFORMATION_MESSAGE);
                                    break;
                                default:
                                    JOptionPane.showMessageDialog(null, "No action selected for message " + (i + 1) + ".", "Action Skipped", JOptionPane.WARNING_MESSAGE);
                                    break;
                            }
                        }
                        JOptionPane.showMessageDialog(null, "Batch processing complete. Total messages attempted: " + numMessages, "Batch Summary", JOptionPane.INFORMATION_MESSAGE);

                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Invalid number of messages entered. Please enter a numeric value.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;

                case "2": // Display Sent Messages Report (Part 3)
                    JOptionPane.showMessageDialog(null, Message.displaySentMessagesReport(), "Sent Messages Report", JOptionPane.INFORMATION_MESSAGE);
                    break;

                case "3": // Display Sender and Recipient of Sent Messages (Part 3)
                    JOptionPane.showMessageDialog(null, Message.displaySenderAndRecipientOfSentMessages(this.currentLoggedInUser), "Sent Message Details (Sender/Recipient)", JOptionPane.INFORMATION_MESSAGE);
                    break;

                case "4": // Display Longest Message (Overall - Part 3)
                    JOptionPane.showMessageDialog(null, Message.displayLongestMessageOverall(), "Longest Message", JOptionPane.INFORMATION_MESSAGE);
                    break;

                case "5": // Search Message by ID (Part 3)
                    String idToSearch = JOptionPane.showInputDialog("Enter Message ID to search:");
                    if (idToSearch != null && !idToSearch.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, Message.searchMessageById(idToSearch.trim()), "Search Result by ID", JOptionPane.INFORMATION_MESSAGE);
                    }
                    break;

                case "6": // Search Messages by Recipient (Part 3)
                    String recipientToSearch = JOptionPane.showInputDialog("Enter Recipient's number (e.g., +27718693002 or 0838884567) to search messages for:");
                    if (recipientToSearch != null && !recipientToSearch.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, Message.searchMessagesByRecipient(recipientToSearch.trim()), "Search Result by Recipient", JOptionPane.INFORMATION_MESSAGE);
                    }
                    break;

                case "7": // Delete Message by Hash (Part 3)
                    String hashToDelete = JOptionPane.showInputDialog("Enter Message Hash to delete:");
                    if (hashToDelete != null && !hashToDelete.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, Message.deleteMessageByHash(hashToDelete.trim()), "Delete Result by Hash", JOptionPane.INFORMATION_MESSAGE);
                    }
                    break;

                case "8": // Reload Stored Messages from JSON (Part 3)
                    Message.loadStoredMessagesFromJSON(".", this.currentLoggedInUser); // "." for current directory
                    JOptionPane.showMessageDialog(null, "Attempted to reload stored messages from JSON files.", "Reload Stored Messages", JOptionPane.INFORMATION_MESSAGE);
                    break;

                case "9": // Exit Messaging
                    continueMessaging = false;
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Invalid choice. Please select an option from the menu.", "Menu Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
        JOptionPane.showMessageDialog(null, "Exiting messaging feature.", "QuickChat Messaging", JOptionPane.INFORMATION_MESSAGE);
        // After exiting messaging, perhaps go back to login or offer to exit app?
        // For now, it just exits this loop. The main app flow (JFrame) would handle actual app exit.
    }

    /**
     * The main entry point for the QuickChat application.
     * Creates a QuickChat instance and starts the application.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        QuickChat quickChatApp = new QuickChat();
        quickChatApp.start();
    }
}