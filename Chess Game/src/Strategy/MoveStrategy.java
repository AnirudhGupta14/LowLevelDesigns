package Strategy;

import Entities.Board;
import Entities.Color;
import Entities.Position;

import java.util.List;

/**
 * Strategy Pattern — Movement Strategy interface.
 * Each piece type has its own concrete implementation.
 */
public interface MoveStrategy {
    /**
     * Returns all board positions reachable by this piece from {@code from}.
     * Does NOT check whether the king is left in check after the move.
     * That check is handled by MoveValidator.
     *
     * @param from  Current position of the piece
     * @param board Current board state
     * @param color Color of the moving piece (used to avoid capturing own pieces)
     * @return List of valid destination positions
     */
    List<Position> getValidMoves(Position from, Board board, Color color);
}
