package Observer;

import Constants.BallType;
import Constants.WicketType;
import Entities.Ball;
import Entities.ScoreCard;

/**
 * Observer 2 — Live commentary generator.
 * Reads the last ball event and produces ball-by-ball commentary text.
 * Acquires a readLock for any scorecard reads.
 */
public class CommentaryGenerator implements ScoreObserver {

    @Override
    public void onBallProcessed(Ball ball, ScoreCard scoreCard) {
        System.out.printf("[COMMENTARY] %d.%d  %s%n",
                ball.getOverNumber(), ball.getBallNumber(),
                buildCommentary(ball));
    }

    private String buildCommentary(Ball ball) {
        return switch (ball.getType()) {
            case DOT -> "Dot ball! Good tight delivery.";
            case ONE -> "Nudged away for 1.";
            case TWO -> "Driven nicely — 2 runs!";
            case THREE -> "Quick running — 3 runs!";
            case FOUR -> "🔴 FOUR! Cracking shot to the boundary!";
            case SIX -> "🚀 SIX! That's gone all the way!";
            case WIDE -> "Wide ball — 1 extra to the batting side.";
            case NO_BALL -> "No-ball! Free hit on the next delivery.";
            case WICKET -> buildWicketCommentary(ball);
        };
    }

    private String buildWicketCommentary(Ball ball) {
        String base = "🎉 WICKET! " + ball.getBatsmanId() + " is OUT!";
        return switch (ball.getWicketType()) {
            case BOWLED -> base + " Bowled — timber!";
            case CAUGHT -> base + " Caught by " + ball.getFielderIdIfAny() + "!";
            case LBW -> base + " LBW — plumb in front!";
            case RUN_OUT -> base + " Run out — direct hit by " + ball.getFielderIdIfAny() + "!";
            case STUMPED -> base + " Stumped — great work by the keeper!";
            case HIT_WICKET -> base + " Hit wicket — unlucky!";
            default -> base;
        };
    }
}
