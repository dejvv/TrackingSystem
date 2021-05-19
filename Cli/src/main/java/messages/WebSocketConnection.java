package messages;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.concurrent.*;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Settings;

public class WebSocketConnection extends WebSocketClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String ACCOUNTS_TOPIC = Settings.getSettings("settings.properties").getAccountsTopic();
    private EventManager eventManager;
    public WebSocketConnection(URI serverUri) {
        super(serverUri);
        this.connect();
    }

    /**
     * Attempts to reconnect to socket every 1000 ms. Reconnecting is taking max 1000 ms and takes place in separate thread.
     * This method is called in {@link #onClose(int, String, boolean)} when current socket connection is lost - when socket is closed.
     */
    public void delayedReconnect() {
        try {
            Thread.sleep(   1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (this.isOpen()) {
            WebSocketConnection.LOGGER.info("Already connected, reconnecting aborted.");
            return;
        }
        WebSocketConnection.LOGGER.info("Reconnecting...");
        Callable<Boolean> task0 = () -> {
            this.reconnect();
            return false;
        };
        ExecutorService executor0 = Executors.newFixedThreadPool(4);
        try {
            Future<Boolean> future = executor0.submit(task0);
            future.get(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.getMessage();
        } finally {
            executor0.shutdown();
        }
    }

    /**
     * Send an message to socket. Message is formatted as JSON object and consists of type, topic and message attributes.
     * This method is meant to send only one message to the PubSub component of the Tracking System. That happens when socket connection is established.
     * Message should be indicating that the current socket is subscribing to all the messages that will arrive to topic
     * defined in {@link #ACCOUNTS_TOPIC}.
     * See more at https://github.com/dejvv/trackingsystem.
     * @param type Type of message. Possible values are PUBLISH, SUBSCRIBE, UNSUBSCRIBE, UNSUBSCRIBEALL.
     * @param topic All messages that are published to the given topic will be received by the current socket (if type is SUBSCRIBE).
     *              If type is PUBLISH message will be published to the given topic.
     * @param message If not null, represents message that is send to PubSub component of the Tracking System.
     */
    private void sendMessage (String type, String topic, String message) {
        send("{\"type\":\"" + type + "\", \"topic\":\"" + topic + "\", \"message\":\"" + message + "\"}");
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        WebSocketConnection.LOGGER.info("Connected:"+this.getConnection().getRemoteSocketAddress());
        this.sendMessage("SUBSCRIBE", WebSocketConnection.ACCOUNTS_TOPIC, null);
    }

    @Override
    public void onMessage(String message) {
        this.eventManager.display(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        WebSocketConnection.LOGGER.info("Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);
        this.delayedReconnect();
    }

    @Override
    public void onError(Exception e) {
        WebSocketConnection.LOGGER.error("Error happend:"+e.getMessage());
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }
}
