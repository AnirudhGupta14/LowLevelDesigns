package entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MatchUpdateEvent {
    private final String matchId;
    private final String updateType;
    private final String message;
    private final String currentScore;
    private BallEvent ballEvent;
    private final LocalDateTime timestamp;

    public MatchUpdateEvent(String matchId, String updateType, String message, String currentScore) {
        this.matchId = matchId;
        this.updateType = updateType;
        this.message = message;
        this.currentScore = currentScore;
        this.timestamp = LocalDateTime.now();
    }

    public MatchUpdateEvent(String matchId, BallEvent ballEvent, String currentScore) {
        this.matchId = matchId;
        this.updateType = "BALL_UPDATE";
        this.ballEvent = ballEvent;
        this.message = ballEvent.getCommentary();
        this.currentScore = currentScore;
        this.timestamp = LocalDateTime.now();
    }
}

