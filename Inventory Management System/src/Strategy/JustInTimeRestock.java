package Strategy;

import entities.Product;

public class JustInTimeRestock implements RestockStrategy {

    @Override
    public int restock(Product product) {
        int restockQty = product.getLowStockThreshold() * 2;
        System.out.println("📦 JIT Restock: Adding " + restockQty + " units of \"" + product.getName() + "\"");
        return restockQty;
    }
}
