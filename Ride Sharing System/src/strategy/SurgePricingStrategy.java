package strategy;

import enums.VehicleType;
import models.Location;

/**
 * Surge pricing: multiplies the base fare by a configurable surge multiplier.
 * Applied during high-demand periods (peak hours, bad weather, events).
 * Decorates the base pricing logic rather than duplicating it.
 */
public class SurgePricingStrategy implements PricingStrategy {

    private final PricingStrategy baseStrategy;
    private final double surgeMultiplier;

    /**
     * @param baseStrategy    Underlying pricing logic (typically
     *                        BasePricingStrategy)
     * @param surgeMultiplier Multiplier applied on top of base fare (e.g., 1.5 for
     *                        1.5x surge)
     */
    public SurgePricingStrategy(PricingStrategy baseStrategy, double surgeMultiplier) {
        if (surgeMultiplier < 1.0) {
            throw new IllegalArgumentException("Surge multiplier must be >= 1.0");
        }
        this.baseStrategy = baseStrategy;
        this.surgeMultiplier = surgeMultiplier;
    }

    @Override
    public double calculateFare(Location pickup, Location dropoff, VehicleType vehicleType) {
        double baseFare = baseStrategy.calculateFare(pickup, dropoff, vehicleType);
        double surgeFare = baseFare * surgeMultiplier;
        System.out.printf("[SurgePricing] Base: %.2f → Surge(%.1fx): %.2f%n",
                baseFare, surgeMultiplier, surgeFare);
        return surgeFare;
    }

    public double getSurgeMultiplier() {
        return surgeMultiplier;
    }
}
