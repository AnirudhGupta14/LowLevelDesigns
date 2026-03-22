package strategy;

import enums.VehicleType;
import models.Driver;
import models.Location;
import java.util.Comparator;
import java.util.List;

/**
 * Matches the highest-rated available driver of the requested vehicle type.
 * Useful as a quality-first matching algorithm (e.g., premium rides).
 */
public class HighestRatedDriverStrategy implements DriverMatchingStrategy {

    @Override
    public Driver matchDriver(Location pickupLocation,
            List<Driver> availableDrivers,
            VehicleType vehicleType) {
        return availableDrivers.stream()
                .filter(d -> d.isAvailable()
                        && d.getVehicleType() == vehicleType)
                .max(Comparator.comparingDouble(Driver::getRating))
                .orElse(null);
    }
}
