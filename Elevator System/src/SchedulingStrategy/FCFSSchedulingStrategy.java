package SchedulingStrategy;

import Constants.Direction;
import Services.Elevator;
import Services.ElevatorRequest;

import java.util.Queue;

public class FCFSSchedulingStrategy implements SchedulingStrategy {

    @Override
    public int getNextStop(Elevator elevator) {

        Direction elevatorDirection = elevator.getDirection();
        int currentFloor = elevator.getCurrentFloor();

        Queue<ElevatorRequest> requestQueue = elevator.getRequests();

        if (requestQueue.isEmpty())
            return currentFloor;

        int nextRequestedFloor = requestQueue.poll().getFloor();

        if (nextRequestedFloor == currentFloor)
            return currentFloor;
        // Set elevator's direction based on its current state and next floor
        if (elevatorDirection == Direction.IDLE) {
            elevator.setDirection(
                    nextRequestedFloor > currentFloor ? Direction.UP : Direction.DOWN);
        } else if (elevatorDirection == Direction.UP
                && nextRequestedFloor < currentFloor) {
            elevator.setDirection(Direction.DOWN);
        } else if (nextRequestedFloor > currentFloor) {
            elevator.setDirection(Direction.UP);
        }

        return nextRequestedFloor;
    }
}
