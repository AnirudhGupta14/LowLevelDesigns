package Pieces;

import Entities.Color;
import Entities.PieceType;
import Strategy.KingMoveStrategy;

public class King extends Piece {
    public King(Color color) {
        super(color, PieceType.KING, new KingMoveStrategy());
    }
}
