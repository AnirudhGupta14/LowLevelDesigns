package Entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a cricket team with its squad of players.
 */
public class Team {
    private final String name;
    private final List<Player> players;

    public Team(String name, List<Player> players) {
        this.name = name;
        this.players = new ArrayList<>(players);
    }

    public Player getPlayer(String playerId) {
        return players.stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Player " + playerId + " not in team " + name));
    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    @Override
    public String toString() {
        return name;
    }
}
