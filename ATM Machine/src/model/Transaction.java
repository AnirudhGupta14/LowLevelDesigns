package model;

import enums.TransactionStatus;
import enums.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Immutable record of an ATM transaction.
 *
 * Design: Immutable after creation (all fields final) — thread-safe by design.
 */
public class Transaction {

    private final String transactionId;
    private final String accountId;
    private final TransactionType type;
    private final double amount;
    private final TransactionStatus status;
    private final LocalDateTime timestamp;

    public Transaction(String accountId, TransactionType type, double amount, TransactionStatus status) {
        this.transactionId = UUID.randomUUID().toString();
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("Transaction[%s | %s | %s | Amount=%.2f | %s | %s]",
                transactionId.substring(0, 8), accountId, type, amount, status, timestamp);
    }
}
