package quickchat.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser; // For reading JSON
import org.json.simple.parser.ParseException; // For handling JSON parsing errors

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList; // For checking list contents

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the Message class in the QuickChat application.
 * This class tests core messaging functionalities including validation,
 * hash creation, sending, storage, and Part 3 array operations.
 * All tests are designed using JUnit 5.
 */
public class MessageTest {

    // Test data for Part 2 specific tests (can remain for those tests)
    private final String validRecipientP2 = "+27718693002"; // Renamed to avoid confusion
    private final String messagePayloadP2 = "Hi Mike, can you join us for dinner tonight";
    private final String invalidRecipientP2 = "08575975889";

    private Message defaultMessageP2; // For Part 2 tests
    private final String testUser = "testUser"; // Default sender for most tests

    /**
     * Sets up the testing environment before each test method.
     * Resets static counters/lists in Message class and populates Part 3 test data.
     */
    @BeforeEach
    public void setUp() {
        Message.resetMessageCounterForTesting(); // Clears lists and messageCounter
        Message.populateWithPart3TestData();     // Load Part 3 specific test data
        defaultMessageP2 = new Message(validRecipientP2, messagePayloadP2, testUser); // For existing Part 2 tests
    }

    // --- Part 2 Tests (Mostly Unchanged from your provided file) ---

    @Test
    public void testMessageConstructor_InitializesPropertiesCorrectly() {
        Message msg = new Message("testRecipient", "testPayload", testUser);
        assertNotNull(msg.getMessageID(), "Message ID should be automatically generated and not null.");
        assertTrue(msg.getMessageID().matches("\\d{10}"), "Message ID should consist of 10 digits.");
        assertEquals("testRecipient", msg.getMessageRecipient(), "Recipient should match the constructor argument.");
        assertEquals("testPayload", msg.getMessagePayload(), "Payload should match the constructor argument.");
        assertEquals(testUser, msg.getSenderUsername(), "Sender username should match.");
        assertEquals(0, msg.getMessageIndex(), "Initial message index should be 0.");
        assertEquals("", msg.getMessageHash(), "Initial message hash should be an empty string.");
    }

    @Test
    public void testGetGeneratedIdNotification_ReturnsCorrectFormat() {
        String notification = defaultMessageP2.getGeneratedIdNotification();
        assertTrue(notification.startsWith("Message ID generated: "), "Notification string format is incorrect.");
        assertEquals(defaultMessageP2.getMessageID(), notification.substring("Message ID generated: ".length()), "Notification should accurately contain the generated Message ID.");
    }

    @Test
    public void testValidatePayloadLength_ValidPayload_ReturnsSuccessMessage() {
        String shortPayload = "Hello";
        assertEquals("Message ready to send.", defaultMessageP2.validatePayloadLength(shortPayload));
        String exactLengthPayload = new String(new char[250]).replace('\0', 'a');
        assertEquals("Message ready to send.", defaultMessageP2.validatePayloadLength(exactLengthPayload));
    }

    @Test
    public void testValidatePayloadLength_TooLongPayload_ReturnsFailureMessageWithExcessCount() {
        String longPayload = new String(new char[251]).replace('\0', 'a');
        assertEquals("Message exceeds 250 characters by 1, please reduce size.", defaultMessageP2.validatePayloadLength(longPayload));
    }
    
    @Test
    public void testValidatePayloadLength_EmptyPayload_ReturnsSuccessMessage() {
        assertEquals("Message ready to send.", defaultMessageP2.validatePayloadLength(""));
    }

    @Test
    public void testValidatePayloadLength_NullPayload_ReturnsFailureMessage() {
        assertEquals("Message exceeds 250 characters by -250, please reduce size.", defaultMessageP2.validatePayloadLength(null));
    }
    
    @Test
    public void testValidateRecipientNumber_ValidFormat_ReturnsSuccessMessage() {
        assertEquals("Cell phone number successfully captured.", defaultMessageP2.validateRecipientNumber("+27123456789"));
    }
    
