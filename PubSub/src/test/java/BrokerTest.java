import definition.Action;
import io.javalin.websocket.WsContext;
import org.eclipse.jetty.websocket.api.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import subscription.Subscriber;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

class BrokerTest {
    private static final String PUBLISH = Action.TYPE.PUBLISH.toString();
    private static final String SUBSCRIBE_TYPE = Action.TYPE.SUBSCRIBE.toString();
    private static final String UNSUBSCRIBE_TYPE = Action.TYPE.UNSUBSCRIBE.toString();
    private static final String UNSUBSCRIBEALL_TYPE = Action.TYPE.UNSUBSCRIBEALL.toString();

    @Test
    void executePublish() {
        String topic = "test topic1";
        String message = "test message";
        Action actionSubscribe = new Action(BrokerTest.PUBLISH, topic, message);
        Broker broker = new Broker();
        Subscriber subscriber1 = new Subscriber(new WsContextTest("test session id1", new SessionTest()));
        broker.execute(actionSubscribe, subscriber1);
        assertTrue(broker.getLastErrorMessage().isBlank());
    }

    @Test
    void executeSubscribe() {
        String topic = "test topic1";
        String message = "test message";
        Action actionSubscribe = new Action(BrokerTest.SUBSCRIBE_TYPE, topic, message);
        Broker broker = new Broker();
        Subscriber subscriber1 = new Subscriber(new WsContextTest("test session id1", new SessionTest()));
        broker.execute(actionSubscribe, subscriber1);
        assertTrue(broker.getLastErrorMessage().isBlank());
    }

    @Test
    void executeUnsubscribe() {
        String topic = "test topic1";
        String message = "test message";
        Action actionSubscribe = new Action(BrokerTest.UNSUBSCRIBE_TYPE, topic, message);
        Broker broker = new Broker();
        Subscriber subscriber1 = new Subscriber(new WsContextTest("test session id1", new SessionTest()));
        broker.execute(actionSubscribe, subscriber1);
        assertTrue(broker.getLastErrorMessage().isBlank());
    }

    @Test
    void executeUnsubscribeAll() {
        String topic = "test topic1";
        String message = "test message";
        Action actionSubscribe = new Action(BrokerTest.UNSUBSCRIBEALL_TYPE, topic, message);
        Broker broker = new Broker();
        Subscriber subscriber1 = new Subscriber(new WsContextTest("test session id1", new SessionTest()));
        broker.execute(actionSubscribe, subscriber1);
        assertTrue(broker.getLastErrorMessage().isBlank());
    }

    @Test
    void executeSubscriberNull() {
        String topic = "test topic1";
        String message = "test message";
        Action actionSubscribe = new Action(BrokerTest.SUBSCRIBE_TYPE, topic, message);
        Broker broker = new Broker();
        broker.execute(actionSubscribe, null);
        assertFalse(broker.getLastErrorMessage().isBlank());
    }

    @Test
    void executeActionNull() {
        String topic = "test topic1";
        String message = "test message";
        Subscriber subscriber1 = new Subscriber(new WsContextTest("test session id1", new SessionTest()));
        Broker broker = new Broker();
        broker.execute(null, subscriber1);
        assertFalse(broker.getLastErrorMessage().isBlank());
    }

    @Test
    void executeActionTypeNull() {
        String topic = "test topic1";
        String message = "test message";
        Action actionSubscribe = new Action(null, topic, message);
        Broker broker = new Broker();
        Subscriber subscriber1 = new Subscriber(new WsContextTest("test session id1", new SessionTest()));
        broker.execute(actionSubscribe, subscriber1);
        assertFalse(broker.getLastErrorMessage().isBlank());
    }

    @Test
    void executeActionTypeEmpty() {
        String topic = "test topic1";
        String message = "test message";
        Action actionSubscribe = new Action("", topic, message);
        Broker broker = new Broker();
        Subscriber subscriber1 = new Subscriber(new WsContextTest("test session id1", new SessionTest()));
        broker.execute(actionSubscribe, subscriber1);
        assertFalse(broker.getLastErrorMessage().isBlank());
    }

