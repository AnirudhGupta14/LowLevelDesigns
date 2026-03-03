package Pieces;

import Entities.Board;
import Entities.Color;
import Entities.Position;
import Entities.PieceType;
import Strategy.MoveStrategy;

import java.util.List;

/**
 * Abstract base class for all chess pieces.
 * Holds: Color, PieceType, and a pluggable MoveStrategy (Strategy Pattern).
 */
public abstract class Piece {
    protected final Color color;
    protected final PieceType type;
    protected MoveStrategy moveStrategy;

    protected Piece(Color color, PieceType type, MoveStrategy moveStrategy) {
        this.color = color;
        this.type = type;
        this.moveStrategy = moveStrategy;
    }

    public Color getColor() {
        return color;
    }

    public PieceType getType() {
        return type;
    }

    /**
     * Returns all squares this piece can move to from {@code from}.
     * Delegates all logic to the MoveStrategy.
     */
    public List<Position> getValidMoves(Position from, Board board) {
        return moveStrategy.getValidMoves(from, board, color);
    }

    /** Allows swapping the movement strategy at runtime. */
    public void setMoveStrategy(MoveStrategy strategy) {
        this.moveStrategy = strategy;
    }

    @Override
    public String toString() {
        return color + "_" + type;
    }
}
