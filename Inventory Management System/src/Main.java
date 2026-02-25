import Observer.LowStockAlertObserver;
import Services.InventoryManager;
import Services.OrderService;
import Services.WarehouseService;
import Strategy.BulkRestock;
import Strategy.JustInTimeRestock;
import entities.Category;
import entities.Product;

public class Main {

    public static void main(String[] args) {

        // ─── 1. Initialize services ────────────────────────────────────
        InventoryManager inventory = InventoryManager.getInstance();
        OrderService orderService = new OrderService(inventory);
        WarehouseService warehouse = new WarehouseService(new JustInTimeRestock());

        // Register low-stock observer
        inventory.addObserver(new LowStockAlertObserver());

        System.out.println("═══════════════════════════════════════════════");
        System.out.println("   📦 INVENTORY MANAGEMENT SYSTEM — DEMO");
        System.out.println("═══════════════════════════════════════════════\n");

        // ─── 2. Add products ───────────────────────────────────────────
        System.out.println("── Adding Products ──────────────────────────\n");

        inventory.addProduct(new Product("P001", "Laptop", Category.ELECTRONICS, 999.99, 15, 5));
        inventory.addProduct(new Product("P002", "Running Shoes", Category.SPORTS, 129.99, 8, 3));
        inventory.addProduct(new Product("P003", "Office Chair", Category.FURNITURE, 349.99, 4, 2));
        inventory.addProduct(new Product("P004", "T-Shirt", Category.CLOTHING, 29.99, 50, 10));
        inventory.addProduct(new Product("P005", "Protein Bar", Category.FOOD, 3.99, 100, 20));

        inventory.displayInventory();

        // ─── 3. Place orders (stock deducted + low-stock alerts) ───────
        System.out.println("── Placing Orders ──────────────────────────\n");

        orderService.placeOrder("P001", 12); // Laptop: 15 → 3 (triggers low-stock alert)
        orderService.placeOrder("P002", 6); // Shoes: 8 → 2 (triggers low-stock alert)
        orderService.placeOrder("P003", 1); // Chair: 4 → 3

        inventory.displayInventory();

        // ─── 4. Ship and deliver an order ──────────────────────────────
        System.out.println("── Order Lifecycle ─────────────────────────\n");

        orderService.shipOrder("ORD-001");
        orderService.deliverOrder("ORD-001");

        // ─── 5. Cancel an order (stock restored) ──────────────────────
        System.out.println("\n── Cancelling Order ────────────────────────\n");

        orderService.cancelOrder("ORD-002"); // Shoes: 2 → 8 (restored)

        inventory.displayInventory();

        // ─── 6. Restock with JIT strategy ─────────────────────────────
        System.out.println("── Restocking (JIT Strategy) ───────────────\n");

        warehouse.restockProduct(inventory.getProduct("P001"), inventory);

        // ─── 7. Switch to Bulk strategy and restock ───────────────────
        System.out.println("\n── Restocking (Bulk Strategy) ──────────────\n");

        warehouse.setStrategy(new BulkRestock());
        warehouse.restockProduct(inventory.getProduct("P003"), inventory);

        // ─── 8. Try placing order with insufficient stock ─────────────
        System.out.println("\n── Edge Case: Insufficient Stock ───────────\n");

        orderService.placeOrder("P004", 999);

        // ─── 9. Final state ───────────────────────────────────────────
        inventory.displayInventory();
        orderService.displayOrders();

        System.out.println("═══════════════════════════════════════════════");
        System.out.println("   ✅ DEMO COMPLETE");
        System.out.println("═══════════════════════════════════════════════");
    }
}