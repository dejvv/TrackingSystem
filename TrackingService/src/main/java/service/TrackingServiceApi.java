package service;

import io.javalin.Javalin;
import message.Dispatcher;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Settings;

import java.lang.invoke.MethodHandles;
import java.net.Inet4Address;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;

/**
 * Runs an http server that will check for account validity (path /:accountId). If the given account is valid
 * message with data is send over network to the PubSub component.
 */
public class TrackingServiceApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String ACCOUNTS_TOPIC = Settings.getSettings("settings.properties").getAccountsTopic();

    public static void main(String[] args) {
        String currentHost = "undefined";
        try {
            currentHost = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        TrackingService trackingService = new TrackingService();
        Dispatcher dispatcher = null;
        try {
            Settings settings = Settings.getSettings("settings.properties");
            dispatcher = new Dispatcher(new URI(settings.getPubSubConnectionUrl()));
            dispatcher.connect();
        } catch (URISyntaxException e) {
            TrackingServiceApi.LOGGER.error("Error connecting to PubSub:" + e.getMessage());
            e.printStackTrace();
        }
        Javalin app = Javalin.create()
                .events(event -> {
                    event.serverStarted(() -> trackingService.databaseConnect());
                    event.serverStartFailed(() -> trackingService.databaseDisconnect());
                    event.serverStopping(() -> trackingService.databaseDisconnect());
                })
                .start(7000);

        Dispatcher finalDispatcher = dispatcher;
        String finalCurrentHost = currentHost;
        app.get("/:accountId", ctx -> {
            String accountId = ctx.pathParam("accountId");
            String data = ctx.queryParam("data");
            final CompletableFuture<Boolean> accountValid = trackingService.isAccountValid(accountId);
            ctx.result(
                accountValid
                    .thenApply((isActive) -> {
                        if (finalDispatcher != null && isActive) {
                            String message = accountId + ";" + DateTime.now().toString() + ";" + data;
                            finalDispatcher.sendMessage("PUBLISH", TrackingServiceApi.ACCOUNTS_TOPIC, message);
                        }
                        return "isActive:" + String.valueOf(isActive) + ";host:" + finalCurrentHost;
                    })
                    .exceptionally((exception) -> {
                        TrackingServiceApi.LOGGER.error("Error fetching account data:"+exception.getMessage());
                        return String.valueOf(false);
                    })
            );
        });
//        System.out.println("[TrackingServiceApi @ main] starting...");
//        TrackingService trackingService = new TrackingService();
//        System.out.println("[TrackingServiceApi @ main] running");
//        Javalin app = Javalin.create().start(7000);

//        app.get("/:accountId", ctx -> {
//            String accountId = ctx.pathParam("accountId");
//            System.out.println("[TrackingServiceApi @ main] accountid:"+accountId);
//            Boolean isAccountValid = trackingService.isAccountValid(accountId);
//            ctx.result(String.valueOf(isAccountValid));
//        });
    }
}
