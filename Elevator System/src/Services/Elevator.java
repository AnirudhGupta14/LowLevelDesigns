package Services;

import Constants.Direction;
import Constants.ElevatorState;
import Entities.InternalButton;
import ElevatorObserver.ElevatorObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Represents a single elevator car.
 *
 * Design Patterns:
 * - Observer Subject : notifies ElevatorObserver on floor arrival, door, state.
 * - State : ElevatorState (IDLE, MOVING, DOOR_OPEN, MAINTENANCE).
 *
 * Threading (Producer-Consumer):
 * - Runs its own worker thread via start().
 * - Waits (Object.wait) when queues are empty; woken by addDestination().
 * - The dispatcher thread (in ElevatorController) acts as the producer that
 * calls addDestination() after picking this elevator for a request.
 */
public class Elevator implements Runnable {

    private final int id;
    private int currentFloor;
    private Direction direction;
    private ElevatorState state;

    private final int totalFloors;
    private final InternalButton[] buttons;

    // SCAN queues — guarded by `this`
    private final TreeSet<Integer> upQueue;
    private final TreeSet<Integer> downQueue;

    // Observer list
    private final List<ElevatorObserver> observers;

    // Worker thread lifecycle
    private volatile boolean running = false;
    private Thread workerThread;

    public Elevator(int id, int totalFloors) {
        this.id = id;
        this.totalFloors = totalFloors;
        this.currentFloor = 0;
        this.direction = Direction.IDLE;
        this.state = ElevatorState.IDLE;

        this.buttons = new InternalButton[totalFloors];
        for (int i = 0; i < totalFloors; i++) {
            buttons[i] = new InternalButton(i);
        }

        this.upQueue = new TreeSet<>();
        this.downQueue = new TreeSet<>((a, b) -> b - a); // descending
        this.observers = new ArrayList<>();
    }

    // ─────────────────── Thread Lifecycle ───────────────────

    /** Start the elevator's worker thread. */
    public void start() {
        running = true;
        workerThread = new Thread(this, "Elevator-" + id + "-Worker");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    /** Gracefully stop the worker thread. */
    public void stop() {
        running = false;
        synchronized (this) {
            notifyAll(); // unblock any waiting thread
        }
    }

    /**
     * Worker thread body — Producer-Consumer consumer side.
     * Blocks (waits) when both queues are empty; wakes when addDestination()
     * adds work.
     */
    @Override
    public void run() {
        while (running) {
            synchronized (this) {
                while (running && upQueue.isEmpty() && downQueue.isEmpty()
                        && state != ElevatorState.MAINTENANCE) {
                    setStateInternal(ElevatorState.IDLE);
                    direction = Direction.IDLE;
                    try {
                        wait(); // 🔒 release lock, sleep until notified
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            if (!running)
                break;
            step(); // process one floor movement
            sleep(400); // simulate travel time between floors
        }
    }

    // ─────────────────── Observer Registration ───────────────────

    public void addObserver(ElevatorObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ElevatorObserver observer) {
        observers.remove(observer);
    }

    // ─────────────────── Request Handling ───────────────────

    /**
     * Add a destination floor to the correct SCAN queue.
     * Wakes the worker thread if it was idle (Producer notifies Consumer).
     */
    public synchronized void addDestination(int floor) {
        if (floor < 0 || floor >= totalFloors)
            return;

        if (floor > currentFloor) {
            upQueue.add(floor);
        } else if (floor < currentFloor) {
            downQueue.add(floor);
        } else {
            // Already here — open/close immediately on the worker thread
            upQueue.add(floor); // dummy add so worker wakes and processes it
        }
        notifyAll(); // 🔔 wake the waiting worker thread
    }

    // ─────────────────── One-Step Movement ───────────────────

    private synchronized void step() {
        if (state == ElevatorState.MAINTENANCE)
            return;
        if (state == ElevatorState.DOOR_OPEN)
            return;
        if (upQueue.isEmpty() && downQueue.isEmpty())
            return;

        if (direction == Direction.UP
                || (direction == Direction.IDLE && !upQueue.isEmpty())) {

            direction = Direction.UP;
            setStateInternal(ElevatorState.MOVING);
            currentFloor++;
            notifyFloorArrival();

            if (upQueue.contains(currentFloor)) {
                upQueue.remove(currentFloor);
                buttons[currentFloor].reset();
                openDoors();
                closeDoors();
            }
            if (upQueue.isEmpty()) {
                direction = downQueue.isEmpty() ? Direction.IDLE : Direction.DOWN;
            }

        } else if (direction == Direction.DOWN || !downQueue.isEmpty()) {

            direction = Direction.DOWN;
            setStateInternal(ElevatorState.MOVING);
            currentFloor--;
            notifyFloorArrival();

            if (downQueue.contains(currentFloor)) {
                downQueue.remove(currentFloor);
                buttons[currentFloor].reset();
                openDoors();
                closeDoors();
            }
            if (downQueue.isEmpty()) {
                direction = upQueue.isEmpty() ? Direction.IDLE : Direction.UP;
            }
        }
    }

    // ─────────────────── Door Operations ───────────────────

    private void openDoors() {
        setStateInternal(ElevatorState.DOOR_OPEN);
        notifyDoorOpen();
        sleep(500); // door open duration
    }

    private void closeDoors() {
        notifyDoorClose();
        // After closing: stay IDLE until next step decides otherwise
        if (state == ElevatorState.DOOR_OPEN) {
            setStateInternal(ElevatorState.IDLE);
        }
    }

    // ─────────────────── Maintenance ───────────────────

    public synchronized void setMaintenance(boolean maintenance) {
        if (maintenance) {
            setStateInternal(ElevatorState.MAINTENANCE);
        } else {
            direction = Direction.IDLE;
            setStateInternal(ElevatorState.IDLE);
            notifyAll(); // wake worker so it re-checks queues
        }
    }

    // ─────────────────── Notify Helpers ───────────────────

    /**
     * Internal setState — does NOT require external synchronisation (caller holds
     * lock).
     */
    private void setStateInternal(ElevatorState newState) {
        this.state = newState;
        for (ElevatorObserver obs : observers)
            obs.onStateChange(this);
    }

    private void notifyFloorArrival() {
        for (ElevatorObserver obs : observers)
            obs.onFloorArrival(this, currentFloor);
    }

    private void notifyDoorOpen() {
        for (ElevatorObserver obs : observers)
            obs.onDoorOpen(this, currentFloor);
    }

    private void notifyDoorClose() {
        for (ElevatorObserver obs : observers)
            obs.onDoorClose(this, currentFloor);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ─────────────────── Getters ───────────────────

    public int getId() {
        return id;
    }

    public synchronized int getCurrentFloor() {
        return currentFloor;
    }

    public synchronized Direction getDirection() {
        return direction;
    }

    public synchronized ElevatorState getState() {
        return state;
    }

    public int getTotalFloors() {
        return totalFloors;
    }

    public synchronized boolean hasWork() {
        return !upQueue.isEmpty() || !downQueue.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("Elevator-%d [Floor=%d, Dir=%s, State=%s, UpQ=%s, DownQ=%s]",
                id, currentFloor, direction, state, upQueue, downQueue);
    }
}
