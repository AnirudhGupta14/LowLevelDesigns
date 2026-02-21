package Product;

import Constants.ProductCategory;
import Services.SupplierInfo;

import java.util.ArrayList;
import java.util.List;

public abstract class Product {
    private String name;
    private String sku;
    private String description;
    private double price;
    private final List<ProductCategory> categories;
    private final List<SupplierInfo> suppliers;
    private Integer quantity;
    private Integer threshold;

    public Product(String name, String sku, String description, double price, Integer quantity) {
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.suppliers = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<ProductCategory> getCategories() {
        return categories;
    }

    public void setCategories(ProductCategory category) {
        this.categories.add(category);
    }

    public List<SupplierInfo> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(SupplierInfo supplier) {
        this.suppliers.add(supplier);
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }
}