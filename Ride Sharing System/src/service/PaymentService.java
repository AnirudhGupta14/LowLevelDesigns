package service;

import enums.PaymentMethod;
import enums.PaymentStatus;
import models.Payment;
import models.Ride;
import java.util.HashMap;
import java.util.Map;

/**
 * Service handling payment processing for completed rides.
 * Simulates payment gateway integration.
 */
public class PaymentService {
    private final Map<String, Payment> payments = new HashMap<>();

    /**
     * Processes payment for a completed ride.
     * 
     * @return the created Payment record
     */
    public Payment processPayment(Ride ride, PaymentMethod method) {
        Payment payment = new Payment(ride, ride.getFare(), method);
        System.out.printf("[PaymentService] Processing %s for Ride[%s] → ₹%.2f%n",
                method, ride.getId(), ride.getFare());

        // Simulate payment gateway — always succeeds in this simulation
        boolean success = simulateGateway(method, ride.getFare());
        payment.setStatus(success ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        payments.put(payment.getId(), payment);

        System.out.printf("[PaymentService] Payment %s: %s%n", payment.getId(), payment.getStatus());
        return payment;
    }

    public Payment getPayment(String paymentId) {
        return payments.get(paymentId);
    }

    public void refundPayment(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null)
            throw new IllegalArgumentException("Payment not found: " + paymentId);
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only COMPLETED payments can be refunded");
        }
        payment.setStatus(PaymentStatus.REFUNDED);
        System.out.printf("[PaymentService] Refunded: %s%n", paymentId);
    }

    private boolean simulateGateway(PaymentMethod method, double amount) {
        // CASH always succeeds; other methods succeed if amount is reasonable
        return method == PaymentMethod.CASH || amount < 10000;
    }
}
