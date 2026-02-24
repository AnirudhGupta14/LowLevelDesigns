package observer;

import models.Booking;

// Observer interface for booking lifecycle events
public interface BookingObserver {
    void onBookingConfirmed(Booking booking);

    void onBookingExpired(Booking booking);

    void onBookingCancelled(Booking booking);
}
