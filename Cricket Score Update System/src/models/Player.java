package models;

import enums.PlayerRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
    private String playerId;
    private String name;
    private PlayerRole role;
    private PlayerStats battingStats;
    private PlayerStats bowlingStats;

    public Player(String playerId, String name, PlayerRole role) {
        this.playerId = playerId;
        this.name = name;
        this.role = role;
        this.battingStats = new PlayerStats();
        this.bowlingStats = new PlayerStats();
    }
}

