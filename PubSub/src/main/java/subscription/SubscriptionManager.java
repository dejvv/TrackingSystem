package subscription;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages subscriptions to certain topics.
 * Topics are represented as plain Strings.
 * {@link Subscriber}s can subscribe to topics.
 */
public class SubscriptionManager {
    private Map<String, Set<Subscriber>> subscriptionMap; //  which subscribers are subscribed to a topic, {topic => set of subscribes}
    public SubscriptionManager () {
        this.subscriptionMap = new ConcurrentHashMap<>();
    }

    /**
     * Establish subscription to the given topic.
     * @param topic The topic to which subscription is created. Should not be null or only formed of space characters.
     * @param subscriber {@link Subscriber} that is subscribing to messages from the topic. Should not be null.
     * @throws SubscriptionManagerException
     */
    public void addSubscription (String topic, Subscriber subscriber) throws SubscriptionManagerException {
        this.requireNonNullOrEmptyTopic(topic);
        this.requireNonNullOrEmptySubscriber(subscriber);
        Set<Subscriber> subscribers = this.subscriptionMap.get(topic);
        if (subscribers == null) {
            subscribers = new HashSet<>();
//            subscribers.addAll(this.broadcastSubscribers);
        }
        subscribers.add(subscriber);
        this.subscriptionMap.put(topic, subscribers);
    }

    /**
     * Removes subscription to the given topic for the {@link Subscriber}.
     * @param topic Topic from which the {@link Subscriber} should be removed. Should not be null or only formed of space characters.
     * @param subscriber {@link Subscriber} which is unsubscribing from the given topic. Should not be null.
     * @throws SubscriptionManagerException
     */
    public void removeSubscription (String topic, Subscriber subscriber) throws SubscriptionManagerException {
        this.requireNonNullOrEmptyTopic(topic);
        this.requireNonNullOrEmptySubscriber(subscriber);
        Set<Subscriber> subscribers = this.subscriptionMap.get(topic);
        if (subscribers == null) {
            return;
        }
        subscribers.remove(subscriber);
        if (subscribers.size() == 0) {
            this.subscriptionMap.remove(topic);
        } else {
            this.subscriptionMap.put(topic, subscribers);
        }
    }

    /**
     * Removes subscriptions to all topics that the given {@link Subscriber} is subscribed to.
     * @param subscriber {@link Subscriber} which is unsubscribing from all topics that exists. Should not be null.
     * @throws SubscriptionManagerException
     */
    public void removeAllSubscriptions (Subscriber subscriber) throws SubscriptionManagerException {
        // 08.02.2021, David Zagoršek - optimisation - if there is a lot of topics, it makes sense that each subscribers is aware of it's topics => store topics on subscriber
        this.requireNonNullOrEmptySubscriber(subscriber);
        for (String currentTopic : this.subscriptionMap.keySet()) {
            this.removeSubscription(currentTopic, subscriber);
        }
    }

    /**
     * Finds {@link Subscriber}s that are subscribed to the given topic.
     * @param topic Topic for which all the {@link Subscriber}s should be received. Should not be null or only formed of space characters.
     * @return Set of {@link Subscriber}s that are subscribed to the given topic.
     */
    public Set<Subscriber> getTopicSubscribers (String topic) {
        Set<Subscriber> subscribers = null;
        if (topic != null && !topic.isBlank()) {
            subscribers = this.subscriptionMap.get(topic);
        }
        if (subscribers == null) {
            subscribers = new HashSet<>();
        }
        return subscribers;
    }

    /**
     * Checks whether the given topic is null or contains only space characters. In that case {@link SubscriptionManagerException}
     * is thrown with the explanation that the topic is required parameter.
     * @param topic Topic. Should not be null or only formed of space characters.
     * @throws SubscriptionManagerException
     */
    private void requireNonNullOrEmptyTopic (String topic) throws SubscriptionManagerException {
        SubscriptionManagerUtil.requireNonNullOrEmpty(topic, SubscriptionManagerException.ERROR_MESSAGE_TOPIC_BLANK);
    }

    /**
     * Checks whether the given {@link Subscriber} is null. In that case {@link SubscriptionManagerException}
     * is thrown with the explanation that the {@link Subscriber} is required parameter.
     * @param subscriber {@link} Subscriber. Should not be null.
     * @throws SubscriptionManagerException
     */
    private void requireNonNullOrEmptySubscriber (Subscriber subscriber) throws SubscriptionManagerException {
        SubscriptionManagerUtil.requireNonNullOrEmpty(subscriber, SubscriptionManagerException.ERROR_MESSAGE_SUBSCRIBER_NULL);
    }

    public Map<String, Set<Subscriber>> getSubscriptionMap() {
        return this.subscriptionMap;
    }
}
