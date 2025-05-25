package quickchat.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the Message class in the QuickChat application.
 * This class tests the core messaging functionalities including validation of message properties,
 * message hash creation, the message sending process, and message storage.
 * All tests are designed using JUnit 5.
 */
public class MessageTest {

    // Test data based on project specifications
    private final String validRecipient1 = "+27718693002";
    private final String messagePayload1 = "Hi Mike, can you join us for dinner tonight"; // Length: 44

    private final String invalidRecipient2 = "08575975889"; // Invalid format (missing +27 prefix)
    private final String messagePayload2 = "Hi Keegan, did you receive the payment?"; // Length: 39

    private Message message; // A default Message instance for use in some tests

    /**
     * Sets up the testing environment before each test method.
     * This method resets the static message counter in the Message class to ensure
     * test independence, particularly for tests involving message indexing and counting.
     * It also initializes a default Message object.
     */
    @BeforeEach
    public void setUp() {
        Message.resetMessageCounterForTesting();
        message = new Message(validRecipient1, messagePayload1);
    }

    /**
     * Tests the Message constructor to ensure it correctly initializes all properties
     * of a new Message object, including auto-generated ID, recipient, payload,
     * and default values for index and hash.
     */
    @Test
    public void testMessageConstructor_InitializesPropertiesCorrectly() {
        Message msg = new Message("testRecipient", "testPayload");
        assertNotNull(msg.getMessageID(), "Message ID should be automatically generated and not null.");
        assertTrue(msg.getMessageID().matches("\\d{10}"), "Message ID should consist of 10 digits.");
        assertEquals("testRecipient", msg.getMessageRecipient(), "Recipient should match the constructor argument.");
        assertEquals("testPayload", msg.getMessagePayload(), "Payload should match the constructor argument.");
        assertEquals(0, msg.getMessageIndex(), "Initial message index should be 0.");
        assertEquals("", msg.getMessageHash(), "Initial message hash should be an empty string.");
    }

    /**
     * Tests the getGeneratedIdNotification method to ensure it returns a string
     * in the correct format, confirming Message ID generation.
     */
    @Test
    public void testGetGeneratedIdNotification_ReturnsCorrectFormat() {
        String notification = message.getGeneratedIdNotification();
        assertTrue(notification.startsWith("Message ID generated: "), "Notification string format is incorrect.");
        assertEquals(message.getMessageID(), notification.substring("Message ID generated: ".length()), "Notification should accurately contain the generated Message ID.");
    }

    // --- Payload Length Validation Tests ---

    /**
     * Tests validatePayloadLength with valid payloads (short and exact max length).
     * Expects the success message "Message ready to send."
     */
    @Test
    public void testValidatePayloadLength_ValidPayload_ReturnsSuccessMessage() {
        String shortPayload = "Hello";
        assertEquals("Message ready to send.", message.validatePayloadLength(shortPayload), "Short payload should be valid.");

        String exactLengthPayload = new String(new char[250]).replace('\0', 'a');
        assertEquals("Message ready to send.", message.validatePayloadLength(exactLengthPayload), "Payload of exact maximum length (250 chars) should be valid.");
    }

    /**
     * Tests validatePayloadLength with payloads exceeding the maximum length.
     * Expects a failure message indicating the number of excess characters.
     */
    @Test
    public void testValidatePayloadLength_TooLongPayload_ReturnsFailureMessageWithExcessCount() {
        String longPayload = new String(new char[251]).replace('\0', 'a'); // 251 chars
        assertEquals("Message exceeds 250 characters by 1, please reduce size.", message.validatePayloadLength(longPayload), "Payload exceeding max length by 1 char should report correctly.");

        String veryLongPayload = new String(new char[300]).replace('\0', 'b'); // 300 chars
        assertEquals("Message exceeds 250 characters by 50, please reduce size.", message.validatePayloadLength(veryLongPayload), "Payload exceeding max length by 50 chars should report correctly.");
    }

    /**
     * Tests validatePayloadLength with an empty payload.
     * As per current logic, an empty payload is valid at this stage (length is 0 <= 250).
     * Further validation for non-empty content occurs in sentMessage().
     */
    @Test
    public void testValidatePayloadLength_EmptyPayload_ReturnsSuccessMessage() {
        assertEquals("Message ready to send.", message.validatePayloadLength(""), "Empty payload should be considered valid by length check.");
    }
    
    /**
     * Tests validatePayloadLength with a null payload.
     * Expects a failure message, as implemented in the Message class.
     */
    @Test
    public void testValidatePayloadLength_NullPayload_ReturnsFailureMessage() {
        assertEquals("Message exceeds 250 characters by -250, please reduce size.", message.validatePayloadLength(null), "Null payload should result in a specific failure message.");
    }

