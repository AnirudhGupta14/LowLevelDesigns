package Services;

import Product.Product;
import Product.ProductFactory;

import java.util.ArrayList;
import java.util.List;

public class InventoryManager {
    private static InventoryManager instance;

    // System components
    private final List<Warehouse> warehouses;
    private final ProductFactory productFactory;

    // Private constructor to prevent instantiation from outside
    private InventoryManager() {
        // Initialize collections and dependencies
        warehouses = new ArrayList<>();
        productFactory = new ProductFactory();
    }

    // Static method to get the singleton instance with thread safety
    public static synchronized InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();
        }
        return instance;
    }

    // Warehouse management
    public void addWarehouse(Warehouse warehouse) {
        warehouses.add(warehouse);
    }

    public void removeWarehouse(Warehouse warehouse) {
        warehouses.remove(warehouse);
    }

    // Product inventory operations
    public Product getProductBySku(String sku) {
        for (Warehouse warehouse : warehouses) {
            Product product = warehouse.getProductBySku(sku);
            if (product != null) {
                return product;
            }
        }
        return null;
    }

    // Check stock levels and apply replenishment strategy if needed
    public void checkAndReplenish(String sku) {
        Product product = getProductBySku(sku);
        if (product != null) {
            // If product is below threshold, notify observers
            if (product.getQuantity() < product.getThreshold()) {

            }
        }
    }
}
