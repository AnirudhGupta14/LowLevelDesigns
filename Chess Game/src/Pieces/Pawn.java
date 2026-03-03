package Pieces;

import Entities.Color;
import Entities.PieceType;
import Strategy.PawnMoveStrategy;

public class Pawn extends Piece {
    public Pawn(Color color) {
        super(color, PieceType.PAWN, new PawnMoveStrategy());
    }
}
