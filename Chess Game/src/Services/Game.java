package Services;

import Constants.Status;
import Pieces.King;
import Pieces.Piece;

public class Game {
    private Board board;
    // Assuming player1 is always WHITE
    private Player player1;
    // Assuming player2 is always BLACK
    private Player player2;
    boolean isWhiteTurn;
    private Status status;

    // Constructor to initialize the game with two players
    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = Board.getInstance(8); // Initialize board with 8 rows
        this.isWhiteTurn = true;
        this.status = Status.ACTIVE;
    }

    // Start the game
    public void start() {
        // Continue the game till the status is active
        while (this.status == Status.ACTIVE) {
            // Player1 will make the move if it's white's turn
            // else Player2 will make the move
            if (isWhiteTurn) {
                makeMove(new Move(startCell, endCell), player1);
            } else {
                makeMove(new Move(startCell, endCell), player2);
            }
        }
    }

    // Make a move in the game
    public void makeMove(Move move, Player player) {
        // Initial check for a valid move
        // To check if source and destination don't contain the same color pieces
        if (move.isValid()) {
            Piece sourcePiece = move.getStartCell().getPiece();
            // Check if the source piece can be moved or not
            if (sourcePiece.canMove(this.board, move.getStartCell(), move.getEndCell())) {
                Piece destinationPiece = move.getEndCell().getPiece();
                // Check if the destination cell contains some piece
                if (destinationPiece != null) {
                    // If the destination cell contains King and currently white is
                    // playing --> White wins
                    if (destinationPiece instanceof King && isWhiteTurn) {
                        this.status = Status.WHITE_WIN;
                        return;
                    }
                    // If the destination cell contains King and currently Black is
                    // playing --> Black wins
                    if (destinationPiece instanceof King && !isWhiteTurn) {
                        this.status = Status.BLACK_WIN;
                        return;
                    }
                    // Set the destination piece as killed
                    destinationPiece.setKilled(true);
                }
                // Moving the source piece to the destination cell
                move.getEndCell().setPiece(sourcePiece);
                // Setting the source cell to null (means it doesn't have any piece)
                move.getStartCell().setPiece(null);
                // Toggling the turn
                isWhiteTurn = !isWhiteTurn;
            }
        }
    }
}
