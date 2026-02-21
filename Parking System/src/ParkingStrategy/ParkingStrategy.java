package ParkingStrategy;

import ParkingSpots.ParkingSpot;
import Services.ParkingLot;
import Vehicles.Vehicle;

public interface ParkingStrategy {
    ParkingSpot findSpot(ParkingLot lot, Vehicle vehicle);
}