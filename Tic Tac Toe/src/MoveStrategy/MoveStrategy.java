package MoveStrategy;

import Enums.Symbol;
import Services.Board;

public interface MoveStrategy {
    int[] getMove(Board board, Symbol symbol, Symbol opponentSymbol);
}
