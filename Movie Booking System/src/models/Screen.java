package models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Screen {
    private final String screenId;
    private final String name;
    private final Theatre theatre;
    private final List<Seat> seats;

    public Screen(String name, Theatre theatre) {
        this.screenId = UUID.randomUUID().toString();
        this.name = name;
        this.theatre = theatre;
        this.seats = new ArrayList<>();
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Screen screen = (Screen) obj;
        return screenId.equals(screen.screenId);
    }

    @Override
    public int hashCode() {
        return screenId.hashCode();
    }
}