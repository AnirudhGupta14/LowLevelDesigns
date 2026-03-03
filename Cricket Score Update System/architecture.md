# 🏏 Cricket Score Update System — Architecture

## Overview

A Java-based real-time cricket scoreboard built around a **Producer-Consumer** pipeline protected by a **ReentrantReadWriteLock**. Ball-by-ball events flow from a publisher thread through a `LinkedBlockingQueue` to a processor thread. Multiple observers read the scorecard concurrently under shared read-locks while the processor holds the exclusive write-lock.

---

## Block Diagram

```mermaid
graph TB
    subgraph "Entry Point"
        Main["Main.java"]
    end

    subgraph "Singleton Controller"
        MC["MatchController (Singleton)\nLinkedBlockingQueue&lt;Ball&gt; capacity=50"]
    end

    subgraph "Producer Thread"
        SP["ScorePublisher\n(Runnable)"]
    end

    subgraph "Shared Queue"
        BQ["🔗 LinkedBlockingQueue&lt;Ball&gt;\nput() blocks when full\ntake() blocks when empty"]
    end

    subgraph "Consumer Thread"
        SPC["ScoreProcessor\n(Runnable)"]
    end

    subgraph "ScoreCard  (ReentrantReadWriteLock)"
        SC["ScoreCard\n── writeLock (exclusive)\n── readLock  (shared)"]
    end

    subgraph "Observer Pattern (readLock)"
        SO["«interface» ScoreObserver"]
        SBD["ScoreBoardDisplay\nonBallProcessed()"]
        CG["CommentaryGenerator\nonBallProcessed()"]
        ST["StatisticsTracker\nonBallProcessed()"]
    end

    subgraph "Entities"
        M["Match"]
        T1["Team (batting)"]
        T2["Team (bowling)"]
        P["Player"]
        B["Ball (event unit)"]
        O["Over"]
    end

    Main --> MC
    MC -->|"startMatch()"| M
    MC -->|"new thread"| SP
    MC -->|"new thread"| SPC

    SP -->|"put(Ball)"| BQ
    BQ -->|"take(Ball)"| SPC

    SPC -->|"writeLock → update(ball)"| SC
    SPC -->|"notifyAll observers"| SO

    SBD -->|"implements"| SO
    CG  -->|"implements"| SO
    ST  -->|"implements"| SO

    SBD -->|"readLock → getScoreSummary()"| SC
    CG  -->|"reads ball fields"| B
    ST  -->|"readLock → getOvers()"| SC

    M --> T1
    M --> T2
    M --> SC
    T1 --> P
    T2 --> P
    SC --> O
    O --> B
```

---

## Producer-Consumer + Lock Flow (per delivery)

```mermaid
sequenceDiagram
    participant PUB as ScorePublisher (Producer)
    participant Q   as LinkedBlockingQueue
    participant PRO as ScoreProcessor (Consumer)
    participant SC  as ScoreCard (writeLock)
    participant OBS as Observers (readLock)

    PUB->>Q: put(ball) 🔒 blocks if full
    Note over Q: Ball waits in queue
    Q-->>PRO: take() 🔒 blocks if empty
    PRO->>SC: writeLock.lock()
    SC-->>PRO: exclusive access granted
    PRO->>SC: update(ball) — runs/wickets updated
    PRO->>SC: writeLock.unlock()
    Note over SC: Write released
    PRO->>OBS: onBallProcessed(ball, scoreCard)
    OBS->>SC: readLock.lock() (multiple observers concurrently)
    OBS-->>OBS: read score / run rate / overs
    OBS->>SC: readLock.unlock()
```

---

## ReentrantReadWriteLock Usage

```mermaid
graph LR
    subgraph "Writers (exclusive)"
        PRO["ScoreProcessor\nwriteLock.lock()\n scoreCard.update(ball)\nwriteLock.unlock()"]
    end

    subgraph "Readers (concurrent)"
        D["ScoreBoardDisplay\nreadLock.lock()\ngetScoreSummary()\nreadLock.unlock()"]
        S["StatisticsTracker\nreadLock.lock()\ngetOvers()\nreadLock.unlock()"]
    end

    SC["ScoreCard\nReentrantReadWriteLock"]

    PRO -->|"exclusive write"| SC
    D   -->|"shared read"| SC
    S   -->|"shared read"| SC
```

> **Rule**: While `writeLock` is held → ALL readers block.  
> While only `readLock`s are held → multiple readers proceed simultaneously.

---

## Class Diagram

