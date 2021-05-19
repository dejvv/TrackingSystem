package messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;

public class EventManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String MESSAGE_DELIMITER = ";";
    Set<String> filters;
    public EventManager(Set<String> filters) {
        if (filters == null) {
            filters = new HashSet<>();
        }
        this.filters = filters;
    }

    /**
     * Transforms and outputs message in JSON format to stdout.
     * @param message Message to be outputted to stdout. Should consist of 3 parts separated by a semicolon.
     */
    public void display (String message) {
        if (message.isBlank()) {
            EventManager.LOGGER.warn("Message is blank and can not be displayed.");
            return;
        }
        String[] messageParts = message.split(EventManager.MESSAGE_DELIMITER); // accountId;accountName;timestamp
        if (messageParts.length != 3) {
            EventManager.LOGGER.warn("Message consists of " + messageParts.length + " parts. Should consist of 3 parts.");
            return;
        }
        if (this.filters.size() != 0) {
            if (!this.filters.contains(messageParts[0])) {
                return;
            }
        }
        String displayedMessage = "{\"accountId\":\"" + messageParts[0] + "\", \"timestamp\":\"" + messageParts[1] + "\", \"data\":\"" + messageParts[2] + "\"}";
        System.out.println(displayedMessage);
    }
}
