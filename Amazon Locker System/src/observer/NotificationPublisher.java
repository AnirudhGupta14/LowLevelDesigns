package observer;

public interface NotificationPublisher {
    void addSubscriber(NotificationSubscriber subscriber);

    void removeSubscriber(NotificationSubscriber subscriber);

    void notifySubscribers(entities.Customer customer, String message);
}
