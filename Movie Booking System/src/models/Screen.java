package models;

import java.util.ArrayList;
import java.util.List;

public class Screen {
    private final String id;
    private final String name;
    private final List<Seat> seats;

    public Screen(String id, String name) {
        this.id = id;
        this.name = name;
        this.seats = new ArrayList<>();
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Seat> getSeats() {
        return new ArrayList<>(seats);
    }

    @Override
    public String toString() {
        return name + " (" + seats.size() + " seats)";
    }
}
