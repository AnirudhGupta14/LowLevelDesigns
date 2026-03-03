package Entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents one over in an innings.
 * An over consists of 6 legal deliveries.
 */
public class Over {
    private final int overNumber;
    private final List<Ball> balls;
    private int runsInOver;
    private int wicketsInOver;

    public Over(int overNumber) {
        this.overNumber = overNumber;
        this.balls = new ArrayList<>();
    }

    public synchronized void addBall(Ball ball) {
        balls.add(ball);
        runsInOver += ball.getRunsScored();
        if (ball.getType() == Constants.BallType.WICKET)
            wicketsInOver++;
    }

    /** Number of legal deliveries bowled in this over */
    public synchronized int legalBallsCount() {
        return (int) balls.stream().filter(Ball::isLegalDelivery).count();
    }

    public synchronized boolean isComplete() {
        return legalBallsCount() >= 6;
    }

    public int getOverNumber() {
        return overNumber;
    }

    public synchronized int getRunsInOver() {
        return runsInOver;
    }

    public synchronized int getWicketsInOver() {
        return wicketsInOver;
    }

    public synchronized List<Ball> getBalls() {
        return Collections.unmodifiableList(balls);
    }

    @Override
    public String toString() {
        return String.format("Over-%d  [%d runs, %d wickets, %d legal balls]",
                overNumber, runsInOver, wicketsInOver, legalBallsCount());
    }
}
