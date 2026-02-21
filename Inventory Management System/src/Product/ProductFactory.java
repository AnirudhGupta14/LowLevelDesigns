package Product;

import Constants.ProductCategory;
import Services.SupplierInfo;

public class ProductFactory {
    public Product createProduct(ProductCategory category, String sku, String description, SupplierInfo supplier, String name, double price, int quantity) {
        switch (category) {
            case ELECTRONICS:
                return new ElectronicsProduct(sku, name, price, quantity, description, supplier);
            case CLOTHING:
                return new ClothingProduct(sku, name, price, quantity, description, supplier);
            default:
                throw new IllegalArgumentException("Unsupported product category: " + category);
        }
    }
}
