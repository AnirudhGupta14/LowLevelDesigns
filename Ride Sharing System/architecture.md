# Ride Sharing System — Architecture

## Overview

A **Low-Level Design (LLD)** of a Ride-Sharing platform (think Ola/Uber) implemented in Java. The system supports both **solo cab booking** and **shared ride pooling**, with pluggable pricing strategies (standard, shared, surge), nearest-driver matching, thread-safe concurrent booking, and a full Observer-based notification/audit trail.

Design principles applied: **SOLID**, **Single Source of Truth (Singleton)**, and five design patterns — **Strategy, Observer, Facade, Factory, and Thread-Safety via ReentrantLock**.

---

## Package Structure

```
src/
├── Main.java                                ← Demo entry point (6 scenarios)
├── enums/
│   ├── RideStatus.java                      ← REQUESTED, DRIVER_ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED
│   ├── DriverStatus.java                    ← AVAILABLE, ON_TRIP, ON_SHARED, OFFLINE
│   ├── RideType.java                        ← SOLO, SHARED
│   └── PaymentStatus.java                   ← PENDING, SUCCESS, FAILED, REFUNDED
├── model/
│   ├── Location.java                        ← Immutable lat/lng value object + distanceTo()
│   ├── User.java                            ← Rider data + wallet (debit/credit)
│   ├── Driver.java                          ← Driver + vehicle + status + ReentrantLock
│   ├── Ride.java                            ← Core aggregate: lifecycle transitions
│   ├── Payment.java                         ← Payment record (PENDING → SUCCESS/FAILED)
│   └── SharedRidePool.java                  ← Thread-safe pool; joinPool() with lock
├── strategy/
│   ├── PricingStrategy.java                 ← Interface: calculateFare(ride, distance)
│   ├── StandardPricingStrategy.java         ← ₹30 base + ₹12/km
│   ├── SharedPricingStrategy.java           ← 60% of standard (40% discount)
│   └── SurgePricingStrategy.java            ← standard × surge multiplier (≥1.0)
├── matching/
│   ├── DriverMatchingStrategy.java          ← Interface: findDriver(ride, candidates)
│   ├── NearestDriverStrategy.java           ← O(n) Euclidean distance scan
│   └── SharedRideMatchingStrategy.java      ← Prefers ON_SHARED drivers; fallback nearest
├── observer/
│   ├── RideObserver.java                    ← Interface: onBooked / onStarted / onCompleted / onCancelled
│   ├── NotificationService.java             ← Push/SMS/email notifications
│   └── RideAuditLogger.java                 ← Structured audit trail for compliance
├── service/
│   ├── DriverService.java                   ← Interface (DIP)
│   ├── DriverServiceImpl.java               ← ConcurrentHashMap driver registry
│   ├── PaymentService.java                  ← Interface (DIP)
│   ├── PaymentServiceImpl.java              ← Wallet-based charge + refund
│   └── RideServiceImpl.java                 ← Singleton; global booking lock + driver lock
└── facade/
    └── RideSharingFacade.java               ← Simplified API; hides all service wiring
```

---

## High-Level Block Diagram

```mermaid
flowchart TD
    User["👤 User / Thread"]
    Facade["RideSharingFacade\n(Facade Pattern)"]
    RideSvc["RideServiceImpl\n(Singleton + Global Lock)"]
    DriverSvc["DriverService\n(Interface)"]
    DriverImpl["DriverServiceImpl\n(ConcurrentHashMap)"]
    PaySvc["PaymentService\n(Interface)"]
    PayImpl["PaymentServiceImpl\n(Wallet Debit)"]
    Pricing["PricingStrategy\n(Interface)"]
    Standard["StandardPricingStrategy"]
    Shared["SharedPricingStrategy"]
    Surge["SurgePricingStrategy"]
    Matching["DriverMatchingStrategy\n(Interface)"]
    Nearest["NearestDriverStrategy"]
    SharedMatch["SharedRideMatchingStrategy"]
    Pool["SharedRidePool\n(ReentrantLock)"]
    DriverLock["Driver\n(ReentrantLock per driver)"]
    Observer["RideObserver\n(Interface)"]
    Notify["NotificationService"]
    Audit["RideAuditLogger"]

    User --> Facade
    Facade --> RideSvc
    RideSvc --> DriverSvc
    RideSvc --> PaySvc
    RideSvc --> Pricing
    RideSvc --> Matching
    RideSvc --> Pool
    RideSvc --> DriverLock
    RideSvc -- "notifyObservers()" --> Observer

    DriverSvc --> DriverImpl
    PaySvc --> PayImpl
    Pricing --> Standard
    Pricing --> Shared
    Pricing --> Surge
    Matching --> Nearest
    Matching --> SharedMatch
    Observer --> Notify
    Observer --> Audit
```

