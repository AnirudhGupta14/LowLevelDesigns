package services;

import entities.BallEvent;
import enums.ExtraType;
import enums.PlayerRole;
import controllers.MatchManager;
import models.Match;
import models.Player;
import models.Team;
import observer.UserSubscriber;

public class Main {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║     CRICKET SCORE UPDATE SYSTEM - REAL-TIME MATCH TRACKING PLATFORM    ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════╝\n");

        // Initialize models.Match Manager (Singleton)
        MatchManager matchManager = MatchManager.getInstance();

        // Demo: Create multiple concurrent matches with different formats
        System.out.println("\n>>> CREATING MULTIPLE CONCURRENT MATCHES <<<\n");
        
        // models.Match 1: T20 IPL models.Match
        Match iplMatch = createIPLMatch();
        matchManager.addMatch(iplMatch);

        // models.Match 2: ODI International models.Match
        Match odiMatch = createODIMatch();
        matchManager.addMatch(odiMatch);

        // models.Match 3: Test models.Match
        Match testMatch = createTestMatch();
        matchManager.addMatch(testMatch);

        // Display all active matches
        matchManager.displayActiveMatches();
        // Demo: Real-time updates with Observer Pattern
        System.out.println("\n>>> DEMONSTRATING REAL-TIME UPDATES WITH OBSERVER PATTERN <<<\n");
        
        // Subscribe users to the IPL match
        UserSubscriber user1 = new UserSubscriber("U001", "Rahul", true);
        UserSubscriber user2 = new UserSubscriber("U002", "Priya", false);
        UserSubscriber user3 = new UserSubscriber("U003", "Amit", true);
        
        iplMatch.subscribe(user1);
        iplMatch.subscribe(user2);
        iplMatch.subscribe(user3);

        // Demo: Simulate a complete T20 match with ball-by-ball updates
        System.out.println("\n>>> SIMULATING LIVE T20 MATCH: " + iplMatch.getMatchName() + " <<<\n");

        // Display final scorecard
        System.out.println("\n>>> GENERATING FINAL SCORECARD <<<\n");
        Scorecard scorecard = iplMatch.generateScorecard();
        scorecard.display();

        // Complete the match
        matchManager.completeMatch(iplMatch.getMatchId());

        // Display final match manager status
        System.out.println("\n>>> MATCH MANAGER STATUS <<<");
        System.out.println("Active Matches: " + matchManager.getActiveMatchCount());
        System.out.println("Completed Matches: " + matchManager.getCompletedMatchCount());

        System.out.println("\n╔════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    DEMO COMPLETED SUCCESSFULLY                          ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════╝");
    }

    private static Match createIPLMatch() {
        // Create teams for IPL T20 match
        Team mumbaiIndians = new Team("MI", "Mumbai Indians");
        Team chennaiSuperkings = new Team("CSK", "Chennai Super Kings");

        // Add players to Mumbai Indians
        Player rohit = new Player("P001", "Rohit Sharma", PlayerRole.BATSMAN);
        Player ishan = new Player("P002", "Ishan Kishan", PlayerRole.WICKET_KEEPER);
        Player surya = new Player("P003", "Suryakumar Yadav", PlayerRole.BATSMAN);
        Player hardik = new Player("P004", "Hardik Pandya", PlayerRole.ALL_ROUNDER);
        Player bumrah = new Player("P005", "Jasprit Bumrah", PlayerRole.BOWLER);
        Player chahar = new Player("P006", "Rahul Chahar", PlayerRole.BOWLER);

        mumbaiIndians.addPlayer(rohit);
        mumbaiIndians.addPlayer(ishan);
        mumbaiIndians.addPlayer(surya);
        mumbaiIndians.addPlayer(hardik);
        mumbaiIndians.addPlayer(bumrah);
        mumbaiIndians.addPlayer(chahar);
        mumbaiIndians.setCaptain(rohit);

        // Add players to Chennai Super Kings
        Player dhoni = new Player("P101", "MS Dhoni", PlayerRole.WICKET_KEEPER);
        Player ruturaj = new Player("P102", "Ruturaj Gaikwad", PlayerRole.BATSMAN);
        Player jadeja = new Player("P103", "Ravindra Jadeja", PlayerRole.ALL_ROUNDER);
        Player moeen = new Player("P104", "Moeen Ali", PlayerRole.ALL_ROUNDER);
        Player deepak = new Player("P105", "Deepak Chahar", PlayerRole.BOWLER);
        Player bravo = new Player("P106", "Dwayne Bravo", PlayerRole.BOWLER);

        chennaiSuperkings.addPlayer(dhoni);
        chennaiSuperkings.addPlayer(ruturaj);
        chennaiSuperkings.addPlayer(jadeja);
        chennaiSuperkings.addPlayer(moeen);
        chennaiSuperkings.addPlayer(deepak);
        chennaiSuperkings.addPlayer(bravo);
        chennaiSuperkings.setCaptain(dhoni);

        // Create match using Factory Pattern
        return MatchFactory.createT20Match(mumbaiIndians, chennaiSuperkings, 
                                          "Wankhede Stadium, Mumbai", "IPL");
    }

    private static Match createODIMatch() {
        Team india = new Team("IND", "India");
        Team australia = new Team("AUS", "Australia");

        // Add some players
        india.addPlayer(new Player("P201", "Virat Kohli", PlayerRole.BATSMAN));
        india.addPlayer(new Player("P202", "Rohit Sharma", PlayerRole.BATSMAN));
        india.addPlayer(new Player("P203", "Kuldeep Yadav", PlayerRole.BOWLER));

        australia.addPlayer(new Player("P301", "Steve Smith", PlayerRole.BATSMAN));
        australia.addPlayer(new Player("P302", "David Warner", PlayerRole.BATSMAN));
        australia.addPlayer(new Player("P303", "Pat Cummins", PlayerRole.BOWLER));

        return MatchFactory.createODIMatch(india, australia, 
                                          "MCG, Melbourne", "International");
    }

    private static Match createTestMatch() {
        Team england = new Team("ENG", "England");
        Team newZealand = new Team("NZ", "New Zealand");

        england.addPlayer(new Player("P401", "Joe Root", PlayerRole.BATSMAN));
        england.addPlayer(new Player("P402", "Ben Stokes", PlayerRole.ALL_ROUNDER));
        england.addPlayer(new Player("P403", "James Anderson", PlayerRole.BOWLER));

        newZealand.addPlayer(new Player("P501", "Kane Williamson", PlayerRole.BATSMAN));
        newZealand.addPlayer(new Player("P502", "Ross Taylor", PlayerRole.BATSMAN));
        newZealand.addPlayer(new Player("P503", "Trent Boult", PlayerRole.BOWLER));

        return MatchFactory.createTestMatch(england, newZealand, 
                                           "Lord's, London", "International");
    }
}
