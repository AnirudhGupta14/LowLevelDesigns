package models;

import enums.SeatCategory;
import enums.SeatStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Seat {
    private final String seatId;
    private final int row;
    private final int column;
    private final SeatCategory category;
    private final Screen screen;
    private SeatStatus status;

    public Seat(int row, int column, SeatCategory category, Screen screen) {
        this.seatId = UUID.randomUUID().toString();
        this.row = row;
        this.column = column;
        this.category = category;
        this.screen = screen;
        this.status = SeatStatus.AVAILABLE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Seat seat = (Seat) obj;
        return seatId.equals(seat.seatId);
    }

    @Override
    public int hashCode() {
        return seatId.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Seat[%d,%d]", row, column);
    }
}


