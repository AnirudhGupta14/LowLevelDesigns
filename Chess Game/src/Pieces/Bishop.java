package Pieces;

import Movement.MovementStrategy;
import Services.Board;
import Services.Cell;

// Bishop class with movement strategy
public class Bishop extends Piece {
    private MovementStrategy strategy;
    public Bishop(boolean white) {
        super(white, new BishopMovementStrategy());
    }
    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {
        return strategy.canMove(board, startCell, endCell);
    }
}