package service;

import com.github.jasync.sql.db.Configuration;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.ConnectionPoolConfigurationBuilder;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.general.ArrayRowData;
import com.github.jasync.sql.db.pool.ConnectionPool;
import com.github.jasync.sql.db.pool.PoolConfiguration;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder;
import com.github.jasync.sql.db.postgresql.pool.PostgreSQLConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Settings;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import io.javalin.Javalin;


public class TrackingServiceAllInOneTest {
    private static Logger logger = LoggerFactory.getLogger(TrackingServiceAllInOneTest.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        logger.error("starting");
            logger.warn("starting warn");
            logger.info("starting info");
            logger.debug("starting debug");
            logger.trace("starting trace");
        Settings settings = Settings.getSettings(System.getenv("TRACKING_SERVICE_SETTINGS_LOCATION"));

//        Configuration configuration =
//                    new Configuration(
//                            "username",
//                            "host.com",
//                            5324,
//                            "password",
//                            "schema"
//                    );
//            PoolConfiguration poolConfiguration = new PoolConfiguration(
//                    100,                            // maxObjects
//                    TimeUnit.MINUTES.toMillis(15),  // maxIdle
//                    10_000,                         // maxQueueSize
//                    TimeUnit.SECONDS.toMillis(30)   // validationInterval
//            );
        ConnectionPoolConfigurationBuilder config = new ConnectionPoolConfigurationBuilder();
        config.setUsername(settings.getUsername());
        config.setHost(settings.getUrl());
        config.setPort(Integer.parseInt(settings.getPort()));
        config.setPassword(settings.getPassword());
        config.setDatabase(settings.getDatabase());
        config.setMaxActiveConnections(100);
        config.setMaxIdleTime(TimeUnit.MINUTES.toMillis(15));
        config.setMaxPendingQueries(10000);
        config.setConnectionValidationInterval(TimeUnit.SECONDS.toMillis(30));
//            ConnectionPool<PostgreSQLConnection> connection = new ConnectionPool<>(
//                    new PostgreSQLConnectionFactory(configuration), poolConfiguration);
//        ConnectionPool<PostgreSQLConnection> connection = PostgreSQLConnectionBuilder.createConnectionPool(config);
//        connection.connect().get();
//            CompletableFuture<QueryResult> future = connection.sendPreparedStatement("select * from table limit 2");
//            QueryResult queryResult = future.get();
//            System.out.println(Arrays.toString(((ArrayRowData) (queryResult.getRows().get(0))).getColumns()));
//            System.out.println(Arrays.toString(((ArrayRowData) (queryResult.getRows().get(1))).getColumns()));

        // for PostgreSQL use PostgreSQLConnectionBuilder instead of MySQLConnectionBuilder
        Connection connection = PostgreSQLConnectionBuilder.createConnectionPool(config);
        Javalin app = Javalin.create()
            .events(event -> {
                event.serverStarting(() -> {
                    connection.connect().get();
                    logger.info("Database connection established");
                });
                event.serverStopping(() -> {
                    logger.info("Javalin stopping...");
                    connection.disconnect().get();
                    logger.info("Database connection closed");
                });
            })
            .start(7000);

        app.get("/:accountId", (ctx) -> {
            String accountId = ctx.pathParam("accountId");
            final CompletableFuture<QueryResult> queryResultCompletableFuture = connection.sendPreparedStatement("SELECT accountName, isActive FROM Account WHERE accountId="+accountId);
            ctx.result(
                    queryResultCompletableFuture
//                            .thenApply((t) -> "got result: " + t.getRows().get(0).get(0))
                            .thenApply((t) -> "got result: " + Arrays.toString(((ArrayRowData) (t.getRows().get(0))).getColumns()))
            );
        });
        }
}
