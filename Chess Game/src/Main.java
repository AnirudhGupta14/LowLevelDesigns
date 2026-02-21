import Services.Game;
import Services.Player;

public class Main {

    public static void main(String[] args) {
        Player player1 = new Player("Player1", true); // White
        Player player2 = new Player("Player2", false); // Black
        // Initialize game
        Game chessGame = new Game(player1, player2);
        // Start the game
        chessGame.start();
    }
}