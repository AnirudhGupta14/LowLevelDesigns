package Pieces;

import Movement.MovementStrategy;
import Services.Board;
import Services.Cell;

public class King extends Piece {
    private MovementStrategy strategy;
    public King(boolean white) {
        super(white, new KingMovementStrategy());
    }
    @Override
    public boolean canMove(Board board, Cell startCell, Cell endCell) {
        return strategy.canMove(board, startCell, endCell);
    }
}