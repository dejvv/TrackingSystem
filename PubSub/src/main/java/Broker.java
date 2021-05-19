import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import subscription.Subscriber;
import subscription.SubscriptionManager;
import definition.Action;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Set;

/**
 * Does some action based on the {@link Action} type.
 * Actions consists of publishing given {@link Action}s to {@link Subscriber}s and managing subscriptions to
 * the topics with help of {@link SubscriptionManager}.
 */
public class Broker {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private SubscriptionManager subscriptionManager;
    // stores error message that happened in current transaction while executing action (execute method). It is reset after every call to execute.
    private String lastErrorMessage;
    public Broker () {
        this.subscriptionManager = new SubscriptionManager();
        this.lastErrorMessage = "";
    }

    /**
     * Executes an action based on the {@link Action} type.
     * Action can represents publishing a message, subscription and unsubscription from the given topic and
     * unsubscription from all topics that exists.
     * @param action Based on the {@link Action.TYPE} some action is executed. {@link Action} also contains additional data,
     *                such as topic and some data. Should not be null.
     * @param subscriber {@link Subscriber} for which given action is performed. Should not be null.
     */
    public void execute(Action action, Subscriber subscriber) {
        this.lastErrorMessage = "";
        if (action == null || subscriber == null) {
            this.onError(new Exception("Action or Subscriber is missing"));
            return;
        }
        if (action.getType() == null || action.getType().isBlank()) {
            this.onError(new Exception("Action type is missing"));
            return;
        }
        if (!Action.getPossibleTypes().contains(action.getType())) {
            this.onError(new Exception("Wrong Action.TYPE. Should be something of " + Arrays.toString(Action.TYPE.values())));
            return;
        }
        switch (Action.TYPE.valueOf(action.getType())) {
            case PUBLISH: {
                this.publish(action.getTopic(), action.getMessage(), subscriber);
                break;
            }
            case SUBSCRIBE: {
                this.subscribe(action.getTopic(), subscriber);
                break;
            }
            case UNSUBSCRIBE: {
                this.unsubscribe(action.getTopic(), subscriber);
                break;
            }
            case UNSUBSCRIBEALL: {
                this.unsubscribeAll(subscriber);
                break;
            }
            default: {
                this.onError(new Exception("Wrong Action.TYPE. Should be something of " + Arrays.toString(Action.TYPE.values())));
                break;
            }
        }
    }

    /**
     * Publishes messages to all the {@link Subscriber}s that are subscribed to the {@code topic}.
     * @param topic The topic for which subscribed {@link Subscriber}s receives message. Should not be null or formed only from space characters.
     * @param message Data that should be send to all {@link Subscriber}s of the {@code topic}.
     * @param sender Represents {@link Subscriber} that has published the {@code message}. Will not receive self-published {@code message}s.
     */
    private void publish (String topic, String message, Subscriber sender) {
        try {
            if (topic == null || topic.isBlank()) {
                throw new Exception("Topic is null or formed only from space characters");
            }
            if (message == null || message.isBlank()) {
                throw new Exception("Message is null or formed only from space characters");
            }
            if (sender == null) {
                throw new Exception("Sender is null");
            }
            Set<Subscriber> subscribers = this.subscriptionManager.getTopicSubscribers(topic);
            subscribers
                    .stream()
                    .filter(subscriber -> subscriber != sender)
                    .forEach(subscriber -> subscriber.getWebsocketContext().send(message));
        } catch (Exception e) {
            this.onError(e);
        }
    }

    /**
     * Creates the subscription to the given {@code topic} with the help of a {@link SubscriptionManager}.
     * That means that the {@code subscriber} will receive all messages that will be published to the {@code topic}.
     * @param topic The topic to which subscription will be created.
     * @param subscriber {@link Subscriber} that will start receiving messages to the {@code topic}.
     */
    private void subscribe (String topic, Subscriber subscriber) {
        try {
            if (topic == null || topic.isBlank()) {
                throw new Exception("Topic is null or formed only from space characters");
            }
            if (subscriber == null) {
                throw new Exception("Subscriber is null");
            }
            this.subscriptionManager.addSubscription(topic, subscriber);
            Broker.LOGGER.info("Added subscription, subscriber: " + subscriber + ", topic: " + topic);
        } catch (Exception e) {
            this.onError(e);
        }
    }

    /**
     * Unsubscribe the {@code subscriber} from the given {@code topic} with the help of a {@link SubscriptionManager}.
     * That means that the {@code subscriber} will stop receiving messages that are published to the {@code topic}.
     * @param topic The topic from which subscription will be removed.
     * @param subscriber {@link Subscriber} that will stop receiving messages published to the {@code topic}.
     */
    private void unsubscribe (String topic, Subscriber subscriber) {
        try {
            if (topic == null || topic.isBlank()) {
                throw new Exception("Topic is null or formed only from space characters");
            }
            if (subscriber == null) {
                throw new Exception("Subscriber is null");
            }
            this.subscriptionManager.removeSubscription(topic, subscriber);
            Broker.LOGGER.info("Removed subscription, subscriber: " + subscriber + ", topic: " + topic);
        } catch (Exception e) {
            this.onError(e);
        }
    }

    /**
     * Unsubscribe the {@code subscriber} from all topics with the help of a {@link SubscriptionManager}.
     * That means that the {@code subscriber} will stop receiving messages that are published to any of the available topics.
     * @param subscriber {@link Subscriber} that will stop receiving messages published to any of the available topics.
     */
    private void unsubscribeAll (Subscriber subscriber) {
        try {
            if (subscriber == null) {
                throw new Exception("Subscriber is null");
            }
            this.subscriptionManager.removeAllSubscriptions(subscriber);
            Broker.LOGGER.info("Removed all subscriptions, subscriber: " + subscriber);
        } catch (Exception e) {
            this.onError(e);
        }
    }

    /**
     * Logs the error message and stack trace that the given {@code exception} contains. If the give {@code exception}
     * is null, a message explaining that the given {@code exception} is null is logged.
     * @param exception Exception that has occurred and contains info about error message and stack trace.
     *                  Should not be null.
     */
    private void onError (Exception exception) {
        this.lastErrorMessage = "Exception is null";
        if (exception != null) {
            this.lastErrorMessage = exception.getMessage();
            Broker.LOGGER.error(exception.getMessage(), exception);
        } else {
            Broker.LOGGER.error(this.lastErrorMessage);
        }
    }

    public String getLastErrorMessage() {
        return this.lastErrorMessage;
    }
}
