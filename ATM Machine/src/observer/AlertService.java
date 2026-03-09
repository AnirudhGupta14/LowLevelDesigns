package observer;

import enums.TransactionStatus;
import model.Transaction;

/**
 * Raises alerts on suspicious ATM activity.
 *
 * Currently alerts on:
 * - Repeated INVALID_PIN attempts (handled in AuthenticationService)
 * - FAILED transactions
 *
 * In production: would send alerts to a fraud-detection service.
 */
public class AlertService implements ATMObserver {

    @Override
    public void onTransaction(Transaction transaction) {
        if (transaction.getStatus() == TransactionStatus.FAILED
                || transaction.getStatus() == TransactionStatus.CARD_BLOCKED
                || transaction.getStatus() == TransactionStatus.INSUFFICIENT_FUNDS) {
            System.out.println("[ALERT] Suspicious/Failed transaction detected: " + transaction);
        }
    }
}
