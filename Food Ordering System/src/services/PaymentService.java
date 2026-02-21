package services;

import enums.OrderingStatus;
import enums.PaymentMethod;
import enums.PaymentStatus;
import models.Order;
import models.Payment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentService {
    private final Map<String, Payment> payments;
    private final Map<String, List<Payment>> userPaymentHistory;
    private final Map<String, List<Payment>> orderPaymentHistory;
    private final Random random;

    public PaymentService() {
        this.payments = new ConcurrentHashMap<>();
        this.userPaymentHistory = new ConcurrentHashMap<>();
        this.orderPaymentHistory = new ConcurrentHashMap<>();
        this.random = new Random();
    }

    public Payment createPayment(Order order, PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }

        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        // Check if payment already exists for this order
        if (hasExistingPayment(order.getOrderId())) {
            throw new IllegalStateException("Payment already exists for order: " + order.getOrderId());
        }

        double amount = paymentMethod.getTotalAmount(order.getTotalAmount());
        Payment payment = new Payment(order, amount, paymentMethod);
        
        // Store payment
        payments.put(payment.getPaymentId(), payment);
        
        // Update payment history
        updatePaymentHistory(payment);
        
        return payment;
    }

    public boolean processPayment(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            return false;
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            return false; // Payment already processed
        }

        // Simulate payment processing
        boolean success = simulatePaymentProcessing(payment);
        
        if (success) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.getOrder().setOrderingStatus(OrderingStatus.CONFIRMED);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.getOrder().setOrderingStatus(OrderingStatus.CANCELLED);
        }

        return success;
    }

    public boolean refundPayment(String paymentId, double refundAmount) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            return false;
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            return false; // Can only refund successful payments
        }

        if (refundAmount > payment.getAmount()) {
            return false; // Cannot refund more than paid amount
        }

        // Simulate refund processing
        boolean success = simulateRefundProcessing(payment, refundAmount);
        
        if (success) {
            payment.setStatus(PaymentStatus.REFUNDED);
        }

        return success;
    }

    public Payment getPayment(String paymentId) {
        return payments.get(paymentId);
    }

    public Payment getPaymentByOrderId(String orderId) {
        return payments.values().stream()
                .filter(payment -> payment.getOrder().getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    public List<Payment> getUserPaymentHistory(String userId) {
        return userPaymentHistory.getOrDefault(userId, new ArrayList<>());
    }

    public List<Payment> getOrderPaymentHistory(String orderId) {
        return orderPaymentHistory.getOrDefault(orderId, new ArrayList<>());
    }

    public double calculateTotalAmount(Order order, PaymentMethod paymentMethod) {
        return paymentMethod.getTotalAmount(order.getTotalAmount());
    }

    public double calculateFee(Order order, PaymentMethod paymentMethod) {
        return paymentMethod.calculateFee(order.getTotalAmount());
    }

    public boolean hasExistingPayment(String orderId) {
        return payments.values().stream()
                .anyMatch(payment -> payment.getOrder().getOrderId().equals(orderId));
    }

    public PaymentStatus getPaymentStatus(String paymentId) {
        Payment payment = payments.get(paymentId);
        return payment != null ? payment.getStatus() : null;
    }

    public PaymentStatus getPaymentStatusByOrderId(String orderId) {
        Payment payment = getPaymentByOrderId(orderId);
        return payment != null ? payment.getStatus() : null;
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return payments.values().stream()
                .filter(payment -> payment.getStatus() == status)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<Payment> getPaymentsByMethod(PaymentMethod paymentMethod) {
        return payments.values().stream()
                .filter(payment -> payment.getPaymentMethod() == paymentMethod)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public Map<PaymentMethod, Integer> getPaymentMethodStatistics() {
        Map<PaymentMethod, Integer> stats = new HashMap<>();
        for (Payment payment : payments.values()) {
            PaymentMethod method = payment.getPaymentMethod();
            stats.put(method, stats.getOrDefault(method, 0) + 1);
        }
        return stats;
    }

    public double getTotalRevenue() {
        return payments.values().stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public double getTotalRefunds() {
        return payments.values().stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.REFUNDED)
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public int getTotalTransactions() {
        return payments.size();
    }

    public int getSuccessfulTransactions() {
        return (int) payments.values().stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.SUCCESS)
                .count();
    }

    public int getFailedTransactions() {
        return (int) payments.values().stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.FAILED)
                .count();
    }

    public double getSuccessRate() {
        int total = getTotalTransactions();
        if (total == 0) return 0.0;
        return (double) getSuccessfulTransactions() / total * 100.0;
    }

    public List<Payment> getRecentPayments(int limit) {
        return payments.values().stream()
                .sorted((p1, p2) -> p2.getPaymentTime().compareTo(p1.getPaymentTime()))
                .limit(limit)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public Map<String, Object> getPaymentStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTransactions", getTotalTransactions());
        stats.put("successfulTransactions", getSuccessfulTransactions());
        stats.put("failedTransactions", getFailedTransactions());
        stats.put("successRate", getSuccessRate());
        stats.put("totalRevenue", getTotalRevenue());
        stats.put("totalRefunds", getTotalRefunds());
        stats.put("paymentMethodStats", getPaymentMethodStatistics());
        return stats;
    }

    public PaymentMethod[] getAvailablePaymentMethods() {
        return PaymentMethod.values();
    }

    public PaymentMethod[] getOnlinePaymentMethods() {
        return PaymentMethod.getOnlinePaymentMethods();
    }

    public PaymentMethod[] getInstantPaymentMethods() {
        return PaymentMethod.getInstantPaymentMethods();
    }

    public PaymentMethod[] getCardPaymentMethods() {
        return PaymentMethod.getCardPaymentMethods();
    }

    public boolean isPaymentMethodAvailable(PaymentMethod paymentMethod) {
        return paymentMethod != null;
    }

    private void updatePaymentHistory(Payment payment) {
        String userId = payment.getOrder().getCustomer().getUserId();
        String orderId = payment.getOrder().getOrderId();

        // Update user payment history
        userPaymentHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(payment);
        
        // Update order payment history
        orderPaymentHistory.computeIfAbsent(orderId, k -> new ArrayList<>()).add(payment);
    }

    private boolean simulatePaymentProcessing(Payment payment) {
        // Simulate payment processing with different success rates based on payment method
        // In real implementation, this would integrate with payment gateways
        try {
            Thread.sleep(1000); // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        double successRate = getSuccessRateForPaymentMethod(payment.getPaymentMethod());
        return random.nextDouble() < successRate;
    }

    private boolean simulateRefundProcessing(Payment payment, double refundAmount) {
        // Simulate refund processing with 98% success rate
        try {
            Thread.sleep(2000); // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return random.nextDouble() < 0.98;
    }

    private double getSuccessRateForPaymentMethod(PaymentMethod paymentMethod) {
        return switch (paymentMethod) {
            case CREDIT_CARD, DEBIT_CARD -> 0.95;
            case UPI -> 0.98;
            case NET_BANKING -> 0.92;
            case WALLET -> 0.99;
            case CASH_ON_DELIVERY -> 1.0; // Always successful
        };
    }
}