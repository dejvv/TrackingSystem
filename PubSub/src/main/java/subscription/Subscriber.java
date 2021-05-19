package subscription;

import io.javalin.websocket.WsContext;

import java.util.Objects;

/**
 * Represents a subscriber that can subscribe to messages from different topics. See {@link SubscriptionManager}.
 */
public class Subscriber {
    private WsContext websocketContext;
    public Subscriber(WsContext websocketContext) {
        this.websocketContext = Objects.requireNonNull(websocketContext);
    }

    public WsContext getWebsocketContext() {
        return this.websocketContext;
    }

    @Override
    public int hashCode () {
        return this.websocketContext.hashCode();
    }

    @Override
    public boolean equals (Object otherSubscriber) {
        return this.websocketContext.equals(otherSubscriber);
    }

    @Override
    public String toString() {
        return this.websocketContext.getSessionId();
    }
}
