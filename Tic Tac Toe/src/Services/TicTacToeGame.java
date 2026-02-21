package Services;

import Enums.Symbol;

public class TicTacToeGame {

    private Player[] players;
    private Board board;
    private int currentPlayerIndex;

    public TicTacToeGame(Player p1, Player p2) {
        players = new Player[]{p1, p2};
        board = Board.getInstance();
        board.initialize();
        currentPlayerIndex = 0;
    }


    public void start() {
        while (true) {
            board.printBoard();
            Player current = players[currentPlayerIndex];
            System.out.println(current.getName() + "'s Turn (" + current.getSymbol() + ")");

            int[] move = current.getMove(board, getOpponentSymbol());
            if (move == null) {
                System.out.println("No valid moves left!");
                break;
            }

            board.placeMove(move[0], move[1], current.getSymbol());

            if (board.checkWinner(current.getSymbol())) {
                board.printBoard();
                System.out.println("🎉 " + current.getName() + " wins!");
                break;
            } else if (board.isFull()) {
                board.printBoard();
                System.out.println("It's a draw!");
                break;
            }

            switchTurn();
        }
    }

    private void switchTurn() {
        currentPlayerIndex = 1 - currentPlayerIndex;
    }

    private Symbol getOpponentSymbol() {
        return players[1 - currentPlayerIndex].getSymbol();
    }
}
