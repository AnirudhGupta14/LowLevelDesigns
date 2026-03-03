package SchedulingStrategy;

import Constants.Direction;
import Constants.ElevatorState;
import Entities.ElevatorRequest;
import Services.Elevator;

import java.util.List;

/**
 * SCAN (Elevator/Disk-Scan) Scheduling Strategy.
 *
 * Prefers an elevator that is already moving toward the request floor in the
 * same direction.
 * Falls back to the nearest idle elevator. This is the classic "look" algorithm
 * used in real
 * elevator systems to minimize unnecessary direction changes.
 */
public class ScanSchedulingStrategy implements SchedulingStrategy {

    @Override
    public Elevator selectElevator(List<Elevator> elevators, ElevatorRequest request) {
        Elevator best = null;
        int bestScore = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            if (elevator.getState() == ElevatorState.MAINTENANCE)
                continue;

            int score = computeScore(elevator, request);
            if (score < bestScore) {
                bestScore = score;
                best = elevator;
            }
        }
        return best;
    }

    /**
     * Lower score = better candidate.
     * - Same direction moving toward request: distance (best)
     * - Idle: distance + small penalty
     * - Moving away or opposite direction: totalFloors penalty (worst)
     */
    private int computeScore(Elevator elevator, ElevatorRequest request) {
        int distance = Math.abs(elevator.getCurrentFloor() - request.getSourceFloor());

        if (elevator.getState() == ElevatorState.IDLE) {
            return distance + 5;
        }

        boolean movingUp = elevator.getDirection() == Direction.UP;
        boolean requestUp = request.getDirection() == Direction.UP;
        boolean enRoute = movingUp
                ? elevator.getCurrentFloor() <= request.getSourceFloor()
                : elevator.getCurrentFloor() >= request.getSourceFloor();

        if (enRoute && movingUp == requestUp) {
            return distance; // ideal: already heading that way
        }

        return distance + 50; // penalty for wrong direction
    }
}
