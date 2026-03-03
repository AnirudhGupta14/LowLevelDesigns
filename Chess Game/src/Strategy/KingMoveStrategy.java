package Strategy;

import Entities.Board;
import Entities.Color;
import Entities.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * King moves one step in any of the 8 directions.
 * Does NOT handle castling (kept simple for LLD scope).
 */
public class KingMoveStrategy implements MoveStrategy {

    private static final int[][] DIRECTIONS = {
            { -1, -1 }, { -1, 0 }, { -1, 1 },
            { 0, -1 }, { 0, 1 },
            { 1, -1 }, { 1, 0 }, { 1, 1 }
    };

    @Override
    public List<Position> getValidMoves(Position from, Board board, Color color) {
        List<Position> moves = new ArrayList<>();
        for (int[] dir : DIRECTIONS) {
            Position target = new Position(from.getRow() + dir[0], from.getCol() + dir[1]);
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
