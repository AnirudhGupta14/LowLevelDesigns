package models;

import enums.ExtraType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerStats {
    // Batting stats
    private int runs;
    private int ballsFaced;
    private int fours;
    private int sixes;
    private boolean isOut;
    private String dismissalType;

    // Bowling stats
    private int ballsBowled;
    private int runsConceded;
    private int wicketsTaken;
    private int maidenOvers;
    private int wides;
    private int noBalls;

    public PlayerStats() {
        this.runs = 0;
        this.ballsFaced = 0;
        this.fours = 0;
        this.sixes = 0;
        this.isOut = false;
        this.ballsBowled = 0;
        this.runsConceded = 0;
        this.wicketsTaken = 0;
        this.maidenOvers = 0;
        this.wides = 0;
        this.noBalls = 0;
    }

    // Batting methods
    public void addRuns(int runs) {
        this.runs += runs;
        this.ballsFaced++;
        if (runs == 4) fours++;
        if (runs == 6) sixes++;
    }

    public void markOut(String dismissalType) {
        this.isOut = true;
        this.dismissalType = dismissalType;
    }

    public double getStrikeRate() {
        return ballsFaced > 0 ? (runs * 100.0) / ballsFaced : 0.0;
    }

    // Bowling methods
    public void addBall(int runsConceded, boolean isWicket) {
        this.ballsBowled++;
        this.runsConceded += runsConceded;
        if (isWicket) {
            this.wicketsTaken++;
        }
    }

    public void addExtra(ExtraType type, int runs) {
        if (type == ExtraType.WIDE) {
            this.wides++;
            this.runsConceded += runs;
        } else if (type == ExtraType.NO_BALL) {
            this.noBalls++;
            this.runsConceded += runs;
        }
    }

    public double getEconomyRate() {
        double overs = ballsBowled / 6.0;
        return overs > 0 ? runsConceded / overs : 0.0;
    }

    public double getBowlingAverage() {
        return wicketsTaken > 0 ? (double) runsConceded / wicketsTaken : 0.0;
    }
}