---

## Ride Booking Flow

```mermaid
flowchart TD
    A["bookRide(user, pickup, drop, type)"]
    B{"Acquire global\nbooking lock"}
    C["Fetch available drivers\nDriverService.getAvailableDrivers()"]
    D{"type == SHARED?"}
    E["SharedRideMatchingStrategy\nfindDriver()"]
    F{"Existing pool\nwith capacity?"}
    G["Join existing pool"]
    H["Create new pool\n+ assign driver"]
    I["NearestDriverStrategy\nfindDriver()"]
    J["Acquire Driver lock\nsetStatus(ON_TRIP/ON_SHARED)"]
    K["ride.assignDriver()"]
    L["notifyObservers(onRideBooked)"]
    M["Return Ride object"]

    A --> B --> C --> D
    D -- "Yes" --> E --> F
    F -- "Yes" --> G --> K
    F -- "No"  --> H --> J --> K
    D -- "No"  --> I --> J --> K
    K --> L --> M
```

---

## Ride Completion Flow

```mermaid
sequenceDiagram
    participant App as RideSharingFacade
    participant RS as RideServiceImpl
    participant PS as PricingStrategy
    participant Pay as PaymentService
    participant Obs as Observers

    App->>RS: completeRide(rideId)
    RS->>RS: compute distance(pickup, dropoff)
    RS->>PS: calculateFare(ride, distance)
    PS-->>RS: fare ₹X
    RS->>Pay: processPayment(ride, user, fare)
    Pay->>Pay: user.debit(fare)
    Pay-->>RS: Payment(SUCCESS)
    RS->>RS: driver.setStatus(AVAILABLE)
    RS->>Obs: onRideCompleted(ride, payment)
    RS-->>App: Payment object
```

---

## Locking Strategy

```mermaid
flowchart LR
    T1["Thread-1\n(Book Solo)"]
    T2["Thread-2\n(Book Solo)"]
    GL["Global bookingLock\n(ReentrantLock)"]
    DL["Driver.lock\n(ReentrantLock per driver)"]
    Status["driver.status"]
    Pool["SharedRidePool.lock\n(ReentrantLock)"]

    T1 -- "bookingLock.lock()" --> GL
    T2 -- "bookingLock.lock() ← BLOCKED" --> GL
    GL --> DL
    DL --> Status
    Status -- "setStatus(ON_TRIP)" --> T1
    T1 -- "bookingLock.unlock()" --> GL
    GL -- "T2 acquires lock" --> T2
    T2 -- "no driver available → null" --> Status

    T3["Thread-3\n(Join Pool)"] -- "pool.lock()" --> Pool
    T4["Thread-4\n(Join Pool)"] -- "pool.lock() ← BLOCKED" --> Pool
```

**Two-level locking:**
- **Global booking lock** — prevents two concurrent `bookRide()` calls from selecting the same driver
- **Driver-level lock** — guards atomic status transition (AVAILABLE → ON_TRIP/ON_SHARED)
- **SharedRidePool lock** — guards concurrent `joinPool()` calls on the same pool

---

## Class Diagram (Key Relationships)

