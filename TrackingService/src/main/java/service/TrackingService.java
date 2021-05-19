package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.CompletableFuture;

/**
 * TrackingService is able to check whether {@link model.Account} is valid or not.
 */
public class TrackingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private TrackingServiceDao trackingServiceDao;

    /**
     * When new TrackingService is created it also creates
     */
    public TrackingService () {
        TrackingService.LOGGER.info("Starting...");
        this.trackingServiceDao = new TrackingServiceDao();
        TrackingService.LOGGER.info("Started");
    }

    /**
     * Checks whether account with the given id is valid. Account is valid if it is active.
     * Account data is received from the database and the implementation can be found in {@link TrackingServiceDao}.
     * @param accountId Id of the account for which validity should be checked.
     * @return CompletableFuture which when completed returns data whether account is active or not.
     * @see model.Account
     * @see TrackingServiceDao
     */
    public CompletableFuture<Boolean> isAccountValid (String accountId) {
        return this.trackingServiceDao.get(accountId)
                .thenApply(account -> account != null && account.getActive());
    }

    /**
     * Connects to the database with the help of {@link TrackingServiceDao}.
     */
    protected void databaseConnect () {
        this.trackingServiceDao.connect();
    }

    /**
     * Closes the connection to the database with the help of {@link TrackingServiceDao}.
     */
    protected void databaseDisconnect () {
        this.trackingServiceDao.disconnect();
    }
}
