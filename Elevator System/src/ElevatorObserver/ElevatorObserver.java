package ElevatorObserver;

import Services.Elevator;

/**
 * Observer interface for elevator events.
 * Implement this to react to floor arrivals, door events, and state changes.
 */
public interface ElevatorObserver {
    void onFloorArrival(Elevator elevator, int floor);

    void onDoorOpen(Elevator elevator, int floor);

    void onDoorClose(Elevator elevator, int floor);

    void onStateChange(Elevator elevator);
}
