package service;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import dao.Dao;
import model.Account;
import util.Settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jasync.sql.db.general.ArrayRowData;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.ConnectionPoolConfigurationBuilder;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder;

/**
 * Represents database access object that is able to receive data about {@link Account} from Postgres database.
 */
public class TrackingServiceDao implements Dao {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String UNDEFINED_ACCOUNT_NAME = "undefined";

    private Connection connection;

    public TrackingServiceDao () {
        this.initConnection();
    }

    /**
     * Receives data about {@link Account}. If connection to database does not exists or if
     * accountId is blank or it does not represents an {@link Integer}
     * a CompletableFuture with inactive account will be returned.
     * @param accountId Id of the account for which data should be received.
     * @return CompletableFuture which when completed returns data about {@link Account}.
     */
    @Override
    public CompletableFuture<Account> get(String accountId) {
        CompletableFuture<Account> undefinedAccountFuture = CompletableFuture.supplyAsync(() -> new Account(accountId, TrackingServiceDao.UNDEFINED_ACCOUNT_NAME, false));

        if (accountId.isBlank()) {
            return undefinedAccountFuture;
        }
        if (this.connection == null) {
            return undefinedAccountFuture;
        }
        for (int i = 0; i < accountId.length(); i++) {
            if (!Character.isDigit(accountId.charAt(i))) {
                return undefinedAccountFuture;
            }
        }

        return this.connection.sendPreparedStatement("SELECT accountId, accountName, isActive FROM Account WHERE accountId=?", Collections.singletonList(accountId))
                .thenApply((result) -> {
                    if (result.getRows().size() == 0) {
                        return new Account(accountId, TrackingServiceDao.UNDEFINED_ACCOUNT_NAME, false);
                    }
                    ArrayRowData arrayRowData = (ArrayRowData) result.getRows().get(0);
                    return new Account(
                            String.valueOf(arrayRowData.getColumns()[0]),
                            (String) arrayRowData.getColumns()[1],
                            (boolean) arrayRowData.getColumns()[2]
                    );
                }).exceptionally((exception) -> {
                    TrackingServiceDao.LOGGER.error("Error retrieving account:"+exception.getMessage());
                    return new Account(accountId, TrackingServiceDao.UNDEFINED_ACCOUNT_NAME, false);
                });
    }

    /**
     * Reserves pool of database connections that can be used by the application.
     * Max active connections are received through {@link Settings}.
     */
    private void initConnection () {
        if (this.connection != null && this.connection.isConnected()) {
            return;
        }
        TrackingServiceDao.LOGGER.info("Initialising pool of database connections...");
        Settings settings = Settings.getSettings("settings.properties");
        ConnectionPoolConfigurationBuilder config = new ConnectionPoolConfigurationBuilder();
        config.setUsername(settings.getUsername());
        config.setHost(settings.getUrl());
        config.setPort(Integer.parseInt(settings.getPort()));
        config.setPassword(settings.getPassword());
        config.setDatabase(settings.getDatabase());
        config.setMaxActiveConnections(settings.getMaxConnctions());
        config.setMaxIdleTime(TimeUnit.MINUTES.toMillis(15));
        config.setMaxPendingQueries(10000);
        config.setConnectionValidationInterval(TimeUnit.SECONDS.toMillis(30));
        this.connection = PostgreSQLConnectionBuilder.createConnectionPool(config);
        TrackingServiceDao.LOGGER.info("Pool of database connections initialised");
    }

    /**
     * Starts the connection process to the database.
     */
    protected void connect () {
        this.initConnection();
    }

    /**
     * Closes all connections to the database.
     */
    protected void disconnect () {
        if (this.connection != null) {
            try {
                this.connection.disconnect();
                TrackingServiceDao.LOGGER.info("Database connection closed");
            } catch (Exception exception) {
                TrackingServiceDao.LOGGER.error("Error while disconnecting from database:" + Arrays.toString(exception.getStackTrace()));
            }
        }
    }

    @Override
    public List<String> getAll() {
        return null;
    }
}
