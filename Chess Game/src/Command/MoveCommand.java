package Command;

import Entities.Board;

/**
 * Command Pattern — encapsulates a chess move with execute() and undo()
 * capability.
 * Allows full move history and undo functionality.
 */
public class MoveCommand {
    private final Move move;

    public MoveCommand(Move move) {
        this.move = move;
    }

    /**
     * Executes the move: places the moving piece at the destination,
     * clears the source, (the captured piece is already removed by
     * Board.movePiece).
     */
    public void execute(Board board) {
        board.applyMove(move);
    }

    /**
     * Undoes the move: restores the moving piece to its source and
     * places the captured piece (if any) back at the destination.
     */
    public void undo(Board board) {
        board.undoMove(move);
    }

    public Move getMove() {
        return move;
    }

    @Override
    public String toString() {
        return "MoveCommand{" + move + "}";
    }
}
