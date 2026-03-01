package Observer;

import entities.Order;

public interface OrderSubject {
    void addObserver(OrderObserver observer);

    void removeObserver(OrderObserver observer);

    void notifyObservers(Order order);
}
