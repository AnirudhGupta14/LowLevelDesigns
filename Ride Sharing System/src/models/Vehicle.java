package models;

import enums.VehicleType;

/**
 * Represents a vehicle registered on the platform.
 * Associated with exactly one Driver.
 */
public class Vehicle {
    private final String id;
    private final String make;
    private final String model;
    private final String licensePlate;
    private final VehicleType vehicleType;

    public Vehicle(String id, String make, String model,
            String licensePlate, VehicleType vehicleType) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
    }

    public String getId() {
        return id;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    @Override
    public String toString() {
        return String.format("Vehicle[%s %s (%s) - %s]",
                make, model, vehicleType, licensePlate);
    }
}
