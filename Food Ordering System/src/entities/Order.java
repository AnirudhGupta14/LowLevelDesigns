package entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Order {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    private final String id;
    private final Customer customer;
    private final Restaurant restaurant;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final double totalAmount;
    private final LocalDateTime timestamp;

    public Order(String id, Customer customer, Restaurant restaurant, List<OrderItem> items) {
        this.id = id;
        this.customer = customer;
        this.restaurant = restaurant;
        this.items = items;
        this.status = OrderStatus.PLACED;
        this.totalAmount = items.stream().mapToDouble(OrderItem::getSubtotal).sum();
        this.timestamp = LocalDateTime.now();
    }

    // ─── Getters ──────────────────────────────────────────────

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // ─── Setters ──────────────────────────────────────────────

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    // ─── Display ──────────────────────────────────────────────

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  📋 Order %s │ %s │ %s%n", id, status, timestamp.format(FMT)));
        sb.append(String.format("     Customer   : %s%n", customer.getName()));
        sb.append(String.format("     Restaurant : %s%n", restaurant.getName()));
        sb.append("     Items:\n");
        for (OrderItem item : items) {
            sb.append(item).append("\n");
        }
        sb.append(String.format("     ─────────────────────────────────────%n"));
        sb.append(String.format("     Total      : ₹%.2f%n", totalAmount));
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
