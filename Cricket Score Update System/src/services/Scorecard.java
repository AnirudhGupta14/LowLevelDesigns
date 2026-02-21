package services;

import enums.MatchStatus;
import helpers.StringUtils;
import models.Innings;
import models.Over;
import models.Player;
import models.PlayerStats;
import models.Match;

import java.util.List;

public class Scorecard {
    private final Match match;

    public Scorecard(Match match) {
        this.match = match;
    }

    public void display() {
        System.out.println("\n" + StringUtils.repeat("=", 80));
        System.out.println("SCORECARD: " + match.getMatchName());
        System.out.println("Format: " + match.getFormat() + " | Venue: " + match.getVenue());
        System.out.println("models.Match Type: " + match.getMatchType());
        System.out.println("Status: " + match.getStatus());
        System.out.println(StringUtils.repeat("=", 80));

        List<Innings> inningsList = match.getInningsList();
        
        for (Innings innings : inningsList) {
            displayInnings(innings);
        }

        if (match.getStatus() == MatchStatus.COMPLETED) {
            System.out.println("\nRESULT: " + match.getFinalScore());
        }

        System.out.println(StringUtils.repeat("=", 80) + "\n");
    }

    private void displayInnings(Innings innings) {
        System.out.println("\n--- INNINGS " + innings.getInningsNumber() + ": " + 
                          innings.getBattingTeam().getName() + " ---");
        System.out.println("Score: " + innings.getScore() + " in " + 
                          innings.getOvers().size() + " overs");
        System.out.println("Run Rate: " + String.format("%.2f", innings.getCurrentRunRate()));
        System.out.println();

        // Batting Performance
        System.out.println("BATTING:");
        System.out.println(String.format("%-20s %5s %6s %4s %4s %8s %10s", 
                          "models.Player", "Runs", "Balls", "4s", "6s", "SR", "Status"));
        System.out.println(StringUtils.repeat("-", 70));

        for (Player player : innings.getBattingTeam().getPlayers()) {
            PlayerStats stats = player.getBattingStats();
            if (stats.getBallsFaced() > 0) {
                String status = stats.isOut() ? stats.getDismissalType() : "Not Out";
                System.out.println(String.format("%-20s %5d %6d %4d %4d %8.2f %10s",
                    player.getName(),
                    stats.getRuns(),
                    stats.getBallsFaced(),
                    stats.getFours(),
                    stats.getSixes(),
                    stats.getStrikeRate(),
                    status));
            }
        }

        System.out.println(StringUtils.repeat("-", 70));
        System.out.println("Extras: " + innings.getTotalExtras());
        System.out.println("Total: " + innings.getTotalRuns() + "/" + innings.getTotalWickets());
        System.out.println();

        // Bowling Performance
        System.out.println("BOWLING:");
        System.out.println(String.format("%-20s %6s %6s %7s %5s %8s", 
                          "models.Player", "Overs", "Runs", "Wickets", "Econ", "Avg"));
        System.out.println(StringUtils.repeat("-", 70));

        for (Player player : innings.getBowlingTeam().getPlayers()) {
            PlayerStats stats = player.getBowlingStats();
            if (stats.getBallsBowled() > 0) {
                double overs = stats.getBallsBowled() / 6.0;
                System.out.println(String.format("%-20s %6.1f %6d %7d %5.2f %8.2f",
                    player.getName(),
                    overs,
                    stats.getRunsConceded(),
                    stats.getWicketsTaken(),
                    stats.getEconomyRate(),
                    stats.getBowlingAverage()));
            }
        }
        System.out.println();

        // models.Over Summary
        System.out.println("OVER SUMMARY:");
        for (Over over : innings.getOvers()) {
            System.out.println(over.getOverSummary());
        }
    }
}

