package Observer;

/**
 * Concrete Observer — logs all game events to the console with colored output.
 */
public class ConsoleLogger implements GameEventObserver {

    // ANSI color codes for terminal output
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";
    private static final String BLUE = "\u001B[34m";

    @Override
    public void onEvent(GameEvent event) {
        String color;
        switch (event.getType()) {
            case MOVE_MADE:
                color = GREEN;
                break;
            case PIECE_CAPTURED:
                color = CYAN;
                break;
            case CHECK:
                color = YELLOW;
                break;
            case CHECKMATE:
                color = RED;
                break;
            case STALEMATE:
                color = BLUE;
                break;
            case INVALID_MOVE:
                color = RED;
                break;
            case GAME_STARTED:
                color = GREEN;
                break;
            default:
                color = RESET;
                break;
        }
        System.out.println(color + event + RESET);
    }
}
