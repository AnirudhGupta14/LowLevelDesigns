package strategy;

import enums.VehicleType;
import models.Location;

/**
 * Base fare calculation: baseFare + (ratePerKm × distance).
 * Rate and base fare vary by vehicle type.
 */
public class BasePricingStrategy implements PricingStrategy {

    @Override
    public double calculateFare(Location pickup, Location dropoff, VehicleType vehicleType) {
        double distanceKm = pickup.distanceTo(dropoff);

        double baseFare;
        double ratePerKm;

        switch (vehicleType) {
            case BIKE:
                baseFare = 10.0;
                ratePerKm = 5.0;
                break;
            case AUTO:
                baseFare = 20.0;
                ratePerKm = 8.0;
                break;
            case SEDAN:
                baseFare = 40.0;
                ratePerKm = 12.0;
                break;
            case SUV:
                baseFare = 60.0;
                ratePerKm = 18.0;
                break;
            default:
                baseFare = 40.0;
                ratePerKm = 12.0;
        }

        double fare = baseFare + (ratePerKm * distanceKm);
        // Minimum fare per vehicle type
        double minimumFare = baseFare * 1.5;
        return Math.max(fare, minimumFare);
    }
}
