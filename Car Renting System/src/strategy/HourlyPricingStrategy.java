package strategy;

import models.Reservation;

/**
 * Charges by the hour.
 * Total = pricePerHour × durationHours
 */
public class HourlyPricingStrategy implements PricingStrategy {

    @Override
    public double calculatePrice(Reservation reservation) {
        long hours = reservation.getDurationHours();
        double rate = reservation.getVehicle().getPricePerHour();
        return rate * hours;
    }

    @Override
    public String toString() {
        return "HourlyPricing";
    }
}
