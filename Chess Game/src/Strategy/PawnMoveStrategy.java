package Strategy;

import Entities.Board;
import Entities.Color;
import Entities.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Pawn move strategy:
 * - Moves forward 1 square (2 squares from starting rank).
 * - Captures diagonally forward.
 * - Direction is color-based: WHITE moves towards higher rows, BLACK towards
 * lower.
 */
public class PawnMoveStrategy implements MoveStrategy {

    @Override
    public List<Position> getValidMoves(Position from, Board board, Color color) {
        List<Position> moves = new ArrayList<>();
        // WHITE moves up (increasing row), BLACK moves down (decreasing row)
        int direction = (color == Color.WHITE) ? 1 : -1;
        int startRow = (color == Color.WHITE) ? 1 : 6;

        int nextRow = from.getRow() + direction;

        // 1-square forward
        Position oneStep = new Position(nextRow, from.getCol());
        if (oneStep.isValid() && board.getCell(oneStep).isEmpty()) {
            moves.add(oneStep);

            // 2-square forward from starting rank
            if (from.getRow() == startRow) {
                Position twoStep = new Position(nextRow + direction, from.getCol());
                if (twoStep.isValid() && board.getCell(twoStep).isEmpty()) {
                    moves.add(twoStep);
                }
            }
        }

        // Diagonal captures
        for (int dc : new int[] { -1, 1 }) {
            Position capture = new Position(nextRow, from.getCol() + dc);
            if (capture.isValid() && !board.getCell(capture).isEmpty() &&
                    board.getCell(capture).getPiece().getColor() != color) {
                moves.add(capture);
            }
        }

        return moves;
    }
}
