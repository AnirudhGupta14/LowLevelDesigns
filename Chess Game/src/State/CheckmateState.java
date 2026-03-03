package State;

import Command.Move;
import Services.Game;
import Observer.GameEvent;
import Observer.GameEventType;

/**
 * State Pattern — Represents a finished game (checkmate).
 * Rejects all further moves.
 */
public class CheckmateState implements GameState {

    private final String winner;

    public CheckmateState(String winner) {
        this.winner = winner;
    }

    @Override
    public void handleMove(Game game, Move move) {
        game.notifyObservers(new GameEvent(GameEventType.INVALID_MOVE,
                "Game is over! " + winner + " wins by checkmate. No more moves allowed."));
    }

    @Override
    public String getStateName() {
        return "CHECKMATE";
    }

    public String getWinner() {
        return winner;
    }
}
