package ParkingSpots;

import Vehicles.Vehicle;

public abstract class ParkingSpot {
    public String id;
    public boolean isFree;
    public ParkingLotEnums.ParkingSpotType type;
    public Vehicle vehicle;

    public ParkingSpot(String id, ParkingLotEnums.ParkingSpotType type) {
        this.id = id;
        this.type = type;
        this.isFree = true;
    }

    public void assignVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.isFree = false;
    }

    public void removeVehicle() {
        this.vehicle = null;
        this.isFree = true;
    }
}
