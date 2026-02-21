package Services;

import Constants.ParkingLotEnums;
import ParkingSpots.ParkingSpot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingLot {
    public List<Floor> floors;
    public List<Entrance> entrances;
    public List<Exit> exits;
    public DisplayBoard displayBoard;

    public ParkingLot() {
        floors = new ArrayList<>();
        entrances = new ArrayList<>();
        exits = new ArrayList<>();
        displayBoard = new DisplayBoard();
    }

    public void updateDisplay() {
        Map<ParkingLotEnums.ParkingSpotType, Integer> freeSpots = new HashMap<>();
        for (ParkingLotEnums.ParkingSpotType type : ParkingLotEnums.ParkingSpotType.values()) {
            freeSpots.put(type, 0);
        }
        for (Floor floor : floors) {
            for (ParkingLotEnums.ParkingSpotType type : ParkingLotEnums.ParkingSpotType.values()) {
                for (ParkingSpot spot : floor.spots.get(type)) {
                    if (spot.isFree) {
                        freeSpots.put(type, freeSpots.get(type) + 1);
                    }
                }
            }
        }
        displayBoard.update(freeSpots);
    }
}

