package Pieces;

import Movement.MovementStrategy;
import Services.Board;
import Services.Cell;

// Queen Class with movement strategy
public class Queen extends Piece {
    private MovementStrategy strategy;
    public Queen(boolean white) {
        super(white, new QueenMovementStrategy());
    }
    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {
        return strategy.canMove(board, startCell, endCell);
    }
}