package models;

import entities.BallEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Innings {
    private final int inningsNumber;
    private final Team battingTeam;
    private final Team bowlingTeam;
    private final List<Over> overs;
    private int totalRuns;
    private int totalWickets;
    private int totalExtras;
    private Player striker;
    private Player nonStriker;
    private Over currentOver;
    private boolean isCompleted;
    private final int maxOvers;

    public Innings(int inningsNumber, Team battingTeam, Team bowlingTeam, int maxOvers) {
        this.inningsNumber = inningsNumber;
        this.battingTeam = battingTeam;
        this.bowlingTeam = bowlingTeam;
        this.maxOvers = maxOvers;
        this.overs = new ArrayList<>();
        this.totalRuns = 0;
        this.totalWickets = 0;
        this.totalExtras = 0;
        this.isCompleted = false;
    }

    public void startNewOver(Player bowler) {
        if (currentOver != null && !currentOver.isComplete()) {
            throw new IllegalStateException("Current over is not complete");
        }
        currentOver = new Over(overs.size() + 1, bowler);
        overs.add(currentOver);
    }

    public void addBall(BallEvent ballEvent) {
        if (currentOver == null) {
            throw new IllegalStateException("No over in progress");
        }

        currentOver.addBall(ballEvent);
        totalRuns += ballEvent.getRuns();

        // Update player stats
        if (ballEvent.isWicket()) {
            totalWickets++;
            ballEvent.getDismissedPlayer().getBattingStats().markOut(ballEvent.getDismissalType());
            ballEvent.getBowler().getBowlingStats().addBall(0, true);
        } else if (ballEvent.isExtra()) {
            totalExtras += ballEvent.getRuns();
            ballEvent.getBowler().getBowlingStats().addExtra(ballEvent.getExtraType(), ballEvent.getRuns());
        } else {
            // Normal ball
            ballEvent.getBatsman().getBattingStats().addRuns(ballEvent.getRuns());
            ballEvent.getBowler().getBowlingStats().addBall(ballEvent.getRuns(), false);
        }

        // Rotate strike on odd runs
        if (!ballEvent.isWicket() && ballEvent.getRuns() % 2 == 1) {
            rotateStrike();
        }

        // Check if innings is complete
        if (totalWickets >= 10 || overs.size() >= maxOvers) {
            isCompleted = true;
        }
    }

    private void rotateStrike() {
        Player temp = striker;
        striker = nonStriker;
        nonStriker = temp;
    }

    public double getCurrentRunRate() {
        double oversCompleted = overs.size() - 1 + (currentOver != null ? currentOver.getBalls().size() / 6.0 : 0);
        return oversCompleted > 0 ? totalRuns / oversCompleted : 0.0;
    }

    public String getScore() {
        return totalRuns + "/" + totalWickets;
    }
}


