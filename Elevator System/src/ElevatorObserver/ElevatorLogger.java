package ElevatorObserver;

import Services.Elevator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Concrete observer that logs all elevator events with timestamps.
 * Useful for auditing and maintenance diagnostics.
 */
public class ElevatorLogger implements ElevatorObserver {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String now() {
        return LocalDateTime.now().format(FORMATTER);
    }

    @Override
    public void onFloorArrival(Elevator elevator, int floor) {
        System.out.printf("[LOG %s] Elevator-%d ► Floor %d arrived%n",
                now(), elevator.getId(), floor);
    }

    @Override
    public void onDoorOpen(Elevator elevator, int floor) {
        System.out.printf("[LOG %s] Elevator-%d ► Door OPEN  @ Floor %d%n",
                now(), elevator.getId(), floor);
    }

    @Override
    public void onDoorClose(Elevator elevator, int floor) {
        System.out.printf("[LOG %s] Elevator-%d ► Door CLOSE @ Floor %d%n",
                now(), elevator.getId(), floor);
    }

    @Override
    public void onStateChange(Elevator elevator) {
        System.out.printf("[LOG %s] Elevator-%d ► State = %s @ Floor %d%n",
                now(), elevator.getId(), elevator.getState(), elevator.getCurrentFloor());
    }
}
