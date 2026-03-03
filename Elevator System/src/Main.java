import Constants.Direction;
import Entities.ElevatorRequest;
import ElevatorObserver.ElevatorDisplay;
import ElevatorObserver.ElevatorLogger;
import ElevatorObserver.MaintenanceAlert;
import Services.Building;
import Services.Elevator;

/**
 * ┌───────────────────────────────────────────────────────────────────┐
 * │ Elevator System — Entry Point │
 * │ │
 * │ Producer-Consumer Architecture: │
 * │ • building.requestElevator() → puts request into BlockingQueue │
 * │ • Dispatcher thread → take() → assigns elevator │
 * │ • Each Elevator thread → wait() until work arrives │
 * └───────────────────────────────────────────────────────────────────┘
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        // ─── Build environment ────────────────────────────────────────────
        final int TOTAL_FLOORS = 10;

        Building building = new Building("TechTower", TOTAL_FLOORS);
        System.out.println("🏢 " + building.getName() + " — " + TOTAL_FLOORS + " floors\n");

        // ─── Add elevators to the building ────────────────────────────────
        Elevator e1 = new Elevator(1, TOTAL_FLOORS);
        Elevator e2 = new Elevator(2, TOTAL_FLOORS);
        Elevator e3 = new Elevator(3, TOTAL_FLOORS);

        building.addElevator(e1);
        building.addElevator(e2);
        building.addElevator(e3);

        // ─── Attach observers globally via building ───────────────────────
        building.addGlobalObserver(new ElevatorDisplay());
        building.addGlobalObserver(new ElevatorLogger());
        building.addGlobalObserver(new MaintenanceAlert());

        // ─── Start dispatcher + all elevator worker threads ───────────────
        building.start();

        // ─── Scenario 1: External hall-call requests (Producers) ──────────
        System.out.println("━━━ Scenario 1: Hall calls ━━━");
        building.requestElevator(new ElevatorRequest(3, Direction.UP));
        building.requestElevator(new ElevatorRequest(7, Direction.DOWN));
        building.requestElevator(new ElevatorRequest(5, Direction.UP));

        // Let elevators process the hall calls
        Thread.sleep(5000);

        // ─── Scenario 2: Internal cabin button presses ────────────────────
        System.out.println("\n━━━ Scenario 2: In-cabin destination buttons ━━━");
        building.requestDestination(1, 9);
        building.requestDestination(2, 2);
        building.requestDestination(3, 6);

        Thread.sleep(6000);

        // ─── Scenario 3: Maintenance mode ─────────────────────────────────
        System.out.println("\n━━━ Scenario 3: Maintenance Mode ━━━");
        e2.setMaintenance(true);
        building.printStatus();

        // New request while e2 is in maintenance — rerouted to e1 or e3
        building.requestElevator(new ElevatorRequest(4, Direction.UP));
        Thread.sleep(4000);

        // Restore elevator-2
        System.out.println("\n━━━ Elevator-2 restored ━━━");
        e2.setMaintenance(false);
        building.printStatus();

        // Wait for all remaining work to drain
        Thread.sleep(3000);

        building.shutdown();
    }
}