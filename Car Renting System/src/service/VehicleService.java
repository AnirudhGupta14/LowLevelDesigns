package service;

import constants.VehicleType;
import models.Store;
import models.Vehicle;

import java.util.List;

/**
 * Manages vehicles within a store — add, remove, search.
 */
public class VehicleService {
    private final Store store;

    public VehicleService(Store store) {
        this.store = store;
    }

    public void addVehicle(Vehicle vehicle) {
        store.addVehicle(vehicle);
    }

    public void removeVehicle(String vehicleId) {
        store.removeVehicle(vehicleId);
        System.out.println("  [VehicleService] Vehicle " + vehicleId + " removed from store.");
    }

    /**
     * Search available vehicles. Pass null to get all types.
     */
    public List<Vehicle> searchAvailableVehicles(VehicleType type) {
        List<Vehicle> results = store.searchAvailableVehicles(type);
        System.out.println("  [VehicleService] Search (" + (type != null ? type : "ALL") + ")"
                + " → " + results.size() + " available vehicle(s).");
        return results;
    }
}
