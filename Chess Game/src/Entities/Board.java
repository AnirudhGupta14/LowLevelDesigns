package Entities;

import Command.Move;
import Factory.PieceFactory;
import Pieces.Piece;

/**
 * The chess board — an 8x8 grid of Cells.
 * Responsible for:
 * - Initializing the standard starting position
 * - Providing cell lookup
 * - Applying and undoing moves (used by Command pattern)
 */
public class Board {
    private final Cell[][] grid;

    public Board() {
        grid = new Cell[8][8];
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                grid[r][c] = new Cell(new Position(r, c));
            }
        }
    }

    public Cell getCell(Position pos) {
        return grid[pos.getRow()][pos.getCol()];
    }

    public Cell getCell(int row, int col) {
        return grid[row][col];
    }

    /** Applies a move directly to the board (used by MoveCommand). */
    public void applyMove(Move move) {
        Cell from = getCell(move.getFrom());
        Cell to = getCell(move.getTo());
        to.setPiece(from.getPiece());
        from.setPiece(null);
    }

    /** Undoes a move (restores both source and destination cells). */
    public void undoMove(Move move) {
        Cell from = getCell(move.getFrom());
        Cell to = getCell(move.getTo());
        from.setPiece(to.getPiece());
        to.setPiece(move.getCapturedPiece()); // null if no capture
    }

    /** Sets up the standard chess starting position. */
    public void initializeBoard() {
        // Back-rank order
        PieceType[] backRank = {
                PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };

        for (int c = 0; c < 8; c++) {
            // White back rank (row 0) and pawns (row 1)
            grid[0][c].setPiece(PieceFactory.createPiece(backRank[c], Color.WHITE));
            grid[1][c].setPiece(PieceFactory.createPiece(PieceType.PAWN, Color.WHITE));

            // Black back rank (row 7) and pawns (row 6)
            grid[7][c].setPiece(PieceFactory.createPiece(backRank[c], Color.BLACK));
            grid[6][c].setPiece(PieceFactory.createPiece(PieceType.PAWN, Color.BLACK));
        }
    }

    /** Finds the position of a specific color's king. */
    public Position findKing(Color color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = grid[r][c].getPiece();
                if (p != null && p.getType() == PieceType.KING && p.getColor() == color) {
                    return new Position(r, c);
                }
            }
        }
        throw new IllegalStateException("No " + color + " king found on the board!");
    }

    /** Prints the board to the console for debugging. */
    public void printBoard() {
        System.out.println("  a b c d e f g h");
        for (int r = 7; r >= 0; r--) {
            System.out.print((r + 1) + " ");
            for (int c = 0; c < 8; c++) {
                System.out.print(grid[r][c].getDisplaySymbol() + " ");
            }
            System.out.println((r + 1));
        }
        System.out.println("  a b c d e f g h");
    }
}
