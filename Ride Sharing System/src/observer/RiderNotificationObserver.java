package observer;

import models.Ride;

/**
 * Sends push notifications to the Rider when ride status changes.
 */
public class RiderNotificationObserver implements RideStatusObserver {

    @Override
    public void onRideStatusChanged(Ride ride) {
        String message = buildMessage(ride);
        // In production: integrate with Firebase FCM / APNs
        System.out.printf("[NOTIFICATION → Rider %s] %s%n",
                ride.getRider().getName(), message);
    }

    private String buildMessage(Ride ride) {
        switch (ride.getStatus()) {
            case DRIVER_ASSIGNED:
                return String.format("Your driver %s is assigned! Vehicle: %s",
                        ride.getDriver().getName(),
                        ride.getDriver().getVehicle());
            case DRIVER_EN_ROUTE:
                return "Your driver is on the way to pick you up.";
            case RIDE_STARTED:
                return "Your ride has started. Enjoy your journey!";
            case COMPLETED:
                return String.format("Ride completed! Fare: ₹%.2f", ride.getFare());
            case CANCELLED:
                return "Your ride has been cancelled.";
            default:
                return "Ride status updated: " + ride.getStatus();
        }
    }
}
