package Command;

import Entities.Position;
import Pieces.Piece;

/**
 * Command Pattern — represents a single chess move.
 * Stores the source, destination, moving piece, and any captured piece for undo
 * support.
 */
public class Move {
    private final Position from;
    private final Position to;
    private final Piece movedPiece;
    private final Piece capturedPiece; // null if no capture

    public Move(Position from, Position to, Piece movedPiece, Piece capturedPiece) {
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public boolean isCapture() {
        return capturedPiece != null;
    }

    @Override
    public String toString() {
        String capture = isCapture() ? " x" + capturedPiece.getType() : "";
        return movedPiece + ": " + from + " → " + to + capture;
    }
}
