package model;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a bank account.
 *
 * ACID / Locking:
 * Each Account owns a ReentrantLock so that concurrent transactions
 * on the same account are serialized, preventing dirty reads and
 * lost updates.
 *
 * Design: Single Responsibility — only holds account data & its lock.
 */
public class Account {

    private final String accountId;
    private final String hashedPin; // In production: use BCrypt / PBKDF2
    private double balance;
    private int failedPinAttempts;

    // Per-account lock — finer granularity than a global lock
    private final ReentrantLock lock = new ReentrantLock();

    public Account(String accountId, String hashedPin, double initialBalance) {
        this.accountId = accountId;
        this.hashedPin = hashedPin;
        this.balance = initialBalance;
        this.failedPinAttempts = 0;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getAccountId() {
        return accountId;
    }

    public String getHashedPin() {
        return hashedPin;
    }

    public double getBalance() {
        return balance;
    }

    public int getFailedPinAttempts() {
        return failedPinAttempts;
    }

    // ── Mutators (must hold lock before calling) ──────────────────────────────

    public void debit(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Debit amount must be positive.");
        if (amount > balance)
            throw new IllegalStateException("Insufficient funds.");
        this.balance -= amount;
    }

    public void credit(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Credit amount must be positive.");
        this.balance += amount;
    }

    public void incrementFailedAttempts() {
        this.failedPinAttempts++;
    }

    public void resetFailedAttempts() {
        this.failedPinAttempts = 0;
    }

    // ── Lock accessors ────────────────────────────────────────────────────────

    public ReentrantLock getLock() {
        return lock;
    }

    @Override
    public String toString() {
        return "Account{id='" + accountId + "', balance=" + balance + "}";
    }
}
