package models;

import enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public class Booking {
    private final String id;
    private final Show show;
    private final List<Seat> seats;
    private final User user;
    private BookingStatus status;
    private final double totalAmount;
    private final LocalDateTime bookingTime;

    public Booking(String id, Show show, List<Seat> seats, User user) {
        this.id = id;
        this.show = show;
        this.seats = seats;
        this.user = user;
        this.status = BookingStatus.PENDING;
        this.totalAmount = seats.stream().mapToDouble(Seat::getPrice).sum();
        this.bookingTime = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Show getShow() {
        return show;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public User getUser() {
        return user;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Booking[" + id + "] " + user.getName() + " | " + show.getMovie().getTitle()
                + " | " + seats.size() + " seats | $" + totalAmount + " | " + status;
    }
}
