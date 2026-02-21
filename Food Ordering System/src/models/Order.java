package models;

import enums.OrderingStatus;
import enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
public class Order {
    private final String orderId;
    private final User customer;
    private final Map<Food, Integer> orderItems; // Food and quantity
    private OrderingStatus orderingStatus;
    private PaymentStatus paymentStatus;
    private final LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private double totalAmount;
    private String deliveryAddress;
    private String specialInstructions;

    public Order(User customer, String deliveryAddress) {
        this.orderId = UUID.randomUUID().toString();
        this.customer = customer;
        this.orderItems = new HashMap<>();
        this.orderingStatus = OrderingStatus.CREATED;
        this.paymentStatus = PaymentStatus.PENDING;
        this.orderDate = LocalDateTime.now();
        this.deliveryAddress = deliveryAddress;
        this.totalAmount = 0.0;
    }

    public void addFoodItem(Food food, int quantity) {
        if (food != null && quantity > 0 && food.isAvailable()) {
            orderItems.put(food, quantity);
            calculateTotalAmount();
        }
    }

    public void removeFoodItem(Food food) {
        if (food != null && orderItems.containsKey(food)) {
            orderItems.remove(food);
            calculateTotalAmount();
        }
    }

    public void updateFoodQuantity(Food food, int newQuantity) {
        if (food != null && orderItems.containsKey(food)) {
            if (newQuantity <= 0) {
                removeFoodItem(food);
            } else {
                orderItems.put(food, newQuantity);
                calculateTotalAmount();
            }
        }
    }

    private void calculateTotalAmount() {
        totalAmount = orderItems.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    public int getTotalItems() {
        return orderItems.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean canBeCancelled() {
        return orderingStatus == OrderingStatus.CREATED || 
               orderingStatus == OrderingStatus.PAYMENT_PENDING;
    }

    public void confirmOrder() {
        if (orderingStatus == OrderingStatus.CREATED) {
            orderingStatus = OrderingStatus.CONFIRMED;
        }
    }

    public void cancelOrder() {
        if (canBeCancelled()) {
            orderingStatus = OrderingStatus.CANCELLED;
            // Restore food quantities
            orderItems.forEach((food, quantity) -> food.addQuantity(quantity));
        }
    }

    public void markPaymentSuccess() {
        if (paymentStatus == PaymentStatus.PENDING) {
            paymentStatus = PaymentStatus.SUCCESS;
            if (orderingStatus == OrderingStatus.PAYMENT_PENDING) {
                orderingStatus = OrderingStatus.CONFIRMED;
            }
        }
    }

    public void markPaymentFailed() {
        if (paymentStatus == PaymentStatus.PENDING) {
            paymentStatus = PaymentStatus.FAILED;
        }
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        if (deliveryDate != null && deliveryDate.isAfter(orderDate)) {
            this.deliveryDate = deliveryDate;
        }
    }

    public boolean isDelivered() {
        return deliveryDate != null && deliveryDate.isBefore(LocalDateTime.now());
    }

    public int getEstimatedPreparationTime() {
        return orderItems.entrySet().stream()
                .mapToInt(entry -> entry.getKey().getPreparationTime() * entry.getValue())
                .max()
                .orElse(0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Order order = (Order) obj;
        return orderId.equals(order.orderId);
    }

    @Override
    public int hashCode() {
        return orderId.hashCode();
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", customer=" + customer.getName() +
                ", orderingStatus=" + orderingStatus +
                ", paymentStatus=" + paymentStatus +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", totalItems=" + getTotalItems() +
                '}';
    }
}
