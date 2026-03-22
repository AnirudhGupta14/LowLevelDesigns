package models;

import enums.VehicleType;

/**
 * Represents a driver on the platform.
 * Tracks availability, current location, and associated vehicle.
 */
public class Driver extends User {
    private Vehicle vehicle;
    private boolean available;
    private Location currentLocation;
    private VehicleType vehicleType;

    public Driver(String name, String email, String phone, Vehicle vehicle) {
        super(name, email, phone);
        this.vehicle = vehicle;
        this.vehicleType = vehicle.getVehicleType();
        this.available = true; // driver starts as available
    }

    // ── Getters / Setters ────────────────────────────────────────────────────
    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location loc) {
        this.currentLocation = loc;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    @Override
    public String toString() {
        return String.format("Driver[name=%s, vehicle=%s, available=%b, rating=%.2f]",
                getName(), vehicle, available, getRating());
    }
}
