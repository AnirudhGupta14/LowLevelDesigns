# Chess Game — Architecture Overview

A full Java chess engine demonstrating **5 classical Design Patterns** with complete rule validation, game state management, and move history.

---

## Block Diagram

```mermaid
graph TB
    subgraph EntryPoint["Entry Point"]
        Main["Main.java<br/>(Scholar's Mate Demo)"]
    end

    subgraph Services["Services"]
        Game["Game<br/>(Singleton)"]
        MV["MoveValidator<br/>(Rule Engine)"]
        Player["Player"]
    end

    subgraph Entities["Entities"]
        Board["Board<br/>(8×8 Grid)"]
        Cell["Cell"]
        Position["Position"]
        Color["Color (enum)"]
        PieceType["PieceType (enum)"]
    end

    subgraph Strategy["Strategy Pattern — Movement"]
        MS["«interface»<br/>MoveStrategy"]
        KingS["KingMoveStrategy"]
        QueenS["QueenMoveStrategy"]
        RookS["RookMoveStrategy"]
        BishopS["BishopMoveStrategy"]
        KnightS["KnightMoveStrategy"]
        PawnS["PawnMoveStrategy"]
    end

    subgraph Pieces["Pieces (Factory Pattern)"]
        PF["PieceFactory"]
        Piece["«abstract»<br/>Piece"]
        King["King"]
        Queen["Queen"]
        Rook["Rook"]
        Bishop["Bishop"]
        Knight["Knight"]
        Pawn["Pawn"]
    end

    subgraph StatePattern["State Pattern — Game Phases"]
        GS["«interface»<br/>GameState"]
        RS["RunningState"]
        CS["CheckState"]
        CM["CheckmateState"]
        SS["StalemateState"]
    end

    subgraph ObserverPattern["Observer Pattern — Events"]
        GEO["«interface»<br/>GameEventObserver"]
        CL["ConsoleLogger"]
        GE["GameEvent"]
        GET["GameEventType (enum)"]
    end

    subgraph CommandPattern["Command Pattern — History"]
        Move["Move"]
        MC["MoveCommand"]
    end

    Main --> Game
    Game --> Board
    Game --> Player
    Game --> GS
    Game --> GEO
    Game --> MC

    GS --> RS & CS & CM & SS
    RS & CS --> MV
    MV --> Board

    Board --> Cell --> Position
    Board --> PF
    PF --> Piece
    Piece --> King & Queen & Rook & Bishop & Knight & Pawn
    Piece --> MS
    MS --> KingS & QueenS & RookS & BishopS & KnightS & PawnS
    QueenS --> RookS & BishopS

    GEO --> CL
    GE --> GET
    MC --> Move
```

---

## Design Patterns Summary

| Pattern | Interface/Class | Purpose |
|---------|----------------|---------|
| **Strategy** | `MoveStrategy` → 6 concrete strategies | Pluggable, piece-specific movement rules |
| **Factory** | `PieceFactory` | Creates pieces pre-wired with the correct strategy |
| **State** | `GameState` → Running/Check/Checkmate/Stalemate | Lifecycle transitions, clean state handling |
| **Observer** | `GameEventObserver` → `ConsoleLogger` | Decoupled event notifications (moves, check, etc.) |
| **Command** | `MoveCommand` (execute/undo) | Move history with full undo support |

---

## Class Diagram

