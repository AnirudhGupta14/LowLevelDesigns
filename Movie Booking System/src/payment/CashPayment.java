package payment;

import enums.PaymentStatus;

public class CashPayment implements Payment {
    @Override
    public PaymentStatus pay(double amount) {
        System.out.println("💵 Cash payment of $" + amount + " received.");
        return PaymentStatus.PAID;
    }
}
