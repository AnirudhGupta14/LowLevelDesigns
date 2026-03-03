package Entities;

import Constants.BallType;
import Constants.WicketType;

/**
 * Represents a single delivery (ball) event.
 * This is the core unit that flows through the Producer-Consumer pipeline.
 *
 * The producer (ScorePublisher) creates Ball objects and puts them into
 * the BlockingQueue. The consumer (ScoreProcessor) takes them out and
 * updates the ScoreCard.
 */
public class Ball {
    private final int overNumber; // 0-indexed
    private final int ballNumber; // 1-indexed within the over
    private final BallType type;
    private final WicketType wicketType;
    private final int runsScored; // runs from this delivery

    // Players involved
    private final String batsmanId;
    private final String bowlerId;
    private final String fielderIdIfAny; // for run-out / caught

    public Ball(int overNumber,
            int ballNumber,
            BallType type,
            WicketType wicketType,
            int runsScored,
            String batsmanId,
            String bowlerId,
            String fielderIdIfAny) {
        this.overNumber = overNumber;
        this.ballNumber = ballNumber;
        this.type = type;
        this.wicketType = wicketType;
        this.runsScored = runsScored;
        this.batsmanId = batsmanId;
        this.bowlerId = bowlerId;
        this.fielderIdIfAny = fielderIdIfAny;
    }

    // ─── Getters ───────────────────────────
    public int getOverNumber() {
        return overNumber;
    }

    public int getBallNumber() {
        return ballNumber;
    }

    public BallType getType() {
        return type;
    }

    public WicketType getWicketType() {
        return wicketType;
    }

    public int getRunsScored() {
        return runsScored;
    }

    public String getBatsmanId() {
        return batsmanId;
    }

    public String getBowlerId() {
        return bowlerId;
    }

    public String getFielderIdIfAny() {
        return fielderIdIfAny;
    }

    /** True for legal deliveries that count toward the over tally */
    public boolean isLegalDelivery() {
        return type != BallType.WIDE && type != BallType.NO_BALL;
    }

    @Override
    public String toString() {
        return String.format("Ball[%d.%d | %s | %d run(s) | bat=%s | bowl=%s]",
                overNumber, ballNumber, type, runsScored, batsmanId, bowlerId);
    }
}