```mermaid
classDiagram
    class RideServiceImpl {
        -volatile instance: RideServiceImpl
        -bookingLock: ReentrantLock
        -observers: List~RideObserver~
        -soloStrategy: PricingStrategy
        -sharedStrategy: PricingStrategy
        -matchingStrategy: DriverMatchingStrategy
        +getInstance() RideServiceImpl
        +bookRide(user, pickup, drop, type) Ride
        +startRide(rideId) void
        +completeRide(rideId) Payment
        +cancelRide(rideId) void
        +addObserver(o) void
    }

    class PricingStrategy {
        <<interface>>
        +calculateFare(ride, distance) double
        +getStrategyName() String
    }

    class DriverMatchingStrategy {
        <<interface>>
        +findDriver(ride, candidates) Driver
        +getStrategyName() String
    }

    class RideObserver {
        <<interface>>
        +onRideBooked(ride) void
        +onRideStarted(ride) void
        +onRideCompleted(ride, payment) void
        +onRideCancelled(ride) void
    }

    class Driver {
        -lock: ReentrantLock
        -status: DriverStatus
        +getLock() ReentrantLock
        +setStatus(s) void
        +isAvailable() boolean
    }

    class SharedRidePool {
        -lock: ReentrantLock
        -rides: List~Ride~
        +joinPool(ride) boolean
        +close() void
    }

    class RideSharingFacade {
        +bookSoloRide(user, pickup, drop) Ride
        +bookSharedRide(user, pickup, drop) Ride
        +setSoloPricingStrategy(s) void
        +setMatchingStrategy(s) void
        +addObserver(o) void
    }

    RideServiceImpl --> PricingStrategy
    RideServiceImpl --> DriverMatchingStrategy
    RideServiceImpl --> RideObserver
    RideServiceImpl --> SharedRidePool
    RideServiceImpl --> Driver
    RideSharingFacade --> RideServiceImpl
    PricingStrategy <|.. StandardPricingStrategy
    PricingStrategy <|.. SharedPricingStrategy
    PricingStrategy <|.. SurgePricingStrategy
    DriverMatchingStrategy <|.. NearestDriverStrategy
    DriverMatchingStrategy <|.. SharedRideMatchingStrategy
    RideObserver <|.. NotificationService
    RideObserver <|.. RideAuditLogger
```

---

## Design Patterns Summary

| Pattern | Where Used | Why |
|---|---|---|
| **Singleton** | `RideServiceImpl` | Single source of truth for all bookings; double-checked locking |
| **Strategy** | `PricingStrategy` | Swap Standard / Shared / Surge at runtime without changing RideService |
| **Strategy** | `DriverMatchingStrategy` | Swap Nearest / Shared matching algorithm without touching booking logic |
| **Observer** | `RideObserver`, `NotificationService`, `RideAuditLogger` | Decouple ride events from notification and audit logic |
| **Facade** | `RideSharingFacade` | Single clean API hiding wiring of services, observers, strategies |

---

## SOLID Principles

| Principle | Implementation |
|---|---|
| **S**ingle Responsibility | `User`=data; `RideServiceImpl`=booking logic; `PaymentServiceImpl`=payment; `Driver`=entity |
| **O**pen/Closed | Add new `PricingStrategy` or `RideObserver` without modifying `RideServiceImpl` |
| **L**iskov Substitution | `DriverServiceImpl` fully substitutes `DriverService`; any mock can replace it in tests |
| **I**nterface Segregation | `RideObserver`, `PricingStrategy`, `DriverMatchingStrategy`, `DriverService` are narrow, focused |
| **D**ependency Inversion | `RideServiceImpl` depends on `DriverService`, `PaymentService` interfaces — not on concretions |

---

## Concurrency Guarantees

| Scenario | Mechanism | Guarantee |
|---|---|---|
| Two users book the last driver simultaneously | Global `bookingLock` (ReentrantLock) | Only one booking proceeds; second sees no drivers |
| Status flip AVAILABLE → ON_TRIP | Driver-level `ReentrantLock` inside global lock | Atomic read-check-write; no race on status |
| Two users join a shared pool simultaneously | `SharedRidePool.lock` (ReentrantLock) | Atomic capacity check and add; no overbooking |
| Payment double-charge | User.debit() called inside completed ride (single thread) | One completion per ride; `activeRides.remove()` is ConcurrentHashMap atomic |
| Driver registry lookup | `ConcurrentHashMap` in `DriverServiceImpl` | Thread-safe read/write without external lock |
