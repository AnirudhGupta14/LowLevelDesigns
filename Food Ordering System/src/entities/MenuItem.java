package entities;

import java.util.Objects;

public class MenuItem {

    private final String id;
    private String name;
    private MenuCategory category;
    private double price;
    private boolean available;

    public MenuItem(String id, String name, MenuCategory category, double price, boolean available) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.available = available;
    }

    // ─── Getters ──────────────────────────────────────────────

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MenuCategory getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }

    // ─── Setters ──────────────────────────────────────────────

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(MenuCategory category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // ─── Display ──────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format("  %-6s │ %-22s │ %-14s │ ₹%8.2f │ %s",
                id, name, category, price, available ? "✅" : "❌");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MenuItem item = (MenuItem) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
