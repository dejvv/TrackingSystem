package subscription;

import io.javalin.websocket.WsContext;
import org.eclipse.jetty.websocket.api.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionManagerTest {
    @Test
    public void addSubscription() {
        WsContext wsContext = new WsContextTest("test session id", new SessionTest());
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        boolean errorHappend = false;
        String topic = "test topic";
        try {
            subscriptionManager.addSubscription(topic, new Subscriber(wsContext));
        } catch (SubscriptionManagerException e) {
            errorHappend = true;
        }
        assertFalse(errorHappend);
        assertNotEquals(null, subscriptionManager.getSubscriptionMap());
        assertEquals(1, subscriptionManager.getSubscriptionMap().size());
        assertNotNull(subscriptionManager.getTopicSubscribers(topic));
        assertEquals(1, subscriptionManager.getTopicSubscribers(topic).size());
    }

    @Test
    public void addSubscriptionTopicNull() {
        WsContext wsContext = new WsContextTest("test session id", new SessionTest());
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        boolean errorHappend = false;
        String errorMessage = null;
        String topic = null;
        try {
            subscriptionManager.addSubscription(topic, new Subscriber(wsContext));
        } catch (SubscriptionManagerException e) {
            errorHappend = true;
            errorMessage = e.getMessage();
        }
        assertTrue(errorHappend);
        assertEquals(SubscriptionManagerException.ERROR_MESSAGE_TOPIC_BLANK, errorMessage);
        assertNotEquals(null, subscriptionManager.getSubscriptionMap());
        assertEquals(0, subscriptionManager.getSubscriptionMap().size());
    }

    @Test
    public void addSubscriptionTopicEmpty() {
        WsContext wsContext = new WsContextTest("test session id", new SessionTest());
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        boolean errorHappend = false;
        String errorMessage = null;
        String topic = "";
        try {
            subscriptionManager.addSubscription(topic, new Subscriber(wsContext));
        } catch (SubscriptionManagerException e) {
            errorHappend = true;
            errorMessage = e.getMessage();
        }
        assertTrue(errorHappend);
        assertEquals(SubscriptionManagerException.ERROR_MESSAGE_TOPIC_BLANK, errorMessage);
        assertNotEquals(null, subscriptionManager.getSubscriptionMap());
        assertEquals(0, subscriptionManager.getSubscriptionMap().size());
    }

    @Test
    public void addSubscriptionTopicOnlySpaces() {
        WsContext wsContext = new WsContextTest("test session id", new SessionTest());
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        boolean errorHappend = false;
        String errorMessage = null;
        String topic = "        ";
        try {
            subscriptionManager.addSubscription(topic, new Subscriber(wsContext));
        } catch (SubscriptionManagerException e) {
            errorHappend = true;
            errorMessage = e.getMessage();
        }
        assertTrue(errorHappend);
        assertEquals(SubscriptionManagerException.ERROR_MESSAGE_TOPIC_BLANK, errorMessage);
        assertNotEquals(null, subscriptionManager.getSubscriptionMap());
        assertEquals(0, subscriptionManager.getSubscriptionMap().size());
    }

    @Test
    public void addSubscriptionSubscriberNull() {
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        boolean errorHappend = false;
        String errorMessage = null;
        String topic = "test topic";
        try {
            subscriptionManager.addSubscription(topic, null);
        } catch (SubscriptionManagerException e) {
            errorHappend = true;
            errorMessage = e.getMessage();
        }
        assertTrue(errorHappend);
        assertEquals(SubscriptionManagerException.ERROR_MESSAGE_SUBSCRIBER_NULL, errorMessage);
        assertNotEquals(null, subscriptionManager.getSubscriptionMap());
        assertEquals(0, subscriptionManager.getSubscriptionMap().size());
    }

    @Test
    void removeSubscriptionOneSubscriber() {
        WsContext wsContext = new WsContextTest("test session id", new SessionTest());
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        boolean errorHappend = false;
        String errorMessage = null;
        String topic = "test topic";
        Subscriber subscriber = new Subscriber(wsContext);
        try {
            subscriptionManager.addSubscription(topic, subscriber);
            subscriptionManager.removeSubscription(topic, subscriber);
        } catch (SubscriptionManagerException e) {
            errorHappend = true;
            errorMessage = e.getMessage();
        }
        assertFalse(errorHappend);
        assertNull(errorMessage);
        assertNotNull(subscriptionManager.getSubscriptionMap());
        assertEquals(0, subscriptionManager.getSubscriptionMap().size());
        assertNotNull(subscriptionManager.getTopicSubscribers(topic));
        assertEquals(0, subscriptionManager.getTopicSubscribers(topic).size());
    }

    @Test
    void removeSubscriptionSubscriberNull() {
        WsContext wsContext = new WsContextTest("test session id", new SessionTest());
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        boolean errorHappend = false;
        String errorMessage = null;
        String topic = "test topic";
        Subscriber subscriber = new Subscriber(wsContext);
        try {
            subscriptionManager.addSubscription(topic, subscriber);
            subscriptionManager.removeSubscription(topic, null);
        } catch (SubscriptionManagerException e) {
            errorHappend = true;
            errorMessage = e.getMessage();
        }
        assertTrue(errorHappend);
        assertEquals(SubscriptionManagerException.ERROR_MESSAGE_SUBSCRIBER_NULL, errorMessage);
        assertNotNull(subscriptionManager.getSubscriptionMap());
        assertEquals(1, subscriptionManager.getSubscriptionMap().size());
        assertNotNull(subscriptionManager.getTopicSubscribers(topic));
        assertEquals(1, subscriptionManager.getTopicSubscribers(topic).size());
    }

    @Test
    void removeSubscriptionMultipleSubscribersOne() {
        WsContext wsContext1 = new WsContextTest("test session id1", new SessionTest());
        WsContext wsContext2 = new WsContextTest("test session id2", new SessionTest());
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        boolean errorHappend = false;
        String errorMessage = null;
        String topic = "test topic";
        Subscriber subscriber1 = new Subscriber(wsContext1);
        Subscriber subscriber2 = new Subscriber(wsContext2);
        try {
            subscriptionManager.addSubscription(topic, subscriber1);
            subscriptionManager.addSubscription(topic, subscriber2);
            subscriptionManager.removeSubscription(topic, subscriber1);
        } catch (SubscriptionManagerException e) {
            errorHappend = true;
            errorMessage = e.getMessage();
        }
        assertFalse(errorHappend);
        assertNull(errorMessage);
        assertNotNull(subscriptionManager.getSubscriptionMap());
        assertEquals(1, subscriptionManager.getSubscriptionMap().size());
        assertNotNull(subscriptionManager.getTopicSubscribers(topic));
        assertEquals(1, subscriptionManager.getTopicSubscribers(topic).size());
    }

    @Test
    void removeSubscriptionMultipleSubscribersAll() {
        WsContext wsContext1 = new WsContextTest("test session id1", new SessionTest());
        WsContext wsContext2 = new WsContextTest("test session id2", new SessionTest());
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        boolean errorHappend = false;
        String errorMessage = null;
        String topic = "test topic";
        Subscriber subscriber1 = new Subscriber(wsContext1);
        Subscriber subscriber2 = new Subscriber(wsContext2);
        try {
            subscriptionManager.addSubscription(topic, subscriber1);
            subscriptionManager.addSubscription(topic, subscriber2);
            subscriptionManager.removeSubscription(topic, subscriber1);
            subscriptionManager.removeSubscription(topic, subscriber2);
        } catch (SubscriptionManagerException e) {
            errorHappend = true;
            errorMessage = e.getMessage();
        }
        assertFalse(errorHappend);
        assertNull(errorMessage);
        assertNotNull(subscriptionManager.getSubscriptionMap());
        assertEquals(0, subscriptionManager.getSubscriptionMap().size());
        assertNotNull(subscriptionManager.getTopicSubscribers(topic));
        assertEquals(0, subscriptionManager.getTopicSubscribers(topic).size());
    }

    @Test
    void removeAllSubscriptions() {
        WsContext wsContext1 = new WsContextTest("test session id1", new SessionTest());
        WsContext wsContext2 = new WsContextTest("test session id2", new SessionTest());
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        boolean errorHappend = false;
        String errorMessage = null;
        String topic1 = "test topic1";
        String topic2 = "test topic2";
        Subscriber subscriber1 = new Subscriber(wsContext1);
        Subscriber subscriber2 = new Subscriber(wsContext2);
        try {
            subscriptionManager.addSubscription(topic1, subscriber1);
            subscriptionManager.addSubscription(topic1, subscriber2);
            subscriptionManager.addSubscription(topic2, subscriber2);
            subscriptionManager.removeAllSubscriptions(subscriber2);
        } catch (SubscriptionManagerException e) {
            errorHappend = true;
            errorMessage = e.getMessage();
        }
        assertFalse(errorHappend);
        assertNull(errorMessage);
        assertNotNull(subscriptionManager.getSubscriptionMap());
        assertEquals(1, subscriptionManager.getSubscriptionMap().size());

        assertNotNull(subscriptionManager.getTopicSubscribers(topic1));
        assertEquals(1, subscriptionManager.getTopicSubscribers(topic1).size());
        assertFalse(subscriptionManager.getTopicSubscribers(topic1).contains(subscriber2));

        assertNotNull(subscriptionManager.getTopicSubscribers(topic2));
        assertEquals(0, subscriptionManager.getTopicSubscribers(topic2).size());
    }

    @Test
    void removeAllSubscriptionsSubscriberNull() {
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        boolean errorHappend = false;
        String errorMessage = null;
        Subscriber subscriber1 = null;
        try {
            subscriptionManager.removeAllSubscriptions(subscriber1);
        } catch (SubscriptionManagerException e) {
            errorHappend = true;
            errorMessage = e.getMessage();
        }
        assertTrue(errorHappend);
        assertEquals(SubscriptionManagerException.ERROR_MESSAGE_SUBSCRIBER_NULL, errorMessage);
    }

    @Test
    void removeAllSubscriptionsIfNonExists() {
        WsContext wsContext1 = new WsContextTest("test session id1", new SessionTest());
        WsContext wsContext2 = new WsContextTest("test session id2", new SessionTest());
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        boolean errorHappend = false;
        String errorMessage = null;
        String topic1 = "test topic1";
        String topic2 = "test topic2";
        Subscriber subscriber1 = new Subscriber(wsContext1);
        Subscriber subscriber2 = new Subscriber(wsContext2);
        try {
            subscriptionManager.addSubscription(topic1, subscriber1);
            subscriptionManager.addSubscription(topic2, subscriber1);
            subscriptionManager.removeAllSubscriptions(subscriber2);
        } catch (SubscriptionManagerException e) {
            errorHappend = true;
            errorMessage = e.getMessage();
        }
        assertFalse(errorHappend);
        assertNull(errorMessage);

        Map<String, Set<Subscriber>> subscriptionMap = subscriptionManager.getSubscriptionMap();
        assertNotNull(subscriptionMap);
        assertEquals(2, subscriptionMap.size());

        assertNotNull(subscriptionManager.getTopicSubscribers(topic1));
        assertEquals(1, subscriptionManager.getTopicSubscribers(topic1).size());
        assertTrue(subscriptionManager.getTopicSubscribers(topic1).contains(subscriber1));
        assertFalse(subscriptionManager.getTopicSubscribers(topic1).contains(subscriber2));

        assertNotNull(subscriptionManager.getTopicSubscribers(topic2));
        assertEquals(1, subscriptionManager.getTopicSubscribers(topic2).size());
        assertTrue(subscriptionManager.getTopicSubscribers(topic2).contains(subscriber1));
        assertFalse(subscriptionManager.getTopicSubscribers(topic2).contains(subscriber2));

        for (String topic : subscriptionMap.keySet()) {
            assertFalse(subscriptionMap.get(topic).contains(subscriber2));
        }
    }

    @Test
    void getTopicSubscribers() {
        WsContext wsContext1 = new WsContextTest("test session id1", new SessionTest());
        WsContext wsContext2 = new WsContextTest("test session id2", new SessionTest());
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        boolean errorHappend = false;
        String errorMessage = null;
        String topic1 = "test topic1";
        String topic2 = "test topic2";
        Subscriber subscriber1 = new Subscriber(wsContext1);
        Subscriber subscriber2 = new Subscriber(wsContext2);
        try {
            subscriptionManager.addSubscription(topic1, subscriber1);
            subscriptionManager.addSubscription(topic1, subscriber2);
            subscriptionManager.addSubscription(topic2, subscriber1);
        } catch (SubscriptionManagerException e) {
            errorHappend = true;
            errorMessage = e.getMessage();
        }
        assertFalse(errorHappend);
        assertNull(errorMessage);

        assertNotNull(subscriptionManager.getSubscriptionMap());
        assertEquals(2, subscriptionManager.getSubscriptionMap().size());

        assertNotNull(subscriptionManager.getTopicSubscribers(topic1));
        assertEquals(2, subscriptionManager.getTopicSubscribers(topic1).size());
        assertTrue(subscriptionManager.getTopicSubscribers(topic1).contains(subscriber1));
        assertTrue(subscriptionManager.getTopicSubscribers(topic1).contains(subscriber2));

        assertNotNull(subscriptionManager.getTopicSubscribers(topic2));
        assertEquals(1, subscriptionManager.getTopicSubscribers(topic2).size());
        assertTrue(subscriptionManager.getTopicSubscribers(topic2).contains(subscriber1));
        assertFalse(subscriptionManager.getTopicSubscribers(topic2).contains(subscriber2));
    }

    @Test
    void getTopicSubscribersTopicNull() {
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        String topic1 = null;
        assertNotNull(subscriptionManager.getTopicSubscribers(topic1));
        assertEquals(0, subscriptionManager.getTopicSubscribers(topic1).size());
    }

    @Test
    void getTopicSubscribersTopicEmpty() {
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        String topic1 = "";
        assertNotNull(subscriptionManager.getTopicSubscribers(topic1));
        assertEquals(0, subscriptionManager.getTopicSubscribers(topic1).size());
    }

    @Test
    void getTopicSubscribersTopicOnlySpace() {
        WsContext wsContext1 = new WsContextTest("test session id1", new SessionTest());
        WsContext wsContext2 = new WsContextTest("test session id2", new SessionTest());
        SubscriptionManager subscriptionManager = new SubscriptionManager();
        boolean errorHappend = false;
        String errorMessage = null;
        String topic1 = "       ";
        Subscriber subscriber1 = new Subscriber(wsContext1);
        Subscriber subscriber2 = new Subscriber(wsContext2);
        try {
            subscriptionManager.addSubscription(topic1, subscriber1);
        } catch (SubscriptionManagerException e) {
            errorHappend = true;
            errorMessage = e.getMessage();
        }
        assertTrue(errorHappend);
        assertEquals(SubscriptionManagerException.ERROR_MESSAGE_TOPIC_BLANK, errorMessage);
        assertNotNull(subscriptionManager.getTopicSubscribers(topic1));
        assertEquals(0, subscriptionManager.getTopicSubscribers(topic1).size());
    }

    private class WsContextTest extends WsContext {
        public WsContextTest(@NotNull String sessionId, @NotNull Session session) {
            super(sessionId, session);
        }
    }

    private class SessionTest implements Session {
        public SessionTest() {

        }
        @Override
        public void close() {

        }

        @Override
        public void close(CloseStatus closeStatus) {

        }

        @Override
        public void close(int i, String s) {

        }

        @Override
        public void disconnect() throws IOException {

        }

        @Override
        public long getIdleTimeout() {
            return 0;
        }

        @Override
        public InetSocketAddress getLocalAddress() {
            return null;
        }

        @Override
        public WebSocketPolicy getPolicy() {
            return null;
        }

        @Override
        public String getProtocolVersion() {
            return null;
        }

        @Override
        public RemoteEndpoint getRemote() {
            return null;
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            return null;
        }

        @Override
        public UpgradeRequest getUpgradeRequest() {
            return null;
        }

        @Override
        public UpgradeResponse getUpgradeResponse() {
            return null;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public void setIdleTimeout(long l) {

        }

        @Override
        public SuspendToken suspend() {
            return null;
        }
    }
}