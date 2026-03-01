package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Restaurant {

    private final String id;
    private String name;
    private String address;
    private final List<MenuItem> menu;

    public Restaurant(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.menu = new ArrayList<>();
    }

    // ─── Getters ──────────────────────────────────────────────

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public List<MenuItem> getMenu() {
        return menu;
    }

    // ─── Menu Management ──────────────────────────────────────

    public void addMenuItem(MenuItem item) {
        menu.add(item);
        System.out.printf("  ✅ Added \"%s\" (₹%.2f) to %s's menu%n", item.getName(), item.getPrice(), name);
    }

    public void removeMenuItem(String itemId) {
        menu.removeIf(item -> item.getId().equals(itemId));
    }

    public MenuItem getMenuItem(String itemId) {
        return menu.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    // ─── Display ──────────────────────────────────────────────

    public void displayMenu() {
        System.out.printf("  🍽️  Menu — %s (%s)%n", name, address);
        System.out.println("  ──────┬────────────────────────┬────────────────┬───────────┬──────");
        System.out.println("  ID    │ Name                   │ Category       │ Price     │ Avail");
        System.out.println("  ──────┼────────────────────────┼────────────────┼───────────┼──────");
        for (MenuItem item : menu) {
            System.out.println(item);
        }
        System.out.println("  ──────┴────────────────────────┴────────────────┴───────────┴──────\n");
    }

    @Override
    public String toString() {
        return String.format("🏪 %s — %s (%d items)", name, address, menu.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Restaurant r = (Restaurant) o;
        return Objects.equals(id, r.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
