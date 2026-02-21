package models;

import enums.BookingStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Booking {
    private final String bookingId;
    private final User user;
    private final Show show;
    private final List<Seat> seats;
    private final LocalDateTime bookingTime;
    private BookingStatus status;
    private Payment payment;

    public Booking(User user, Show show, List<Seat> seats) {
        this.bookingId = UUID.randomUUID().toString();
        this.user = user;
        this.show = show;
        this.seats = List.copyOf(seats);
        this.bookingTime = LocalDateTime.now();
        this.status = BookingStatus.CREATED;
    }

    public void confirm() {
        if (status != BookingStatus.CREATED) {
            throw new IllegalStateException("Cannot confirm booking in current state: " + status);
        }
        this.status = BookingStatus.CONFIRMED;
    }

    public void expire() {
        if (status == BookingStatus.CREATED) {
            this.status = BookingStatus.EXPIRED;
        }
    }

    public void cancel() {
        if (status == BookingStatus.CREATED || status == BookingStatus.CONFIRMED) {
            this.status = BookingStatus.CANCELLED;
        }
    }

    public double calculateTotalAmount() {
        return seats.stream()
                .mapToDouble(this::getSeatPrice)
                .sum();
    }

    private double getSeatPrice(Seat seat) {
        return switch (seat.getCategory()) {
            case SILVER -> 200.0;
            case GOLD -> 300.0;
            case PLATINUM -> 500.0;
        };
    }

    public List<Seat> getSeats() { return List.copyOf(seats); }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Booking booking = (Booking) obj;
        return bookingId.equals(booking.bookingId);
    }

    @Override
    public int hashCode() {
        return bookingId.hashCode();
    }
}