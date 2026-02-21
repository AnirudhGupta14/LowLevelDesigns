package services;

import enums.BookingStatus;
import enums.SeatStatus;
import models.Booking;
import models.Seat;
import models.Show;
import models.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BookingService {
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();
    private final SeatLockService seatLockService;

    public BookingService(SeatLockService seatLockService) {
        this.seatLockService = seatLockService;
    }

    public Booking createBooking(User user, Show show, List<Seat> seats) {
        // Validate seats are available
        if (!areSeatsAvailable(show, seats)) {
            throw new IllegalStateException("One or more seats are not available");
        }

        // Lock seats for the user
        for (Seat seat : seats) {
            seatLockService.lockSeat(seat, show, user, 5); // 5 minutes timeout
        }

        // Create booking
        Booking booking = new Booking(user, show, seats);
        bookings.put(booking.getBookingId(), booking);

        return booking;
    }

    public Optional<Booking> getBookingById(String bookingId) {
        return Optional.ofNullable(bookings.get(bookingId));
    }

    public List<Booking> getBookingsByUser(User user) {
        return bookings.values().stream()
                .filter(booking -> booking.getUser().equals(user))
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsByShow(Show show) {
        return bookings.values().stream()
                .filter(booking -> booking.getShow().equals(show))
                .collect(Collectors.toList());
    }

    public void confirmBooking(String bookingId, User user) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found");
        }

        if (!booking.getUser().equals(user)) {
            throw new IllegalStateException("User not authorized to confirm this booking");
        }

        if (booking.getStatus() != BookingStatus.CREATED) {
            throw new IllegalStateException("Cannot confirm booking in current state");
        }

        // Validate all seats are still locked by this user
        for (Seat seat : booking.getSeats()) {
            if (!seatLockService.isSeatLockedByUser(seat, booking.getShow(), user)) {
                throw new IllegalStateException("Seat lock expired or invalid");
            }
        }

        // Confirm booking and unlock seats
        booking.confirm();
        seatLockService.unlockSeats(booking.getSeats(), booking.getShow(), user);

        // Mark seats as booked
        for (Seat seat : booking.getSeats()) {
            seat.setStatus(SeatStatus.BOOKED);
        }
    }

    public void cancelBooking(String bookingId, User user) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found");
        }

        if (!booking.getUser().equals(user)) {
            throw new IllegalStateException("User not authorized to cancel this booking");
        }

        booking.cancel();
        seatLockService.unlockSeats(booking.getSeats(), booking.getShow(), user);

        // Mark seats as available
        for (Seat seat : booking.getSeats()) {
            seat.setStatus(SeatStatus.AVAILABLE);
        }
    }

    public void expireBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking != null && booking.getStatus() == BookingStatus.CREATED) {
            booking.expire();
            seatLockService.unlockSeats(booking.getSeats(), booking.getShow(), booking.getUser());

            // Mark seats as available
            for (Seat seat : booking.getSeats()) {
                seat.setStatus(SeatStatus.AVAILABLE);
            }
        }
    }

    public List<Seat> getBookedSeats(Show show) {
        return bookings.values().stream()
                .filter(booking -> booking.getShow().equals(show))
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
                .flatMap(booking -> booking.getSeats().stream())
                .collect(Collectors.toList());
    }

    public List<Seat> getAvailableSeats(Show show) {
        List<Seat> allSeats = show.getScreen().getSeats();
        List<Seat> bookedSeats = getBookedSeats(show);
        List<Seat> lockedSeats = seatLockService.getLockedSeats(show);

        return allSeats.stream()
                .filter(seat -> !bookedSeats.contains(seat))
                .filter(seat -> !lockedSeats.contains(seat))
                .collect(Collectors.toList());
    }

    public boolean areSeatsAvailable(Show show, List<Seat> seats) {
        List<Seat> availableSeats = getAvailableSeats(show);
        return new HashSet<>(availableSeats).containsAll(seats);
    }
}