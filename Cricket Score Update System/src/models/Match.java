package models;

import entities.BallEvent;
import enums.MatchFormat;
import enums.MatchStatus;
import lombok.Getter;
import lombok.Setter;
import observer.MatchObserver;
import entities.MatchUpdateEvent;
import services.Scorecard;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Match {
    private String matchId;
    private String matchName;
    private MatchFormat format;
    private Team team1;
    private Team team2;
    private List<Innings> inningsList;
    private Innings currentInnings;
    private MatchStatus status;
    private List<MatchObserver> observers;
    private String venue;
    private String matchType; // International, Domestic, IPL, etc.

    public Match(String matchId, String matchName, MatchFormat format, Team team1, Team team2, String venue, String matchType) {
        this.matchId = matchId;
        this.matchName = matchName;
        this.format = format;
        this.team1 = team1;
        this.team2 = team2;
        this.venue = venue;
        this.matchType = matchType;
        this.inningsList = new ArrayList<>();
        this.status = MatchStatus.NOT_STARTED;
        this.observers = new ArrayList<>();
    }

    // Observer Pattern Methods
    public void subscribe(MatchObserver observer) {
        observers.add(observer);
        notifyObservers(new MatchUpdateEvent(matchId, "SUBSCRIPTION", 
            "New subscriber joined", getCurrentScore()));
    }

    public void unsubscribe(MatchObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(MatchUpdateEvent event) {
        for (MatchObserver observer : observers) {
            observer.onMatchUpdate(event);
        }
    }

    // models.Match Control Methods
    public void startMatch() {
        if (status != MatchStatus.NOT_STARTED) {
            throw new IllegalStateException("models.Match has already started");
        }
        status = MatchStatus.IN_PROGRESS;
        notifyObservers(new MatchUpdateEvent(matchId, "MATCH_START", 
            matchName + " has started!", "0/0"));
    }

    public void startInnings(Team battingTeam, Team bowlingTeam) {
        int inningsNumber = inningsList.size() + 1;
        currentInnings = new Innings(inningsNumber, battingTeam, bowlingTeam, format.getOversPerInnings());
        inningsList.add(currentInnings);
        
        notifyObservers(new MatchUpdateEvent(matchId, "INNINGS_START", 
            "models.Innings " + inningsNumber + ": " + battingTeam.getName() + " batting", "0/0"));
    }

    public void setBatsmen(Player striker, Player nonStriker) {
        if (currentInnings == null) {
            throw new IllegalStateException("No innings in progress");
        }
        currentInnings.setStriker(striker);
        currentInnings.setNonStriker(nonStriker);
    }

    public void startNewOver(Player bowler) {
        if (currentInnings == null) {
            throw new IllegalStateException("No innings in progress");
        }
        currentInnings.startNewOver(bowler);
        notifyObservers(new MatchUpdateEvent(matchId, "OVER_START", 
            "models.Over " + currentInnings.getOvers().size() + ": " + bowler.getName() + " to bowl",
            getCurrentScore()));
    }

    public void recordBall(BallEvent ballEvent) {
        if (currentInnings == null) {
            throw new IllegalStateException("No innings in progress");
        }
        
        currentInnings.addBall(ballEvent);
        
        // Notify observers with ball-by-ball update
        notifyObservers(new MatchUpdateEvent(matchId, ballEvent, getCurrentScore()));
        
        // Check if innings is complete
        if (currentInnings.isCompleted()) {
            endInnings();
        }
    }

    private void endInnings() {
        notifyObservers(new MatchUpdateEvent(matchId, "INNINGS_END", 
            "models.Innings " + currentInnings.getInningsNumber() + " completed: " +
            currentInnings.getBattingTeam().getName() + " scored " + getCurrentScore(), 
            getCurrentScore()));
        currentInnings = null;
    }

    public void endMatch() {
        status = MatchStatus.COMPLETED;
        String result = determineWinner();
        notifyObservers(new MatchUpdateEvent(matchId, "MATCH_END",
            "models.Match completed! " + result, getFinalScore()));
    }

    public void abandonMatch() {
        status = MatchStatus.ABANDONED;
        notifyObservers(new MatchUpdateEvent(matchId, "MATCH_ABANDONED", 
            "models.Match has been abandoned", getCurrentScore()));
    }

    // Score and Statistics Methods
    public String getCurrentScore() {
        if (currentInnings == null) {
            return "0/0";
        }
        return currentInnings.getScore();
    }

    public String getFinalScore() {
        StringBuilder sb = new StringBuilder();
        for (Innings innings : inningsList) {
            sb.append(innings.getBattingTeam().getName())
              .append(": ")
              .append(innings.getScore())
              .append(" in ")
              .append(innings.getOvers().size())
              .append(" overs");
            if (inningsList.indexOf(innings) < inningsList.size() - 1) {
                sb.append(" | ");
            }
        }
        return sb.toString();
    }

    private String determineWinner() {
        if (inningsList.size() < 2) {
            return "models.Match incomplete";
        }
        
        Innings innings1 = inningsList.get(0);
        Innings innings2 = inningsList.get(1);
        
        if (innings1.getTotalRuns() > innings2.getTotalRuns()) {
            return innings1.getBattingTeam().getName() + " won by " + 
                   (innings1.getTotalRuns() - innings2.getTotalRuns()) + " runs";
        } else if (innings2.getTotalRuns() > innings1.getTotalRuns()) {
            return innings2.getBattingTeam().getName() + " won by " + 
                   (10 - innings2.getTotalWickets()) + " wickets";
        } else {
            return "models.Match tied!";
        }
    }

    public Scorecard generateScorecard() {
        return new Scorecard(this);
    }

    public int getObserverCount() { return observers.size(); }
}