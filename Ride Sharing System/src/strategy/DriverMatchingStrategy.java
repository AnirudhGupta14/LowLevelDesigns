package strategy;

import enums.VehicleType;
import models.Driver;
import models.Location;
import java.util.List;

/**
 * Strategy Pattern: Defines algorithm for matching a driver to a ride request.
 * Concrete strategies: NearestDriverStrategy, HighestRatedDriverStrategy
 */
public interface DriverMatchingStrategy {
    /**
     * @param pickupLocation   Rider's pickup point
     * @param availableDrivers Pool of currently available drivers
     * @param vehicleType      Vehicle type requested by the rider
     * @return best matching driver, or null if no driver is available
     */
    Driver matchDriver(Location pickupLocation,
            List<Driver> availableDrivers,
            VehicleType vehicleType);
}
