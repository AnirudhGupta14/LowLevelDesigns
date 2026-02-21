package ElevatorObserver;

import Constants.Direction;
import Services.Elevator;

public interface ElevatorObserver {
    void onElevatorDirectionChange(Elevator elevator, Direction direction);
    void onElevatorFloorChange(Elevator elevator, int floor);

}