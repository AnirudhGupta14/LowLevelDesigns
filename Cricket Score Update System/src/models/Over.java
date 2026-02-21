package models;

import entities.BallEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Over {
    private final int overNumber;
    private final Player bowler;
    private final List<BallEvent> balls;
    private int runsInOver;

    public Over(int overNumber, Player bowler) {
        this.overNumber = overNumber;
        this.bowler = bowler;
        this.balls = new ArrayList<>();
        this.runsInOver = 0;
    }

    public void addBall(BallEvent ball) {
        balls.add(ball);
        runsInOver += ball.getRuns();
    }

    public boolean isComplete() {
        int legalDeliveries = 0;
        for (BallEvent ball : balls) {
            if (ball.countsAsBall()) {
                legalDeliveries++;
            }
        }
        return legalDeliveries >= 6;
    }

    public String getOverSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("models.Over ").append(overNumber).append(" (").append(bowler.getName()).append("): ");
        for (BallEvent ball : balls) {
            if (ball.isWicket()) {
                sb.append("W ");
            } else if (ball.isExtra()) {
                sb.append(ball.getExtraType().name().substring(0, 2)).append(" ");
            } else {
                sb.append(ball.getRuns()).append(" ");
            }
        }
        sb.append("| Runs: ").append(runsInOver);
        return sb.toString();
    }
}


