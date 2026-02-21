package models;

import enums.PaymentMethod;
import enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Payment {
    private final String paymentId;
    private final Order order;
    private final double amount;
    private final PaymentMethod paymentMethod;
    private PaymentStatus status;
    private final LocalDateTime paymentTime;

    public Payment(Order order, double amount, PaymentMethod paymentMethod) {
        this.paymentId = UUID.randomUUID().toString();
        this.order = order;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
        this.paymentTime = LocalDateTime.now();
    }
}
