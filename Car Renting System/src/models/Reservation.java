package models;

import constants.ReservationStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Represents a single rental booking.
 * Immutable IDs — only status and amount can change.
 */
public class Reservation {
    private final String reservationId;
    private final User user;
    private final Vehicle vehicle;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private ReservationStatus status;
    private double totalAmount;

    public Reservation(String reservationId, User user, Vehicle vehicle,
            LocalDateTime startTime, LocalDateTime endTime) {
        this.reservationId = reservationId;
        this.user = user;
        this.vehicle = vehicle;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = ReservationStatus.PENDING;
        this.totalAmount = 0.0;
    }

    /** Duration in hours (ceiling). */
    public long getDurationHours() {
        return ChronoUnit.HOURS.between(startTime, endTime) + 1;
    }

    public String getReservationId() {
        return reservationId;
    }

    public User getUser() {
        return user;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void setTotalAmount(double amount) {
        this.totalAmount = amount;
    }

    @Override
    public String toString() {
        return "Reservation[" + reservationId + "] "
                + vehicle.getBrand() + " " + vehicle.getModel()
                + " | User: " + user.getName()
                + " | " + startTime + " → " + endTime
                + " | ₹" + totalAmount + " | " + status;
    }
}
