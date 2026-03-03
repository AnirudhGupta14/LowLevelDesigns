package Services;

import Command.Move;
import Entities.Board;
import Entities.Color;
import Entities.Position;
import Pieces.Piece;

import java.util.List;

/**
 * Validates chess moves — the core rule engine.
 *
 * Responsibilities:
 * 1. Check that a move's destination is in the piece's reachable squares.
 * 2. Simulate the move and verify the moving side's king is not left in check.
 * 3. Detect if a color's king is currently in check.
 * 4. Detect if a color has any legal move remaining (for checkmate/stalemate
 * detection).
 */
public class MoveValidator {

    private MoveValidator() {
    } // Utility class

    /**
     * Returns true if the given move is legal:
     * - The source cell contains a piece of the moving color.
     * - The destination is reachable by the piece's move strategy.
     * - The move does NOT leave the moving side's king in check.
     */
    public static boolean isValidMove(Move move, Board board, Color color) {
        Piece piece = board.getCell(move.getFrom()).getPiece();
        if (piece == null || piece.getColor() != color)
            return false;

        // Check that the destination is in the piece's valid moves
        List<Position> reachable = piece.getValidMoves(move.getFrom(), board);
        if (!reachable.contains(move.getTo()))
            return false;

        // Simulate the move and check for self-check
        board.applyMove(move);
        boolean inCheck = isInCheck(color, board);
        board.undoMove(move);

        return !inCheck;
    }

    /**
     * Returns true if the king of the given color is currently under attack.
     */
    public static boolean isInCheck(Color color, Board board) {
        Position kingPos = board.findKing(color);
        Color opponentColor = color.opposite();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getCell(r, c).getPiece();
                if (piece == null || piece.getColor() != opponentColor)
                    continue;
                Position from = new Position(r, c);
                List<Position> attacks = piece.getValidMoves(from, board);
                if (attacks.contains(kingPos))
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the given color has at least one legal move.
     * Used to distinguish checkmate (in check + no legal move) from
     * stalemate (not in check + no legal move).
     */
    public static boolean hasAnyValidMove(Color color, Board board) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getCell(r, c).getPiece();
                if (piece == null || piece.getColor() != color)
                    continue;
                Position from = new Position(r, c);
                for (Position to : piece.getValidMoves(from, board)) {
                    Piece captured = board.getCell(to).getPiece();
                    Move candidate = new Move(from, to, piece, captured);
                    if (isValidMove(candidate, board, color))
                        return true;
                }
            }
        }
        return false;
    }
}
