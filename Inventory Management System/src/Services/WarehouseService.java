package Services;

import Strategy.RestockStrategy;
import entities.Product;

public class WarehouseService {

    private RestockStrategy strategy;

    public WarehouseService(RestockStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(RestockStrategy strategy) {
        this.strategy = strategy;
    }

    public void restockProduct(Product product, InventoryManager inventoryManager) {
        if (product == null) {
            System.out.println("❌ Cannot restock: product is null.");
            return;
        }
        int addedQty = strategy.restock(product);
        inventoryManager.updateStock(product.getId(), addedQty);
        System.out.println("✅ Restocked \"" + product.getName() + "\" — new quantity: " + product.getQuantity());
    }
}
