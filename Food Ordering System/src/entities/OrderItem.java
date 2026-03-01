package entities;

public class OrderItem {

    private final MenuItem menuItem;
    private final int quantity;
    private final double subtotal;

    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.subtotal = menuItem.getPrice() * quantity;
    }

    // ─── Getters ──────────────────────────────────────────────

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    // ─── Display ──────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format("    %-22s × %d  =  ₹%.2f", menuItem.getName(), quantity, subtotal);
    }
}
