package Strategy;

import Entities.Board;
import Entities.Color;
import Entities.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Queen = Rook + Bishop combined.
 * Delegates to both sub-strategies and merges the results.
 */
public class QueenMoveStrategy implements MoveStrategy {

    private final RookMoveStrategy rookStrategy = new RookMoveStrategy();
    private final BishopMoveStrategy bishopStrategy = new BishopMoveStrategy();

    @Override
    public List<Position> getValidMoves(Position from, Board board, Color color) {
        List<Position> moves = new ArrayList<>();
        moves.addAll(rookStrategy.getValidMoves(from, board, color));
        moves.addAll(bishopStrategy.getValidMoves(from, board, color));
        return moves;
    }
}
