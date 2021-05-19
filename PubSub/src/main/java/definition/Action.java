package definition;

import java.util.HashSet;

/**
 * Represents structure that all messages should have. Every message should contains type, topic and message attributes.
 */
public class Action {
    /**
     * Possible types of action.
     */
    public enum TYPE {
        /**
         * Represents an action of publishing message.
         */
        PUBLISH,
        /**
         * Indicates that a subscriber should start receiving all messages from specified topic.
         */
        SUBSCRIBE,
        /**
         * Indicates that a subscriber should stop receiving all messages from specified topic.
         */
        UNSUBSCRIBE,
        /**
         * Indicates that a subscriber should stop receiving all messages from all topics.
         */
        UNSUBSCRIBEALL;
    }
    private String type;
    private String topic;
    private String message;

    /**
     * Empty constructor for serialization.
     */
    public Action() {}

    public Action(String type, String topic) {
        this(type, topic, null);
    }

    public Action(String type, String topic, String message) {
        this.type = type;
        this.topic = topic;
        this.message = message;
    }

    public String getType() {
        return this.type;
    }

    public String getTopic() {
        return this.topic;
    }

    public String getMessage() {
        return this.message;
    }

    /**
     * Returns all possible types of an Action.
     * @return Set of strings that represents possible types.
     */
    public static HashSet<String> getPossibleTypes () {
        HashSet<String> possibleTypes = new HashSet<>();
        for (TYPE type : TYPE.values()) {
            possibleTypes.add(type.name());
        }
        return possibleTypes;
    }

    @Override
    public String toString() {
        return "Action{" +
                "type=" + this.type +
                ", topic=" + this.topic +
                ", message=" + this.message +
                '}';
    }
}
