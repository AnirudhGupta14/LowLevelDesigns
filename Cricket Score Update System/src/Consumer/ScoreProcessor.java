package Consumer;

import Entities.Ball;
import Entities.Match;
import Entities.ScoreCard;
import Observer.ScoreObserver;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * CONSUMER — ScoreProcessor.
 *
 * Runs on its own thread. Continuously takes Ball events from the
 * BlockingQueue,
 * acquires an exclusive WRITE LOCK on the ScoreCard, updates it, releases the
 * lock,
 * then notifies all registered ScoreObservers (who use READ locks to display
 * data).
 *
 * Producer-Consumer role:
 * ScorePublisher → put(Ball) → LinkedBlockingQueue → [take()] → ScoreProcessor
 *
 * Lock flow per delivery:
 * 1. ballQueue.take() — blocks until ball available
 * 2. scoreCard.update(ball) — acquires writeLock internally, updates, releases
 * 3. for each observer → onBallProcessed() — observers acquire readLock to
 * display
 */
public class ScoreProcessor implements Runnable {

    private final BlockingQueue<Ball> ballQueue;
    private final Match match;
    private final List<ScoreObserver> observers;

    private volatile boolean running = true;

    public ScoreProcessor(BlockingQueue<Ball> ballQueue,
            Match match,
            List<ScoreObserver> observers) {
        this.ballQueue = ballQueue;
        this.match = match;
        this.observers = observers;
    }

    @Override
    public void run() {
        System.out.println("[PROCESSOR]  Consumer thread started. Waiting for deliveries...\n");

        while (running) {
            try {
                // 🔒 Blocks here when queue is empty — no busy-waiting
                Ball ball = ballQueue.take();

                // Sentinel from publisher signals shutdown
                if (isSentinel(ball)) {
                    System.out.println("[PROCESSOR]  Sentinel received — innings complete.");
                    break;
                }

                ScoreCard scoreCard = match.getCurrentCard();

                // ─── WRITE: exclusive update ──────────────────────────────
                // writeLock acquired and released inside ScoreCard.update()
                scoreCard.update(ball);

                // ─── NOTIFY: observers read under readLock ─────────────────
                // Multiple observers can run concurrently if needed (here sequential
                // for simplicity; in production use ExecutorService for parallel observers)
                for (ScoreObserver observer : observers) {
                    observer.onBallProcessed(ball, scoreCard);
                }

                System.out.println(); // blank line for readability

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("[PROCESSOR]  Consumer thread finished.");
    }

    public void stop() {
        running = false;
    }

    private boolean isSentinel(Ball ball) {
        return ball.getOverNumber() == -1;
    }
}
