package models;

import constants.VehicleStatus;
import constants.VehicleType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a rental store with its fleet of vehicles.
 * Provides search capability to find available vehicles by type.
 */
public class Store {
    private final String storeId;
    private final String location;
    private final List<Vehicle> vehicles;

    public Store(String storeId, String location) {
        this.storeId = storeId;
        this.location = location;
        this.vehicles = new ArrayList<>();
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        System.out.println("  [Store] Vehicle added to " + location + ": " + vehicle);
    }

    public void removeVehicle(String vehicleId) {
        vehicles.removeIf(v -> v.getVehicleId().equals(vehicleId));
    }

    /**
     * Returns all available vehicles of the requested type.
     * Pass null to get all available vehicles regardless of type.
     */
    public List<Vehicle> searchAvailableVehicles(VehicleType type) {
        return vehicles.stream()
                .filter(Vehicle::isAvailable)
                .filter(v -> type == null || v.getType() == type)
                .collect(Collectors.toList());
    }

    public List<Vehicle> getAllVehicles() {
        return vehicles;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Store[" + storeId + ", " + location + ", vehicles=" + vehicles.size() + "]";
    }
}