    // --- Recipient Number Validation Tests ---

    /**
     * Tests validateRecipientNumber with a correctly formatted recipient number.
     * Expects the success message "Cell phone number successfully captured."
     */
    @Test
    public void testValidateRecipientNumber_ValidFormat_ReturnsSuccessMessage() {
        assertEquals("Cell phone number successfully captured.", message.validateRecipientNumber("+27123456789"), "Correctly formatted South African number should be valid.");
    }

    /**
     * Tests validateRecipientNumber with various incorrectly formatted recipient numbers.
     * Expects the failure message detailing incorrect format or missing international code.
     */
    @Test
    public void testValidateRecipientNumber_InvalidFormat_ReturnsFailureMessage() {
        String expectedError = "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        assertEquals(expectedError, message.validateRecipientNumber("0712345678"), "Number missing '+27' prefix should be invalid.");
        assertEquals(expectedError, message.validateRecipientNumber("+2712345678"), "Number too short after '+27' should be invalid.");
        assertEquals(expectedError, message.validateRecipientNumber("+271234567890"), "Number too long after '+27' should be invalid.");
        assertEquals(expectedError, message.validateRecipientNumber("invalid"), "Non-numeric or improperly structured number should be invalid.");
        assertEquals(expectedError, message.validateRecipientNumber(""), "Empty string for recipient number should be invalid.");
        assertEquals(expectedError, message.validateRecipientNumber(null), "Null recipient number should be invalid.");
    }

    // --- Message Hash Creation Tests ---

    /**
     * Tests createMessageHash with specific input data to verify correct hash generation
     * based on the defined format (FirstTwoID:Index:FirstWordLastWord, all uppercase).
     */
    @Test
    public void testCreateMessageHash_WithSpecifiedData_ReturnsCorrectHash() {
        // Data for Test Case 1 (Message 1) from specifications:
        // Message: "Hi Mike, can you join us for dinner tonight"
        // Expected Hash (assuming ID starts "00", index 0): "00:0:HITONIGHT"
        String testID = "0012345678"; 
        int testIndex = 0;
        String testPayload = "Hi Mike, can you join us for dinner tonight";
        assertEquals("00:0:HITONIGHT", message.createMessageHash(testID, testIndex, testPayload), "Hash for Test Case 1 data should be '00:0:HITONIGHT'.");

        // Additional test case for robustness
        String testID2 = "AB98765432";
        int testIndex2 = 15;
        String testPayload2 = " Hello world example "; // Note leading/trailing spaces
        assertEquals("AB:15:HELLOEXAMPLE", message.createMessageHash(testID2, testIndex2, testPayload2), "Hash with spaces in payload should correctly use trimmed words.");
    }

    // --- sentMessage() Tests ---

    

    /**
     * Tests sentMessage with an invalid recipient number.
     * Expects a failure message and ensures message state (index, hash, total count) is not altered.
     */
    @Test
    public void testSentMessage_InvalidRecipient_ReturnsFailure() {
        Message msg = new Message(invalidRecipient2, messagePayload1); // Invalid recipient
        assertEquals("Failed to send message: Invalid recipient", msg.sentMessage(), "sentMessage should return failure for an invalid recipient.");
        assertEquals(0, msg.getMessageIndex(), "Message index should remain 0 on a failed send attempt due to invalid recipient.");
        assertTrue(msg.getMessageHash().isEmpty(), "Message hash should remain empty on a failed send attempt.");
        assertEquals(0, Message.returnTotalMessages(), "Total messages sent counter should remain 0 after a failed send.");
    }

    /**
     * Tests sentMessage with a payload that exceeds the maximum allowed length.
     * Expects a failure message and ensures the total message count is not incremented.
     */
    @Test
    public void testSentMessage_PayloadTooLong_ReturnsFailure() {
        String longPayload = new String(new char[251]).replace('\0', 'a');
        Message msg = new Message(validRecipient1, longPayload); // Payload too long
        assertEquals("Failed to send message: Payload too long", msg.sentMessage(), "sentMessage should return failure for a payload exceeding maximum length.");
        assertEquals(0, Message.returnTotalMessages(), "Total messages sent counter should remain 0 if payload is too long.");
    }
    
