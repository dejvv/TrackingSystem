import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import definition.Action;
import subscription.Subscriber;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class PubSub {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static void main(String[] args) {
        Map<WsContext, Subscriber> subscriberMap = new ConcurrentHashMap<>();
        Broker broker = new Broker();
        Javalin app = Javalin.create().start(7070);
        app.ws("/events", ws -> {
            ws.onConnect(ctx -> {
                subscriberMap.put(ctx, new Subscriber(ctx));
                PubSub.LOGGER.info("Connection established, session id:"+subscriberMap.get(ctx).getWebsocketContext().getSessionId());
            });
            ws.onMessage(ctx -> {
                Action action = null;
                try {
                    action = ctx.message(Action.class);
                } catch (Exception e) {
                    ctx.send("Invalid message format. Should be JSON, example: {type:\'SUBSCRIBE\', topic:\'myTopic\', message:\'Hello World!\'}");
                    PubSub.LOGGER.error(subscriberMap.get(ctx) + " -> " + e.getMessage());
                }
                if (action != null) {
                    broker.execute(action, subscriberMap.get(ctx));
                }
            });
            ws.onClose(ctx -> {
                Action action = new Action(Action.TYPE.UNSUBSCRIBEALL.toString(), null, null);
                broker.execute(action, subscriberMap.get(ctx));
                PubSub.LOGGER.info("Connection closed, session id:"+subscriberMap.get(ctx).getWebsocketContext().getSessionId());
            });
            ws.onError(ctx -> {
                try {
                    PubSub.LOGGER.error(Objects.requireNonNull(ctx.error()).getMessage());
                } catch (NullPointerException exception) {
                    PubSub.LOGGER.error(Arrays.toString(exception.getStackTrace()));
                }
            });
        });
    }
}
