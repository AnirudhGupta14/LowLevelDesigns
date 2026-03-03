package Observer;

import Entities.Ball;
import Entities.ScoreCard;

/**
 * Observer interface for score update events.
 * Implementations are notified by ScoreProcessor after each delivery is
 * processed.
 */
public interface ScoreObserver {
    /**
     * Called after the ScoreCard has been updated with the latest ball.
     * Observers hold a readLock to safely access the ScoreCard concurrently.
     *
     * @param ball      the delivery just processed
     * @param scoreCard the updated scorecard (use readLock before reading)
     */
    void onBallProcessed(Ball ball, ScoreCard scoreCard);
}