```mermaid
classDiagram
    class BallType {
        <<enum>>
        DOT ONE TWO THREE FOUR SIX WIDE NO_BALL WICKET
    }

    class WicketType {
        <<enum>>
        BOWLED CAUGHT LBW RUN_OUT STUMPED HIT_WICKET NONE
    }

    class MatchState {
        <<enum>>
        NOT_STARTED FIRST_INNINGS INNINGS_BREAK SECOND_INNINGS COMPLETED
    }

    class Ball {
        -int overNumber
        -int ballNumber
        -BallType type
        -WicketType wicketType
        -int runsScored
        -String batsmanId
        -String bowlerId
        +isLegalDelivery() boolean
    }

    class Over {
        -int overNumber
        -List~Ball~ balls
        -int runsInOver
        -int wicketsInOver
        +addBall(ball)
        +isComplete() boolean
        +legalBallsCount() int
    }

    class ScoreCard {
        -String battingTeamName
        -ReentrantReadWriteLock lock
        -int totalRuns
        -int wickets
        -int legalBallsDelivered
        -List~Over~ overs
        +update(ball)          writeLock
        +getScoreSummary()     readLock
        +getCurrentRunRate()   readLock
        +getOvers()            readLock
        +getLock() ReadWriteLock
    }

    class Player {
        -String id, name
        -int runsScored, ballsFaced
        -int fours, sixes
        -int wicketsTaken, runsConceded
        +addRuns(r)
        +getStrikeRate() double
        +getBowlingFigures() String
    }

    class Team {
        -String name
        -List~Player~ players
        +getPlayer(id) Player
    }

    class Match {
        -String matchId
        -Team team1, team2
        -int totalOvers
        -MatchState state
        -ScoreCard firstInningsCard
        -ScoreCard secondInningsCard
        +startFirstInnings()
        +startSecondInnings()
        +completeMatch()
        +getCurrentCard() ScoreCard
    }

    class ScoreObserver {
        <<interface>>
        +onBallProcessed(ball, scoreCard)
    }

    class ScoreBoardDisplay {
        +onBallProcessed(ball, scoreCard)
    }

    class CommentaryGenerator {
        +onBallProcessed(ball, scoreCard)
    }

    class StatisticsTracker {
        +onBallProcessed(ball, scoreCard)
    }

    class ScorePublisher {
        -BlockingQueue~Ball~ ballQueue
        -int totalOvers
        +run()   PRODUCER thread
        +stop()
    }

    class ScoreProcessor {
        -BlockingQueue~Ball~ ballQueue
        -Match match
        -List~ScoreObserver~ observers
        +run()   CONSUMER thread
        +stop()
    }

    class MatchController {
        -static MatchController instance
        -LinkedBlockingQueue~Ball~ ballQueue
        -List~ScoreObserver~ observers
        -Match activeMatch
        +getInstance() MatchController
        +addObserver(obs)
        +startMatch(match)
        +startSecondInnings(batsman, bowler)
        +awaitInningsCompletion()
        +completeMatch()
    }

    Ball --> BallType
    Ball --> WicketType
    Over --> Ball
    ScoreCard --> Over
    Match --> Team
    Match --> ScoreCard
    Team --> Player

    ScoreBoardDisplay   ..|> ScoreObserver
    CommentaryGenerator ..|> ScoreObserver
    StatisticsTracker   ..|> ScoreObserver

    MatchController --> ScorePublisher : starts thread
    MatchController --> ScoreProcessor : starts thread
    MatchController --> ScoreObserver  : manages list
    ScorePublisher --> Ball           : creates & puts
    ScoreProcessor --> Ball           : takes
    ScoreProcessor --> ScoreCard      : writeLock → update
    ScoreProcessor --> ScoreObserver  : notifies
```

---

## Match Lifecycle State Machine

```mermaid
stateDiagram-v2
    [*] --> NOT_STARTED : Match created
    NOT_STARTED --> FIRST_INNINGS  : startFirstInnings()
    FIRST_INNINGS --> INNINGS_BREAK : allOversComplete or 10 wickets
    INNINGS_BREAK --> SECOND_INNINGS : startSecondInnings()
    SECOND_INNINGS --> COMPLETED : allOversComplete or 10 wickets or target chased
    COMPLETED --> [*]
```

---

## Package Structure

```
src/
├── Main.java
├── Constants/
│   ├── BallType.java         (DOT, ONE, TWO, THREE, FOUR, SIX, WIDE, NO_BALL, WICKET)
│   ├── WicketType.java       (BOWLED, CAUGHT, LBW, RUN_OUT, STUMPED, HIT_WICKET, NONE)
│   └── MatchState.java       (NOT_STARTED → FIRST_INNINGS → INNINGS_BREAK → SECOND_INNINGS → COMPLETED)
├── Entities/
│   ├── Player.java           ← batting + bowling stats (synchronized methods)
│   ├── Team.java             ← squad of players
│   ├── Ball.java             ← atomic event unit flowing through BlockingQueue
│   ├── Over.java             ← 6-legal-ball grouping
│   ├── ScoreCard.java        ← ⭐ ReentrantReadWriteLock (exclusive write / shared read)
│   └── Match.java            ← lifecycle, 2 ScoreCards, result announcement
├── Observer/
│   ├── ScoreObserver.java        ← interface: onBallProcessed(ball, scoreCard)
│   ├── ScoreBoardDisplay.java    ← readLock → live score line
│   ├── CommentaryGenerator.java  ← ball-by-ball commentary text
│   └── StatisticsTracker.java    ← readLock → over-end summary
├── Producer/
│   └── ScorePublisher.java   ← ⭐ PRODUCER — put(Ball) → LinkedBlockingQueue
├── Consumer/
│   └── ScoreProcessor.java   ← ⭐ CONSUMER — take(Ball) → writeLock → update → notify
└── Services/
    └── MatchController.java  ← ⭐ Singleton — owns queue, starts threads, manages lifecycle
```

---

## Component Responsibilities

| Component | Role |
|-----------|------|
| `Ball` | Atomic data unit — flows through the queue |
| `ScoreCard` | Shared mutable state — guarded by `ReentrantReadWriteLock` |
| `ScorePublisher` | **Producer** — generates `Ball` events, calls `queue.put()` |
| `ScoreProcessor` | **Consumer** — calls `queue.take()`, holds `writeLock`, notifies observers |
| `ScoreObserver` (×3) | Read scorecard under `readLock` — multiple run concurrently |
| `MatchController` | **Singleton** — creates the `LinkedBlockingQueue(50)`, starts/joins threads |
| `Match` | Owns innings state and both `ScoreCard`s |
