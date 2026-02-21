package Services;

import Product.Product;

import java.util.HashMap;
import java.util.Map;

public class Warehouse {
    private final int id;
    private final String name;
    private final String location;
    private final Map<String, Product> products; // SKU -> Product

    public Warehouse(int id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.products = new HashMap<>(); // Initialize the product map
    }

    // Adds a new product to the warehouse
    public void addNewProduct(Product product, int quantity) {
        String sku = product.getSku();
        if (!products.containsKey(sku)) {
            products.put(sku, product);
            System.out.println("New product added to warehouse: " + product.getName());
        } else {
            Product existingProduct = products.get(sku);
            if (existingProduct != null) {
                int currentQty = existingProduct.getQuantity();
                existingProduct.setQuantity(currentQty + quantity);
                System.out.println("Added " + quantity + " units to product with SKU: " + sku);
            } else {
                System.out.println("Product with SKU does not exist.");
            }
        }
    }

    public void removeProduct(String sku, int quantity) {
        Product product = products.get(sku);
        if (product == null) {
            System.out.println("Product with SKU " + sku + " not found.");
            return;
        }

        int currentQty = product.getQuantity();
        if (quantity >= currentQty) {
            products.remove(sku);
            System.out.println("Product " + sku + " removed completely from warehouse.");
        } else {
            product.setQuantity(currentQty - quantity);
            System.out.println("Reduced quantity of SKU " + sku + " by " + quantity + ". New quantity: " + product.getQuantity());
        }
    }

    public int getAvailableQuantity(String sku) {
        Product product = products.get(sku);
        if (product != null) {
            return product.getQuantity();
        } else {
            System.out.println("Product with SKU " + sku + " not found.");
            return 0;
        }
    }

    public void printInventory() {
        System.out.println("Inventory for Warehouse: " + name);
        for (String sku : products.keySet()) {
            Product p = products.get(sku);
            int qty = p.getQuantity(); // directly from Product object
            System.out.println("- " + p.getName() + " (SKU: " + sku + ", Quantity: " + qty + ")");
        }
    }

    public Product getProductBySku(String sku) {
        return products.get(sku);
    }
}
