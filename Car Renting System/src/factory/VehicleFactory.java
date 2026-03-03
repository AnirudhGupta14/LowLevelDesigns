package factory;

import constants.VehicleType;
import models.Vehicle;

import java.util.UUID;

/**
 * Factory Pattern — creates Vehicle instances with generated IDs.
 * Centralizes vehicle creation logic so callers don't deal with IDs.
 */
public class VehicleFactory {

    private VehicleFactory() {
    } // Utility class

    public static Vehicle createVehicle(String brand, String model,
            VehicleType type, double pricePerHour) {
        String id = "VH-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return new Vehicle(id, brand, model, type, pricePerHour);
    }
}
