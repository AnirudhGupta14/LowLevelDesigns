package observer;

import entities.Customer;

public interface NotificationSubscriber {
    void sendNotification(Customer customer, String message);
}
