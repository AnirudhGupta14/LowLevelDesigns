package Services;

import Constants.Direction;

// External Request Command
public class ElevatorRequest {
    private int elevatorId; // ID of the elevator involved in the request
    private int floor; // Floor where the request is made
    private Direction direction; // The direction of the elevator request
    private boolean isInternalRequest; // Distinguishes internal vs external requests

    // Constructor to initialize the elevator request
    public ElevatorRequest(int elevatorId, int floor, boolean isInternalRequest, Direction direction) {
        this.elevatorId = elevatorId;
        this.floor = floor;
        this.isInternalRequest = isInternalRequest;
        this.direction = direction;
    }

    public int getFloor() {
        return floor;
    }
}