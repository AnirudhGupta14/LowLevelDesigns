package observer;

import models.Booking;

// Concrete observer that prints notification messages for booking events
public class NotificationService implements BookingObserver {

    @Override
    public void onBookingConfirmed(Booking booking) {
        System.out.println("📧 NOTIFICATION: Booking " + booking.getId() + " CONFIRMED for "
                + booking.getUser().getName() + " — " + booking.getShow().getMovie().getTitle()
                + " (" + booking.getSeats().size() + " seats, $" + booking.getTotalAmount() + ")");
    }

    @Override
    public void onBookingExpired(Booking booking) {
        System.out.println("⏰ NOTIFICATION: Booking " + booking.getId() + " EXPIRED for "
                + booking.getUser().getName() + " — seats have been released.");
    }

    @Override
    public void onBookingCancelled(Booking booking) {
        System.out.println("❌ NOTIFICATION: Booking " + booking.getId() + " CANCELLED for "
                + booking.getUser().getName() + " — seats have been released.");
    }
}
