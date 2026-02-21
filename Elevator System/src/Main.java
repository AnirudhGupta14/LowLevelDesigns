import Constants.Direction;
import ElevatorObserver.ElevatorDisplay;
import SchedulingStrategy.ScanSchedulingStrategy;
import Services.Elevator;
import Services.ElevatorController;

public class Main {

    public static void main(String[] args) {

        ElevatorController controller = new ElevatorController(10);

        ElevatorDisplay display = new ElevatorDisplay();
        for (Elevator elevator : controller.getElevators()) {
            elevator.addObserver(display); // Add the display as an observer for all elevators
        }

        controller.setSchedulingStrategy(new ScanSchedulingStrategy());

        // External Request
        controller.externalRequest(2, 5, Direction.UP);
        controller.externalRequest(4, 2, Direction.UP);
        controller.externalRequest(7, 9, Direction.DOWN);

        // Internal Request
        controller.internalRequest(4, 6);
        controller.internalRequest(5, 7);
        controller.internalRequest(1, 10);
        controller.internalRequest(9, 1);

        controller.step();

    }
}