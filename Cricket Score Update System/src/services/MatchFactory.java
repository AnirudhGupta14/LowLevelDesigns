package services;

import enums.MatchFormat;
import models.Team;
import models.Match;

// Factory Pattern for creating different match formats
public class MatchFactory {
    private static int matchCounter = 1;

    public static Match createMatch(MatchFormat format, Team team1, Team team2,
                                    String venue, String matchType) {
        String matchId = "M" + String.format("%04d", matchCounter++);
        String matchName = team1.getName() + " vs " + team2.getName();
        
        return new Match(matchId, matchName, format, team1, team2, venue, matchType);
    }

    public static Match createT20Match(Team team1, Team team2, String venue, String matchType) {
        return createMatch(MatchFormat.T20, team1, team2, venue, matchType);
    }

    public static Match createODIMatch(Team team1, Team team2, String venue, String matchType) {
        return createMatch(MatchFormat.ODI, team1, team2, venue, matchType);
    }

    public static Match createTestMatch(Team team1, Team team2, String venue, String matchType) {
        return createMatch(MatchFormat.TEST, team1, team2, venue, matchType);
    }
}


