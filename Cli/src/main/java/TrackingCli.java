import filter.Arguments;
import filter.InputReader;
import messages.EventManager;
import messages.WebSocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Settings;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class TrackingCli {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static void main(String[] args) {
        Arguments arguments = new Arguments(args);
        arguments.read();
        if (arguments.containsHelpFlag) {
            return;
        }
        Set<String> accountIdsToDisplay = arguments.getAccountIds();
        try {

            if (accountIdsToDisplay == null || accountIdsToDisplay.isEmpty()) {
                System.out.print("Do you want to filter events based on account ids? (y/n):");
                Set<String> filterEvents = InputReader.readStream(System.in);
                if (filterEvents.contains("y")) {
                    System.out.print("Enter ids of accounts, delimited by space (0x20):");
                    accountIdsToDisplay = InputReader.readStream(System.in);
                }
            }
        } catch (Exception e) {
            TrackingCli.LOGGER.error(e.getMessage());
        }
        EventManager eventManager = new EventManager(accountIdsToDisplay);
        if (accountIdsToDisplay == null || accountIdsToDisplay.isEmpty()) {
            System.out.println("Displaying all events.");
        } else {
            System.out.println("Displaying events with accountId from:" + accountIdsToDisplay);
        }
        try {
            WebSocketConnection webSocketConnection = new WebSocketConnection( new URI(Settings.getSettings("settings.properties").getPubSubConnectionUrl()));
            webSocketConnection.setEventManager(eventManager);
        } catch (URISyntaxException e) {
            TrackingCli.LOGGER.error(e.getMessage());
        }
    }
}
