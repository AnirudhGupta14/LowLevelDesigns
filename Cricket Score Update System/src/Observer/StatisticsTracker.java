package Observer;

import Constants.BallType;
import Entities.Ball;
import Entities.Over;
import Entities.ScoreCard;

import java.util.List;

/**
 * Observer 3 — Statistics tracker.
 * Computes match statistics (over summaries, economy tracking) after each ball.
 * Uses readLock for concurrent-safe access to completed overs.
 */
public class StatisticsTracker implements ScoreObserver {

    @Override
    public void onBallProcessed(Ball ball, ScoreCard scoreCard) {
        // Only print over stats when an over just completed
        if (ball.isLegalDelivery()) {
            int ballsDelivered = scoreCard.getLegalBallsDelivered();
            if (ballsDelivered > 0 && ballsDelivered % 6 == 0) {
                printOverSummary(scoreCard);
            }
        }
    }

    private void printOverSummary(ScoreCard scoreCard) {
        // Acquire readLock to read multiple fields atomically-ish
        scoreCard.getLock().readLock().lock();
        try {
            List<Over> overs = scoreCard.getOvers();
            if (overs.size() < 2)
                return; // need at least 1 completed over

            // Last completed over = second-to-last entry (last is the new empty over)
            Over completedOver = overs.get(overs.size() - 2);

            System.out.printf("[STATS] ━━━ End of Over %d ━━━  %d runs, %d wickets  │  " +
                    "Score: %s  │  CRR: %.2f%n",
                    completedOver.getOverNumber(),
                    completedOver.getRunsInOver(),
                    completedOver.getWicketsInOver(),
                    scoreCard.getScoreSummary(),
                    scoreCard.getCurrentRunRate());
        } finally {
            scoreCard.getLock().readLock().unlock();
        }
    }
}
