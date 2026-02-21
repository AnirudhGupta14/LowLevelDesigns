package Services;

import Enums.Symbol;

public class Board {
    private static Board instance;
    private Symbol[][] board;
    private int size = 3;

    private Board() {
        board = new Symbol[size][size];
        initialize();
    }

    public static Board getInstance() {
        if (instance == null) {
            instance = new Board();
        }
        return instance;
    }

    public void initialize() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = Symbol.EMPTY;
            }
        }
    }

    public void printBoard() {
        System.out.println();
        for (Symbol[] row : board) {
            for (Symbol cell : row) {
                if (cell == Symbol.EMPTY) System.out.print("- ");
                else System.out.print(cell + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public boolean isFree(int row, int col) {
        return board[row][col] == Symbol.EMPTY;
    }

    public boolean placeMove(int row, int col, Symbol symbol) {
        if (isFree(row, col)) {
            board[row][col] = symbol;
            return true;
        }
        return false;
    }

    public boolean isFull() {
        for (Symbol[] row : board) {
            for (Symbol cell : row) {
                if (cell == Symbol.EMPTY) return false;
            }
        }
        return true;
    }

    public boolean checkWinner(Symbol symbol) {
        // Rows and columns
        for (int i = 0; i < size; i++) {
            if ((board[i][0] == symbol && board[i][1] == symbol && board[i][2] == symbol) ||
                    (board[0][i] == symbol && board[1][i] == symbol && board[2][i] == symbol)) {
                return true;
            }
        }
        // Diagonals
        if ((board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol) ||
                (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol)) {
            return true;
        }
        return false;
    }
}