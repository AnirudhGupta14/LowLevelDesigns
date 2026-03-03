import Observer.ConsoleLogger;
import Services.Game;

/**
 * Chess Game — Low Level Design Demo
 *
 * Design Patterns Demonstrated:
 * 1. Strategy — Each piece delegates moves to its MoveStrategy
 * (King/Queen/Rook/Bishop/Knight/Pawn)
 * 2. Factory — PieceFactory creates pieces with the correct strategy
 * 3. State — Game transitions through RunningState → CheckState →
 * CheckmateState
 * 4. Observer — ConsoleLogger receives and prints all game events
 * 5. Command — MoveCommand records history; undoLastMove() reverses the last
 * move
 *
 * Demo sequence: Scholar's Mate (4-move checkmate)
 * 1. e2→e4 (White Pawn)
 * 2. e7→e5 (Black Pawn)
 * 3. f1→c4 (White Bishop)
 * 4. b8→c6 (Black Knight)
 * 5. d1→h5 (White Queen)
 * 6. a7→a6 (Black Pawn — ignores the threat)
 * 7. h5×f7 (White Queen captures f7 — CHECKMATE)
 */
public class Main {

    public static void main(String[] args) {
        // ── Setup ────────────────────────────────────────────────────────────────
        Game.resetInstance(); // ensure clean state
        Game game = Game.getInstance("Alice (White)", "Bob (Black)");
        game.addObserver(new ConsoleLogger());

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║         CHESS GAME — LLD DEMO                ║");
        System.out.println("║   Scholar's Mate in 4 moves (7 half-moves)   ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();
        game.printBoard();
        System.out.println();

        // ── Move 1: e2 → e4 (White Pawn) ───────────────────────────────────────
        printMoveHeader(1, "e2 → e4", "White Pawn advances");
        game.makeMove(1, 4, 3, 4); // row 1 col 4 → row 3 col 4
        game.printBoard();
        System.out.println();

        // ── Move 2: e7 → e5 (Black Pawn) ───────────────────────────────────────
        printMoveHeader(2, "e7 → e5", "Black Pawn responds");
        game.makeMove(6, 4, 4, 4); // row 6 col 4 → row 4 col 4
        game.printBoard();
        System.out.println();

        // ── Move 3: f1 → c4 (White Bishop) ─────────────────────────────────────
        printMoveHeader(3, "f1 → c4", "White Bishop to c4 (targeting f7)");
        game.makeMove(0, 5, 3, 2); // row 0 col 5 → row 3 col 2
        game.printBoard();
        System.out.println();

        // ── Move 4: b8 → c6 (Black Knight) ─────────────────────────────────────
        printMoveHeader(4, "b8 → c6", "Black Knight to c6");
        game.makeMove(7, 1, 5, 2); // row 7 col 1 → row 5 col 2
        game.printBoard();
        System.out.println();

        // ── Move 5: d1 → h5 (White Queen) ──────────────────────────────────────
        printMoveHeader(5, "d1 → h5", "White Queen to h5 (threatens f7)");
        game.makeMove(0, 3, 4, 7); // row 0 col 3 → row 4 col 7
        game.printBoard();
        System.out.println();

        // ── Move 6: a7 → a6 (Black Pawn — misses the threat) ───────────────────
        printMoveHeader(6, "a7 → a6", "Black Pawn to a6 (fatal mistake)");
        game.makeMove(6, 0, 5, 0); // row 6 col 0 → row 5 col 0
        game.printBoard();
        System.out.println();

        // ── Move 7: h5 × f7 (White Queen captures — CHECKMATE) ─────────────────
        printMoveHeader(7, "h5 × f7", "White Queen captures f7 — CHECKMATE!");
        game.makeMove(4, 7, 6, 5); // row 4 col 7 → row 6 col 5
        game.printBoard();
        System.out.println();

        // ── Attempt move after game over ─────────────────────────────────────────
        System.out.println("─── Attempting invalid move after checkmate ───");
        game.makeMove(4, 4, 3, 4);
        System.out.println();

        // ── Undo Demo ────────────────────────────────────────────────────────────
        System.out.println("─── Undo last move (Command Pattern demo) ─────");
        game.undoLastMove();
        game.printBoard();

        System.out.println();
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║         Demo complete!                       ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    private static void printMoveHeader(int num, String move, String desc) {
        System.out.println("─── Move " + num + ": " + move + " (" + desc + ") ───");
    }
}