package Services;

import entities.Order;
import entities.OrderStatus;
import entities.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private final InventoryManager inventoryManager;
    private final List<Order> orders;
    private int orderCounter;

    public OrderService(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
        this.orders = new ArrayList<>();
        this.orderCounter = 0;
    }

    public Order placeOrder(String productId, int quantity) {
        Product product = inventoryManager.getProduct(productId);
        if (product == null) {
            System.out.println("❌ Cannot place order: Product \"" + productId + "\" not found.");
            return null;
        }

        if (!inventoryManager.hasStock(productId, quantity)) {
            System.out.println("❌ Cannot place order: Insufficient stock for \""
                    + product.getName() + "\". Available: " + product.getQuantity());
            return null;
        }

        orderCounter++;
        String orderId = "ORD-" + String.format("%03d", orderCounter);
        double totalAmount = product.getPrice() * quantity;

        Order order = new Order(orderId, productId, quantity, totalAmount);
        order.setStatus(OrderStatus.CONFIRMED);

        // Deduct stock
        inventoryManager.updateStock(productId, -quantity);

        orders.add(order);
        System.out.println("✅ Order placed: " + order);
        return order;
    }

    public void cancelOrder(String orderId) {
        Order order = findOrder(orderId);
        if (order == null) {
            System.out.println("❌ Order \"" + orderId + "\" not found.");
            return;
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            System.out.println("❌ Order \"" + orderId + "\" is already cancelled.");
            return;
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            System.out.println("❌ Cannot cancel a delivered order.");
            return;
        }

        order.setStatus(OrderStatus.CANCELLED);
        // Restore stock
        inventoryManager.updateStock(order.getProductId(), order.getQuantity());
        System.out.println("↩️  Order cancelled & stock restored: " + order);
    }

    public void shipOrder(String orderId) {
        Order order = findOrder(orderId);
        if (order == null) {
            System.out.println("❌ Order \"" + orderId + "\" not found.");
            return;
        }
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            System.out.println("❌ Only CONFIRMED orders can be shipped. Current: " + order.getStatus());
            return;
        }
        order.setStatus(OrderStatus.SHIPPED);
        System.out.println("🚚 Order shipped: " + order);
    }

    public void deliverOrder(String orderId) {
        Order order = findOrder(orderId);
        if (order == null) {
            System.out.println("❌ Order \"" + orderId + "\" not found.");
            return;
        }
        if (order.getStatus() != OrderStatus.SHIPPED) {
            System.out.println("❌ Only SHIPPED orders can be delivered. Current: " + order.getStatus());
            return;
        }
        order.setStatus(OrderStatus.DELIVERED);
        System.out.println("📬 Order delivered: " + order);
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    private Order findOrder(String orderId) {
        for (Order o : orders) {
            if (o.getId().equals(orderId))
                return o;
        }
        return null;
    }

    public void displayOrders() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                           📋 ALL ORDERS                                ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════╣");
        for (Order o : orders) {
            System.out.println("║  " + o);
        }
        System.out.println("╚══════════════════════════════════════════════════════════════════════════╝\n");
    }
}
