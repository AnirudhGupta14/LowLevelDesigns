package Services;

import Strategy.PaymentStrategy;
import entities.Order;

public class PaymentService {

    private PaymentStrategy strategy;

    public PaymentService(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    // ─── Strategy Management ──────────────────────────────────

    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public PaymentStrategy getStrategy() {
        return strategy;
    }

    // ─── Process Payment ──────────────────────────────────────

    public boolean processPayment(Order order) {
        System.out.printf("  💰 Processing payment for Order %s — ₹%.2f via %s%n",
                order.getId(), order.getTotalAmount(), strategy.getPaymentMethod());

        boolean success = strategy.pay(order.getTotalAmount());

        if (success) {
            System.out.printf("  ✅ Payment successful for Order %s%n", order.getId());
        } else {
            System.out.printf("  ❌ Payment failed for Order %s%n", order.getId());
        }

        return success;
    }
}
