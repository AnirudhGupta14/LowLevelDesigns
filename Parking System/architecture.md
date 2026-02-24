# 🅿️ Parking System — Architecture

## Overview

A Java-based multi-floor parking lot system supporting different vehicle types, pluggable parking strategies, multiple payment methods, and admin management. Uses **Strategy**, **Abstract Class Inheritance**, and clean separation of concerns.

---

## Block Diagram

```mermaid
graph TB
    subgraph Entry Point
        Main["Main.java"]
    end

    subgraph Services
        PL["ParkingLot"]
        Floor["Floor"]
        Entrance["Entrance"]
        Exit["Exit"]
        DB["DisplayBoard"]
        Admin["Admin"]
        PA["ParkingAttendant"]
        PT["ParkingTicket"]
    end

    subgraph Vehicles ["Vehicles (Inheritance)"]
        V["Vehicle (abstract)"]
        Car["Car"]
        Motorbike["Motorbike"]
        Truck["Truck"]
    end

    subgraph ParkingSpots ["Parking Spots (Inheritance)"]
        PS["ParkingSpot (abstract)"]
        MS["MiniSpot"]
        CS["CompactSpot"]
        LS["LargeSpot"]
    end

    subgraph ParkingStrategy ["Parking Strategy (Strategy Pattern)"]
        PSI["«interface» ParkingStrategy"]
        NF["NearestFirstParking"]
        FF["FarthestFirstParking"]
    end

    subgraph Payment ["Payment (Strategy Pattern)"]
        PMT["«interface» Payment"]
        Cash["CashPayment"]
        CC["CreditCardPayment"]
    end

    subgraph Constants
        Enums["ParkingLotEnums"]
    end

    Main -->|creates| PL
    Main -->|uses| Admin
    Admin -->|manages| PL

    PL -->|has many| Floor
    PL -->|has many| Entrance
    PL -->|has many| Exit
    PL -->|has one| DB

    Entrance -->|has| PA
    PA -->|creates| PT
    PA -->|uses| PSI
    PSI -->|finds spot in| PL

    NF -->|implements| PSI
    FF -->|implements| PSI

    Floor -->|contains| PS
    MS -->|extends| PS
    CS -->|extends| PS
    LS -->|extends| PS

    Car -->|extends| V
    Motorbike -->|extends| V
    Truck -->|extends| V

    PT -->|links| V
    PT -->|links| PS
    PT -->|calculates fee for| PMT

    Cash -->|implements| PMT
    CC -->|implements| PMT

    V -->|maps to| Enums
    PS -->|uses| Enums
```

---

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Strategy** | `ParkingStrategy` → `NearestFirstParking`, `FarthestFirstParking` | Pluggable algorithms for spot selection |
| **Strategy** | `Payment` → `CashPayment`, `CreditCardPayment` | Flexible payment methods without modifying the core |
| **Inheritance** | `Vehicle` → `Car`, `Motorbike`, `Truck` | Type-specific vehicle behavior with shared base |
| **Inheritance** | `ParkingSpot` → `MiniSpot`, `CompactSpot`, `LargeSpot` | Type-specific spots with shared assign/remove logic |

---

## Class Diagram

```mermaid
classDiagram
    class ParkingLot {
        +List~Floor~ floors
        +List~Entrance~ entrances
        +List~Exit~ exits
        +DisplayBoard displayBoard
        +updateDisplay()
    }

    class Floor {
        +int number
        +Map~SpotType,List~ spots
        +addSpot(spot)
        +removeSpot(spot)
    }

    class Entrance {
        +int id
        +ParkingAttendant attendant
    }

    class Exit {
        +int id
    }

    class DisplayBoard {
        +update(freeSpots)
    }

    class Admin {
        +String name
        +addEntrance(lot, entrance)
        +removeEntrance(lot, entrance)
        +addExit(lot, exit)
        +removeExit(lot, exit)
        +addFloor(lot, floor)
    }

    class ParkingAttendant {
        +String name
        +createTicket(vehicle, lot, strategy) ParkingTicket
        +freeSpot(spot)
    }

    class ParkingTicket {
        +Vehicle vehicle
        +ParkingSpot spot
        +Date startTime
        +calculateFee() double
    }

    class Vehicle {
        <<abstract>>
        +String licenseNumber
        +VehicleType type
        +getSpotTypeForVehicle(vehicle)
    }

    class Car { }
    class Motorbike { }
    class Truck { }

    class ParkingSpot {
        <<abstract>>
        +String id
        +boolean isFree
        +ParkingSpotType type
        +Vehicle vehicle
        +assignVehicle(vehicle)
        +removeVehicle()
    }

    class MiniSpot { }
    class CompactSpot { }
    class LargeSpot { }

    class ParkingStrategy {
        <<interface>>
        +findSpot(lot, vehicle) ParkingSpot
    }

    class NearestFirstParking {
        +findSpot(lot, vehicle) ParkingSpot
    }

    class FarthestFirstParking {
        +findSpot(lot, vehicle) ParkingSpot
    }

    class Payment {
        <<interface>>
        +pay(amount) PaymentStatus
    }

    class CashPayment {
        +pay(amount) PaymentStatus
    }

    class CreditCardPayment {
        +pay(amount) PaymentStatus
    }

    ParkingLot --> Floor
    ParkingLot --> Entrance
    ParkingLot --> Exit
    ParkingLot --> DisplayBoard
    Entrance --> ParkingAttendant
    ParkingAttendant --> ParkingTicket
    ParkingAttendant --> ParkingStrategy
    ParkingTicket --> Vehicle
    ParkingTicket --> ParkingSpot
    Floor --> ParkingSpot
    Car --|> Vehicle
    Motorbike --|> Vehicle
    Truck --|> Vehicle
    MiniSpot --|> ParkingSpot
    CompactSpot --|> ParkingSpot
    LargeSpot --|> ParkingSpot
    NearestFirstParking ..|> ParkingStrategy
    FarthestFirstParking ..|> ParkingStrategy
    CashPayment ..|> Payment
    CreditCardPayment ..|> Payment
```

