package Pieces;

import Entities.Color;
import Entities.PieceType;
import Strategy.KnightMoveStrategy;

public class Knight extends Piece {
    public Knight(Color color) {
        super(color, PieceType.KNIGHT, new KnightMoveStrategy());
    }
}
