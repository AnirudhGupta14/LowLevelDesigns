package models;

import enums.VehicleType;

/**
 * Represents a driver on the platform.
 * Tracks availability, current location, and associated vehicle.
 */
public class Driver extends User {
    private Vehicle vehicle;
    private int availableSeats;
    private VehicleType currentRideType;
    private Location currentLocation;
    private VehicleType vehicleType;

    public Driver(String name, String email, String phone, Vehicle vehicle) {
        super(name, email, phone);
        this.vehicle = vehicle;
        this.vehicleType = vehicle.getVehicleType();
        this.availableSeats = vehicle.getCapacity(); // driver starts fully available
        this.currentRideType = null;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────
    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public boolean isAvailable() {
        return availableSeats == vehicle.getCapacity();
    }

    public void setAvailable(boolean available) {
        if (available) {
            this.availableSeats = vehicle.getCapacity();
            this.currentRideType = null;
        } else {
            this.availableSeats = 0;
        }
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void reduceCapacity(int seats) {
        this.availableSeats -= seats;
    }

    public void increaseCapacity(int seats) {
        this.availableSeats += seats;
    }

    public VehicleType getCurrentRideType() {
        return currentRideType;
    }

    public void setCurrentRideType(VehicleType type) {
        this.currentRideType = type;
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
        return String.format("Driver[name=%s, vehicle=%s, availableSeats=%d, rating=%.2f]",
                getName(), vehicle, availableSeats, getRating());
    }
}
