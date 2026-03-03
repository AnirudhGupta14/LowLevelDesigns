package Entities;

import Constants.MatchState;

/**
 * Represents a cricket match between two teams.
 * Holds both innings scorecards and tracks match state.
 */
public class Match {
    private final String matchId;
    private final Team team1; // batting first
    private final Team team2; // batting second
    private final int totalOvers;

    private MatchState state;
    private ScoreCard firstInningsCard;
    private ScoreCard secondInningsCard;
    private ScoreCard currentCard; // pointer to active innings scorecard

    public Match(String matchId, Team team1, Team team2, int totalOvers) {
        this.matchId = matchId;
        this.team1 = team1;
        this.team2 = team2;
        this.totalOvers = totalOvers;
        this.state = MatchState.NOT_STARTED;
    }

    public synchronized void startFirstInnings() {
        firstInningsCard = new ScoreCard(team1.getName());
        currentCard = firstInningsCard;
        state = MatchState.FIRST_INNINGS;
        System.out.println("\n🏏  First innings started — " + team1.getName() + " batting\n");
    }

    public synchronized void startSecondInnings() {
        secondInningsCard = new ScoreCard(team2.getName());
        currentCard = secondInningsCard;
        state = MatchState.SECOND_INNINGS;
        System.out.println("\n🏏  Second innings started — " + team2.getName() + " batting\n");
    }

    public synchronized void completeMatch() {
        state = MatchState.COMPLETED;
        announceResult();
    }

    private void announceResult() {
        int t1 = firstInningsCard.getTotalRuns();
        int t2 = secondInningsCard != null ? secondInningsCard.getTotalRuns() : 0;

        System.out.println("\n════════════════════════════════════════");
        System.out.println("  MATCH RESULT");
        System.out.printf("  %s: %d%n", team1.getName(), t1);
        System.out.printf("  %s: %d%n", team2.getName(), t2);
        if (t1 > t2) {
            System.out.printf("  🏆 %s won by %d runs!%n", team1.getName(), t1 - t2);
        } else if (t2 > t1) {
            int wicketsLeft = 10 - secondInningsCard.getWickets();
            System.out.printf("  🏆 %s won by %d wickets!%n", team2.getName(), wicketsLeft);
        } else {
            System.out.println("  🤝 Match Tied!");
        }
        System.out.println("════════════════════════════════════════\n");
    }

    // ─── Getters ─────────────────────────────
    public String getMatchId() {
        return matchId;
    }

    public Team getTeam1() {
        return team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public int getTotalOvers() {
        return totalOvers;
    }

    public synchronized MatchState getState() {
        return state;
    }

    public synchronized ScoreCard getCurrentCard() {
        return currentCard;
    }

    public ScoreCard getFirstInningsCard() {
        return firstInningsCard;
    }

    public ScoreCard getSecondInningsCard() {
        return secondInningsCard;
    }
}
