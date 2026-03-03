package Strategy;

import Entities.Board;
import Entities.Color;
import Entities.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Knight moves in an L-shape: 2 squares in one direction, 1 in the
 * perpendicular.
 * Knights can jump over pieces.
 */
public class KnightMoveStrategy implements MoveStrategy {

    private static final int[][] OFFSETS = {
            { -2, -1 }, { -2, 1 },
            { -1, -2 }, { -1, 2 },
            { 1, -2 }, { 1, 2 },
            { 2, -1 }, { 2, 1 }
    };

    @Override
    public List<Position> getValidMoves(Position from, Board board, Color color) {
        List<Position> moves = new ArrayList<>();
        for (int[] offset : OFFSETS) {
            Position target = new Position(from.getRow() + offset[0], from.getCol() + offset[1]);
            if (!target.isValid())
                continue;
            if (board.getCell(target).isEmpty() ||
                    board.getCell(target).getPiece().getColor() != color) {
                moves.add(target);
            }
        }
        return moves;
    }
}
