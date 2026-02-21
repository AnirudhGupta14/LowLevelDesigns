package payments;

public class PaymentContext {
    private PaymentStrategy paymentStrategy;

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void payAmount(double amount) {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Payment method not selected");
        }
        paymentStrategy.pay(amount);
    }
}

