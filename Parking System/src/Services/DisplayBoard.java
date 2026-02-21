package Services;

import Constants.ParkingLotEnums;

import java.util.Map;

class DisplayBoard {
    public void update(Map<ParkingLotEnums.ParkingSpotType, Integer> freeSpots) {
        System.out.println("Available Spots: " + freeSpots);
    }
}
