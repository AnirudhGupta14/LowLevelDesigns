package Observer;

import Constants.BallType;
import Entities.Ball;
import Entities.ScoreCard;

/**
 * Observer 1 — Live scoreboard display.
 * Acquires a readLock to safely read the ScoreCard while multiple
 * other observers may also be reading concurrently.
 */
public class ScoreBoardDisplay implements ScoreObserver {

    @Override
    public void onBallProcessed(Ball ball, ScoreCard scoreCard) {
        // Acquire readLock — safe to read concurrently with other observers
        scoreCard.getLock().readLock().lock();
        try {
            System.out.printf("[SCOREBOARD] %-40s  | CRR: %.2f%n",
                    scoreCard.getScoreSummary(),
                    scoreCard.getCurrentRunRate());
        } finally {
            scoreCard.getLock().readLock().unlock();
        }
    }
}
