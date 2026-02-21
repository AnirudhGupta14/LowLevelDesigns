package ParkingStrategy;

import ParkingSpots.ParkingSpot;
import Services.Floor;
import Services.ParkingLot;
import Vehicles.Vehicle;

import java.util.List;

public class NearestFirstParking implements ParkingStrategy {
    public ParkingSpot findSpot(ParkingLot lot, Vehicle vehicle) {
        ParkingLotEnums.ParkingSpotType requiredType = vehicle.getSpotTypeForVehicle(vehicle);

        for (Floor floor : lot.floors) {
            List<ParkingSpot> spots = floor.spots.get(requiredType);
            for (ParkingSpot spot : spots) {
                if (spot.isFree) {
                    return spot;
                }
            }
        }
        return null;
    }
}