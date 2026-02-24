package models;

import enums.SeatCategory;

public class PlatinumSeat extends Seat {
    public PlatinumSeat(String id, int row, int col) {
        super(id, row, col, SeatCategory.PLATINUM, 400.0);
    }
}
