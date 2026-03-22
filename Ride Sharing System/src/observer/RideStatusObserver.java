package observer;

import models.Ride;

/**
 * Observer Pattern: Interface for receiving ride status change notifications.
 * Concrete observers: RiderNotificationObserver, DriverNotificationObserver
 */
public interface RideStatusObserver {
    /**
     * Called whenever a Ride's status changes.
     * 
     * @param ride The ride whose status changed (use ride.getStatus() for new
     *             state)
     */
    void onRideStatusChanged(Ride ride);
}
