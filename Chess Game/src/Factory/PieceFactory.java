package Factory;

import Entities.Color;
import Entities.PieceType;
import Pieces.*;

/**
 * Factory Pattern — creates the correct concrete Piece for a given type and
 * color.
 * Each piece is pre-wired with its corresponding MoveStrategy.
 */
public class PieceFactory {

    private PieceFactory() {
    } // Utility class — no instantiation

    public static Piece createPiece(PieceType type, Color color) {
        switch (type) {
            case KING:
                return new King(color);
            case QUEEN:
                return new Queen(color);
            case ROOK:
                return new Rook(color);
            case BISHOP:
                return new Bishop(color);
            case KNIGHT:
                return new Knight(color);
            case PAWN:
                return new Pawn(color);
            default:
                throw new IllegalArgumentException("Unknown piece type: " + type);
        }
    }
}
