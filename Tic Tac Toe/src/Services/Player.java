package Services;

import Enums.Symbol;
import MoveStrategy.MoveStrategy;

public class Player {
    private String name;
    private Symbol symbol;
    private MoveStrategy moveStrategy;

    public Player(String name, Symbol symbol, MoveStrategy moveStrategy) {
        this.name = name;
        this.symbol = symbol;
        this.moveStrategy = moveStrategy;
    }

    public String getName() {
        return name;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public int[] getMove(Board board, Symbol opponentSymbol) {
        return moveStrategy.getMove(board, symbol, opponentSymbol);
    }
}