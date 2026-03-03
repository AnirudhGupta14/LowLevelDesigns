package strategy;

import models.Reservation;

/**
 * Charges a flat daily rate (rate × 24 × days).
 * Gives a ~10% discount compared to hourly for full-day rentals.
 */
public class DailyPricingStrategy implements PricingStrategy {

    private static final int HOURS_PER_DAY = 24;
    private static final double DAILY_DISCOUNT = 0.90; // 10% off

    @Override
    public double calculatePrice(Reservation reservation) {
        long hours = reservation.getDurationHours();
        long days = (hours / HOURS_PER_DAY) + (hours % HOURS_PER_DAY > 0 ? 1 : 0);
        double dailyRate = reservation.getVehicle().getPricePerHour() * HOURS_PER_DAY * DAILY_DISCOUNT;
        return dailyRate * days;
    }

    @Override
    public String toString() {
        return "DailyPricing";
    }
}
