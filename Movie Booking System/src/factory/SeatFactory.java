package factory;

import enums.SeatCategory;
import models.*;

// Factory class to create Seat instances based on category type
public class SeatFactory {
    public static Seat createSeat(SeatCategory category, String id, int row, int col) {
        switch (category) {
            case SILVER:
                return new SilverSeat(id, row, col);
            case GOLD:
                return new GoldSeat(id, row, col);
            case PLATINUM:
                return new PlatinumSeat(id, row, col);
            default:
                throw new IllegalArgumentException("Unknown seat category: " + category);
        }
    }
}
