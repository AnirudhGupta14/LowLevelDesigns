package ElevatorObserver;

import Services.Elevator;
import Constants.ElevatorState;

/**
 * Concrete observer that triggers a maintenance alert when an elevator
 * enters MAINTENANCE state.
 */
public class MaintenanceAlert implements ElevatorObserver {

    @Override
    public void onFloorArrival(Elevator elevator, int floor) {
        /* no-op */ }

    @Override
    public void onDoorOpen(Elevator elevator, int floor) {
        /* no-op */ }

    @Override
    public void onDoorClose(Elevator elevator, int floor) {
        /* no-op */ }

    @Override
    public void onStateChange(Elevator elevator) {
        if (elevator.getState() == ElevatorState.MAINTENANCE) {
            System.out.printf("[ALERT] ⚠️  Elevator-%d is now in MAINTENANCE mode at Floor %d. " +
                    "Please dispatch a technician!%n",
                    elevator.getId(), elevator.getCurrentFloor());
        }
    }
}
