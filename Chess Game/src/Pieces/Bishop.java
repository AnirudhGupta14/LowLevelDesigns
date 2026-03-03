package Pieces;

import Entities.Color;
import Entities.PieceType;
import Strategy.BishopMoveStrategy;

public class Bishop extends Piece {
    public Bishop(Color color) {
        super(color, PieceType.BISHOP, new BishopMoveStrategy());
    }
}
