package models;
import constants.VehicleTypes;

import java.util.*;

public class VehicleStock {
    private Map<VehicleTypes, List<Vehicle>> stockMap;

    public VehicleStock() {
        stockMap = new HashMap<>();
    }

    // Add a vehicle to a type
    public void addVehicle(VehicleTypes type, Vehicle vehicle) {
        stockMap.computeIfAbsent(type, k -> new ArrayList<>()).add(vehicle);
    }

    // Get all vehicles of a type
    public List<Vehicle> getVehiclesByType(VehicleTypes type) {
        return stockMap.getOrDefault(type, Collections.emptyList());
    }

    // Optional: remove a vehicle
    public void removeVehicle(VehicleTypes type, Vehicle vehicle) {
        List<Vehicle> list = stockMap.get(type);
        if (list != null) {
            list.remove(vehicle);
            if (list.isEmpty()) {
                stockMap.remove(type);
            }
        }
    }
}