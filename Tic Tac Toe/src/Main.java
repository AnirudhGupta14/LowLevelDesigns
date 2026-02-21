import Enums.Symbol;
import MoveStrategy.SmartMoveStrategy;
import Services.Player;
import Services.TicTacToeGame;

public class Main {
    public static void main(String[] args) {
        Player p1 = new Player("Player 1", Symbol.X, new SmartMoveStrategy());
        Player p2 = new Player("Player 2", Symbol.O, new SmartMoveStrategy());

        TicTacToeGame game = new TicTacToeGame(p1, p2);
        game.start();
    }
}