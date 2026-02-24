package models;

import java.time.LocalDateTime;
import java.util.*;

public class Show {
    private final String id;
    private final Movie movie;
    private final Screen screen;
    private final LocalDateTime startTime;
    private final Map<Seat, Boolean> seatAvailability; // true = available

    public Show(String id, Movie movie, Screen screen, LocalDateTime startTime) {
        this.id = id;
        this.movie = movie;
        this.screen = screen;
        this.startTime = startTime;
        this.seatAvailability = new LinkedHashMap<>();
        for (Seat seat : screen.getSeats()) {
            seatAvailability.put(seat, true);
        }
    }

    public String getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public Screen getScreen() {
        return screen;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public List<Seat> getAvailableSeats() {
        List<Seat> available = new ArrayList<>();
        for (Map.Entry<Seat, Boolean> entry : seatAvailability.entrySet()) {
            if (entry.getValue()) {
                available.add(entry.getKey());
            }
        }
        return available;
    }

    public boolean isSeatAvailable(Seat seat) {
        return seatAvailability.getOrDefault(seat, false);
    }

    public synchronized void markSeatBooked(Seat seat) {
        seatAvailability.put(seat, false);
    }

    public synchronized void markSeatAvailable(Seat seat) {
        seatAvailability.put(seat, true);
    }

    @Override
    public String toString() {
        return movie.getTitle() + " @ " + screen.getName() + " [" + startTime + "]";
    }
}
