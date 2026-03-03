package observer;

import models.Reservation;

/**
 * Concrete Observer — simulates sending email notifications to the user.
 */
public class EmailNotificationObserver implements ReservationObserver {

    @Override
    public void onReservationCreated(Reservation reservation) {
        System.out.println("  [EMAIL] Booking confirmed! Sent to "
                + reservation.getUser().getEmail()
                + " | " + reservation.getVehicle().getBrand() + " "
                + reservation.getVehicle().getModel()
                + " | ₹" + reservation.getTotalAmount());
    }

    @Override
    public void onReservationCancelled(Reservation reservation) {
        System.out.println("  [EMAIL] Cancellation notice sent to "
                + reservation.getUser().getEmail()
                + " for reservation " + reservation.getReservationId());
    }

    @Override
    public void onReservationCompleted(Reservation reservation) {
        System.out.println("  [EMAIL] Thank you! Invoice sent to "
                + reservation.getUser().getEmail()
                + " | Total paid: ₹" + reservation.getTotalAmount());
    }
}
