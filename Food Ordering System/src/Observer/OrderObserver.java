package Observer;

import entities.Order;

public interface OrderObserver {
    void onOrderStatusChanged(Order order);
}