    @Test
    public void testValidateRecipientNumber_POESpecialCaseFormat_ReturnsSuccessMessage() {
        // Test for the specific format from POE Part 3 Test Data 4
        assertEquals("Cell phone number successfully captured.", defaultMessageP2.validateRecipientNumber("0838884567"));
    }

    @Test
    public void testValidateRecipientNumber_InvalidFormat_ReturnsFailureMessage() {
        String expectedError = "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        assertEquals(expectedError, defaultMessageP2.validateRecipientNumber("0712345678")); // Missing +27
        assertEquals(expectedError, defaultMessageP2.validateRecipientNumber("+2712345678"));  // Too short
        assertEquals(expectedError, defaultMessageP2.validateRecipientNumber("invalid"));
    }

    @Test
    public void testCreateMessageHash_WithSpecifiedData_ReturnsCorrectHash() {
        String testID = "0012345678";
        int testIndex = 0; // For Part 2 test case, index is 0
        String payload = "Hi Mike, can you join us for dinner tonight";
        // Expected: 00:0:HITONIGHT
        assertEquals("00:0:HITONIGHT", defaultMessageP2.createMessageHash(testID, testIndex, payload));
    }

    @Test
    public void testSentMessage_InvalidRecipient_ReturnsFailure() {
        Message msg = new Message(invalidRecipientP2, messagePayloadP2, testUser);
        assertEquals("Failed to send message: Invalid recipient", msg.sentMessage());
        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    public void testSentMessage_PayloadTooLong_ReturnsFailure() {
        String longPayload = new String(new char[251]).replace('\0', 'a');
        Message msg = new Message(validRecipientP2, longPayload, testUser);
        assertEquals("Failed to send message: Payload too long", msg.sentMessage());
        assertEquals(0, Message.returnTotalMessages());
    }
    
    @Test
    public void testSentMessage_EmptyPayload_ReturnsFailure() {
        Message msg = new Message(validRecipientP2, "   ", testUser);
        assertEquals("Failed to send message: Message content cannot be empty", msg.sentMessage());
        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    public void testStoreMessage_Draft_CreatesFileWithIndex0AndSender(@TempDir Path tempDir) throws IOException {
        // Use a unique filename for testStoreMessage_Draft to avoid conflicts if run in parallel or if files are not cleaned perfectly.
        // Path.resolve helps create a path within the temporary directory.
        String sender = "draftUser";
        Message draftMessage = new Message(validRecipientP2, "This is a draft.", sender);
        // Ensure it's a draft (index 0, empty hash initially)
        draftMessage.setMessageIndex(0);
        draftMessage.setMessageHash("");

        // Expected filename format from Message.storeMessage() for drafts
        String expectedFileNameSuffix = "message_draft_" + draftMessage.getMessageID() + ".json";
        File messageFile = tempDir.resolve(expectedFileNameSuffix).toFile();
    
        // Override the default file writing path to use tempDir for this test
        // This requires a way to tell storeMessage to write to a specific path,
        // or the test needs to manage files in the default path.
        // For simplicity with current storeMessage, we assume it writes to current dir and we'll clean it up.
        // If storeMessage is refactored to take a path, this test becomes cleaner.
        // Let's simulate storeMessage by directly checking its logic if it wrote to default path:
        String defaultPathFileName = "message_draft_" + draftMessage.getMessageID() + ".json";
        File defaultPathFile = new File(defaultPathFileName);
        if (defaultPathFile.exists()) {
            assertTrue(defaultPathFile.delete(), "Cleanup: Failed to delete pre-existing test file in working directory.");
        }

        assertEquals("Message successfully stored as " + defaultPathFileName, draftMessage.storeMessage());
        assertTrue(defaultPathFile.exists(), "JSON file for the draft message should be created in the working directory.");

        String content = Files.readString(defaultPathFile.toPath());
        assertTrue(content.contains("\"MESSAGE_ID\":\"" + draftMessage.getMessageID() + "\""));
        assertTrue(content.contains("\"SENDER_USERNAME\":\"" + sender + "\""));
        assertTrue(content.contains("\"MESSAGE_INDEX\":0"));
        assertTrue(content.contains("\"MESSAGE_HASH\":\"\""));
        assertTrue(content.contains("\"MESSAGE_PAYLOAD\":\"This is a draft.\""));

        assertTrue(defaultPathFile.delete(), "Cleanup: Failed to delete test file from working directory after test completion.");
    }


    @Test
    public void testStoreMessage_SentMessage_CreatesFileWithCorrectIndexAndHash(@TempDir Path tempDir) throws IOException {
        Message sentMsg = new Message(validRecipientP2, "This is a sent message.", testUser);
        sentMsg.sentMessage(); // This will set index and hash, and add to sentMessages list

        assertNotEquals(0, sentMsg.getMessageIndex(), "Index of a sent message should not be 0.");
        assertFalse(sentMsg.getMessageHash().isEmpty(), "Hash of a sent message should not be empty.");

        // Filename based on Message.java logic
        String expectedFileName = "message_" + sentMsg.getMessageIndex() + ".json";
        File messageFile = tempDir.resolve(expectedFileName).toFile(); // Using tempDir

        // Temporarily adjust storeMessage to write to tempDir or check working dir
        // For now, assume storeMessage writes to working dir
        File defaultPathFile = new File(expectedFileName);
         if (defaultPathFile.exists()) {
            assertTrue(defaultPathFile.delete(), "Cleanup: Failed to delete pre-existing test file in working directory.");
        }

        assertEquals("Message successfully stored as " + expectedFileName, sentMsg.storeMessage());
        assertTrue(defaultPathFile.exists(), "JSON file for the sent message should be created in working directory.");

        String content = Files.readString(defaultPathFile.toPath());
        assertTrue(content.contains("\"MESSAGE_ID\":\"" + sentMsg.getMessageID() + "\""));
        assertTrue(content.contains("\"SENDER_USERNAME\":\"" + testUser + "\""));
        assertTrue(content.contains("\"MESSAGE_INDEX\":" + sentMsg.getMessageIndex()));
        assertTrue(content.contains("\"MESSAGE_HASH\":\"" + sentMsg.getMessageHash() + "\""));
        assertTrue(content.contains("\"MESSAGE_PAYLOAD\":\"This is a sent message.\""));
        
        assertTrue(defaultPathFile.delete(), "Cleanup: Failed to delete test file from working directory.");
    }


    // --- Part 3 Unit Tests ---

    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        // populateWithPart3TestData() is called in setUp()
        ArrayList<Message> sent = Message.getSentMessagesList();
        assertEquals(2, sent.size(), "Should be 2 sent messages from test data.");

        // Check payloads of sent messages as per POE
        String expectedPayload1 = "Did you get the cake?"; // Msg1
        String expectedPayload4 = "It is dinner time!";   // Msg4

        boolean foundPayload1 = false;
        boolean foundPayload4 = false;

        for (Message msg : sent) {
            if (msg.getMessagePayload().equals(expectedPayload1)) {
                foundPayload1 = true;
            }
            if (msg.getMessagePayload().equals(expectedPayload4)) {
                foundPayload4 = true;
            }
        }
        assertTrue(foundPayload1, "Sent messages should contain: '" + expectedPayload1 + "'");
        assertTrue(foundPayload4, "Sent messages should contain: '" + expectedPayload4 + "'");
    }

    @Test
    public void testDisplayLongestMessage() {
        // populateWithPart3TestData() is called in setUp()
        // POE Expected: "Where are you? You are late! I have asked you to be on time." (Message 2, Stored)
        String result = Message.displayLongestMessageOverall();
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."),
                   "The longest message should be Message 2's payload.");
        assertTrue(result.contains(Message.getStoredMessagesList().get(0).getMessageRecipient()), "Result should contain recipient of longest message");
         assertTrue(result.contains(Message.getStoredMessagesList().get(0).getSenderUsername()), "Result should contain sender of longest message");
    }

