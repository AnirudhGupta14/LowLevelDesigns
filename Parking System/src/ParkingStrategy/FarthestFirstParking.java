package ParkingStrategy;

import ParkingSpots.ParkingSpot;
import Services.Floor;
import Services.ParkingLot;
import Vehicles.Vehicle;

import java.util.List;
import java.util.ListIterator;

class FarthestFirstParking implements ParkingStrategy {
    public ParkingSpot findSpot(ParkingLot lot, Vehicle vehicle) {
        ParkingLotEnums.ParkingSpotType requiredType = vehicle.getSpotTypeForVehicle(vehicle);

        ListIterator<Floor> iter = lot.floors.listIterator(lot.floors.size());
        while (iter.hasPrevious()) {
            Floor floor = iter.previous();
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