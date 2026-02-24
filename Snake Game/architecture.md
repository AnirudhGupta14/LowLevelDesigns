# 🐍 Snake Game — Architecture

## Overview

A console-based Snake Game built in Java using **Singleton**, **Strategy**, **Factory**, and **Observer** design patterns. Features a grid-based game board, food spawning with bonus mechanics, and pluggable movement strategies.

---

## Block Diagram

```mermaid
graph TB
    subgraph Entry Point
        Main["MainGame (main)"]
    end

    subgraph Services
        SG["SnakeGame"]
        GB["GameBoard (Singleton)"]
    end

    subgraph Food ["Food (Factory Pattern)"]
        FF["FoodFactory"]
        FI["FoodItem (abstract)"]
        NF["NormalFood"]
        BF["BonusFood"]
    end

    subgraph Movement ["Movement (Strategy Pattern)"]
        MS["«interface» MovementStrategy"]
        HMS["HumanMovementStrategy"]
    end

    subgraph Observer ["Observer Pattern"]
        SO["«interface» SnakeObserver"]
    end

    subgraph Helpers
        Pair["Pair (generic)"]
    end

    subgraph Constants
        FT["FoodType (NORMAL, BONUS)"]
    end

    Main -->|creates & controls| SG
    SG -->|uses| GB
    SG -->|delegates movement to| MS
    HMS -->|implements| MS
    SG -->|triggers events via| SO

    FF -->|creates| FI
    NF -->|extends| FI
    BF -->|extends| FI
    FF -->|uses| FT

    SG -->|tracks snake body with| Pair
    MS -->|returns new position as| Pair
```

---

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Singleton** | `GameBoard` | Only one game board should exist per game session |
| **Strategy** | `MovementStrategy` → `HumanMovementStrategy` | Makes movement logic pluggable (human, AI, etc.) |
| **Factory** | `FoodFactory` | Creates `NormalFood` or `BonusFood` based on type string |
| **Observer** | `SnakeObserver` | Notifies listeners on game events (food eaten, game over, board full) |

---

## Class Diagram

```mermaid
classDiagram
    class SnakeGame {
        -GameBoard board
        -Deque~Pair~ snake
        -Set~Pair~ snakeMap
        -Map~Pair,Pair~ foodMap
        -Boolean isAlive
        -Integer score
        -MovementStrategy movementStrategy
        +move(direction) Integer
        +addToFoodMap(key, score)
        +getScore() Integer
        +setMovementStrategy(strategy)
    }

    class GameBoard {
        -static GameBoard instance
        -int width
        -int height
        +static getInstance(w, h) GameBoard
        +getWidth() int
        +getHeight() int
    }

    class FoodItem {
        <<abstract>>
        #int row
        #int column
        #int points
        +getRow() int
        +getColumn() int
        +getPoints() int
    }

    class NormalFood {
        +NormalFood(row, col)
    }

    class BonusFood {
        +BonusFood(row, col)
    }

    class FoodFactory {
        +static createFood(position, type) FoodItem
    }

    class MovementStrategy {
        <<interface>>
        +getNextPosition(currentHead, direction) Pair
    }

    class HumanMovementStrategy {
        +getNextPosition(currentHead, direction) Pair
    }

    class SnakeObserver {
        <<interface>>
        +onFoodEaten()
        +onSpaceFilled()
        +onLossingGame()
    }

    class Pair {
        +U first
        +V second
    }

    class FoodType {
        <<enum>>
        NORMAL
        BONUS
    }

    SnakeGame --> GameBoard
    SnakeGame --> MovementStrategy
    SnakeGame --> Pair
    HumanMovementStrategy ..|> MovementStrategy
    NormalFood --|> FoodItem
    BonusFood --|> FoodItem
    FoodFactory --> FoodItem : creates
    FoodFactory --> FoodType
```

---

## Component Responsibilities

### `MainGame`
- Entry point — reads user commands (`foodAdd`, `moveSnake`, `score`, `quit`)
- Creates `SnakeGame` and drives the game loop via stdin commands

### `SnakeGame`
- Core game logic — maintains snake body (Deque), tracks alive/dead state & score
- Handles movement, boundary checks, self-collision, and food consumption
- Delegates next-position calculation to `MovementStrategy`

### `GameBoard` (Singleton)
- Represents the grid dimensions (width × height)
- Single instance ensures consistent board across the game

### Food System
| Class | Responsibility |
|-------|---------------|
| `FoodItem` _(abstract)_ | Base class with position (row, col) and points |
| `NormalFood` | Standard food — 1 point |
| `BonusFood` | Bonus food — 3 points |
| `FoodFactory` | Creates food by type string using `FoodType` enum |

### `MovementStrategy` (Interface)
- Defines `getNextPosition()` contract
- `HumanMovementStrategy` — computes new head position based on direction input

### `SnakeObserver` (Interface)
- Event hooks: `onFoodEaten()`, `onSpaceFilled()`, `onLossingGame()`

---

## Game Flow

```mermaid
flowchart TD
    A[Start MainGame] --> B[Create SnakeGame]
    B --> C[Read User Command]
    C --> D{Command Type}
    D -->|foodAdd| E[Add food to foodMap]
    D -->|moveSnake| F[Get direction input]
    F --> G[Compute new head via Strategy]
    G --> H{Boundary or Self-Collision?}
    H -->|Yes| I["💀 Game Over (return -1)"]
    H -->|No| J{Food at new head?}
    J -->|Yes| K[Grow snake + Add score]
    J -->|No| L[Move snake - remove tail]
    K --> C
    L --> C
    D -->|score| M[Print current score]
    D -->|quit| N[Set alive = false]
    M --> C
    E --> C
```

---

## Folder Structure

```
Snake Game/
└── src/
    ├── Main.java
    ├── Constants/
    │   └── FoodType.java          (enum)
    ├── Food/
    │   ├── BonusFood.java
    │   ├── FoodFactory.java       (factory)
    │   ├── FoodItem.java          (abstract)
    │   └── NormalFood.java
    ├── Helpers/
    │   └── Pair.java              (generic utility)
    ├── MovementStrategy/
    │   ├── HumanMovementStrategy.java
    │   └── MovementStrategy.java  (interface)
    ├── Observers/
    │   └── SnakeObserver.java     (interface)
    └── Services/
        ├── GameBoard.java         (Singleton)
        ├── MainGame.java          (entry point)
        └── SnakeGame.java
```