    @Test
    public void testSearchForMessageID() {
        // populateWithPart3TestData() is called in setUp()
        // POE Test Data: message 4. Expected: "0838884567", ""It is dinner time!""
        Message msg4 = null;
        for(Message m : Message.getSentMessagesList()){
            if(m.getMessagePayload().equals("It is dinner time!")){
                msg4 = m;
                break;
            }
        }
        assertNotNull(msg4, "Test message 4 should be found in sent list for this test setup.");

        String result = Message.searchMessageById(msg4.getMessageID());
        assertTrue(result.contains("0838884567"), "Search result should contain recipient of Message 4.");
        assertTrue(result.contains("It is dinner time!"), "Search result should contain payload of Message 4.");
        assertTrue(result.contains(msg4.getSenderUsername()), "Search result should contain sender of Message 4.");
    }

    @Test
    public void testSearchMessagesByRecipient() {
        // populateWithPart3TestData() is called in setUp()
        // POE Test Data: Recipient "+27838884567"
        // Expected messages:
        // 1. "Where are you? You are late! I have asked you to be on time." (Msg2, Stored)
        // 2. "Ok, I am leaving without you." (Msg5, Stored)
        String recipientToSearch = "+27838884567";
        String result = Message.searchMessagesByRecipient(recipientToSearch);

        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."), "Result should contain Message 2's payload.");
        assertTrue(result.contains("Ok, I am leaving without you."), "Result should contain Message 5's payload.");
        
        // Also check sender information is present for these messages in the output string
        // (Assuming 'testUser' was the sender for these in populateWithPart3TestData)
        String msg2Sender = "";
        String msg5Sender = "";

        for(Message m : Message.getStoredMessagesList()){
            if(m.getMessagePayload().equals("Where are you? You are late! I have asked you to be on time.")) msg2Sender = m.getSenderUsername();
            if(m.getMessagePayload().equals("Ok, I am leaving without you.")) msg5Sender = m.getSenderUsername();
        }
        
        assertTrue(result.contains("Sender: " + msg2Sender) && result.indexOf("Where are you? You are late!") > result.indexOf("Sender: " + msg2Sender),
           "Sender info should be linked with Message 2's payload.");
        assertTrue(result.contains("Sender: " + msg5Sender) && result.indexOf("Ok, I am leaving without you.") > result.indexOf("Sender: " + msg5Sender),
           "Sender info should be linked with Message 5's payload.");
    }

