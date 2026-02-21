package services;

import enums.PaymentStatus;
import models.Booking;
import models.Payment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentService {
    private final Map<String, Payment> payments = new ConcurrentHashMap<>();

    public Payment createPayment(Booking booking, String paymentMethod) {
        Payment payment = new Payment(booking, booking.calculateTotalAmount(), paymentMethod);
        payments.put(payment.getPaymentId(), payment);
        booking.setPayment(payment);
        return payment;
    }

    public Optional<Payment> getPaymentById(String paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }

    public PaymentStatus processPayment(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found");
        }

        // Simulate payment processing
        boolean paymentSuccess = simulatePaymentProcessing(payment);

        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.SUCCESS);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        return payment.getStatus();
    }

    public PaymentStatus refundPayment(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found");
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Cannot refund payment that was not successful");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        return payment.getStatus();
    }

    public boolean isPaymentSuccessful(String paymentId) {
        Payment payment = payments.get(paymentId);
        return payment != null && payment.getStatus() == PaymentStatus.SUCCESS;
    }

    private boolean simulatePaymentProcessing(Payment payment) {
        // Simulate payment processing with 90% success rate
        return Math.random() > 0.1;
    }
}