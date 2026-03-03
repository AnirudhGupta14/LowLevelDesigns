package Entities;

import Pieces.Piece;

/**
 * Represents a single cell on the chess board.
 * A cell holds a Position and an optional Piece.
 */
public class Cell {
    private final Position position;
    private Piece piece;

    public Cell(Position position) {
        this.position = position;
        this.piece = null;
    }

    public Position getPosition() {
        return position;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public boolean isEmpty() {
        return piece == null;
    }

    /** Display symbol: lowercase = black, uppercase = white, dot = empty. */
    public String getDisplaySymbol() {
        if (isEmpty())
            return ".";
        String symbol = piece.getType().toString();
        return piece.getColor() == Color.WHITE ? symbol : symbol.toLowerCase();
    }
}
