package enums;

/**
 * Represents the payment processing states.
 */
public enum PaymentStatus {
    PENDING, // Payment initiated but not processed
    COMPLETED, // Payment processed successfully
    FAILED, // Payment processing failed
    REFUNDED // Payment was refunded (e.g., after cancellation)
}
