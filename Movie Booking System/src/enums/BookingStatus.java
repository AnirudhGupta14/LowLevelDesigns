package enums;

public enum BookingStatus {
    CREATED,        // Booking has been created but not yet confirmed
    CONFIRMED,      // Booking has been successfully confirmed
    EXPIRED,        // Booking has expired due to timeout
    CANCELLED,      // Booking has been cancelled by user
    PAYMENT_PENDING // Payment is pending
}