---

## Component Responsibilities

### Core Services

| Class | Responsibility |
|-------|---------------|
| `ParkingLot` | Top-level container — holds floors, entrances, exits, display board |
| `Floor` | Holds a map of spot-type → list of parking spots |
| `Entrance` | Entry gate with an assigned `ParkingAttendant` |
| `Exit` | Exit gate identified by ID |
| `DisplayBoard` | Shows available spots count per type |
| `Admin` | Manages parking lot structure — adds/removes entrances, exits, floors |
| `ParkingAttendant` | Creates tickets using a `ParkingStrategy`, frees spots on exit |
| `ParkingTicket` | Links vehicle to spot, records start time, calculates time-based fee |

### Vehicle Hierarchy

| Class | Maps To |
|-------|---------|
| `Motorbike` | `MINI` spot |
| `Car` | `COMPACT` spot |
| `Truck` | `LARGE` spot |

### Spot Hierarchy

| Class | Type |
|-------|------|
| `MiniSpot` | For motorbikes |
| `CompactSpot` | For cars |
| `LargeSpot` | For trucks |

---

## Parking Flow

```mermaid
flowchart TD
    A[Vehicle arrives at Entrance] --> B[ParkingAttendant receives vehicle]
    B --> C["ParkingStrategy.findSpot()"]
    C --> D{Spot Available?}
    D -->|No| E["Print: No spot available"]
    D -->|Yes| F["ParkingSpot.assignVehicle()"]
    F --> G[Create ParkingTicket]
    G --> H["ParkingLot.updateDisplay()"]
    H --> I[Vehicle is parked]

    I --> J[Vehicle exits]
    J --> K["ParkingTicket.calculateFee()"]
    K --> L["Payment.pay(fee)"]
    L --> M["ParkingAttendant.freeSpot()"]
    M --> N["ParkingSpot.removeVehicle()"]
    N --> O[Spot is free again]
```

---

## Vehicle → Spot Type Mapping

```mermaid
graph LR
    Motorbike["🏍️ Motorbike"] -->|MINI| MiniSpot["MiniSpot"]
    Car["🚗 Car"] -->|COMPACT| CompactSpot["CompactSpot"]
    Truck["🚛 Truck"] -->|LARGE| LargeSpot["LargeSpot"]
```

---

## Enums Reference

| Enum | Values |
|------|--------|
| `ParkingSpotType` | `MINI`, `COMPACT`, `LARGE` |
| `VehicleType` | `MOTORBIKE`, `CAR`, `TRUCK` |
| `PaymentStatus` | `UNPAID`, `PAID`, `FAILED` |

---

## Folder Structure

```
Parking System/
└── src/
    ├── Main.java
    ├── Constants/
    │   └── ParkingLotEnums.java
    ├── ParkingSpots/
    │   ├── CompactSpot.java
    │   ├── LargeSpot.java
    │   ├── MiniSpot.java
    │   └── ParkingSpot.java       (abstract)
    ├── ParkingStrategy/
    │   ├── FarthestFirstParking.java
    │   ├── NearestFirstParking.java
    │   └── ParkingStrategy.java   (interface)
    ├── PaymentStatus/
    │   ├── CashPayment.java
    │   ├── CreditCardPayment.java
    │   └── Payment.java           (interface)
    ├── Services/
    │   ├── Admin.java
    │   ├── DisplayBoard.java
    │   ├── Entrance.java
    │   ├── Exit.java
    │   ├── Floor.java
    │   ├── ParkingAttendant.java
    │   ├── ParkingLot.java
    │   └── ParkingTicket.java
    └── Vehicles/
        ├── Car.java
        ├── Motorbike.java
        ├── Truck.java
        └── Vehicle.java           (abstract)
```
