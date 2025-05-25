package quickchat.core;

import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern; // AI Assisted recipient cellnumber validation

public class Message {
    private final String MESSAGE_ID;        // Unique identifier for each message
    private final String MESSAGE_RECIPIENT; // Recipient's cell number
    private final String MESSAGE_PAYLOAD;   // Content of the message
    private int MESSAGE_INDEX;              // Order of the sent message, set when sent
    private String MESSAGE_HASH;            // Hash of the message, set when sent
    private static int messageCounter = 0;  // Counts total sent messages
    private static String lastSentMessage = ""; // Part3 placeholder, will change into Array

    private static final int MAX_PAYLOAD_LENGTH = 250;

    /**
     * Constructor to create a new message with a unique ID.
     *
     * @param recipient The recipient's cell number (e.g., +27123456789)
     * @param payload   The message content
     */
    public Message(final String recipient, final String payload) {
        // Generate a 10-digit random number as a string for MESSAGE_ID
        this.MESSAGE_ID = String.format("%010d", (long)(Math.random() * 10000000000L));
        this.MESSAGE_RECIPIENT = recipient;
        this.MESSAGE_PAYLOAD = payload;
        this.MESSAGE_INDEX = 0;   
        this.MESSAGE_HASH = "";    
    }

    /**
     * Getter for MESSAGE_ID.
     * @return The message ID.
     */
    public String getMessageID() {
        return MESSAGE_ID;
    }

    /**
     * Getter for MESSAGE_RECIPIENT.
     * @return The message recipient.
     */
    public String getMessageRecipient() {
        return MESSAGE_RECIPIENT;
    }

    /**
     * Getter for MESSAGE_PAYLOAD.
     * @return The message payload.
     */
    public String getMessagePayload() {
        return MESSAGE_PAYLOAD;
    }

    /**
     * Getter for MESSAGE_INDEX.
     * @return The message index.
     */
    public int getMessageIndex() {
        return MESSAGE_INDEX;
    }

    /**
     * Getter for MESSAGE_HASH.
     * @return The message hash.
     */
    public String getMessageHash() {
        return MESSAGE_HASH;
    }


