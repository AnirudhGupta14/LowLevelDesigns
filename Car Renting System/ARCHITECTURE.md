# Car Renting System — Architecture Overview

A Java-based Low-Level Design of a car renting system demonstrating **4 design patterns** — Strategy, Factory, Observer, Singleton — with clean, interview-friendly layered architecture.

---

## Block Diagram

```mermaid
graph TB
    subgraph EP["Entry Point"]
        Main["Main.java"]
    end

    subgraph Services["Services"]
        RS["ReservationService<br/>(Singleton)"]
        VS["VehicleService"]
        US["UserService"]
    end

    subgraph Models["Models / Entities"]
        Store["Store"]
        Vehicle["Vehicle"]
        Reservation["Reservation"]
        User["User"]
    end

    subgraph Enums["Constants / Enums"]
        VT["VehicleType"]
        VS2["VehicleStatus"]
        ResS["ReservationStatus"]
    end

    subgraph StrategyPat["Strategy Pattern — Pricing"]
        PS["«interface»<br/>PricingStrategy"]
        HP["HourlyPricingStrategy"]
        DP["DailyPricingStrategy"]
    end

    subgraph FactoryPat["Factory Pattern"]
        VF["VehicleFactory"]
    end

    subgraph ObserverPat["Observer Pattern — Notifications"]
        RO["«interface»<br/>ReservationObserver"]
        EN["EmailNotificationObserver"]
    end

    Main --> RS & VS & US
    RS --> PS
    RS --> RO
    PS --> HP & DP
    RO --> EN
    VS --> Store
    Store --> Vehicle
    Vehicle --> VT & VS2
    RS --> Reservation
    Reservation --> ResS & User & Vehicle
    VF --> Vehicle
    Main --> VF
```

---

## Design Patterns Summary

| Pattern    | Class                                | Purpose                                                          |
|------------|--------------------------------------|------------------------------------------------------------------|
| **Strategy**  | `PricingStrategy` → Hourly / Daily | Swap pricing algorithms at runtime without changing service code |
| **Factory**   | `VehicleFactory`                   | Creates `Vehicle` objects with auto-generated IDs                |
| **Observer**  | `ReservationObserver` → Email      | Sends notifications on booking create / cancel / complete        |
| **Singleton** | `ReservationService`               | One global instance managing all reservations                    |

---

## Class Diagram

```mermaid
classDiagram
    class PricingStrategy {
        <<interface>>
        +calculatePrice(Reservation) double
    }
    class HourlyPricingStrategy {
        +calculatePrice(Reservation) double
    }
    class DailyPricingStrategy {
        -HOURS_PER_DAY: int
        -DAILY_DISCOUNT: double
        +calculatePrice(Reservation) double
    }
    PricingStrategy <|.. HourlyPricingStrategy
    PricingStrategy <|.. DailyPricingStrategy

    class ReservationObserver {
        <<interface>>
        +onReservationCreated(Reservation)
        +onReservationCancelled(Reservation)
        +onReservationCompleted(Reservation)
    }
    class EmailNotificationObserver {
        +onReservationCreated(Reservation)
        +onReservationCancelled(Reservation)
        +onReservationCompleted(Reservation)
    }
    ReservationObserver <|.. EmailNotificationObserver

    class Vehicle {
        -vehicleId: String
        -brand: String
        -model: String
        -type: VehicleType
        -pricePerHour: double
        -status: VehicleStatus
        +isAvailable() boolean
        +setStatus(VehicleStatus)
    }

    class VehicleFactory {
        +createVehicle(brand, model, type, price) Vehicle$
    }
    VehicleFactory ..> Vehicle

    class Store {
        -storeId: String
        -location: String
        -vehicles: List~Vehicle~
        +addVehicle(Vehicle)
        +searchAvailableVehicles(VehicleType) List~Vehicle~
    }
    Store --> Vehicle

    class Reservation {
        -reservationId: String
        -user: User
        -vehicle: Vehicle
        -startTime: LocalDateTime
        -endTime: LocalDateTime
        -status: ReservationStatus
        -totalAmount: double
        +getDurationHours() long
    }
    Reservation --> User
    Reservation --> Vehicle

    class ReservationService {
        -instance: ReservationService$
        -reservations: Map
        -observers: List
        -pricingStrategy: PricingStrategy
        +getInstance() ReservationService$
        +setPricingStrategy(PricingStrategy)
        +addObserver(ReservationObserver)
        +createReservation(User, Vehicle, start, end) Reservation
        +cancelReservation(String)
        +completeReservation(String)
    }
    ReservationService --> PricingStrategy
    ReservationService --> ReservationObserver
    ReservationService --> Reservation

    class VehicleService {
        -store: Store
        +addVehicle(Vehicle)
        +searchAvailableVehicles(VehicleType) List~Vehicle~
    }
    VehicleService --> Store

    class UserService {
        -users: Map
        +registerUser(name, email, phone) User
        +getUser(String) User
    }
```

---

## Reservation Lifecycle Flow

```mermaid
flowchart TD
    A["createReservation(user, vehicle, start, end)"] --> B{Vehicle AVAILABLE?}
    B -- No  --> C["Return null + log error"]
    B -- Yes --> D{PricingStrategy set?}
    D -- No  --> E["Throw IllegalStateException"]
    D -- Yes --> F["Calculate price via Strategy"]
    F --> G["Create Reservation — CONFIRMED"]
    G --> H["Set vehicle → RENTED"]
    H --> I["notifyObservers: onReservationCreated"]

    J["cancelReservation(id)"] --> K{Status CONFIRMED?}
    K -- No  --> L["Log error"]
    K -- Yes --> M["Set → CANCELLED"]
    M --> N["Set vehicle → AVAILABLE"]
    N --> O["notifyObservers: onReservationCancelled"]

    P["completeReservation(id)"] --> Q{Status CONFIRMED?}
    Q -- No  --> R["Log error"]
    Q -- Yes --> S["Set → COMPLETED"]
    S --> T["Set vehicle → AVAILABLE"]
    T --> U["notifyObservers: onReservationCompleted"]
```

---

## Project Structure

| Layer      | Package       | Key Files                                                                |
|------------|---------------|--------------------------------------------------------------------------|
| Entry Point| *(default)*   | `Main.java`                                                              |
| Constants  | `constants`   | `VehicleType`, `VehicleStatus`, `ReservationStatus`                      |
| Models     | `models`      | `Vehicle`, `User`, `Reservation`, `Store`                                |
| Strategy   | `strategy`    | `PricingStrategy`, `HourlyPricingStrategy`, `DailyPricingStrategy`       |
| Factory    | `factory`     | `VehicleFactory`                                                         |
| Observer   | `observer`    | `ReservationObserver`, `EmailNotificationObserver`                       |
| Services   | `service`     | `ReservationService` (Singleton), `VehicleService`, `UserService`        |

---

## Verification Results

| Scenario | Result |
|----------|--------|
| Compile 20 files with `javac`                       | ✅ Zero errors |
| Add vehicles via Factory (auto-generated IDs)       | ✅ Working |
| Search available CAR type vehicles                  | ✅ 2 found |
| Create reservation with Hourly pricing (5h × ₹150) | ✅ ₹900 billed |
| Block double-booking of rented vehicle              | ✅ FAILED message shown |
| Cancel reservation → vehicle freed                  | ✅ Email sent, status CANCELLED |
| Swap to Daily pricing at runtime                    | ✅ Strategy pattern works |
| Complete reservation → invoice email               | ✅ Status COMPLETED |
| Vehicle available again after completion            | ✅ Search shows 2 cars |
