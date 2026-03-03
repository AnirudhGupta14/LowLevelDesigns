# 🏢 Elevator System — Architecture

## Overview

A Java-based multi-elevator system built using **Observer**, **Strategy**, **State**, and **Singleton** design patterns. Supports multiple elevator cars in a building, pluggable dispatch algorithms (SCAN, NearestCar), real-time observer notifications (display board, audit log, maintenance alerts), and SCAN-queue–based floor scheduling.

---

## Block Diagram

```mermaid
graph TB
    subgraph "Entry Point"
        Main["Main.java"]
    end

    subgraph "Building & Floors"
        Building["Building"]
        Floor["Floor (0..N-1)"]
        ExtBtn["ExternalButton (UP / DOWN)"]
    end

    subgraph "Controller (Singleton)"
        EC["ElevatorController"]
    end

    subgraph "Scheduling Strategy"
        SS["«interface» SchedulingStrategy"]
        SCAN["ScanSchedulingStrategy"]
        NC["NearestCarStrategy"]
    end

    subgraph "Elevator Cars"
        E1["Elevator-1"]
        E2["Elevator-2"]
        E3["Elevator-3"]
        IBtn["InternalButton[]"]
        UpQ["upQueue (TreeSet)"]
        DwnQ["downQueue (TreeSet)"]
    end

    subgraph "Observer Pattern"
        EO["«interface» ElevatorObserver"]
        ED["ElevatorDisplay"]
        EL["ElevatorLogger"]
        MA["MaintenanceAlert"]
    end

    subgraph "Entities"
        ER["ElevatorRequest"]
    end

    Main --> Building
    Main --> EC
    Building --> Floor
    Floor --> ExtBtn

    Main --> ER
    EC --> ER
    EC --> SS
    SCAN -->|implements| SS
    NC -->|implements| SS
    EC --> E1
    EC --> E2
    EC --> E3

    E1 --> IBtn
    E1 --> UpQ
    E1 --> DwnQ
    E2 --> IBtn
    E3 --> IBtn

    ED -->|implements| EO
    EL -->|implements| EO
    MA -->|implements| EO

    E1 -->|notifies| EO
    E2 -->|notifies| EO
    E3 -->|notifies| EO
```

---

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Singleton** | `ElevatorController` | One global controller manages all elevators and state |
| **Observer** | `ElevatorObserver` ← `Elevator` | Decouples elevators from display/logging/alert logic |
| **Strategy** | `SchedulingStrategy` → `ScanSchedulingStrategy`, `NearestCarStrategy` | Swap dispatch algorithm at runtime without touching elevator code |
| **State** | `ElevatorState` (IDLE, MOVING, DOOR_OPEN, MAINTENANCE) | Models elevator lifecycle cleanly; prevents invalid transitions |

---

## Class Diagram

```mermaid
classDiagram
    class ElevatorState {
        <<enum>>
        IDLE
        MOVING
        DOOR_OPEN
        MAINTENANCE
    }

    class Direction {
        <<enum>>
        UP
        DOWN
        IDLE
    }

    class ButtonStatus {
        <<enum>>
        PRESSED
        UNPRESSED
    }

    class ExternalButton {
        -int floor
        -Direction direction
        -ButtonStatus status
        +press()
        +reset()
    }

    class InternalButton {
        -int floor
        -ButtonStatus status
        +press()
        +reset()
    }

    class Floor {
        -int floorNumber
        -ExternalButton upButton
        -ExternalButton downButton
        +getFloor() int
    }

    class ElevatorRequest {
        -int sourceFloor
        -int destinationFloor
        -Direction direction
        +getSourceFloor() int
        +getDestinationFloor() int
        +getDirection() Direction
    }

    class ElevatorObserver {
        <<interface>>
        +onFloorArrival(elevator, floor)
        +onDoorOpen(elevator, floor)
        +onDoorClose(elevator, floor)
        +onStateChange(elevator)
    }

    class ElevatorDisplay {
        +onFloorArrival(elevator, floor)
        +onDoorOpen(elevator, floor)
        +onDoorClose(elevator, floor)
        +onStateChange(elevator)
    }

    class ElevatorLogger {
        +onFloorArrival(elevator, floor)
        +onDoorOpen(elevator, floor)
        +onDoorClose(elevator, floor)
        +onStateChange(elevator)
    }

    class MaintenanceAlert {
        +onStateChange(elevator)
    }

    class Elevator {
        -int id
        -int currentFloor
        -Direction direction
        -ElevatorState state
        -TreeSet upQueue
        -TreeSet downQueue
        -List~ElevatorObserver~ observers
        +addObserver(obs)
        +removeObserver(obs)
        +addDestination(floor)
        +step()
        +setMaintenance(bool)
        +hasWork() boolean
    }

    class SchedulingStrategy {
        <<interface>>
        +selectElevator(elevators, request) Elevator
    }

    class ScanSchedulingStrategy {
        +selectElevator(elevators, request) Elevator
    }

    class NearestCarStrategy {
        +selectElevator(elevators, request) Elevator
    }

    class ElevatorController {
        -static ElevatorController instance
        -List~Elevator~ elevators
        -SchedulingStrategy strategy
        +static getInstance() ElevatorController
        +addElevator(elevator)
        +setStrategy(strategy)
        +addGlobalObserver(observer)
        +handleExternalRequest(request)
        +handleInternalRequest(elevatorId, destFloor)
        +run(maxSteps)
        +printStatus()
    }

    class Building {
        -String name
        -int totalFloors
        -List~Floor~ floors
        +getFloor(n) Floor
        +getTotalFloors() int
    }

    ElevatorDisplay ..|> ElevatorObserver
    ElevatorLogger  ..|> ElevatorObserver
    MaintenanceAlert ..|> ElevatorObserver

    ScanSchedulingStrategy ..|> SchedulingStrategy
    NearestCarStrategy     ..|> SchedulingStrategy

    Elevator --> ElevatorState
    Elevator --> Direction
    Elevator --> InternalButton
    Elevator --> ElevatorObserver : notifies

    ElevatorController --> Elevator : manages
    ElevatorController --> SchedulingStrategy : delegates dispatch

    Floor --> ExternalButton
    Building --> Floor

    ExternalButton --> Direction
    ExternalButton --> ButtonStatus
    InternalButton --> ButtonStatus

    ElevatorRequest --> Direction
```

