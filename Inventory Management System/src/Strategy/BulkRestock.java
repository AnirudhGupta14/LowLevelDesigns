package Strategy;

import entities.Product;

public class BulkRestock implements RestockStrategy {

    @Override
    public int restock(Product product) {
        int restockQty = product.getLowStockThreshold() * 10;
        System.out.println("📦 Bulk Restock: Adding " + restockQty + " units of \"" + product.getName() + "\"");
        return restockQty;
    }
}
