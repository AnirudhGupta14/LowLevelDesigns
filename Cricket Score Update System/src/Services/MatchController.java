package Services;

import Consumer.ScoreProcessor;
import Entities.Match;
import Observer.ScoreObserver;
import Producer.ScorePublisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import Entities.Ball;

/**
 * MatchController — Singleton orchestrator.
 *
 * Owns the BlockingQueue and wires Producer + Consumer together.
 * Manages observer registration and innings transitions.
 *
 * Thread diagram:
 * Main Thread
 * │
 * ├── publisherThread → ScorePublisher.run() (PRODUCER)
 * │ puts Ball events into LinkedBlockingQueue
 * │
 * └── processorThread → ScoreProcessor.run() (CONSUMER)
 * takes Ball events, writes ScoreCard, notifies observers
 */
public class MatchController {

    private static MatchController instance;

    // Bounded queue of capacity 50 — back-pressure if consumer falls behind
    private final LinkedBlockingQueue<Ball> ballQueue;
    private final List<ScoreObserver> observers;

    private Match activeMatch;
    private Thread publisherThread;
    private Thread processorThread;
    private ScorePublisher publisher;
    private ScoreProcessor processor;

    private MatchController() {
        this.ballQueue = new LinkedBlockingQueue<>(50);
        this.observers = new ArrayList<>();
    }

    public static synchronized MatchController getInstance() {
        if (instance == null) {
            instance = new MatchController();
        }
        return instance;
    }

    // ─────────────────── Observer Management ───────────────────

    public void addObserver(ScoreObserver observer) {
        observers.add(observer);
    }

    public List<ScoreObserver> getObservers() {
        return Collections.unmodifiableList(observers);
    }

    // ─────────────────── Match Lifecycle ───────────────────

    public void startMatch(Match match) {
        this.activeMatch = match;
        match.startFirstInnings();
        startInnings("Batsman-1", "Bowler-1");
    }

    /**
     * Start the producer and consumer threads for a new innings.
     */
    private void startInnings(String batsmanId, String bowlerId) {
        // Create producer and consumer
        publisher = new ScorePublisher(ballQueue, batsmanId, bowlerId,
                activeMatch.getTotalOvers());
        processor = new ScoreProcessor(ballQueue, activeMatch, observers);

        // Consumer starts first — ready to process as soon as producer publishes
        processorThread = new Thread(processor, "Score-Processor");
        publisherThread = new Thread(publisher, "Score-Publisher");

        processorThread.start();
        publisherThread.start();
    }

    /**
     * Block until the current innings finishes (publisher + processor done).
     */
    public void awaitInningsCompletion() {
        try {
            publisherThread.join();
            processorThread.join();
            System.out.println("[CONTROLLER] Innings complete.\n");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Transition to second innings with new batting/bowling sides.
     */
    public void startSecondInnings(String batsmanId, String bowlerId) {
        activeMatch.startSecondInnings();
        // Clear any remaining stale events from the queue between innings
        ballQueue.clear();
        startInnings(batsmanId, bowlerId);
    }

    public void completeMatch() {
        activeMatch.completeMatch();
    }

    public Match getActiveMatch() {
        return activeMatch;
    }
}
