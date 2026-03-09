package hardware;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Simulates the physical cash dispenser unit of an ATM.
 *
 * Locking:
 * Uses a ReentrantLock to guarantee that only one transaction at a time
 * can read-then-decrement the cash count, preventing double-dispensing
 * (a classic TOCTOU race condition).
 *
 * Design: Single Responsibility — only manages physical cash inventory.
 */
public class CashDispenser {

    private int cashCount; // Number of available notes (simplified)
    private final int noteValue; // Denomination of each note (e.g., 100)
    private final ReentrantLock dispenserLock = new ReentrantLock();

    public CashDispenser(int initialCashCount, int noteValue) {
        this.cashCount = initialCashCount;
        this.noteValue = noteValue;
    }

    /**
     * Dispenses the requested amount from the ATM.
     * Caller must provide a valid amount that is a multiple of noteValue.
     *
     * @param amount Amount to dispense
     * @throws IllegalArgumentException if amount is not a multiple of noteValue
     * @throws IllegalStateException    if ATM has insufficient cash
     */
    public boolean dispense(double amount) {
        dispenserLock.lock();
        try {
            int notesRequired = (int) (amount / noteValue);

            if (amount % noteValue != 0) {
                System.out.println("[CashDispenser] Amount " + amount
                        + " is not dispensable (note denomination: " + noteValue + ").");
                return false;
            }
            if (notesRequired > cashCount) {
                System.out.println("[CashDispenser] Insufficient cash. Available: "
                        + (cashCount * noteValue) + ", Requested: " + amount);
                return false;
            }

            cashCount -= notesRequired;
            System.out.println("[CashDispenser] Dispensed " + notesRequired + " note(s) of "
                    + noteValue + ". Remaining notes: " + cashCount);
            return true;
        } finally {
            dispenserLock.unlock();
        }
    }

    /**
     * Accepts deposit cash (increases cash count).
     */
    public void accept(double amount) {
        dispenserLock.lock();
        try {
            int notesAdded = (int) (amount / noteValue);
            cashCount += notesAdded;
            System.out.println("[CashDispenser] Accepted " + notesAdded
                    + " note(s). Total notes: " + cashCount);
        } finally {
            dispenserLock.unlock();
        }
    }

    public boolean hasSufficientCash(double amount) {
        dispenserLock.lock();
        try {
            return (int) (amount / noteValue) <= cashCount;
        } finally {
            dispenserLock.unlock();
        }
    }

    public int getCashCount() {
        return cashCount;
    }

    public int getNoteValue() {
        return noteValue;
    }
}
