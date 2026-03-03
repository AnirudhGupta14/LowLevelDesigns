package Entities;

import Constants.Direction;

/**
 * Represents a request for elevator service.
 * Either an external hall call (from a floor) or an internal cabin request
 * (destination floor).
 */
public class ElevatorRequest {
    private final int sourceFloor;
    private final int destinationFloor;
    private final Direction direction;

    // External request constructor (hall call)
    public ElevatorRequest(int sourceFloor, Direction direction) {
        this.sourceFloor = sourceFloor;
        this.destinationFloor = -1; // not yet known
        this.direction = direction;
    }

    // Internal request constructor (cabin button pressed)
    public ElevatorRequest(int sourceFloor, int destinationFloor) {
        this.sourceFloor = sourceFloor;
        this.destinationFloor = destinationFloor;
        this.direction = (destinationFloor > sourceFloor) ? Direction.UP : Direction.DOWN;
    }

    public int getSourceFloor() {
        return sourceFloor;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        if (destinationFloor == -1) {
            return String.format("ExternalRequest[floor=%d, dir=%s]", sourceFloor, direction);
        }
        return String.format("InternalRequest[src=%d, dst=%d, dir=%s]",
                sourceFloor, destinationFloor, direction);
    }
}
