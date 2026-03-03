package service;

import constants.ReservationStatus;
import constants.VehicleStatus;
import models.Reservation;
import models.User;
import models.Vehicle;
import observer.ReservationObserver;
import strategy.PricingStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton Pattern — central reservation manager.
 *
 * Responsibilities:
 * - Create, cancel, and complete reservations
 * - Apply a pluggable PricingStrategy (Strategy Pattern)
 * - Notify all registered observers (Observer Pattern) on every lifecycle event
 */
public class ReservationService {

    // ── Singleton ───────────────────────────────────────────────────
    private static ReservationService instance;

    public static synchronized ReservationService getInstance() {
        if (instance == null) {
            instance = new ReservationService();
        }
        return instance;
    }

    private ReservationService() {
    }

    // ── State ────────────────────────────────────────────────────────
    private final Map<String, Reservation> reservations = new HashMap<>();
    private final List<ReservationObserver> observers = new ArrayList<>();
    private PricingStrategy pricingStrategy;

    // ── Observer management ──────────────────────────────────────────
    public void addObserver(ReservationObserver observer) {
        observers.add(observer);
    }

    public void setPricingStrategy(PricingStrategy strategy) {
        this.pricingStrategy = strategy;
        System.out.println("  [ReservationService] Pricing strategy set to: " + strategy);
    }

    // ── Core Operations ──────────────────────────────────────────────

    /**
     * Create a reservation for a vehicle.
     * Validates: vehicle must be AVAILABLE, pricingStrategy must be set.
     */
    public Reservation createReservation(User user, Vehicle vehicle,
            LocalDateTime startTime, LocalDateTime endTime) {
        if (!vehicle.isAvailable()) {
            System.out.println("  [ReservationService] FAILED: Vehicle "
                    + vehicle.getVehicleId() + " is not available.");
            return null;
        }
        if (pricingStrategy == null) {
            throw new IllegalStateException("No pricing strategy set on ReservationService.");
        }

        String id = "RES-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Reservation reservation = new Reservation(id, user, vehicle, startTime, endTime);

        // Calculate price via Strategy Pattern
        double price = pricingStrategy.calculatePrice(reservation);
        reservation.setTotalAmount(price);
        reservation.setStatus(ReservationStatus.CONFIRMED);

        // Mark vehicle as rented
        vehicle.setStatus(VehicleStatus.RENTED);
        reservations.put(id, reservation);

        System.out.println("  [ReservationService] Created: " + reservation);
        // Notify observers
        for (ReservationObserver obs : observers) {
            obs.onReservationCreated(reservation);
        }
        return reservation;
    }

    /**
     * Cancel a reservation — frees the vehicle back to AVAILABLE.
     */
    public void cancelReservation(String reservationId) {
        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            System.out.println("  [ReservationService] Reservation not found: " + reservationId);
            return;
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED ||
                reservation.getStatus() == ReservationStatus.COMPLETED) {
            System.out.println("  [ReservationService] Cannot cancel — status is: " + reservation.getStatus());
            return;
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.getVehicle().setStatus(VehicleStatus.AVAILABLE);
        System.out.println("  [ReservationService] Cancelled: " + reservationId);
        for (ReservationObserver obs : observers) {
            obs.onReservationCancelled(reservation);
        }
    }

    /**
     * Complete a reservation — marks vehicle as AVAILABLE again.
     */
    public void completeReservation(String reservationId) {
        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            System.out.println("  [ReservationService] Reservation not found: " + reservationId);
            return;
        }
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            System.out.println("  [ReservationService] Cannot complete — status is: " + reservation.getStatus());
            return;
        }
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.getVehicle().setStatus(VehicleStatus.AVAILABLE);
        System.out.println("  [ReservationService] Completed: " + reservationId);
        for (ReservationObserver obs : observers) {
            obs.onReservationCompleted(reservation);
        }
    }

    public Reservation getReservation(String reservationId) {
        return reservations.get(reservationId);
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations.values());
    }
}
