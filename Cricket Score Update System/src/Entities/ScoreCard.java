package Entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Represents the live scoreboard for one innings.
 *
 * Concurrency Design — ReentrantReadWriteLock:
 * ┌──────────────────────────────────────────────────────────────────┐
 * │ WRITERS (exclusive) READERS (concurrent) │
 * │ ───────────────────── ──────────────────────────────── │
 * │ ScoreProcessor.update() → ScoreBoardDisplay.display() │
 * │ (holds writeLock) CommentaryGenerator.comment() │
 * │ StatisticsTracker.compute() │
 * │ Multiple observers can read simultaneously while no write │
 * │ is happening. A write blocks all readers until it completes. │
 * └──────────────────────────────────────────────────────────────────┘
 */
public class ScoreCard {

    private final String battingTeamName;

    // ✅ ReentrantReadWriteLock: many readers OR one writer at a time
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    // Guarded mutable state
    private int totalRuns;
    private int wickets;
    private int legalBallsDelivered; // total legal balls in innings
    private final List<Ball> ballHistory;
    private final List<Over> overs;
    private Over currentOver;

    public ScoreCard(String battingTeamName) {
        this.battingTeamName = battingTeamName;
        this.ballHistory = new ArrayList<>();
        this.overs = new ArrayList<>();
        this.currentOver = new Over(0);
        overs.add(currentOver);
    }

    // ─────────────────── Write (exclusive lock) ───────────────────

    /**
     * Update scoreboard with a new delivery.
     * Called exclusively by the ScoreProcessor (consumer thread).
     */
    public void update(Ball ball) {
        lock.writeLock().lock();
        try {
            ballHistory.add(ball);
            currentOver.addBall(ball);

            totalRuns += ball.getRunsScored();

            if (ball.getType() == Constants.BallType.WICKET) {
                wickets++;
            }

            if (ball.isLegalDelivery()) {
                legalBallsDelivered++;
            }

            // Rotate to a new over when 6 legal balls bowled
            if (currentOver.isComplete()) {
                int nextOverNum = currentOver.getOverNumber() + 1;
                currentOver = new Over(nextOverNum);
                overs.add(currentOver);
            }
        } finally {
            lock.writeLock().unlock(); // always release!
        }
    }

    // ─────────────────── Read (shared lock) ───────────────────

    /** Safe concurrent read of current score */
    public String getScoreSummary() {
        lock.readLock().lock();
        try {
            int completedOvers = legalBallsDelivered / 6;
            int extraBalls = legalBallsDelivered % 6;
            return String.format("%s  %d/%d  (%.1f overs)",
                    battingTeamName, totalRuns, wickets,
                    completedOvers + extraBalls / 10.0);
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getTotalRuns() {
        lock.readLock().lock();
        try {
            return totalRuns;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getWickets() {
        lock.readLock().lock();
        try {
            return wickets;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getLegalBallsDelivered() {
        lock.readLock().lock();
        try {
            return legalBallsDelivered;
        } finally {
            lock.readLock().unlock();
        }
    }

    public double getCurrentRunRate() {
        lock.readLock().lock();
        try {
            double oversPlayed = legalBallsDelivered / 6.0;
            return oversPlayed == 0 ? 0 : totalRuns / oversPlayed;
        } finally {
            lock.readLock().unlock();
        }
    }

    /** Get snapshot of all overs — safe for concurrent read */
    public List<Over> getOvers() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(new ArrayList<>(overs));
        } finally {
            lock.readLock().unlock();
        }
    }

    /** Get last N balls for commentary */
    public List<Ball> getLastBalls(int n) {
        lock.readLock().lock();
        try {
            int size = ballHistory.size();
            int from = Math.max(0, size - n);
            return Collections.unmodifiableList(new ArrayList<>(ballHistory.subList(from, size)));
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getBattingTeamName() {
        return battingTeamName;
    }

    // Expose the lock so StatisticsTracker can hold readLock for complex reads
    public ReadWriteLock getLock() {
        return lock;
    }
}
