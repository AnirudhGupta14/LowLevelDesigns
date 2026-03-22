package strategy;

import enums.VehicleType;
import models.Driver;
import models.Location;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SharedDriverStrategy implements DriverMatchingStrategy {

    @Override
    public Driver matchDriver(Location pickupLocation, List<Driver> availableDrivers, VehicleType vehicleType) {
        if (availableDrivers == null || availableDrivers.isEmpty()) {
            return null;
        }

        // Prefer drivers who are already on a SHARED ride to maximize carpooling
        // efficiency
        Optional<Driver> sharingDriver = availableDrivers.stream()
                .filter(d -> d.getCurrentRideType() == VehicleType.SHARED && d.getCurrentLocation() != null)
                .min(Comparator.comparingDouble(d -> d.getCurrentLocation().distanceTo(pickupLocation)));

        return sharingDriver.orElseGet(() -> availableDrivers.stream()
                .filter(d -> d.getCurrentLocation() != null)
                .min(Comparator.comparingDouble(d -> d.getCurrentLocation().distanceTo(pickupLocation)))
                .orElse(null));

        // Fallback: Nearest available empty driver who is eligible for SHARED
    }
}
