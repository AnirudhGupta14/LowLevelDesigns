package observer;

import entities.Customer;

public class EmailNotificationService implements NotificationSubscriber {

    @Override
    public void sendNotification(Customer customer, String message) {
        System.out.println("Sending Email to " + customer.getEmail() + " | Message: " + message);
    }
}
