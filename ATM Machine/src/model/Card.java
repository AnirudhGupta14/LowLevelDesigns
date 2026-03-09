package model;

/**
 * Represents a bank card inserted into the ATM.
 *
 * Design: Plain value object (no business logic here — SRP).
 */
public class Card {

    private final String cardNumber;
    private final String accountId; // Links card to an Account
    private final String expiryDate; // Format: MM/YY
    private boolean isBlocked;

    public Card(String cardNumber, String accountId, String expiryDate) {
        this.cardNumber = cardNumber;
        this.accountId = accountId;
        this.expiryDate = expiryDate;
        this.isBlocked = false;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void block() {
        this.isBlocked = true;
    }

    @Override
    public String toString() {
        return "Card{number='****" + cardNumber.substring(cardNumber.length() - 4)
                + "', account='" + accountId + "', blocked=" + isBlocked + "}";
    }
}
