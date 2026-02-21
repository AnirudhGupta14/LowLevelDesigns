package models;

import constants.VehicleTypes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Store {
    private String storeId;
    private VehicleStock vehicleStock;

    public Store(String storeId) {
        this.storeId = storeId;
        this.vehicleStock = new VehicleStock(); // each store gets its own stock
    }

    public String getStoreId() {
        return storeId;
    }

    public VehicleStock getVehicleStock() {
        return vehicleStock;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void setVehicleStock(VehicleStock vehicleStock) {
        this.vehicleStock = vehicleStock;
    }

    public void addNewVehicle(VehicleTypes type, Vehicle vehicle) {
        vehicleStock.addVehicle(type, vehicle);
    }

    public void removeVehicle(VehicleTypes type, Vehicle vehicle) {
        vehicleStock.removeVehicle(type, vehicle);
    }

    public List<Vehicle> getVehicleByType(VehicleTypes type) {
        return vehicleStock.getVehiclesByType(type);
    }

    public List<Vehicle> getVehicleByTypeSorted(VehicleTypes type, String sortBy) {
        List<Vehicle> vehicles = getVehicleByType(type);

        Comparator<Vehicle> comparator = switch (sortBy.toLowerCase()) {
            case "price" -> Comparator.comparing(Vehicle::getPrice);
            case "color" -> Comparator.comparing(Vehicle::getColor);
            case "manufacturingyear" -> Comparator.comparing(Vehicle::getManufacturingYear);
            case "carcompany" -> Comparator.comparing(Vehicle::getCarCompany);
            default -> throw new IllegalArgumentException("Invalid sort key: " + sortBy);
        };

        return vehicles.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
