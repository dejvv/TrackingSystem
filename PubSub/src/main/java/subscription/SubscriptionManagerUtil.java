package subscription;

/**
 * Provides utility methods for {@link SubscriptionManager} such as logic for checking null values.
 */
public class SubscriptionManagerUtil {
    /**
     * Checks whether {@code theObject} is null. If {@code theObject} is the {@link String} type, then checks whether it is a blank value.
     * In that case a {@link SubscriptionManagerException} is thrown.
     * @param theObject An object of any type, in context of {@link SubscriptionManager} it is a {@link String} or a {@link Subscriber}.
     * @param message Message of error that will be thrown in a {@link SubscriptionManagerException} if {@code theObject} is null
     *                or blank in case of a {@code String} type.
     * @throws SubscriptionManagerException
     */
    public static void requireNonNullOrEmpty (Object theObject, String message) throws SubscriptionManagerException {
        if (theObject == null) {
            throw new SubscriptionManagerException(message);
        }
        if (theObject instanceof String) {
            String theObjectString = (String) theObject;
            if (theObjectString.isBlank()) {
                throw new SubscriptionManagerException(message);
            }
        }
    }
}
