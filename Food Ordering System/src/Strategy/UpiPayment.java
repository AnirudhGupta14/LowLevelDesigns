package Strategy;

public class UpiPayment implements PaymentStrategy {

    private final String upiId;

    public UpiPayment(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public boolean pay(double amount) {
        System.out.printf("  📱 Paid ₹%.2f via UPI (%s)%n", amount, upiId);
        return true;
    }

    @Override
    public String getPaymentMethod() {
        return "UPI";
    }
}
