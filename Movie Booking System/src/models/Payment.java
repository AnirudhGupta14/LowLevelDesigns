package models;

import enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Payment {
    private final String paymentId;
    private final Booking booking;
    private final double amount;
    private final String paymentMethod;
    private PaymentStatus status;
    private final LocalDateTime paymentTime;

    public Payment(Booking booking, double amount, String paymentMethod) {
        this.paymentId = UUID.randomUUID().toString();
        this.booking = booking;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
        this.paymentTime = LocalDateTime.now();
    }
}
