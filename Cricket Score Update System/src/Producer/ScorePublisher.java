package Producer;

import Constants.BallType;
import Constants.WicketType;
import Entities.Ball;

import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * PRODUCER — ScorePublisher.
 *
 * Simulates a live data feed (e.g. hawk-eye / hawk-feed sensor) that generates
 * ball-by-ball events and puts them into the shared BlockingQueue.
 *
 * Producer-Consumer role:
 * ScorePublisher → puts(Ball) → LinkedBlockingQueue → ScoreProcessor
 *
 * In a real system this would be replaced by a Kafka consumer or a WebSocket
 * feed pushing data from the ground.
 */
public class ScorePublisher implements Runnable {

    private final BlockingQueue<Ball> ballQueue;
    private final String batsmanId;
    private final String bowlerId;
    private final int totalOvers;

    private static final BallType[] BALL_TYPES = BallType.values();
    private static final Random RNG = new Random();

    private volatile boolean running = true;

    public ScorePublisher(BlockingQueue<Ball> ballQueue,
            String batsmanId,
            String bowlerId,
            int totalOvers) {
        this.ballQueue = ballQueue;
        this.batsmanId = batsmanId;
        this.bowlerId = bowlerId;
        this.totalOvers = totalOvers;
    }

    @Override
    public void run() {
        int legalBalls = 0;
        int overNumber = 0;
        int ballInOver = 0;

        while (running && overNumber < totalOvers) {
            BallType type = randomBallType();
            int runs = runsForType(type);

            Ball ball = new Ball(
                    overNumber,
                    ballInOver + 1,
                    type,
                    type == BallType.WICKET ? randomWicketType() : WicketType.NONE,
                    runs,
                    batsmanId,
                    bowlerId,
                    "Fielder-" + (RNG.nextInt(9) + 1));

            try {
                System.out.printf("[PUBLISHER]  Publishing → %s%n", ball);
                ballQueue.put(ball); // 🔒 blocks if queue is full
                Thread.sleep(600); // simulate real-time feed interval
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            // Advance legal ball counter
            if (type.isLegalDelivery()) {
                legalBalls++;
                ballInOver++;
                if (ballInOver == 6) {
                    ballInOver = 0;
                    overNumber++;
                }
            }
        }

        running = false;
        System.out.println("[PUBLISHER]  📡 All overs published. Signalling end...");
        // Inject a sentinel null to tell the consumer to stop
        try {
            ballQueue.put(createSentinel());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        running = false;
    }

    // ─── Helpers ─────────────────────────────────────────────────

    private BallType randomBallType() {
        // Weighted random: dots most common, sixes rare
        int roll = RNG.nextInt(100);
        if (roll < 30)
            return BallType.DOT;
        if (roll < 50)
            return BallType.ONE;
        if (roll < 60)
            return BallType.TWO;
        if (roll < 65)
            return BallType.THREE;
        if (roll < 75)
            return BallType.FOUR;
        if (roll < 80)
            return BallType.SIX;
        if (roll < 85)
            return BallType.WIDE;
        if (roll < 90)
            return BallType.NO_BALL;
        return BallType.WICKET;
    }

    private int runsForType(BallType type) {
        return switch (type) {
            case DOT -> 0;
            case ONE -> 1;
            case TWO -> 2;
            case THREE -> 3;
            case FOUR -> 4;
            case SIX -> 6;
            case WIDE, NO_BALL -> 1;
            case WICKET -> 0;
        };
    }

    private WicketType randomWicketType() {
        WicketType[] types = { WicketType.BOWLED, WicketType.CAUGHT, WicketType.LBW,
                WicketType.RUN_OUT, WicketType.STUMPED };
        return types[RNG.nextInt(types.length)];
    }

    /** Sentinel ball — signals consumer to shut down */
    private Ball createSentinel() {
        return new Ball(-1, -1, BallType.DOT, WicketType.NONE, 0, "SENTINEL", "SENTINEL", "");
    }

    private boolean isLegalDelivery(BallType type) {
        return type != BallType.WIDE && type != BallType.NO_BALL;
    }
}
