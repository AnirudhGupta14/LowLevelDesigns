package observer;

import models.Ride;

/**
 * Sends push notifications to the Driver when ride status changes.
 */
public class DriverNotificationObserver implements RideStatusObserver {

    @Override
    public void onRideStatusChanged(Ride ride) {
        if (ride.getDriver() == null)
            return;
        String message = buildMessage(ride);
        // In production: integrate with Firebase FCM / APNs
        System.out.printf("[NOTIFICATION → Driver %s] %s%n",
                ride.getDriver().getName(), message);
    }

    private String buildMessage(Ride ride) {
        switch (ride.getStatus()) {
            case DRIVER_ASSIGNED:
                return String.format("New ride request! Pickup: %s",
                        ride.getPickupLocation());
            case DRIVER_EN_ROUTE:
                return "Navigate to pickup location.";
            case RIDE_STARTED:
                return String.format("Ride started. Navigate to: %s",
                        ride.getDropoffLocation());
            case COMPLETED:
                return String.format("Ride completed! Earnings: ₹%.2f", ride.getFare());
            case CANCELLED:
                return "Ride was cancelled. You are available for new rides.";
            default:
                return "Ride status updated: " + ride.getStatus();
        }
    }
}
