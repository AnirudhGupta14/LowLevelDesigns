package Services;

import ParkingSpots.ParkingSpot;
import ParkingStrategy.ParkingStrategy;
import Vehicles.Vehicle;

public class ParkingAttendant {
    String name;

    public ParkingAttendant(String name) {
        this.name = name;
    }

    public ParkingTicket createTicket(Vehicle vehicle, ParkingLot lot, ParkingStrategy strategy) {
        ParkingSpot spot = strategy.findSpot(lot, vehicle);
        if (spot == null) {
            System.out.println("No available spot for " + vehicle.licenseNumber);
            return null;
        }
        spot.assignVehicle(vehicle);
        System.out.println("Ticket created for vehicle " + vehicle.licenseNumber + " at spot " + spot.id);
        lot.updateDisplay();
        return new ParkingTicket(vehicle, spot);
    }

    public void freeSpot(ParkingSpot spot) {
        spot.removeVehicle();
        System.out.println("Spot " + spot.id + " is now free.");
    }
}

