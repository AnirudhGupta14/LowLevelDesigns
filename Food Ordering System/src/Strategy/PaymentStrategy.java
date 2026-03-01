package Strategy;

public interface PaymentStrategy {
    boolean pay(double amount);

    String getPaymentMethod();
}
