package Services;

import ParkingSpots.ParkingSpot;
import Vehicles.Vehicle;

import java.util.Date;

public class ParkingTicket {
    Vehicle vehicle;
    public ParkingSpot spot;
    Date startTime;

    public ParkingTicket(Vehicle vehicle, ParkingSpot spot) {
        this.vehicle = vehicle;
        this.spot = spot;
        this.startTime = new Date();
    }

    public double calculateFee() {
        long duration = (new Date().getTime() - startTime.getTime()) / (1000 * 60); // in minutes
        return Math.max(10, duration * 0.5); // base 10 units, 0.5 per minute
    }
}