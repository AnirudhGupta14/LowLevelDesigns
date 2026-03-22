package models;

import enums.RideStatus;
import enums.VehicleType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import observer.RideStatusObserver;

/**
 * Core aggregate — represents a single ride from request to completion.
 * Implements the Observable side of the Observer pattern for status changes.
 */
public class Ride {
    private final String id;
    private final Rider rider;
    private Driver driver;
    private final Location pickupLocation;
    private final Location dropoffLocation;
    private final VehicleType requestedVehicleType;
    private RideStatus status;
    private double fare;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private final List<RideStatusObserver> observers;

    public Ride(String id, Rider rider, Location pickupLocation,
            Location dropoffLocation, VehicleType requestedVehicleType) {
        this.id = id;
        this.rider = rider;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.requestedVehicleType = requestedVehicleType;
        this.status = RideStatus.REQUESTED;
        this.observers = new ArrayList<>();
    }

    // ── Observer Management ───────────────────────────────────────────────────
    public void addObserver(RideStatusObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(RideStatusObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (RideStatusObserver observer : observers) {
            observer.onRideStatusChanged(this);
        }
    }

    // ── Status Transitions ────────────────────────────────────────────────────
    public void updateStatus(RideStatus newStatus) {
        this.status = newStatus;
        if (newStatus == RideStatus.RIDE_STARTED) {
            this.startTime = LocalDateTime.now();
        } else if (newStatus == RideStatus.COMPLETED || newStatus == RideStatus.CANCELLED) {
            this.endTime = LocalDateTime.now();
        }
        notifyObservers();
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────
    public String getId() {
        return id;
    }

    public Rider getRider() {
        return rider;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public Location getDropoffLocation() {
        return dropoffLocation;
    }

    public VehicleType getRequestedVehicleType() {
        return requestedVehicleType;
    }

    public RideStatus getStatus() {
        return status;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return String.format(
                "Ride[id=%s, rider=%s, driver=%s, status=%s, fare=%.2f]",
                id, rider.getName(),
                driver != null ? driver.getName() : "UNASSIGNED",
                status, fare);
    }
}
