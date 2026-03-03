package Strategy;

import Entities.Board;
import Entities.Color;
import Entities.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Bishop moves any number of squares diagonally.
 * Stops at a blocking piece; can capture opponent pieces.
 */
public class BishopMoveStrategy implements MoveStrategy {

    private static final int[][] DIRECTIONS = { { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } };

    @Override
    public List<Position> getValidMoves(Position from, Board board, Color color) {
        List<Position> moves = new ArrayList<>();
        for (int[] dir : DIRECTIONS) {
            int r = from.getRow() + dir[0];
            int c = from.getCol() + dir[1];
            while (true) {
                Position target = new Position(r, c);
                if (!target.isValid())
                    break;
                if (board.getCell(target).isEmpty()) {
                    moves.add(target);
                } else {
                    if (board.getCell(target).getPiece().getColor() != color) {
                        moves.add(target);
                    }
                    break;
                }
                r += dir[0];
                c += dir[1];
            }
        }
        return moves;
    }
}
