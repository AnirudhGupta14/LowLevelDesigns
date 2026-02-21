package models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Team {
    private String teamId;
    private String name;
    private List<Player> players;
    private Player captain;

    public Team(String teamId, String name) {
        this.teamId = teamId;
        this.name = name;
        this.players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    @Override
    public String toString() {
        return name;
    }
}
