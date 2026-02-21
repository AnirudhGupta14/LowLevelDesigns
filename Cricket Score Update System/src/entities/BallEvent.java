package entities;

import enums.BallEventType;
import enums.ExtraType;
import lombok.Getter;
import lombok.Setter;
import models.Player;

@Getter
@Setter
public class BallEvent {
    private BallEventType eventType;
    private int runs;
    private Player batsman;
    private Player bowler;
    private ExtraType extraType;
    private String dismissalType;
    private Player dismissedPlayer;
    private String commentary;

    public BallEvent(BallEventType eventType, int runs, Player batsman, Player bowler) {
        this.eventType = eventType;
        this.runs = runs;
        this.batsman = batsman;
        this.bowler = bowler;
    }

    public static BallEvent normalBall(int runs, Player batsman, Player bowler, String commentary) {
        BallEvent event = new BallEvent(BallEventType.NORMAL, runs, batsman, bowler);
        event.commentary = commentary;
        return event;
    }

    public static BallEvent wicket(Player dismissedPlayer, Player bowler, String dismissalType, String commentary) {
        BallEvent event = new BallEvent(BallEventType.WICKET, 0, dismissedPlayer, bowler);
        event.dismissalType = dismissalType;
        event.dismissedPlayer = dismissedPlayer;
        event.commentary = commentary;
        return event;
    }

    public static BallEvent extra(ExtraType extraType, int runs, Player batsman, Player bowler, String commentary) {
        BallEvent event = new BallEvent(BallEventType.EXTRA, runs, batsman, bowler);
        event.extraType = extraType;
        event.commentary = commentary;
        return event;
    }

    public boolean isExtra() {
        return eventType == BallEventType.EXTRA;
    }

    public boolean isWicket() {
        return eventType == BallEventType.WICKET;
    }

    public boolean countsAsBall() {
        // Wides and no-balls don't count as legal deliveries
        return !isExtra() || (extraType != ExtraType.WIDE && extraType != ExtraType.NO_BALL);
    }
}

