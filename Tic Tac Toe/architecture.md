# 🎮 Tic Tac Toe — Architecture

## Overview

A console-based Tic Tac Toe game built in Java using **Singleton** and **Strategy** design patterns. Supports human vs. smart AI player with a pluggable move strategy.

---

## Block Diagram

```mermaid
graph TB
    subgraph Entry Point
        Main["Main.java"]
    end

    subgraph Services
        TTG["TicTacToeGame"]
        Board["Board (Singleton)"]
        Player["Player"]
    end

    subgraph Enums
        Symbol["Symbol (X, O, EMPTY)"]
    end

    subgraph MoveStrategy ["Move Strategy (Strategy Pattern)"]
        MS["«interface» MoveStrategy"]
        SMS["SmartMoveStrategy"]
    end

    Main -->|creates| Player
    Main -->|creates| TTG
    TTG -->|manages turns| Player
    TTG -->|uses| Board
    Player -->|delegates move to| MS
    SMS -->|implements| MS
    Board -->|uses| Symbol
    Player -->|has| Symbol
    SMS -->|reads & validates| Board
```

---

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Singleton** | `Board` | Only one board instance exists throughout the game |
| **Strategy** | `MoveStrategy` → `SmartMoveStrategy` | Allows swapping between different AI/move strategies without changing `Player` |

---

## Class Diagram

```mermaid
classDiagram
    class TicTacToeGame {
        -Player[] players
        -Board board
        -int currentPlayerIndex
        +start()
        -switchTurn()
        -getOpponentSymbol()
    }

    class Board {
        -static Board instance
        -Symbol[][] board
        -int size
        +static getInstance() Board
        +initialize()
        +printBoard()
        +isFree(row, col) boolean
        +placeMove(row, col, symbol) boolean
        +isFull() boolean
        +checkWinner(symbol) boolean
    }

    class Player {
        -String name
        -Symbol symbol
        -MoveStrategy moveStrategy
        +getName() String
        +getSymbol() Symbol
        +getMove(board, opponentSymbol) int[]
    }

    class MoveStrategy {
        <<interface>>
        +getMove(board, symbol, opponentSymbol) int[]
    }

    class SmartMoveStrategy {
        +getMove(board, symbol, opponentSymbol) int[]
    }

    class Symbol {
        <<enum>>
        X
        O
        EMPTY
    }

    TicTacToeGame --> Board
    TicTacToeGame --> Player
    Player --> MoveStrategy
    SmartMoveStrategy ..|> MoveStrategy
    Player --> Symbol
    Board --> Symbol
```

---

## Component Responsibilities

### `TicTacToeGame`
- Orchestrates the game loop (turn-by-turn play)
- Checks for win/draw conditions after each move
- Switches turns between players

### `Board` (Singleton)
- Maintains the 3×3 grid state
- Validates free cells, places moves, checks for winner
- Prints the current board to the console

### `Player`
- Holds player name, symbol, and a `MoveStrategy`
- Delegates move computation to the strategy

### `MoveStrategy` (Interface)
- Defines `getMove()` contract for all move strategies

### `SmartMoveStrategy`
- Implements a priority-based AI:
  1. **Try to win** — checks if any move leads to a win
  2. **Block opponent** — prevents opponent from winning
  3. **Fallback** — picks the first available cell

---

## Game Flow

```mermaid
flowchart TD
    A[Start Game] --> B[Print Board]
    B --> C[Current Player's Turn]
    C --> D{Get Move via Strategy}
    D --> E[Place Move on Board]
    E --> F{Winner?}
    F -->|Yes| G["🎉 Announce Winner"]
    F -->|No| H{Board Full?}
    H -->|Yes| I["Draw!"]
    H -->|No| J[Switch Turn]
    J --> B
```

---

## Folder Structure

```
Tic Tac Toe/
└── src/
    ├── Main.java
    ├── Enums/
    │   └── Symbol.java
    ├── MoveStrategy/
    │   ├── MoveStrategy.java        (interface)
    │   └── SmartMoveStrategy.java
    └── Services/
        ├── Board.java               (Singleton)
        ├── Player.java
        └── TicTacToeGame.java
```
