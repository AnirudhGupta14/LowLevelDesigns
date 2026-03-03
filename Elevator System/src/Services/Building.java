package Services;

import Entities.ElevatorRequest;
import Entities.Floor;
import ElevatorObserver.ElevatorObserver;
import SchedulingStrategy.SchedulingStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the physical building that owns both floors and elevator cars.
 *
 * Lifecycle:
 * - Call start() to launch the dispatcher thread + all elevator worker threads.
 * - Call shutdown() to gracefully stop everything.
 *
 * Design decision:
 * - Building is the single entry-point for configuration: you add elevators
 * here and attach observers here.
 * - Internally it delegates to ElevatorController (Singleton) for dispatch
 * and simulation so the controller stays as the scheduling brain.
 */
public class Building {

    private final String name;
    private final int totalFloors;
    private final List<Floor> floors;
    private final List<Elevator> elevators;

    public Building(String name, int totalFloors) {
        this.name = name;
        this.totalFloors = totalFloors;
        this.floors = new ArrayList<>();
        this.elevators = new ArrayList<>();
        for (int i = 0; i < totalFloors; i++) {
            floors.add(new Floor(i));
        }
    }

    // ──────────────────── Elevator Management ────────────────────

    /**
     * Add an elevator to this building AND register it with the ElevatorController.
     */
    public void addElevator(Elevator elevator) {
        elevators.add(elevator);
        ElevatorController.getInstance().addElevator(elevator);
    }

    /**
     * Attach an observer to every elevator in the building at once.
     */
    public void addGlobalObserver(ElevatorObserver observer) {
        for (Elevator e : elevators) {
            e.addObserver(observer);
        }
    }

    // ──────────────────── Convenience Dispatch ────────────────────

    /**
     * Convenience: set a custom scheduling strategy on the controller.
     */
    public void setSchedulingStrategy(SchedulingStrategy strategy) {
        ElevatorController.getInstance().setStrategy(strategy);
    }

    /**
     * Convenience: submit an external (hall-call) request via the controller.
     */
    public void requestElevator(ElevatorRequest request) {
        ElevatorController.getInstance().handleExternalRequest(request);
    }

    /**
     * Convenience: submit an internal (cabin-button) request via the controller.
     */
    public void requestDestination(int elevatorId, int destinationFloor) {
        ElevatorController.getInstance().handleInternalRequest(elevatorId, destinationFloor);
    }

    /**
     * Start the dispatcher thread and all elevator worker threads.
     * Returns immediately — the system runs asynchronously.
     */
    public void start() {
        ElevatorController.getInstance().start();
    }

    /**
     * Gracefully shut down all threads.
     */
    public void shutdown() {
        ElevatorController.getInstance().shutdown();
    }

    // ──────────────────── Getters ────────────────────

    public Floor getFloor(int floorNumber) {
        if (floorNumber < 0 || floorNumber >= totalFloors)
            throw new IllegalArgumentException("Floor " + floorNumber + " does not exist.");
        return floors.get(floorNumber);
    }

    public List<Floor> getFloors() {
        return Collections.unmodifiableList(floors);
    }

    public List<Elevator> getElevators() {
        return Collections.unmodifiableList(elevators);
    }

    public int getTotalFloors() {
        return totalFloors;
    }

    public String getName() {
        return name;
    }

    public void printStatus() {
        ElevatorController.getInstance().printStatus();
    }
}
