package payment;

import enums.PaymentStatus;

// Strategy Pattern interface for payment methods
public interface Payment {
    PaymentStatus pay(double amount);
}
