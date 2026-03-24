package services;

import entities.Customer;
import entities.Locker;
import entities.Package;
import observer.NotificationPublisher;
import observer.NotificationSubscriber;
import java.util.ArrayList;
import java.util.List;

public class DeliveryService implements NotificationPublisher {
    private static DeliveryService instance;
    private LockerService lockerService;
    private List<NotificationSubscriber> subscribers;

    private DeliveryService() {
        this.lockerService = LockerService.getInstance();
        this.subscribers = new ArrayList<>();
    }

    public static synchronized DeliveryService getInstance() {
        if (instance == null) {
            instance = new DeliveryService();
        }
        return instance;
    }

    @Override
    public void addSubscriber(NotificationSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void removeSubscriber(NotificationSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void notifySubscribers(Customer customer, String message) {
        for (NotificationSubscriber subscriber : subscribers) {
            subscriber.sendNotification(customer, message);
        }
    }

    public void deliverPackage(Package pkg) {
        System.out.println(
                "Attempting to deliver package " + pkg.getId() + " for customer " + pkg.getCustomer().getName());
        Locker locker = lockerService.assignLocker(pkg);
        if (locker == null) {
            System.out.println("Delivery failed. No suitable locker available for package " + pkg.getId());
            return;
        }

        String otp = generateOTP();
        locker.assignPackage(pkg, otp);
        System.out.println("Package " + pkg.getId() + " assigned to Locker " + locker.getId());

        String message = "Your package " + pkg.getId() + " is delivered to Locker " + locker.getId() + ". Use OTP: "
                + otp + " to pickup.";
        notifySubscribers(pkg.getCustomer(), message);
    }

    private String generateOTP() {
        // Simple OTP generation for demo (6 digits)
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
