package State;

import Command.Move;
import Entities.Color;
import Services.Game;
import Services.MoveValidator;
import Observer.GameEvent;
import Observer.GameEventType;

/**
 * State Pattern — Normal running game state.
 * Validates and applies moves, then transitions state based on board analysis.
 * Transitions to: CheckState, CheckmateState, StalemateState, or stays Running.
 */
public class RunningState implements GameState {

    @Override
    public void handleMove(Game game, Move move) {
        Color currentColor = game.getCurrentPlayer().getColor();

        if (!MoveValidator.isValidMove(move, game.getBoard(), currentColor)) {
            game.notifyObservers(new GameEvent(GameEventType.INVALID_MOVE,
                    "Invalid move: " + move));
            return;
        }

        // Execute the move
        game.executeMove(move);
        Color nextColor = currentColor.opposite();

        // Notify move made
        String captureInfo = move.isCapture() ? " (captures " + move.getCapturedPiece().getType() + ")" : "";
        game.notifyObservers(new GameEvent(GameEventType.MOVE_MADE,
                game.getCurrentPlayer().getName() + " plays: " + move + captureInfo));

        // Detect end conditions
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
        }

        game.switchTurn();
    }

    @Override
    public String getStateName() {
        return "RUNNING";
    }
}
