package observer;

import model.Transaction;

/**
 * Observer interface for ATM transaction events.
 *
 * Part of the Observer Pattern:
 * Subject = ATM / TransactionService
 * Observer = TransactionLogger, AlertService, ...
 *
 * OCP: New observers (e.g., SMS notifications) can be added without
 * modifying the ATM core (Open for extension, Closed for modification).
 */
public interface ATMObserver {
    void onTransaction(Transaction transaction);
}
