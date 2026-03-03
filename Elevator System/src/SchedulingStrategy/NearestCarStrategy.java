package SchedulingStrategy;

import Constants.ElevatorState;
import Entities.ElevatorRequest;
import Services.Elevator;

import java.util.List;

/**
 * Nearest Car Scheduling Strategy.
 *
 * Selects the elevator closest to the requesting floor regardless of direction.
 * Simple and fair for light traffic scenarios.
 */
public class NearestCarStrategy implements SchedulingStrategy {

    @Override
    public Elevator selectElevator(List<Elevator> elevators, ElevatorRequest request) {
        Elevator best = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            if (elevator.getState() == ElevatorState.MAINTENANCE)
                continue;

            int distance = Math.abs(elevator.getCurrentFloor() - request.getSourceFloor());
            if (distance < minDistance) {
                minDistance = distance;
                best = elevator;
            }
        }
        return best;
    }
}