    @Test
    void executeActionTypeOnlySpace() {
        String topic = "test topic1";
        String message = "test message";
        Action actionSubscribe = new Action("       ", topic, message);
        Broker broker = new Broker();
        Subscriber subscriber1 = new Subscriber(new WsContextTest("test session id1", new SessionTest()));
        broker.execute(actionSubscribe, subscriber1);
        assertFalse(broker.getLastErrorMessage().isBlank());
    }

    @Test
    void executeActionTypeOnlyWrongValue() {
        String topic = "test topic1";
        String message = "test message";
        Action actionSubscribe = new Action("RANDOM", topic, message);
        Broker broker = new Broker();
        Subscriber subscriber1 = new Subscriber(new WsContextTest("test session id1", new SessionTest()));
        broker.execute(actionSubscribe, subscriber1);
        assertFalse(broker.getLastErrorMessage().isBlank());
    }

    @Test
    void executeActionAndSubscriberNull() {
        String topic = "test topic1";
        String message = "test message";
        Broker broker = new Broker();
        broker.execute(null, null);
        assertFalse(broker.getLastErrorMessage().isBlank());
    }

    @Test
    void executeSubscribeTopicNull() {
        String topic = null;
        String message = "test message";
        Action actionSubscribe = new Action(BrokerTest.SUBSCRIBE_TYPE, topic, message);
        Broker broker = new Broker();
        Subscriber subscriber1 = new Subscriber(new WsContextTest("test session id1", new SessionTest()));
        broker.execute(actionSubscribe, subscriber1);
        assertFalse(broker.getLastErrorMessage().isBlank());
    }

    @Test
    void executeSubscribeTopicEmpty() {
        String topic = "";
        String message = "test message";
        Action actionSubscribe = new Action(BrokerTest.SUBSCRIBE_TYPE, topic, message);
        Broker broker = new Broker();
        Subscriber subscriber1 = new Subscriber(new WsContextTest("test session id1", new SessionTest()));
        broker.execute(actionSubscribe, subscriber1);
        assertFalse(broker.getLastErrorMessage().isBlank());
    }

    @Test
    void executeSubscribeTopicOnlySpace() {
        String topic = "       ";
        String message = "test message";
        Action actionSubscribe = new Action(BrokerTest.SUBSCRIBE_TYPE, topic, message);
        Broker broker = new Broker();
        Subscriber subscriber1 = new Subscriber(new WsContextTest("test session id1", new SessionTest()));
        broker.execute(actionSubscribe, subscriber1);
        assertFalse(broker.getLastErrorMessage().isBlank());
    }

    @Test
    void executeSubscribeMessageNull() {
        String topic = "test topic1";
        String message = null;
        Action actionSubscribe = new Action(BrokerTest.SUBSCRIBE_TYPE, topic, message);
        Broker broker = new Broker();
        Subscriber subscriber1 = new Subscriber(new WsContextTest("test session id1", new SessionTest()));
        broker.execute(actionSubscribe, subscriber1);
        // message does not impact execution of subscription, therefore it can be of any value and subscription shall be successful
        assertTrue(broker.getLastErrorMessage().isBlank());
    }

    @Test
    void executeSubscribeMessageEmpty() {
        String topic = "test topic1";
        String message = "";
        Action actionSubscribe = new Action(BrokerTest.SUBSCRIBE_TYPE, topic, message);
        Broker broker = new Broker();
        Subscriber subscriber1 = new Subscriber(new WsContextTest("test session id1", new SessionTest()));
        broker.execute(actionSubscribe, subscriber1);
        // message does not impact execution of subscription, therefore it can be of any value and subscription shall be successful
        assertTrue(broker.getLastErrorMessage().isBlank());
    }

    @Test
    void executeSubscribeMessageOnlySpace() {
        String topic = "test topic1";
        String message = "       ";
        Action actionSubscribe = new Action(BrokerTest.SUBSCRIBE_TYPE, topic, message);
        Broker broker = new Broker();
        Subscriber subscriber1 = new Subscriber(new WsContextTest("test session id1", new SessionTest()));
        broker.execute(actionSubscribe, subscriber1);
        // message does not impact execution of subscription, therefore it can be of any value and subscription shall be successful
        assertTrue(broker.getLastErrorMessage().isBlank());
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