package Services;

import Constants.ParkingLotEnums;
import ParkingSpots.ParkingSpot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Floor {
    int number;
    public Map<ParkingLotEnums.ParkingSpotType, List<ParkingSpot>> spots;

    public Floor(int number) {
        this.number = number;
        spots = new HashMap<>();
        for (ParkingLotEnums.ParkingSpotType type : ParkingLotEnums.ParkingSpotType.values()) {
            spots.put(type, new ArrayList<>());
        }
    }

    public void addSpot(ParkingSpot spot) {
        spots.get(spot.type).add(spot);
    }

    public void removeSpot(ParkingSpot spot) {
        spots.get(spot.type).remove(spot);
    }
}
