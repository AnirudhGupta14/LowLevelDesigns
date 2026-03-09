package enums;

/**
 * Outcome status of an ATM transaction.
 */
public enum TransactionStatus {
    SUCCESS,
    FAILED,
    INSUFFICIENT_FUNDS,
    INVALID_PIN,
    CARD_BLOCKED,
    CANCELLED
}
