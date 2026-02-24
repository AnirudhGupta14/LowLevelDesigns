package models;

import enums.SeatCategory;

public class SilverSeat extends Seat {
    public SilverSeat(String id, int row, int col) {
        super(id, row, col, SeatCategory.SILVER, 150.0);
    }
}
