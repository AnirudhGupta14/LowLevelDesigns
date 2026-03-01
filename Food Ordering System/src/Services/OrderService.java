package Services;

import Observer.OrderObserver;
import Observer.OrderSubject;
import entities.*;

import java.util.*;

public class OrderService implements OrderSubject {

    private final Map<String, Order> orders;
    private final List<OrderObserver> observers;
    private int orderCounter;

    public OrderService() {
        this.orders = new LinkedHashMap<>();
        this.observers = new ArrayList<>();
        this.orderCounter = 0;
    }

    // ─── Observer Implementation ──────────────────────────────

    @Override
    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Order order) {
        for (OrderObserver observer : observers) {
            observer.onOrderStatusChanged(order);
        }
    }

    // ─── Order Lifecycle ──────────────────────────────────────

    public Order placeOrder(Customer customer, Restaurant restaurant, Map<String, Integer> itemQuantities) {
        // Validate items
        List<OrderItem> orderItems = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : itemQuantities.entrySet()) {
            MenuItem item = restaurant.getMenuItem(entry.getKey());
            if (item == null) {
                System.out.printf("  ❌ Item %s not found in %s's menu%n", entry.getKey(), restaurant.getName());
                return null;
            }
            if (!item.isAvailable()) {
                System.out.printf("  ❌ \"%s\" is currently unavailable%n", item.getName());
                return null;
            }
            orderItems.add(new OrderItem(item, entry.getValue()));
        }

        if (orderItems.isEmpty()) {
            System.out.println("  ❌ Cannot place an empty order");
            return null;
        }

        orderCounter++;
        String orderId = String.format("ORD-%03d", orderCounter);
        Order order = new Order(orderId, customer, restaurant, orderItems);

        orders.put(orderId, order);

        System.out.printf("  ✅ Order %s placed — %d item(s) — ₹%.2f from %s%n",
                orderId, orderItems.size(), order.getTotalAmount(), restaurant.getName());

        notifyObservers(order);
        return order;
    }

    public void confirmOrder(String orderId) {
        Order order = getOrder(orderId);
        if (order == null)
            return;
        if (order.getStatus() != OrderStatus.PLACED) {
            System.out.printf("  ❌ Cannot confirm %s — current status: %s%n", orderId, order.getStatus());
            return;
        }
        order.setStatus(OrderStatus.CONFIRMED);
        System.out.printf("  ✅ Order %s confirmed by %s%n", orderId, order.getRestaurant().getName());
        notifyObservers(order);
    }

    public void prepareOrder(String orderId) {
        Order order = getOrder(orderId);
        if (order == null)
            return;
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            System.out.printf("  ❌ Cannot prepare %s — current status: %s%n", orderId, order.getStatus());
            return;
        }
        order.setStatus(OrderStatus.PREPARING);
        System.out.printf("  👨‍🍳 Order %s is being prepared at %s%n", orderId, order.getRestaurant().getName());
        notifyObservers(order);
    }

    public void outForDelivery(String orderId) {
        Order order = getOrder(orderId);
        if (order == null)
            return;
        if (order.getStatus() != OrderStatus.PREPARING) {
            System.out.printf("  ❌ Cannot dispatch %s — current status: %s%n", orderId, order.getStatus());
            return;
        }
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        System.out.printf("  🚗 Order %s is out for delivery to %s%n", orderId, order.getCustomer().getAddress());
        notifyObservers(order);
    }

    public void deliverOrder(String orderId) {
        Order order = getOrder(orderId);
        if (order == null)
            return;
        if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY) {
            System.out.printf("  ❌ Cannot deliver %s — current status: %s%n", orderId, order.getStatus());
            return;
        }
        order.setStatus(OrderStatus.DELIVERED);
        System.out.printf("  📦 Order %s delivered to %s%n", orderId, order.getCustomer().getName());
        notifyObservers(order);
    }

    public void cancelOrder(String orderId) {
        Order order = getOrder(orderId);
        if (order == null)
            return;
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            System.out.printf("  ❌ Cannot cancel %s — current status: %s%n", orderId, order.getStatus());
            return;
        }
        order.setStatus(OrderStatus.CANCELLED);
        System.out.printf("  🚫 Order %s cancelled — refund of ₹%.2f initiated%n", orderId, order.getTotalAmount());
        notifyObservers(order);
    }

    // ─── Helpers ──────────────────────────────────────────────

    public Order getOrder(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            System.out.printf("  ❌ Order %s not found%n", orderId);
        }
        return order;
    }

    // ─── Display ──────────────────────────────────────────────

    public void displayOrders() {
        System.out.println("  ═══════════════════════════════════════════");
        System.out.println("  📋 ALL ORDERS");
        System.out.println("  ═══════════════════════════════════════════\n");
        for (Order order : orders.values()) {
            System.out.println(order);
        }
    }
}
