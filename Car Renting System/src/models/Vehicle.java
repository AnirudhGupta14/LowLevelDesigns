package models;

import constants.VehicleStatus;
import constants.VehicleType;

/**
 * Represents a vehicle in the rental fleet.
 * Holds pricing per hour (used by PricingStrategy).
 */
public class Vehicle {
    private final String vehicleId;
    private final String brand;
    private final String model;
    private final VehicleType type;
    private final double pricePerHour;
    private VehicleStatus status;

    public Vehicle(String vehicleId, String brand, String model,
            VehicleType type, double pricePerHour) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        this.type = type;
        this.pricePerHour = pricePerHour;
        this.status = VehicleStatus.AVAILABLE;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public VehicleType getType() {
        return type;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return brand + " " + model + " [" + type + "] - ₹" + pricePerHour + "/hr | " + status;
    }
}
