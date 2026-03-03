package Entities;

public enum PieceType {
    KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN;

    @Override
    public String toString() {
        switch (this) {
            case KING:
                return "K";
            case QUEEN:
                return "Q";
            case ROOK:
                return "R";
            case BISHOP:
                return "B";
            case KNIGHT:
                return "N";
            case PAWN:
                return "P";
            default:
                return "?";
        }
    }
}
