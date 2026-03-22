package strategy;

import enums.VehicleType;
import models.Driver;
import models.Location;
import java.util.Comparator;
import java.util.List;

/**
 * Matches the nearest available driver of the requested vehicle type.
 * Uses Haversine distance from Location.distanceTo().
 */
public class NearestDriverStrategy implements DriverMatchingStrategy {

    @Override
    public Driver matchDriver(Location pickupLocation,
            List<Driver> availableDrivers,
            VehicleType vehicleType) {
        return availableDrivers.stream()
                .filter(d -> d.isAvailable()
                        && d.getVehicleType() == vehicleType
                        && d.getCurrentLocation() != null)
                .min(Comparator.comparingDouble(
                        d -> d.getCurrentLocation().distanceTo(pickupLocation)))
                .orElse(null);
    }
}
