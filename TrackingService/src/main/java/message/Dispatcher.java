package message;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.concurrent.*;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dispatcher extends WebSocketClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public Dispatcher(URI serverUri) {
        super(serverUri);
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
            Dispatcher.LOGGER.info("Already connected, reconnecting aborted.");
            return;
        }
        Dispatcher.LOGGER.info("Reconnecting...");
        Callable<Boolean> task0 = () -> {
            this.reconnect();
            return false;
        };
        ExecutorService executor0 = Executors.newFixedThreadPool(4);
        try {
            Future<Boolean> future = executor0.submit(task0);
            future.get(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | NullPointerException | TimeoutException e) {
            e.getMessage();
        } finally {
            executor0.shutdown();
        }
    }

    /**
     * Send an message to socket. Message is formatted as JSON object and consists of type, topic and message attributes.
     * Current format is assured to be able to communicate with PubSub component of the Tracking System.
     * See more at https://github.com/dejvv/trackingsystem.
     * @param type Type of message. Possible values are PUBLISH, SUBSCRIBE, UNSUBSCRIBE, UNSUBSCRIBEALL.
     * @param topic All messages that are published to the given topic will be received by the current socket (if type is SUBSCRIBE).
     *              If type is PUBLISH message will be published to the given topic.
     * @param message If not null, represents message that is send to PubSub component of the Tracking System.
     */
    public void sendMessage (String type, String topic, String message) {
        send("{\"type\":\"" + type + "\", \"topic\":\"" + topic + "\", \"message\":\"" + message + "\"}");
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Dispatcher.LOGGER.info("Connected:"+this.getConnection().getRemoteSocketAddress());
    }

    @Override
    public void onMessage(String message) {
        Dispatcher.LOGGER.info("Message received:"+message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The code codes are documented in class org.java_websocket.framing.CloseFrame
        Dispatcher.LOGGER.info("Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);
        this.delayedReconnect();
    }

    @Override
    public void onError(Exception e) {
        Dispatcher.LOGGER.error(e.getMessage());
    }
}
