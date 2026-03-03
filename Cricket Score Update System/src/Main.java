import Entities.Match;
import Entities.Player;
import Entities.Team;
import Observer.CommentaryGenerator;
import Observer.ScoreBoardDisplay;
import Observer.StatisticsTracker;
import Services.MatchController;

import java.util.List;

/**
 * ┌──────────────────────────────────────────────────────────────────┐
 * │ Cricket Score Update System — Entry Point │
 * │ │
 * │ Architecture: │
 * │ • ScorePublisher → puts Ball into LinkedBlockingQueue(50) │
 * │ • ScoreProcessor ← takes Ball, writeLock → updates ScoreCard │
 * │ • Observers ← readLock → display / commentary / stats │
 * └──────────────────────────────────────────────────────────────────┘
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        // ─── Create Players ───────────────────────────────────────────────
        List<Player> indiaPlayers = List.of(
                new Player("IND-1", "Rohit Sharma"),
                new Player("IND-2", "Virat Kohli"),
                new Player("IND-3", "Shubman Gill"));
        List<Player> ausPlayers = List.of(
                new Player("AUS-1", "David Warner"),
                new Player("AUS-2", "Steve Smith"),
                new Player("AUS-3", "Mitchell Starc"));

        // ─── Create Teams ─────────────────────────────────────────────────
        Team india = new Team("India", indiaPlayers);
        Team australia = new Team("Australia", ausPlayers);

        // ─── Create Match (5-over T5 for quick demo) ──────────────────────
        Match match = new Match("MATCH-001", india, australia, 5);

        // ─── Register Observers ───────────────────────────────────────────
        MatchController controller = MatchController.getInstance();
        controller.addObserver(new ScoreBoardDisplay());
        controller.addObserver(new CommentaryGenerator());
        controller.addObserver(new StatisticsTracker());

        // ─── Start Match (First Innings) ──────────────────────────────────
        controller.startMatch(match);

        // Wait for first innings to complete
        controller.awaitInningsCompletion();

        // ─── Second Innings ───────────────────────────────────────────────
        controller.startSecondInnings("AUS-1", "IND-3");
        controller.awaitInningsCompletion();

        // ─── Match Result ─────────────────────────────────────────────────
        controller.completeMatch();
    }
}
