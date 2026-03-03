package Pieces;

import Entities.Color;
import Entities.PieceType;
import Strategy.QueenMoveStrategy;

public class Queen extends Piece {
    public Queen(Color color) {
        super(color, PieceType.QUEEN, new QueenMoveStrategy());
    }
}
