package Services;

import Entities.ElevatorRequest;
import ElevatorObserver.ElevatorObserver;
import SchedulingStrategy.SchedulingStrategy;
import SchedulingStrategy.ScanSchedulingStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ElevatorController — Singleton orchestrator.
 *
 * Producer-Consumer Design:
 * ┌────────────────────────────────────────────────────────────────┐
 * │ PRODUCERS CONSUMER (dispatcher thread) │
 * │ ───────────────────── ──────────────────────────── │
 * │ handleExternalRequest() ──► LinkedBlockingQueue<Request> │
 * │ handleInternalRequest() ──► │ take() blocks here │
 * │ ▼ │
 * │ SchedulingStrategy.select() │
 * │ │ │
 * │ Elevator.addDestination() ──► 🔔│
 * │ (wakes the elevator's own thread) │
 * └────────────────────────────────────────────────────────────────┘
 *
 * Each Elevator runs its own worker thread (see Elevator.java).
 * The dispatcher thread (here) is the sole consumer of the request queue.
 */
public class ElevatorController {

    private static ElevatorController instance;

    private final List<Elevator> elevators;
    private SchedulingStrategy strategy;

    // ✅ The shared request queue — producers put(), dispatcher take()s
    private final LinkedBlockingQueue<ElevatorRequest> requestQueue;

    private Thread dispatcherThread;
    private volatile boolean running = false;

    private ElevatorController() {
        this.elevators = new ArrayList<>();
        this.strategy = new ScanSchedulingStrategy();
        this.requestQueue = new LinkedBlockingQueue<>();
    }

    public static synchronized ElevatorController getInstance() {
        if (instance == null) {
            instance = new ElevatorController();
        }
        return instance;
    }

    // ─────────────────── Configuration ───────────────────

    public void setStrategy(SchedulingStrategy strategy) {
        this.strategy = strategy;
    }

    public void addElevator(Elevator elevator) {
        elevators.add(elevator);
    }

    public void addGlobalObserver(ElevatorObserver observer) {
        for (Elevator e : elevators)
            e.addObserver(observer);
    }

    public List<Elevator> getElevators() {
        return Collections.unmodifiableList(elevators);
    }

    // ─────────────────── Producers ───────────────────

    /**
     * Producer: enqueue an external hall-call request.
     * Returns immediately — the dispatcher thread handles it asynchronously.
     */
    public void handleExternalRequest(ElevatorRequest request) {
        System.out.printf("%n[CONTROLLER] ← External request queued : %s%n", request);
        requestQueue.offer(request);
    }

    /**
     * Producer: enqueue an internal cabin-button request.
     */
    public void handleInternalRequest(int elevatorId, int destinationFloor) {
        elevators.stream()
                .filter(e -> e.getId() == elevatorId)
                .findFirst()
                .ifPresent(e -> {
                    System.out.printf("[CONTROLLER] ← Internal request queued : Elevator-%d → Floor %d%n",
                            elevatorId, destinationFloor);
                    // Internal requests bypass the queue — destination already known
                    e.addDestination(destinationFloor);
                });
    }

    // ─────────────────── Lifecycle ───────────────────

    /**
     * Start the dispatcher thread AND all elevator worker threads.
     * Call this once after all elevators have been added.
     */
    public void start() {
        running = true;

        // Start each elevator's own worker thread
        for (Elevator elevator : elevators) {
            elevator.start();
        }

        // Start the single dispatcher (consumer) thread
        dispatcherThread = new Thread(this::dispatchLoop, "Dispatcher-Thread");
        dispatcherThread.setDaemon(true);
        dispatcherThread.start();

        System.out.println("\n═══════════════════════════════════════════════");
        System.out.println("  Elevator System Started  (Producer-Consumer)");
        System.out.println("═══════════════════════════════════════════════\n");
    }

    /**
     * Gracefully shut down the dispatcher and all elevator threads.
     */
    public void shutdown() {
        running = false;
        dispatcherThread.interrupt(); // unblock take()
        for (Elevator e : elevators)
            e.stop();
        System.out.println("\n[CONTROLLER] System shut down.");
    }

    // ─────────────────── Consumer (Dispatcher Loop) ───────────────────

    /**
     * The dispatcher thread runs this loop.
     * Blocks on queue.take() — wakes only when a request arrives (true
     * Producer-Consumer).
     */
    private void dispatchLoop() {
        while (running) {
            try {
                // 🔒 Blocks here when queue is empty — no busy-waiting
                ElevatorRequest request = requestQueue.take();

                System.out.printf("[DISPATCHER] Dequeued %s  (queue remaining: %d)%n",
                        request, requestQueue.size());

                Elevator chosen = strategy.selectElevator(elevators, request);
                if (chosen == null) {
                    System.out.println("[DISPATCHER] ❌ No available elevator — re-queuing request.");
                    requestQueue.offer(request); // re-enqueue and retry
                    Thread.sleep(500);
                    continue;
                }

                System.out.printf("[DISPATCHER] → Assigned Elevator-%d to handle %s%n",
                        chosen.getId(), request);

                // Add source floor as pickup destination; elevator thread wakes up
                chosen.addDestination(request.getSourceFloor());

                // If destination is also known (internal-style request), add it too
                if (request.getDestinationFloor() != -1) {
                    chosen.addDestination(request.getDestinationFloor());
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // ─────────────────── Status ───────────────────

    public void printStatus() {
        System.out.println("\n[STATUS] Request queue size: " + requestQueue.size());
        for (Elevator e : elevators) {
            System.out.println("  " + e);
        }
        System.out.println();
    }
}
