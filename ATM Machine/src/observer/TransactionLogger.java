package observer;

import enums.TransactionStatus;
import model.Transaction;

/**
 * Logs every transaction to the console (simulates audit DB write).
 *
 * In production this would write to a persistent audit log / DB.
 */
public class TransactionLogger implements ATMObserver {

    @Override
    public void onTransaction(Transaction transaction) {
        System.out.println("[LOG] " + transaction);
    }
}
