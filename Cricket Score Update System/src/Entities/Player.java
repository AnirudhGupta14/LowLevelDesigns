package Entities;

/**
 * Represents a cricket player.
 * Tracks batting and bowling statistics immutably accumulated during the match.
 */
public class Player {
    private final String id;
    private final String name;

    // Batting stats
    private int runsScored;
    private int ballsFaced;
    private int fours;
    private int sixes;
    private boolean isOut;

    // Bowling stats
    private int ballsBowled; // legal deliveries only
    private int runsConceded;
    private int wicketsTaken;
    private int widesBowled;
    private int noBallsBowled;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // ─── Batting updates ───────────────────────────────
    public synchronized void addRuns(int runs) {
        this.runsScored += runs;
    }

    public synchronized void addBallFaced() {
        this.ballsFaced++;
    }

    public synchronized void addFour() {
        this.fours++;
    }

    public synchronized void addSix() {
        this.sixes++;
    }

    public synchronized void dismiss() {
        this.isOut = true;
    }

    // ─── Bowling updates ───────────────────────────────
    public synchronized void addLegalBall() {
        this.ballsBowled++;
    }

    public synchronized void addRunsConceded(int r) {
        this.runsConceded += r;
    }

    public synchronized void addWicket() {
        this.wicketsTaken++;
    }

    public synchronized void addWide() {
        this.widesBowled++;
    }

    public synchronized void addNoBall() {
        this.noBallsBowled++;
    }

    // ─── Getters ───────────────────────────────────────
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public synchronized int getRunsScored() {
        return runsScored;
    }

    public synchronized int getBallsFaced() {
        return ballsFaced;
    }

    public synchronized int getFours() {
        return fours;
    }

    public synchronized int getSixes() {
        return sixes;
    }

    public synchronized boolean isOut() {
        return isOut;
    }

    public synchronized int getBallsBowled() {
        return ballsBowled;
    }

    public synchronized int getRunsConceded() {
        return runsConceded;
    }

    public synchronized int getWicketsTaken() {
        return wicketsTaken;
    }

    public synchronized double getStrikeRate() {
        return ballsFaced == 0 ? 0 : (runsScored * 100.0 / ballsFaced);
    }

    public synchronized String getBowlingFigures() {
        int overs = ballsBowled / 6;
        int balls = ballsBowled % 6;
        return wicketsTaken + "/" + runsConceded + " (" + overs + "." + balls + ")";
    }

    @Override
    public String toString() {
        return name + " [" + runsScored + "(" + ballsFaced + ")]";
    }
}
