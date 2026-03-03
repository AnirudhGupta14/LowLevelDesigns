package observer;

import models.Reservation;

/**
 * Observer Pattern — interface for components that react to reservation
 * changes.
 */
public interface ReservationObserver {
    void onReservationCreated(Reservation reservation);

    void onReservationCancelled(Reservation reservation);

    void onReservationCompleted(Reservation reservation);
}
