package ElevatorObserver;

import Services.Elevator;

/**
 * Concrete observer that acts as the in-building display panel.
 * Logs all elevator events to the console (simulates a real display board).
 */
public class ElevatorDisplay implements ElevatorObserver {

    @Override
    public void onFloorArrival(Elevator elevator, int floor) {
        System.out.printf("[DISPLAY] Elevator-%d arrived at Floor %d  (Direction: %s)%n",
                elevator.getId(), floor, elevator.getDirection());
    }

    @Override
    public void onDoorOpen(Elevator elevator, int floor) {
        System.out.printf("[DISPLAY] Elevator-%d doors OPENING at Floor %d%n",
                elevator.getId(), floor);
    }

    @Override
    public void onDoorClose(Elevator elevator, int floor) {
        System.out.printf("[DISPLAY] Elevator-%d doors CLOSING at Floor %d%n",
                elevator.getId(), floor);
    }

    @Override
    public void onStateChange(Elevator elevator) {
        System.out.printf("[DISPLAY] Elevator-%d state changed → %s  (Floor: %d)%n",
                elevator.getId(), elevator.getState(), elevator.getCurrentFloor());
    }
}
