package models;

/**
 * Represents a rider (passenger) on the platform.
 * Extends User with rider-specific state.
 */
public class Rider extends User {
    private Location currentLocation;
    private boolean activeRide; // true if rider has an ongoing ride

    public Rider(String name, String email, String phone) {
        super(name, email, phone);
        this.activeRide = false;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location location) {
        this.currentLocation = location;
    }

    public boolean hasActiveRide() {
        return activeRide;
    }

    public void setActiveRide(boolean activeRide) {
        this.activeRide = activeRide;
    }
}
