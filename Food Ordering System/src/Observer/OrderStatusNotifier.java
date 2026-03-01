package Observer;

import entities.Order;
import entities.OrderStatus;

public class OrderStatusNotifier implements OrderObserver {

    @Override
    public void onOrderStatusChanged(Order order) {
        String emoji = getStatusEmoji(order.getStatus());
        System.out.printf("  🔔 [NOTIFICATION] Order %s → %s %s  (Customer: %s)%n",
                order.getId(), order.getStatus(), emoji, order.getCustomer().getName());
    }

    private String getStatusEmoji(OrderStatus status) {
        switch (status) {
            case PLACED:
                return "📝";
            case CONFIRMED:
                return "✅";
            case PREPARING:
                return "👨‍🍳";
            case OUT_FOR_DELIVERY:
                return "🚗";
            case DELIVERED:
                return "📦";
            case CANCELLED:
                return "❌";
            default:
                return "";
        }
    }
}
