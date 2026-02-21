package MoveStrategy;

import Enums.Symbol;
import Services.Board;

public class SmartMoveStrategy implements MoveStrategy {
    public int[] getMove(Board board, Symbol symbol, Symbol opponentSymbol) {
        int size = 3;

        // 1. Try to win
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.isFree(i, j)) {
                    board.placeMove(i, j, symbol);
                    if (board.checkWinner(symbol)) {
                        board.placeMove(i, j, Symbol.EMPTY); // undo
                        return new int[]{i, j};
                    }
                    board.placeMove(i, j, Symbol.EMPTY); // undo
                }
            }
        }

        // 2. Try to block
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.isFree(i, j)) {
                    board.placeMove(i, j, opponentSymbol);
                    if (board.checkWinner(opponentSymbol)) {
                        board.placeMove(i, j, Symbol.EMPTY);
                        return new int[]{i, j};
                    }
                    board.placeMove(i, j, Symbol.EMPTY);
                }
            }
        }

        // 3. Any position
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.isFree(i, j)) return new int[]{i, j};
            }
        }

        return null;
    }
}
