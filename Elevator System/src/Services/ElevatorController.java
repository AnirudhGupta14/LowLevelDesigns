package Services;

import Constants.Direction;
import SchedulingStrategy.SchedulingStrategy;

import java.util.ArrayList;
import java.util.List;

public class ElevatorController {
    private List<Elevator> elevators;
    private SchedulingStrategy schedulingStrategy;

    public void setSchedulingStrategy(SchedulingStrategy schedulingStrategy) {
        this.schedulingStrategy = schedulingStrategy;
    }

    public ElevatorController(int numberOfElevators) {
        this.elevators = new ArrayList<>();
        // Initialize elevators with unique IDs
        for (int i = 1; i <= numberOfElevators; i++) {
            elevators.add(new Elevator(i));
        }
    }

    private Elevator getElevatorById(int elevatorId) {
        for (Elevator elevator : elevators) {
            if (elevator.getId() == elevatorId)
                return elevator;
        }
        return null; // Return null if no matching elevator is found
    }

    public List<Elevator> getElevators() {
        return elevators;
    }

    public void externalRequest(int elevatorId, int floorNumber, Direction direction) {
        System.out.println("External request: Floor " + floorNumber + ", Direction " + direction);
        // Find the elevator by its ID
        Elevator selectedElevator = getElevatorById(elevatorId);
        if (selectedElevator != null) {
            // Add the request to the selected elevator
            selectedElevator.addRequest(new ElevatorRequest(elevatorId, floorNumber, false, direction));
            System.out.println("Assigned elevator " + selectedElevator.getId() + " to floor " + floorNumber);
        } else {
            System.out.println("No elevator available for floor " + floorNumber);
        }
    }

    public void internalRequest(int elevatorId, int floorNumber) {
        // Find the elevator by its ID
        Elevator elevator = getElevatorById(elevatorId);
        System.out.println("Internal request: Elevator " + elevator.getId() + " to floor " + floorNumber);
        // Determine the direction of the request
        Direction direction = floorNumber > elevator.getCurrentFloor()
                ? Direction.UP
                : Direction.DOWN;
        // Add the request to the elevator
        elevator.addRequest(new ElevatorRequest(elevatorId, floorNumber, true, direction));
    }

    // Perform a simulation step by moving all elevators
    public void step() {
        // Iterate through all elevators
        for (Elevator elevator : elevators) {
            // Only process elevators with pending requests
            if (!elevator.getRequests().isEmpty()) {
                // Use the scheduling strategy to find the next stop
                int nextStop = schedulingStrategy.getNextStop(elevator);

                // Move the elevator to the next stop if needed
                if (elevator.getCurrentFloor() != nextStop)
                    elevator.moveToNextStop(nextStop);
            }
        }
    }

}