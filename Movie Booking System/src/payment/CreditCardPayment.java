package payment;

import enums.PaymentStatus;

public class CreditCardPayment implements Payment {
    private final String cardNumber;

    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public PaymentStatus pay(double amount) {
        System.out.println("💳 Credit card (**** " + cardNumber.substring(cardNumber.length() - 4)
                + ") charged $" + amount);
        return PaymentStatus.PAID;
    }
}
