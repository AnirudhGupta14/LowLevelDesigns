package Strategy;

public class CreditCardPayment implements PaymentStrategy {

    private final String cardNumber;

    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public boolean pay(double amount) {
        String maskedCard = "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
        System.out.printf("  💳 Paid ₹%.2f via Credit Card (%s)%n", amount, maskedCard);
        return true;
    }

    @Override
    public String getPaymentMethod() {
        return "CREDIT_CARD";
    }
}
