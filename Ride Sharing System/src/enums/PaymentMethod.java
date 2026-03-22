package enums;

/**
 * Supported payment methods on the platform.
 */
public enum PaymentMethod {
    CASH, // Pay the driver directly in cash
    CREDIT_CARD, // Charge to a credit card
    DEBIT_CARD, // Charge to a debit card
    UPI, // Unified Payments Interface (India)
    WALLET // In-app wallet balance
}
