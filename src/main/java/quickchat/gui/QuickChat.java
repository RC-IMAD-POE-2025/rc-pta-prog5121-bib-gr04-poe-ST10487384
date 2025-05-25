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
 * @version 2.1.0
 */
public class QuickChat {
    private final Registration REGISTRATION;
    private final Login LOGIN;
    private final RegistrationForm REGISTRATION_FORM;
    private final LoginForm LOGIN_FORM;

    /**
     * Constructs a new QuickChat instance, initializing the core logic and GUI components.
     */
    public QuickChat() {
        REGISTRATION = new Registration();
        LOGIN = new Login(REGISTRATION);
        REGISTRATION_FORM = new RegistrationForm(REGISTRATION, this);
        LOGIN_FORM = new LoginForm(LOGIN, this);
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
     */
    public void showLoginForm() {
        LOGIN_FORM.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        LOGIN_FORM.setLocationRelativeTo(null);
        LOGIN_FORM.setVisible(true);
    }

    /**
     * Handles the messaging feature interaction using JOptionPane dialogs.
     * Relies on the Message class for validation logic.
     */
    public void showMessageFeature() {
        JOptionPane.showMessageDialog(null,
                "Welcome to QuickChat 2!",
                "QuickChat by Tshedimosetso Wowana", JOptionPane.INFORMATION_MESSAGE);

        boolean continueMessaging = true;
        while (continueMessaging) {
            String choice = JOptionPane.showInputDialog(null,
                    "Messaging Menu:\n1. Create new Messages\n2. Show Last Sent Message Details\n3. Exit Messaging",
                    "Main Menu", JOptionPane.QUESTION_MESSAGE);

            if (choice == null) { // User cancelled main menu
                continueMessaging = false;
                break;
            }

            switch (choice) {
                case "1": // Process Messages
                    String numMessagesStr = JOptionPane.showInputDialog(null,
                            "How many messages would you like to process?",
                            "Number of Messages", JOptionPane.QUESTION_MESSAGE);

                    if (numMessagesStr == null || numMessagesStr.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Number of messages not provided. Returning to menu.", "Input Missing", JOptionPane.WARNING_MESSAGE);
                        continue; // Back to main messaging menu
                    }

                    int numMessages = Integer.parseInt(numMessagesStr);

                    if (numMessages <= 0) {
                        JOptionPane.showMessageDialog(null, "Please enter a positive number of messages.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        continue; // Back to main messaging menu
                    }

                    for (int i = 0; i < numMessages; i++) {
                        JOptionPane.showMessageDialog(null, "Processing Message " + (i + 1) + " of " + numMessages, "Message Progress", JOptionPane.INFORMATION_MESSAGE);

                        String recipient = JOptionPane.showInputDialog(null, "Enter recipient's cell number (e.g., +27718693002):", "Message " + (i + 1) + " - Recipient", JOptionPane.PLAIN_MESSAGE);
                        if (recipient == null) {
                            JOptionPane.showMessageDialog(null, "Recipient input cancelled for Message " + (i + 1) + ". Skipping.", "Cancelled", JOptionPane.WARNING_MESSAGE);
                            continue; // Skip to next message
                        }

                        String payload = JOptionPane.showInputDialog(null, "Enter message payload:", "Message " + (i + 1) + " - Payload", JOptionPane.PLAIN_MESSAGE);
                        if (payload == null) {
                            JOptionPane.showMessageDialog(null, "Payload input cancelled for Message " + (i + 1) + ". Skipping.", "Cancelled", JOptionPane.WARNING_MESSAGE);
                            continue; // Skip to next message
                        }

                        // Create message object first
                        Message currentMessage = new Message(recipient, payload);
                        JOptionPane.showMessageDialog(null, currentMessage.getGeneratedIdNotification(), "Message ID", JOptionPane.INFORMATION_MESSAGE);

                        // Validate using Message class methods BEFORE showing action dialog
                        String recipientValidationMsg = currentMessage.validateRecipientNumber(recipient);
                        if (!recipientValidationMsg.equals("Cell phone number successfully captured.")) {
                            JOptionPane.showMessageDialog(null, "Validation Failed for Message " + (i+1) + ":\n" + recipientValidationMsg, "Recipient Error", JOptionPane.ERROR_MESSAGE);
                            continue; // Skip to next message
                        }

                        String payloadValidationMsg = currentMessage.validatePayloadLength(payload);
                        if (!payloadValidationMsg.equals("Message ready to send.")) {
                            JOptionPane.showMessageDialog(null, "Validation Failed for Message " + (i+1) + ":\n" + payloadValidationMsg, "Payload Error", JOptionPane.ERROR_MESSAGE);
                            continue; // Skip to next message
                        }

                        // If validations pass, proceed with actions
                        String[] options = {"Send Message", "Store Message", "Disregard Message"};
                        int actionChoice = JOptionPane.showOptionDialog(null,
                                "Choose an action for this message:\nTo: " + currentMessage.getMessageRecipient() + "\nMessage: " + currentMessage.getMessagePayload().substring(0, Math.min(currentMessage.getMessagePayload().length(), 50)) + "...", // Show snippet
                                "Message " + (i + 1) + " - Action",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

                        String resultMessage = "";
                        switch (actionChoice) {
                            case 0: // Send Message
                                resultMessage = currentMessage.sentMessage(); // This method now contains its own internal validation calls too
                                JOptionPane.showMessageDialog(null, resultMessage, "Send Status", JOptionPane.INFORMATION_MESSAGE);
                                if (resultMessage.equals("Message successfully sent.")) {
                                    JOptionPane.showMessageDialog(null, "Details of sent message:\n" + currentMessage.printMessages(), "Sent Message Details", JOptionPane.INFORMATION_MESSAGE);
                                    // Also store the sent message
                                    String storeSentResult = currentMessage.storeMessage();
                                    JOptionPane.showMessageDialog(null, "Sent message also stored: " + storeSentResult, "Store Status", JOptionPane.INFORMATION_MESSAGE);
                                }
                                break;
                            case 1: // Store Message
                                resultMessage = currentMessage.storeMessage();
                                JOptionPane.showMessageDialog(null, resultMessage, "Store Status", JOptionPane.INFORMATION_MESSAGE);
                                break;
                            case 2: // Disregard Message
                                JOptionPane.showMessageDialog(null, "Message disregarded by user.", "Disregarded", JOptionPane.INFORMATION_MESSAGE);
                                break;
                            default: // User closed dialog or an unexpected choice
                                JOptionPane.showMessageDialog(null, "No action selected for message " + (i + 1) + ".", "Action Skipped", JOptionPane.WARNING_MESSAGE);
                                break;
                        }
                    } // End of for loop for numMessages
                    JOptionPane.showMessageDialog(null, "Batch processing complete. Total messages successfully sent in this session: " + Message.returnTotalMessages(), "Batch Summary", JOptionPane.INFORMATION_MESSAGE);
                    break; // Break from main menu switch

                case "2": // Show Last Sent Message Details
                    JOptionPane.showMessageDialog(null, Message.printMessages(), "Last Sent Message", JOptionPane.INFORMATION_MESSAGE);
                    break;

                case "3": // Exit Messaging
                    continueMessaging = false;
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Invalid choice. Please select an option from the menu.", "Menu Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        } // End of while loop for continueMessaging

        JOptionPane.showMessageDialog(null, "Exiting messaging feature.", "QuickChat Messaging", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * The main entry point for the QuickChat application.
     * Creates a QuickChat instance and starts the application.
     */
    public static void main(String[] args) {
        
        QuickChat quickChatApp = new QuickChat();
        quickChatApp.start();
    }
}
