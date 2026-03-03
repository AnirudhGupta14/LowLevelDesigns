package strategy;

import models.Reservation;

/**
 * Strategy Pattern — defines interchangeable pricing algorithms.
 * Easy to add new pricing models (weekend surcharge, loyalty discount, etc.)
 * without changing any service code.
 */
public interface PricingStrategy {
    /**
     * Calculate the total price for a reservation.
     * 
     * @param reservation The booking whose duration and vehicle rate are used
     * @return final price in rupees
     */
    double calculatePrice(Reservation reservation);
}
