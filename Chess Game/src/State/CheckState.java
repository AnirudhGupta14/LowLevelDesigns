package State;

import Command.Move;
import Entities.Color;
import Services.Game;
import Services.MoveValidator;
import Observer.GameEvent;
import Observer.GameEventType;

/**
 * State Pattern — Active game state where the current player's king is in
 * CHECK.
 * Moves that still leave the king in check are rejected.
 * Transitions to: RunningState, CheckmateState.
 */
public class CheckState implements GameState {

    @Override
    public void handleMove(Game game, Move move) {
        Color currentColor = game.getCurrentPlayer().getColor();

        // Validate the move (must relieve the check)
        if (!MoveValidator.isValidMove(move, game.getBoard(), currentColor)) {
            game.notifyObservers(new GameEvent(GameEventType.INVALID_MOVE,
                    "Invalid move while in check: " + move));
            return;
        }

        // Execute
        game.executeMove(move);
        Color nextColor = currentColor.opposite();

        // After the move: check for checkmate, stalemate, or ongoing check
        if (MoveValidator.isInCheck(nextColor, game.getBoard())) {
            if (!MoveValidator.hasAnyValidMove(nextColor, game.getBoard())) {
                game.setState(new CheckmateState(game.getCurrentPlayer().getName()));
                game.notifyObservers(new GameEvent(GameEventType.CHECKMATE,
                        "CHECKMATE! " + game.getCurrentPlayer().getName() + " wins!"));
            } else {
                game.setState(new CheckState());
                game.notifyObservers(new GameEvent(GameEventType.CHECK,
                        game.getOpponentPlayer().getName() + "'s king is in CHECK!"));
            }
        } else if (!MoveValidator.hasAnyValidMove(nextColor, game.getBoard())) {
            game.setState(new StalemateState());
            game.notifyObservers(new GameEvent(GameEventType.STALEMATE, "STALEMATE! It's a draw."));
        } else {
            game.setState(new RunningState());
        }

        game.switchTurn();
    }

    @Override
    public String getStateName() {
        return "CHECK";
    }
}
