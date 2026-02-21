package observer;

import models.*;

public interface Observer {
    void update(User user, String eventType, Object data);
    void update(Restaurant restaurant, String eventType, Object data);
    void update(DeliveryRider rider, String eventType, Object data);
    void update(Order order, String eventType, Object data);
    void update(Food food, String eventType, Object data);
}

