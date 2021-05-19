package model;

/**
 * Represents account data and attributes that are stored in the database.
 * Each account contains id ({@link #accountId}), name ({@link #accountName}) and indicator
 * whether it is active or not ({@link #isActive}).
 */
public class Account {
    private String accountId;
    private String accountName;
    private Boolean isActive;

    public Account (String accountId, String accountName, Boolean isActive) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.isActive = isActive;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Boolean getActive() {
        return this.isActive;
    }

    public void setActive(Boolean active) {
        this.isActive = active;
    }

    @Override
    public String toString() {
        return this.getAccountId() + ';' + this.getAccountName() + ';' + this.getActive();
    }
}
