package controllers;

import helpers.StringUtils;
import models.Match;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// Singleton pattern for managing multiple concurrent matches
public class MatchManager {
    private static MatchManager instance;
    private final Map<String, Match> activeMatches;
    private final Map<String, Match> completedMatches;

    private MatchManager() {
        this.activeMatches = new ConcurrentHashMap<>();
        this.completedMatches = new ConcurrentHashMap<>();
    }

    public static synchronized MatchManager getInstance() {
        if (instance == null) {
            instance = new MatchManager();
        }
        return instance;
    }

    public void addMatch(Match match) {
        activeMatches.put(match.getMatchId(), match);
        System.out.println("models.Match added: " + match.getMatchId() + " - " + match.getMatchName());
    }

    public Match getMatch(String matchId) {
        Match match = activeMatches.get(matchId);
        if (match == null) {
            match = completedMatches.get(matchId);
        }
        return match;
    }

    public void completeMatch(String matchId) {
        Match match = activeMatches.remove(matchId);
        if (match != null) {
            completedMatches.put(matchId, match);
            System.out.println("models.Match completed and archived: " + matchId);
        }
    }

    public List<Match> getAllActiveMatches() {
        return new ArrayList<>(activeMatches.values());
    }

    public List<Match> getAllCompletedMatches() {
        return new ArrayList<>(completedMatches.values());
    }

    public void displayActiveMatches() {
        System.out.println("\n" + StringUtils.repeat("=", 80));
        System.out.println("ACTIVE MATCHES (" + activeMatches.size() + ")");
        System.out.println(StringUtils.repeat("=", 80));
        
        if (activeMatches.isEmpty()) {
            System.out.println("No active matches");
        } else {
            for (Match match : activeMatches.values()) {
                System.out.println("\n[" + match.getMatchId() + "] " + match.getMatchName());
                System.out.println("Format: " + match.getFormat() + " | Status: " + match.getStatus());
                System.out.println("Venue: " + match.getVenue() + " | Type: " + match.getMatchType());
                System.out.println("Score: " + match.getCurrentScore());
                System.out.println("Subscribers: " + match.getObserverCount());
            }
        }
        System.out.println(StringUtils.repeat("=", 80) + "\n");
    }

    public int getActiveMatchCount() {
        return activeMatches.size();
    }

    public int getCompletedMatchCount() {
        return completedMatches.size();
    }
}

