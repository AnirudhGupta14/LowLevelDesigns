package Strategy;

public class CashOnDelivery implements PaymentStrategy {

    @Override
    public boolean pay(double amount) {
        System.out.printf("  💵 Cash on Delivery — ₹%.2f to be collected at doorstep%n", amount);
        return true;
    }

    @Override
    public String getPaymentMethod() {
        return "CASH_ON_DELIVERY";
    }
}
