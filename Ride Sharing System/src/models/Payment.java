package models;

import enums.PaymentMethod;
import enums.PaymentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a payment transaction associated with a completed ride.
 */
public class Payment {
    private final String id;
    private final Ride ride;
    private final double amount;
    private final PaymentMethod paymentMethod;
    private PaymentStatus status;
    private final LocalDateTime createdAt;

    public Payment(Ride ride, double amount, PaymentMethod paymentMethod) {
        this.id = UUID.randomUUID().toString();
        this.ride = ride;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // ── Getters / Setters ──────────────────────────────────────────────────
    public String getId() {
        return id;
    }

    public Ride getRide() {
        return ride;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return String.format("Payment[id=%s, amount=%.2f, method=%s, status=%s]",
                id, amount, paymentMethod, status);
    }
}