```mermaid
classDiagram
    class MoveStrategy {
        <<interface>>
        +getValidMoves(Position, Board, Color) List~Position~
    }
    class KingMoveStrategy { +getValidMoves() List~Position~ }
    class QueenMoveStrategy { +getValidMoves() List~Position~ }
    class RookMoveStrategy  { +getValidMoves() List~Position~ }
    class BishopMoveStrategy{ +getValidMoves() List~Position~ }
    class KnightMoveStrategy{ +getValidMoves() List~Position~ }
    class PawnMoveStrategy  { +getValidMoves() List~Position~ }
    MoveStrategy <|.. KingMoveStrategy
    MoveStrategy <|.. QueenMoveStrategy
    MoveStrategy <|.. RookMoveStrategy
    MoveStrategy <|.. BishopMoveStrategy
    MoveStrategy <|.. KnightMoveStrategy
    MoveStrategy <|.. PawnMoveStrategy
    QueenMoveStrategy --> RookMoveStrategy
    QueenMoveStrategy --> BishopMoveStrategy

    class Piece {
        <<abstract>>
        -Color color
        -PieceType type
        -MoveStrategy moveStrategy
        +getValidMoves(Position, Board) List~Position~
        +setMoveStrategy(MoveStrategy)
    }
    class King   { +King(Color) }
    class Queen  { +Queen(Color) }
    class Rook   { +Rook(Color) }
    class Bishop { +Bishop(Color) }
    class Knight { +Knight(Color) }
    class Pawn   { +Pawn(Color) }
    Piece <|-- King
    Piece <|-- Queen
    Piece <|-- Rook
    Piece <|-- Bishop
    Piece <|-- Knight
    Piece <|-- Pawn
    Piece --> MoveStrategy

    class PieceFactory {
        +createPiece(PieceType, Color) Piece$
    }
    PieceFactory ..> Piece

    class GameState {
        <<interface>>
        +handleMove(Game, Move)
        +getStateName() String
    }
    class RunningState   { +handleMove(Game, Move) }
    class CheckState     { +handleMove(Game, Move) }
    class CheckmateState { -winner: String; +handleMove(Game, Move) }
    class StalemateState { +handleMove(Game, Move) }
    GameState <|.. RunningState
    GameState <|.. CheckState
    GameState <|.. CheckmateState
    GameState <|.. StalemateState

    class GameEventObserver {
        <<interface>>
        +onEvent(GameEvent)
    }
    class ConsoleLogger { +onEvent(GameEvent) }
    GameEventObserver <|.. ConsoleLogger

    class Move {
        -Position from
        -Position to
        -Piece movedPiece
        -Piece capturedPiece
        +isCapture() boolean
    }
    class MoveCommand {
        -Move move
        +execute(Board)
        +undo(Board)
    }
    MoveCommand --> Move

    class Game {
        -Game instance$
        -Board board
        -Player whitePlayer
        -Player blackPlayer
        -Player currentPlayer
        -GameState state
        -Deque~MoveCommand~ moveHistory
        -List~GameEventObserver~ observers
        +getInstance(String,String) Game$
        +makeMove(int,int,int,int)
        +executeMove(Move)
        +undoLastMove()
        +addObserver(GameEventObserver)
        +notifyObservers(GameEvent)
        +switchTurn()
    }
    Game --> Board
    Game --> GameState
    Game --> GameEventObserver
    Game --> MoveCommand

    class MoveValidator {
        +isValidMove(Move, Board, Color) boolean$
        +isInCheck(Color, Board) boolean$
        +hasAnyValidMove(Color, Board) boolean$
    }
    Game --> MoveValidator
```

---

## Move Validation Flow

```mermaid
flowchart TD
    A["makeMove(from, to)"] --> B{Piece at from?}
    B -- No  --> C["INVALID_MOVE event"]
    B -- Yes --> D["Build Move object"]
    D --> E["state.handleMove(game, move)"]
    E --> F{"Destination in<br/>piece.getValidMoves()?"}
    F -- No  --> G["INVALID_MOVE event"]
    F -- Yes --> H["Simulate move on board"]
    H --> I{"Own king in check<br/>after move?"}
    I -- Yes  --> J["Undo simulation → INVALID_MOVE"]
    I -- No   --> K["executeMove() → push MoveCommand"]
    K --> L{"Opponent king<br/>in check?"}
    L -- Yes  --> M{"Opponent has<br/>legal moves?"}
    L -- No   --> N{"Opponent has<br/>legal moves?"}
    M -- No   --> O["→ CheckmateState ✓"]
    M -- Yes  --> P["→ CheckState"]
    N -- No   --> Q["→ StalemateState (draw)"]
    N -- Yes  --> R["→ RunningState"]
```

---

## Project Structure

| Layer | Package | Key Files |
|-------|---------|-----------|
| Entry Point | *(default)* | `Main.java` |
| Entities | `Entities` | `Board`, `Cell`, `Position`, `Color`, `PieceType` |
| Pieces | `Pieces` | `Piece` (abstract), `King`, `Queen`, `Rook`, `Bishop`, `Knight`, `Pawn` |
| Strategy | `Strategy` | `MoveStrategy` (interface), 6 implementations |
| Factory | `Factory` | `PieceFactory` |
| State | `State` | `GameState`, `RunningState`, `CheckState`, `CheckmateState`, `StalemateState` |
| Observer | `Observer` | `GameEventObserver`, `ConsoleLogger`, `GameEvent`, `GameEventType` |
| Command | `Command` | `Move`, `MoveCommand` |
| Services | `Services` | `Game` (Singleton), `MoveValidator`, `Player` |

---

## Verification Results

| Check | Result |
|-------|--------|
| `javac` compilation (35 files) | ✅ Zero errors |
| Pawn opening moves (e2→e4, e7→e5) | ✅ Logged as MOVE_MADE |
| Bishop and Knight development | ✅ Correct diagonal / L-shape moves |
| Queen long-range move (d1→h5) | ✅ Correct |
| Checkmate detection (Scholar's Mate h5×f7) | ✅ CHECKMATE event fired |
| Invalid move after checkmate | ✅ INVALID_MOVE event fired |
| Undo last move (Command pattern) | ✅ Board restored to pre-checkmate state |
