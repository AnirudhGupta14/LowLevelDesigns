package models;

import enums.SeatCategory;

public class GoldSeat extends Seat {
    public GoldSeat(String id, int row, int col) {
        super(id, row, col, SeatCategory.GOLD, 250.0);
    }
}
