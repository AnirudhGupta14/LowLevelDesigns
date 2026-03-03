package SchedulingStrategy;

import Entities.ElevatorRequest;
import Services.Elevator;
import java.util.List;

/**
 * Strategy interface for elevator scheduling algorithms.
 * Implementations determine which elevator handles a given request.
 */
public interface SchedulingStrategy {
    /**
     * Select the best elevator to handle the given request.
     *
     * @param elevators all available elevators
     * @param request   the incoming elevator request
     * @return the chosen Elevator, or null if none is available
     */
    Elevator selectElevator(List<Elevator> elevators, ElevatorRequest request);
}
