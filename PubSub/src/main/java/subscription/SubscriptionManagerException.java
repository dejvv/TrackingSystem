package subscription;

public class SubscriptionManagerException extends Exception {
    public static final String ERROR_MESSAGE_TOPIC_BLANK = "Topic is null or formed only from space characters";
    public static final String ERROR_MESSAGE_SUBSCRIBER_NULL = "Subscriber is null";
    public SubscriptionManagerException (String message) {
        super(message);
    }
}