    @Test
    public void testDeleteMessageByHash() {
        // populateWithPart3TestData() is called in setUp()
        // POE Test: Delete Message 2 ("Where are you? You are late! I have asked you to be on time.")
        Message msg2ToDelete = null;
        // Find Message 2 from the Stored list (as per populateWithPart3TestData)
        for (Message m : Message.getStoredMessagesList()) {
            if (m.getMessagePayload().equals("Where are you? You are late! I have asked you to be on time.")) {
                msg2ToDelete = m;
                break;
            }
        }
        assertNotNull(msg2ToDelete, "Message 2 should exist in StoredMessagesList before deletion.");
        String hashOfMsg2 = msg2ToDelete.getMessageHash();
        assertNotNull(hashOfMsg2, "Hash for Message 2 should not be null.");
        assertFalse(hashOfMsg2.isEmpty(), "Hash for Message 2 should not be empty for this test.");

        String deleteResult = Message.deleteMessageByHash(hashOfMsg2);
        assertEquals("Message \"Where are you? You are late! I have asked you to be on time.\" successfully deleted from Stored Messages.", deleteResult,
                     "Deletion message should confirm successful deletion of Message 2.");

        // Verify it's removed from the list
        boolean stillExists = false;
        for (Message m : Message.getStoredMessagesList()) {
            if (m.getMessageHash().equals(hashOfMsg2)) {
                stillExists = true;
                break;
            }
        }
        assertFalse(stillExists, "Message 2 should no longer be in StoredMessagesList after deletion.");
    }