    /**
     * Tests sentMessage with a payload that is empty or contains only whitespace.
     * Expects a failure message and ensures the total message count is not incremented.
     */
    @Test
    public void testSentMessage_EmptyPayload_ReturnsFailure() {
        Message msg = new Message(validRecipient1, "   "); // Payload is effectively empty after trim
        assertEquals("Failed to send message: Message content cannot be empty", msg.sentMessage(), "sentMessage should return failure for an empty payload.");
        assertEquals(0, Message.returnTotalMessages(), "Total messages sent counter should remain 0 if payload is empty.");
    }


    // --- storeMessage() Tests ---

    /**
     * Tests storeMessage for a draft message (not yet sent).
     * Verifies that a JSON file named "message_0.json" is created and contains
     * the correct message details (ID, index 0, empty hash, payload).
     * Uses @TempDir for managing temporary file creation if needed, but current implementation
     * writes to the working directory. Test cleans up the file.
     */
    @Test
    public void testStoreMessage_Draft_CreatesFileWithIndex0(@TempDir Path tempDir) throws IOException {
        Message draftMessage = new Message(validRecipient1, "This is a draft.");
        
        String expectedFileName = "message_0.json"; // Drafts use index 0
        File messageFile = new File(expectedFileName);
        if (messageFile.exists()) {
            assertTrue(messageFile.delete(), "Cleanup: Failed to delete pre-existing test file.");
        }

        assertEquals("Message successfully stored.", draftMessage.storeMessage(), "storeMessage should return success for a draft.");
        assertTrue(messageFile.exists(), "JSON file for the draft message (message_0.json) should be created.");

        String content = Files.readString(messageFile.toPath());
        assertTrue(content.contains("\"MESSAGE_ID\":\"" + draftMessage.getMessageID() + "\""), "Stored JSON should contain the correct Message ID.");
        assertTrue(content.contains("\"MESSAGE_INDEX\":0"), "Stored JSON for a draft should have MESSAGE_INDEX as 0.");
        assertTrue(content.contains("\"MESSAGE_HASH\":\"\""), "Stored JSON for a draft should have an empty MESSAGE_HASH.");
        assertTrue(content.contains("\"MESSAGE_PAYLOAD\":\"This is a draft.\""), "Stored JSON should contain the correct payload.");

        assertTrue(messageFile.delete(), "Cleanup: Failed to delete test file after test completion.");
    }

    /**
     * Tests storeMessage for a message that has been successfully sent.
     * Verifies that a JSON file (e.g., "message_1.json") is created with the correct
     * message index and populated hash, along with other details.
     * Uses @TempDir for managing temporary file creation if needed. Test cleans up the file.
     */
    @Test
    public void testStoreMessage_SentMessage_CreatesFileWithCorrectIndexAndHash(@TempDir Path tempDir) throws IOException {
        Message sentMsg = new Message(validRecipient1, "This is a sent message.");
        sentMsg.sentMessage(); // Process the message as sent

        assertNotEquals(0, sentMsg.getMessageIndex(), "Index of a sent message should not be 0.");
        assertFalse(sentMsg.getMessageHash().isEmpty(), "Hash of a sent message should not be empty.");

        String expectedFileName = "message_" + sentMsg.getMessageIndex() + ".json";
        File messageFile = new File(expectedFileName);
         if (messageFile.exists()) {
            assertTrue(messageFile.delete(), "Cleanup: Failed to delete pre-existing test file.");
        }

        assertEquals("Message successfully stored.", sentMsg.storeMessage(), "storeMessage should return success for a sent message.");
        assertTrue(messageFile.exists(), "JSON file for the sent message should be created with the correct index in filename.");

        String content = Files.readString(messageFile.toPath());
        assertTrue(content.contains("\"MESSAGE_ID\":\"" + sentMsg.getMessageID() + "\""), "Stored JSON should contain the correct Message ID.");
        assertTrue(content.contains("\"MESSAGE_INDEX\":" + sentMsg.getMessageIndex()), "Stored JSON should contain the correct MESSAGE_INDEX.");
        assertTrue(content.contains("\"MESSAGE_HASH\":\"" + sentMsg.getMessageHash() + "\""), "Stored JSON should contain the correct MESSAGE_HASH.");
        assertTrue(content.contains("\"MESSAGE_PAYLOAD\":\"This is a sent message.\""), "Stored JSON should contain the correct payload.");
        
        assertTrue(messageFile.delete(), "Cleanup: Failed to delete test file after test completion.");
    }

    // --- returnTotalMessages() Tests (basic cases) ---

    /**
     * Tests returnTotalMessages when no messages have been sent.
     * Expects the counter to be 0.
     */
    @Test
    public void testReturnTotalMessages_NoMessagesSent_ReturnsZero() {
        assertEquals(0, Message.returnTotalMessages(), "returnTotalMessages should return 0 initially.");
    }
}
