package State;

import Command.Move;
import Services.Game;

/**
 * State Pattern — defines the interface for all game states.
 * Allows the Game context to delegate move handling to the current state.
 */
public interface GameState {
    /**
     * Handle a move request in the current state.
     * 
     * @param game The game context
     * @param move The move to process
     */
    void handleMove(Game game, Move move);

    /** Human-readable name of this state. */
    String getStateName();
}