    @Test
    public void testDisplayReport() {
        // populateWithPart3TestData() is called in setUp()
        // Report should contain details of sent messages (Msg1 and Msg4 from test data)
        String report = Message.displaySentMessagesReport();

        assertTrue(report.contains("--- Sent Messages Report ---"), "Report should have a title.");

        // Check for Message 1 details
        assertTrue(report.contains("+27834557896"), "Report should contain recipient of Message 1.");
        assertTrue(report.contains("Did you get the cake?"), "Report should contain payload of Message 1.");
        Message msg1 = Message.getSentMessagesList().stream().filter(m -> m.getMessagePayload().equals("Did you get the cake?")).findFirst().orElse(null);
        assertNotNull(msg1);
        assertTrue(report.contains(msg1.getMessageHash()), "Report should contain hash of Message 1.");
        assertTrue(report.contains("Sender: " + msg1.getSenderUsername()), "Report should contain sender of Message 1.");


        // Check for Message 4 details
        assertTrue(report.contains("0838884567"), "Report should contain recipient of Message 4.");
        assertTrue(report.contains("It is dinner time!"), "Report should contain payload of Message 4.");
        Message msg4 = Message.getSentMessagesList().stream().filter(m -> m.getMessagePayload().equals("It is dinner time!")).findFirst().orElse(null);
        assertNotNull(msg4);
        assertTrue(report.contains(msg4.getMessageHash()), "Report should contain hash of Message 4.");
        assertTrue(report.contains("Sender: " + msg4.getSenderUsername()), "Report should contain sender of Message 4.");
        
        assertFalse(report.contains("Where are you? You are late!"), "Report should NOT contain details of non-sent messages (e.g. Message 2).");
    }
    
    // Test for loading from JSON (basic check, relies on file presence)
    @Test
    public void testLoadStoredMessagesFromJSON_LoadsSomething(@TempDir Path tempDir) throws IOException {
        // Create a dummy JSON file that matches the expected format
        String senderForJson = "jsonUser";
        Message sampleMsg = new Message("jsonID12345", "+27999888777", "Payload from JSON", 1, "JS:1:PAYLOADFROM", senderForJson);
        
        JSONObject json = new JSONObject();
        json.put("MESSAGE_ID", sampleMsg.getMessageID());
        json.put("MESSAGE_RECIPIENT", sampleMsg.getMessageRecipient());
        json.put("MESSAGE_PAYLOAD", sampleMsg.getMessagePayload());
        json.put("MESSAGE_INDEX", (long)sampleMsg.getMessageIndex()); // JSON simple might store numbers as Long
        json.put("MESSAGE_HASH", sampleMsg.getMessageHash());
        json.put("SENDER_USERNAME", sampleMsg.getSenderUsername());

        File jsonFile = tempDir.resolve("message_1.json").toFile();
        try (FileWriter fileWriter = new FileWriter(jsonFile)) {
            fileWriter.write(json.toJSONString());
        }

        Message.resetMessageCounterForTesting(); // Clear any existing test data
        Message.loadStoredMessagesFromJSON(tempDir.toString(), "loggedInUserForJsonTest");
        
        ArrayList<Message> loadedMessages = Message.getStoredMessagesList();
        assertFalse(loadedMessages.isEmpty(), "Stored messages list should not be empty after loading from JSON.");
        
        boolean foundSample = loadedMessages.stream()
                                .anyMatch(m -> m.getMessageID().equals("jsonID12345") &&
                                               m.getMessagePayload().equals("Payload from JSON") &&
                                               m.getSenderUsername().equals(senderForJson)); // Sender from JSON should be prioritized
        assertTrue(foundSample, "The sample message from the JSON file should be loaded correctly.");
    }
}