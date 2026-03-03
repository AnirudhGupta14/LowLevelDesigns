package Constants;

/**
 * Represents the outcome type of a single delivery (ball).
 */
public enum BallType {
    DOT, // 0 runs, no wicket
    ONE, // 1 run
    TWO, // 2 runs
    THREE, // 3 runs
    FOUR, // boundary 4
    SIX, // boundary 6
    WIDE, // wide delivery (+1 run, ball not counted)
    NO_BALL, // no-ball (+1 run, ball not counted)
    WICKET // batsman dismissed
}
