package Entities;

import java.util.Objects;

/**
 * Immutable value object representing a board position (0-indexed).
 * row 0 = rank 1 (White's back rank), row 7 = rank 8 (Black's back rank).
 * col 0 = file a, col 7 = file h.
 */
public class Position {
    private final int row;
    private final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    /** Returns true if the position is within the 8x8 board. */
    public boolean isValid() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Position))
            return false;
        Position p = (Position) o;
        return row == p.row && col == p.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    /** Returns algebraic notation, e.g. "e2" */
    @Override
    public String toString() {
        char file = (char) ('a' + col);
        int rank = row + 1;
        return "" + file + rank;
    }
}
