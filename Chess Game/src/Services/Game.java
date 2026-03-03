package Services;

import Command.Move;
import Command.MoveCommand;
import Entities.Board;
import Entities.Color;
import Entities.Position;
import Observer.GameEvent;
import Observer.GameEventObserver;
import Observer.GameEventType;
import Pieces.Piece;
import State.GameState;
import State.RunningState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Singleton Pattern — central game controller.
 *
 * Orchestrates: Board, two Players, GameState (State Pattern),
 * move history Deque (Command Pattern), and event observers (Observer Pattern).
 */
public class Game {
    private static Game instance;

    private final Board board;
    private final Player whitePlayer;
    private final Player blackPlayer;
    private Player currentPlayer;

    private GameState state;
    private final Deque<MoveCommand> moveHistory;
    private final List<GameEventObserver> observers;

    private Game(String whiteName, String blackName) {
        this.board = new Board();
        this.whitePlayer = new Player(whiteName, Color.WHITE);
        this.blackPlayer = new Player(blackName, Color.BLACK);
        this.currentPlayer = whitePlayer;
        this.state = new RunningState();
        this.moveHistory = new ArrayDeque<>();
        this.observers = new ArrayList<>();
    }

    /** Singleton accessor — creates game with given player names on first call. */
    public static synchronized Game getInstance(String whiteName, String blackName) {
        if (instance == null) {
            instance = new Game(whiteName, blackName);
            instance.board.initializeBoard();
        }
        return instance;
    }

    /** Resets the singleton (used for testing or new game). */
    public static synchronized void resetInstance() {
        instance = null;
    }

    // ─────────────────────────────────────────────────────────────
    // Move Execution
    // ─────────────────────────────────────────────────────────────

    /**
     * Primary API: attempt a move from (fromRow, fromCol) to (toRow, toCol).
     * Delegates to the current GameState which validates and executes.
     */
    public void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        Position from = new Position(fromRow, fromCol);
        Position to = new Position(toRow, toCol);

        Piece movingPiece = board.getCell(from).getPiece();
        Piece capturedPiece = board.getCell(to).getPiece();

        if (movingPiece == null) {
            notifyObservers(new GameEvent(GameEventType.INVALID_MOVE,
                    "No piece at " + from));
            return;
        }

        Move move = new Move(from, to, movingPiece, capturedPiece);
        state.handleMove(this, move);
    }

    /**
     * Called by GameState after validation — physically applies the move
     * and pushes it onto the history stack.
     */
    public void executeMove(Move move) {
        MoveCommand cmd = new MoveCommand(move);
        cmd.execute(board);
        moveHistory.push(cmd);
    }

    /** Undo the last move (Command pattern). */
    public void undoLastMove() {
        if (moveHistory.isEmpty()) {
            System.out.println("No moves to undo.");
            return;
        }
        MoveCommand last = moveHistory.pop();
        last.undo(board);
        switchTurn(); // revert turn
        state = new RunningState();
        System.out.println("Undo: " + last.getMove());
    }

    // ─────────────────────────────────────────────────────────────
    // Observer Management
    // ─────────────────────────────────────────────────────────────

    public void addObserver(GameEventObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(GameEvent event) {
        observers.forEach(o -> o.onEvent(event));
    }

    // ─────────────────────────────────────────────────────────────
    // Accessors
    // ─────────────────────────────────────────────────────────────

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getOpponentPlayer() {
        return currentPlayer == whitePlayer ? blackPlayer : whitePlayer;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState s) {
        this.state = s;
    }

    public void switchTurn() {
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
    }

    public boolean isGameOver() {
        return state instanceof State.CheckmateState || state instanceof State.StalemateState;
    }

    public void printBoard() {
        board.printBoard();
    }
}
