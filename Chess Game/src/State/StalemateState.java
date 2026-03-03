package State;

import Command.Move;
import Services.Game;
import Observer.GameEvent;
import Observer.GameEventType;

/**
 * State Pattern — Represents a finished game (stalemate / draw).
 * Rejects all further moves.
 */
public class StalemateState implements GameState {

    @Override
    public void handleMove(Game game, Move move) {
        game.notifyObservers(new GameEvent(GameEventType.INVALID_MOVE,
                "Game is over! It's a stalemate (draw). No more moves allowed."));
    }

    @Override
    public String getStateName() {
        return "STALEMATE";
    }
}
