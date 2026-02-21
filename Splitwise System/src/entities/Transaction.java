package entities;

public class Transaction {
    private final User from;        // User who owes the money
    private final User to;          // User who is owed the money
    private final double amount;    // Transaction amount

    // Constructor to initialize Transaction attributes
    public Transaction(User from, User to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    // Getters for the transaction attributes
    public User getFrom() { return from; }
    public User getTo() { return to; }
    public double getAmount() { return amount; }
}