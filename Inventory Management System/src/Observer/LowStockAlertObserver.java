package Observer;

import entities.Product;

public class LowStockAlertObserver implements InventoryObserver {

    @Override
    public void onStockUpdate(Product product) {
        if (product.getQuantity() <= product.getLowStockThreshold()) {
            System.out.println("⚠️  LOW STOCK ALERT: \"" + product.getName() + "\" has only "
                    + product.getQuantity() + " units left (threshold: " + product.getLowStockThreshold() + ")");
        }
    }
}
