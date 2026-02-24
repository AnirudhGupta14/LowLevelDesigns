package payment;

import enums.PaymentStatus;

public class UPIPayment implements Payment {
    private final String upiId;

    public UPIPayment(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public PaymentStatus pay(double amount) {
        System.out.println("📱 UPI payment of $" + amount + " via " + upiId + " successful.");
        return PaymentStatus.PAID;
    }
}
