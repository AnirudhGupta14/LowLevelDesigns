package Product;

import Constants.ProductCategory;
import Services.SupplierInfo;

public class ClothingProduct extends Product {
    public ClothingProduct(String sku, String name, double price, int quantity, String description, SupplierInfo supplier) {
        super(name, sku, description, price, quantity);
        this.setCategories(ProductCategory.CLOTHING);
        this.setSuppliers(supplier);
    }
}
