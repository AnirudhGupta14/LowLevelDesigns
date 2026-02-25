package Observer;

import entities.Product;

public interface InventoryObserver {
    void onStockUpdate(Product product);
}
