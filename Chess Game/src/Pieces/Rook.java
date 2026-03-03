package Pieces;

import Entities.Color;
import Entities.PieceType;
import Strategy.RookMoveStrategy;

public class Rook extends Piece {
    public Rook(Color color) {
        super(color, PieceType.ROOK, new RookMoveStrategy());
    }
}