---

## Request Flow

```mermaid
flowchart TD
    A["Passenger presses hall button / cabin button"] --> B["ElevatorRequest created"]
    B --> C["ElevatorController.handleExternalRequest / handleInternalRequest"]
    C --> D["SchedulingStrategy.selectElevator(elevators, request)"]
    D --> E{Strategy type?}
    E -->|SCAN| F["Pick elevator moving toward request in same direction"]
    E -->|NearestCar| G["Pick elevator with minimum floor distance"]
    F --> H["Elevator.addDestination(floor)"]
    G --> H
    H --> I{Floor > currentFloor?}
    I -->|Yes| J["Add to upQueue"]
    I -->|No| K["Add to downQueue"]
    J --> L["Simulation step loop"]
    K --> L
    L --> M["Elevator.step() — move 1 floor"]
    M --> N{At destination floor?}
    N -->|Yes| O["Open doors → notify observers → close doors"]
    N -->|No| M
    O --> P["ElevatorObserver.onDoorOpen / onFloorArrival / onDoorClose"]
    P --> Q["ElevatorDisplay / ElevatorLogger / MaintenanceAlert react"]
```

---

## State Machine — Elevator Lifecycle

```mermaid
stateDiagram-v2
    [*] --> IDLE : Elevator created

    IDLE --> MOVING      : addDestination() called
    MOVING --> DOOR_OPEN : Floor with pending request reached
    DOOR_OPEN --> IDLE   : closeDoors()
    IDLE --> MAINTENANCE : setMaintenance(true)
    MOVING --> MAINTENANCE : setMaintenance(true)
    MAINTENANCE --> IDLE : setMaintenance(false)
```

---

## Component Responsibilities

| Component | Package | Responsibility |
|-----------|---------|----------------|
| `Direction` | `Constants` | Enum: UP, DOWN, IDLE |
| `ElevatorState` | `Constants` | Enum: IDLE, MOVING, DOOR_OPEN, MAINTENANCE |
| `ButtonStatus` | `Constants` | Enum: PRESSED, UNPRESSED |
| `ExternalButton` | `Entities` | Hall-call button on each floor per direction |
| `InternalButton` | `Entities` | Cabin panel button for destination floors |
| `Floor` | `Entities` | Models a physical floor; holds ExternalButtons |
| `ElevatorRequest` | `Entities` | Encapsulates a hall call or cabin destination request |
| `ElevatorObserver` | `ElevatorObserver` | Observer interface: floor arrival, door events, state change |
| `ElevatorDisplay` | `ElevatorObserver` | Concrete observer: prints display board messages |
| `ElevatorLogger` | `ElevatorObserver` | Concrete observer: timestamped audit log |
| `MaintenanceAlert` | `ElevatorObserver` | Concrete observer: fires alert on MAINTENANCE state |
| `SchedulingStrategy` | `SchedulingStrategy` | Strategy interface: selects best elevator for a request |
| `ScanSchedulingStrategy` | `SchedulingStrategy` | SCAN algorithm — prefers in-path elevator; minimizes wait |
| `NearestCarStrategy` | `SchedulingStrategy` | Simple minimum-distance selection |
| `Elevator` | `Services` | Single elevator car; SCAN queues, Observer notifications, State |
| `Building` | `Services` | Registry of floors in the building |
| `ElevatorController` | `Services` | **Singleton** — orchestrates all elevators, dispatch, simulation loop |

---

## Package Structure

```
src/
├── Main.java
├── Constants/
│   ├── Direction.java
│   ├── ElevatorState.java
│   └── ButtonStatus.java
├── Entities/
│   ├── ExternalButton.java
│   ├── InternalButton.java
│   ├── Floor.java
│   └── ElevatorRequest.java
├── ElevatorObserver/
│   ├── ElevatorObserver.java      ← interface
│   ├── ElevatorDisplay.java
│   ├── ElevatorLogger.java
│   └── MaintenanceAlert.java
├── SchedulingStrategy/
│   ├── SchedulingStrategy.java    ← interface
│   ├── ScanSchedulingStrategy.java
│   └── NearestCarStrategy.java
└── Services/
    ├── Elevator.java
    ├── Building.java
    └── ElevatorController.java    ← Singleton
```
