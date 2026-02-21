package Product;

import Constants.ProductCategory;
import Services.SupplierInfo;

public class ElectronicsProduct  extends Product {
    public ElectronicsProduct(String sku, String name, double price, int quantity, String description, SupplierInfo supplier) {
        super(name, sku, description, price, quantity);
        this.setCategories(ProductCategory.ELECTRONICS);
        this.setSuppliers(supplier);
    }
}