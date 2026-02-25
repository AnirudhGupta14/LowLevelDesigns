package Services;

import Observer.InventoryObserver;
import Observer.InventorySubject;
import entities.Category;
import entities.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManager implements InventorySubject {

    private static InventoryManager instance;

    private final Map<String, Product> products;
    private final List<InventoryObserver> observers;

    private InventoryManager() {
        this.products = new HashMap<>();
        this.observers = new ArrayList<>();
    }

    public static synchronized InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();
        }
        return instance;
    }

    // ─── Observer management ───────────────────────────────────────

    @Override
    public void addObserver(InventoryObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(InventoryObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Product product) {
        for (InventoryObserver observer : observers) {
            observer.onStockUpdate(product);
        }
    }

    // ─── Product CRUD ──────────────────────────────────────────────

    public void addProduct(Product product) {
        if (products.containsKey(product.getId())) {
            System.out.println("❌ Product with id \"" + product.getId() + "\" already exists.");
            return;
        }
        products.put(product.getId(), product);
        System.out.println("✅ Added: " + product);
    }

    public void removeProduct(String productId) {
        Product removed = products.remove(productId);
        if (removed == null) {
            System.out.println("❌ Product \"" + productId + "\" not found.");
        } else {
            System.out.println("🗑️  Removed: " + removed.getName());
        }
    }

    public Product getProduct(String productId) {
        return products.get(productId);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public List<Product> getProductsByCategory(Category category) {
        List<Product> result = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getCategory() == category) {
                result.add(p);
            }
        }
        return result;
    }

    // ─── Stock operations ──────────────────────────────────────────

    public synchronized void updateStock(String productId, int quantityChange) {
        Product product = products.get(productId);
        if (product == null) {
            System.out.println("❌ Product \"" + productId + "\" not found.");
            return;
        }

        int newQty = product.getQuantity() + quantityChange;
        if (newQty < 0) {
            System.out.println("❌ Insufficient stock for \"" + product.getName()
                    + "\". Available: " + product.getQuantity() + ", requested: " + Math.abs(quantityChange));
            return;
        }

        product.setQuantity(newQty);
        notifyObservers(product);
    }

    public boolean hasStock(String productId, int requiredQty) {
        Product product = products.get(productId);
        return product != null && product.getQuantity() >= requiredQty;
    }

    // ─── Display ───────────────────────────────────────────────────

    public void displayInventory() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    📦 INVENTORY STATUS                      ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║ %-6s ║ %-15s ║ %-12s ║ %8s ║ %5s ║%n", "ID", "Name", "Category", "Price", "Qty");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        for (Product p : products.values()) {
            System.out.printf("║ %-6s ║ %-15s ║ %-12s ║ %8.2f ║ %5d ║%n",
                    p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getQuantity());
        }
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
    }
}
