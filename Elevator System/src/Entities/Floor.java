package Entities;

/**
 * Represents a single floor in the building.
 * Floor number is used as the identifier for requests and elevator targeting.
 * (ExternalButton removed — requests are modelled directly via
 * ElevatorRequest.)
 */
public class Floor {
    private final int floorNumber;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    @Override
    public String toString() {
        return "Floor-" + floorNumber;
    }
}
