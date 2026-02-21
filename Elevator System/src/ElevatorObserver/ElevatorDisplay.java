package ElevatorObserver;

import Constants.Direction;
import Services.Elevator;

public class ElevatorDisplay implements ElevatorObserver {
    @Override
    public void onElevatorDirectionChange(Elevator elevator, Direction direction) {
        System.out.println("Elevator " + elevator.getId() + " direction changed to " + direction);
    }

    @Override
    public void onElevatorFloorChange(Elevator elevator, int floor) {
        System.out.println("Elevator " + elevator.getId() + " moved to floor " + floor);
    }
}