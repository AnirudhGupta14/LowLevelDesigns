package strategy;

import models.Location;
import enums.VehicleType;

/**
 * Strategy Pattern: Defines algorithm for calculating ride fare.
 * Concrete strategies: BasePricingStrategy, SurgePricingStrategy
 */
public interface PricingStrategy {
    /**
     * @param pickupLocation  Start of the ride
     * @param dropoffLocation End of the ride
     * @param vehicleType     Type of vehicle requested
     * @return calculated fare in INR (or applicable currency)
     */
    double calculateFare(Location pickupLocation, Location dropoffLocation, VehicleType vehicleType);
}
