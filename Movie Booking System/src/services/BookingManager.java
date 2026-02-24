package services;

import enums.BookingStatus;
import enums.PaymentStatus;
import models.*;
import observer.BookingObserver;
import payment.Payment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Singleton BookingManager that coordinates the full booking flow:
 * 1. Lock seats → 2. Create PENDING booking → 3. Process payment → 4. Confirm
 * or Expire
 *
 * Notifies all registered BookingObservers on status changes.
 */
public class BookingManager {

    private static BookingManager instance;
    private final SeatLockManager seatLockManager;
    private final List<BookingObserver> observers = new ArrayList<>();
    private final List<Booking> allBookings = new ArrayList<>();
    private final AtomicInteger bookingCounter = new AtomicInteger(1);

    private BookingManager(SeatLockManager seatLockManager) {
        this.seatLockManager = seatLockManager;
    }

    public static synchronized BookingManager getInstance(SeatLockManager seatLockManager) {
        if (instance == null) {
            instance = new BookingManager(seatLockManager);
        }
        return instance;
    }

    // Observer management
    public void addObserver(BookingObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(BookingObserver observer) {
        observers.remove(observer);
    }

    /**
     * Step 1 & 2: Lock seats and create a PENDING booking.
     * Returns null if seats couldn't be locked.
     */
    public synchronized Booking initiateBooking(Show show, List<Seat> seats, User user) {
        // Verify all seats are actually available in the show
        for (Seat seat : seats) {
            if (!show.isSeatAvailable(seat)) {
                System.out.println("❌ Seat " + seat + " is not available for this show.");
                return null;
            }
        }

        // Try to lock the seats
        boolean locked = seatLockManager.lockSeats(show, seats, user);
        if (!locked) {
            System.out.println("❌ Could not lock seats. They may be held by another user.");
            return null;
        }

        // Create the PENDING booking
        String bookingId = "BK-" + bookingCounter.getAndIncrement();
        Booking booking = new Booking(bookingId, show, seats, user);
        allBookings.add(booking);

        System.out.println("📋 Booking initiated: " + booking);
        return booking;
    }

    /**
     * Step 3 & 4: Process payment and confirm booking.
     * If payment fails, booking stays PENDING (user can retry).
     */
    public synchronized boolean confirmBooking(Booking booking, Payment paymentMethod) {
        if (booking.getStatus() != BookingStatus.PENDING) {
            System.out.println("❌ Booking " + booking.getId() + " is not in PENDING state. Current: "
                    + booking.getStatus());
            return false;
        }

        // Check if locks are still valid (haven't expired)
        for (Seat seat : booking.getSeats()) {
            if (!seatLockManager.isLocked(booking.getShow(), seat)) {
                System.out.println("⏰ Lock expired for seat " + seat + ". Booking expired.");
                expireBooking(booking);
                return false;
            }
        }

        // Process payment
        PaymentStatus paymentStatus = paymentMethod.pay(booking.getTotalAmount());
        if (paymentStatus != PaymentStatus.PAID) {
            System.out.println("❌ Payment failed for booking " + booking.getId());
            return false;
        }

        // Mark seats as booked in the show
        for (Seat seat : booking.getSeats()) {
            booking.getShow().markSeatBooked(seat);
        }

        // Unlock seats (they're now permanently booked, no longer just locked)
        seatLockManager.unlockSeats(booking.getShow(), booking.getSeats());

        // Update booking status
        booking.setStatus(BookingStatus.CONFIRMED);
        System.out.println("✅ Booking confirmed: " + booking);

        // Notify observers
        for (BookingObserver observer : observers) {
            observer.onBookingConfirmed(booking);
        }
        return true;
    }

    /**
     * Cancel a booking and release the seats.
     */
    public synchronized void cancelBooking(Booking booking) {
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            // Release seats back to show
            for (Seat seat : booking.getSeats()) {
                booking.getShow().markSeatAvailable(seat);
            }
        }

        // Remove locks if they exist
        seatLockManager.unlockSeats(booking.getShow(), booking.getSeats());

        booking.setStatus(BookingStatus.CANCELLED);
        System.out.println("❌ Booking cancelled: " + booking);

        // Notify observers
        for (BookingObserver observer : observers) {
            observer.onBookingCancelled(booking);
        }
    }

    /**
     * Expire a booking (lock timed out before payment).
     */
    private void expireBooking(Booking booking) {
        booking.setStatus(BookingStatus.EXPIRED);
        seatLockManager.unlockSeats(booking.getShow(), booking.getSeats());
        System.out.println("⏰ Booking expired: " + booking);

        // Notify observers
        for (BookingObserver observer : observers) {
            observer.onBookingExpired(booking);
        }
    }

    /**
     * Get all bookings in the system.
     */
    public List<Booking> getAllBookings() {
        return new ArrayList<>(allBookings);
    }
}