    /**
     * Validates if the message ID is a 10-digit number.
     * (This method might be for external validation if ID was settable,
     * but here ID is auto-generated and should always be valid by construction).
     *
     * @param id The message ID to check
     * @return true if valid (10 digits), false otherwise
     */
    public boolean checkMessageID(final String id) {
        if (id == null || id.length() != 10) {
            return false;
        }
        for (char c : id.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates the message payload length based on the specification.
     *
     * @param payload The message content to validate.
     * @return A string indicating success ("Message ready to send.") or failure
     * ("Message exceeds 250 characters by X, please reduce size.").
     */
    public String validatePayloadLength(final String payload) {
        if (payload == null) { // Handle null payload gracefully
            return "Message exceeds 250 characters by " + (0 - MAX_PAYLOAD_LENGTH) + ", please reduce size.";
        }
        if (payload.length() <= MAX_PAYLOAD_LENGTH) {
            return "Message ready to send.";
        } else {
            int excessCharacters = payload.length() - MAX_PAYLOAD_LENGTH;
            return "Message exceeds 250 characters by " + excessCharacters + ", please reduce size.";
        }
    }

    /**
     * Validates the recipient's cell number format based on the specification.
     * Uses regex for +27 followed by 9 digits, similar to Registration.
     *
     * @param recipient The cell number to check.
     * @return A string indicating success ("Cell phone number successfully captured.")
     * or failure ("Cell phone number is incorrectly formatted...").
     */
    public String validateRecipientNumber(final String recipient) {
        if (recipient == null || recipient.isBlank()) {
             return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
        // Regex pattern ensures +27 prefix and exactly 9 digits (from Registration logic)
        String regexChecker = "^\\+27[0-9]{9}$";
        if (Pattern.matches(regexChecker, recipient)) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    /**
     * Creates a hash from the message ID, index, and payload.
     * The hash format is: FirstTwoCharsOfID:Index:FirstWordOfPayloadLastWordOfPayload (ALL CAPS).
     *
     * @param id      The message ID (expected to be 10 digits).
     * @param index   The message index.
     * @param payload The message content.
     * @return The computed hash in uppercase. Returns empty string if inputs are invalid for hashing.
     */
    public String createMessageHash(final String id, int index, final String payload) {
        if (id == null || id.length() < 2 || payload == null || payload.trim().isEmpty()) {
          
            return ""; 
        }

        String firstTwo = id.substring(0, 2);

        String[] words = payload.trim().split("\\s+");
        String firstWord = "";
        String lastWord = "";

        if (words.length > 0) {
            firstWord = words[0];
            lastWord = words[words.length - 1];
        } else { // Handle cases like payload being only spaces
            return (firstTwo + ":" + index + ":").toUpperCase();
        }
        
        String hash = firstTwo + ":" + index + ":" + firstWord + lastWord;
        return hash.toUpperCase();
    }


    /**
     * Processes the message for sending: validates, updates index and hash.
     *
     * @return A status message: "Message successfully sent." or an error message.
     */
    public String sentMessage() {
        // Validate Payload Length
        String payloadValidationResult = validatePayloadLength(this.MESSAGE_PAYLOAD);
        if (!payloadValidationResult.equals("Message ready to send.")) {
           
             if (this.MESSAGE_PAYLOAD != null && this.MESSAGE_PAYLOAD.length() > MAX_PAYLOAD_LENGTH) {
                 return "Failed to send message: Payload too long"; // Original simple message
             }
             // If it's another payload issue
             return "Failed to send message: Invalid payload content";
        }

        // Validate Recipient Number
        String recipientValidationResult = validateRecipientNumber(this.MESSAGE_RECIPIENT);
        if (!recipientValidationResult.equals("Cell phone number successfully captured.")) {
          
            return "Failed to send message: Invalid recipient";
        }
        
        // Validate Message ID (though it's auto-generated and should be valid)
        if (!checkMessageID(this.MESSAGE_ID)) {
            return "Failed to send message: Invalid message ID"; // Should not happen with current constructor
        }

        // Check for empty payload (though validatePayloadLength might cover some aspects)
         if (this.MESSAGE_PAYLOAD == null || this.MESSAGE_PAYLOAD.trim().isEmpty()) {
            return "Failed to send message: Message content cannot be empty";
        }


        // If all validations pass:
        messageCounter++;
        this.MESSAGE_INDEX = messageCounter;
        this.MESSAGE_HASH = createMessageHash(this.MESSAGE_ID, this.MESSAGE_INDEX, this.MESSAGE_PAYLOAD);
        
        // Update lastSentMessage static variable
        lastSentMessage = "ID: " + this.MESSAGE_ID + ", Recipient: " + this.MESSAGE_RECIPIENT +
                         ", Payload: " + this.MESSAGE_PAYLOAD + ", Hash: " + this.MESSAGE_HASH +
                         ", Index: " + this.MESSAGE_INDEX;
        
        return "Message successfully sent.";
    }

    /**
     * Returns details of the last successfully sent message.
     *
     * @return A string with details of the last sent message, or "No messages sent" if none.
     * Placeholder method for Part3, I will use an Array here to load JSON file messages 
     */
    public static String printMessages() {
        
        return "Coming soon in Part3";
    }

    /**
     * Returns the total number of messages successfully sent.
     *
     * @return The total count of sent messages.
     */
    public static int returnTotalMessages() {
        return messageCounter;
    }

    /**
     * Stores the current message details (draft or sent) into a JSON file.
     * The filename is based on MESSAGE_INDEX.
     *
     * @return A string indicating success ("Message successfully stored.") or failure.
     */
    public String storeMessage() {
        JSONObject json = new JSONObject();
        json.put("MESSAGE_ID", this.MESSAGE_ID);
        json.put("MESSAGE_RECIPIENT", this.MESSAGE_RECIPIENT);
        json.put("MESSAGE_PAYLOAD", this.MESSAGE_PAYLOAD);
        json.put("MESSAGE_INDEX", this.MESSAGE_INDEX); // Will be 0 for drafts, >0 for sent messages
        json.put("MESSAGE_HASH", this.MESSAGE_HASH);   // Will be "" for drafts

        String fileName = "message_" + this.MESSAGE_INDEX + ".json";
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json.toJSONString());
            return "Message successfully stored.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to store message: " + e.getMessage();
        }
    }

    /**
     * Provides a notification string that the Message ID has been generated.
     * This is based on the test specification.
     * @return A string like "Message ID generated"
     */
    public String getGeneratedIdNotification() {
        return "Message ID generated: " + this.MESSAGE_ID;
    }

    /**
     * Resets the static message counter.
     * Useful for ensuring a clean state between test runs if tests involve sending multiple messages.
     */
    public static void resetMessageCounterForTesting() {
        messageCounter = 0;
        lastSentMessage = "";
    }
}
