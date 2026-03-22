# Ride Sharing System — Architecture

## Overview

A production-style **Low-Level Design (LLD)** of an Uber/Ola-like platform built with Java. The system is modelled after standard system design interview expectations, covering complete ride lifecycle, driver matching, dynamic pricing, and real-time notifications.

---

## High-Level Block Diagram

```mermaid
flowchart TD
    subgraph CLIENT["Client Layer"]
        RA["📱 Rider App"]
        DA["📱 Driver App"]
    end

    subgraph FACADE["Facade Layer (Singleton)"]
        RSS["RideSharingSystem\n(Singleton Facade)"]
    end

    subgraph SERVICES["Service Layer"]
        RS["RideService\n(Core Orchestrator)"]
        DS["DriverService"]
        RIS["RiderService"]
        PS["PaymentService"]
        NS["NotificationService"]
    end

    subgraph STRATEGY["Strategy Layer"]
        direction LR
        subgraph PRICING["Pricing Strategy"]
            BPS["BasePricingStrategy"]
            SPS["SurgePricingStrategy\n(wraps Base)"]
        end
        subgraph MATCHING["Driver Matching Strategy"]
            NDS["NearestDriverStrategy\n(Haversine Distance)"]
            HRS["HighestRatedDriverStrategy"]
        end
    end

    subgraph OBSERVER["Observer Layer"]
        RSO["RideStatusObserver\n(interface)"]
        RNO["RiderNotificationObserver"]
        DNO["DriverNotificationObserver"]
    end

    subgraph MODELS["Domain Models"]
        RIDE["Ride\n(Observable)"]
        RIDER["Rider"]
        DRIVER["Driver"]
        VEHICLE["Vehicle"]
        PAYMENT["Payment"]
        LOC["Location\n(Haversine)"]
    end

    subgraph REPO["Repository Layer (In-Memory)"]
        RIDERREPO["RiderRepository"]
        DRIVERREPO["DriverRepository"]
        RIDEREPO["RideRepository"]
    end

    subgraph ENUMS["Enums"]
        RSE["RideStatus"]
        PSE["PaymentStatus"]
        VTE["VehicleType"]
        PME["PaymentMethod"]
    end

    %% Client to Facade
    RA --> RSS
    DA --> RSS

    %% Facade to Services
    RSS --> RS
    RSS --> DS
    RSS --> RIS
    RSS --> PS
    RSS --> NS

    %% RideService uses strategies
    RS --> PRICING
    RS --> MATCHING

    %% Strategy wrapping
    SPS -->|"decorates"| BPS

    %% Observer connections
    RIDE -->|"notifies"| RSO
    RSO --> RNO
    RSO --> DNO
    RNO -->|"alerts"| RA
    DNO -->|"alerts"| DA

    %% Service to Repository
    RS --> RIDEREPO
    RS --> DRIVERREPO
    DS --> DRIVERREPO
    RIS --> RIDERREPO

    %% Models
    RIDE --> RIDER
    RIDE --> DRIVER
    DRIVER --> VEHICLE
    RIDE --> LOC
    PAYMENT --> RIDE

    %% Service to Models
    RS --> RIDE
    PS --> PAYMENT

    %% Enums used by models
    RIDE -.-> RSE
    PAYMENT -.-> PSE
    VEHICLE -.-> VTE
    PAYMENT -.-> PME
```

---

## Ride Lifecycle State Machine

```mermaid
stateDiagram-v2
    [*] --> REQUESTED : Rider requests ride
    REQUESTED --> DRIVER_ASSIGNED : Driver matched
    DRIVER_ASSIGNED --> DRIVER_EN_ROUTE : Driver accepts
    DRIVER_EN_ROUTE --> RIDE_STARTED : Driver picks up rider
    RIDE_STARTED --> COMPLETED : Destination reached
    REQUESTED --> CANCELLED : Cancelled before assignment
    DRIVER_ASSIGNED --> CANCELLED : Cancelled before pickup
    DRIVER_EN_ROUTE --> CANCELLED : Cancelled before pickup
    COMPLETED --> [*]
    CANCELLED --> [*]
```

---

## Class Hierarchy

```mermaid
classDiagram
    class User {
        <<abstract>>
        -String id
        -String name
        -String email
        -String phone
        -double rating
        +updateRating(double)
    }

    class Rider {
        -Location currentLocation
        -boolean activeRide
        +hasActiveRide() bool
    }

    class Driver {
        -Vehicle vehicle
        -boolean available
        -Location currentLocation
        +isAvailable() bool
        +setAvailable(bool)
    }

    class Vehicle {
        -String id
        -String make
        -String model
        -String licensePlate
        -VehicleType vehicleType
    }

    class Location {
        -double latitude
        -double longitude
        +distanceTo(Location) double
    }

    class Ride {
        -String id
        -RideStatus status
        -double fare
        +updateStatus(RideStatus)
        +addObserver(RideStatusObserver)
        +notifyObservers()
    }

    class Payment {
        -String id
        -double amount
        -PaymentMethod method
        -PaymentStatus status
    }

    class PricingStrategy {
        <<interface>>
        +calculateFare(Location, Location, VehicleType) double
    }

    class DriverMatchingStrategy {
        <<interface>>
        +matchDriver(Location, List~Driver~, VehicleType) Driver
    }

    class RideStatusObserver {
        <<interface>>
        +onRideStatusChanged(Ride)
    }

    User <|-- Rider
    User <|-- Driver
    Driver "1" --> "1" Vehicle
    Ride "1" --> "1" Rider
    Ride "1" --> "0..1" Driver
    Ride "2" --> "1" Location
    Payment "1" --> "1" Ride
    PricingStrategy <|.. BasePricingStrategy
    PricingStrategy <|.. SurgePricingStrategy
    SurgePricingStrategy --> PricingStrategy : decorates
    DriverMatchingStrategy <|.. NearestDriverStrategy
    DriverMatchingStrategy <|.. HighestRatedDriverStrategy
    RideStatusObserver <|.. RiderNotificationObserver
    RideStatusObserver <|.. DriverNotificationObserver
```

---

## Request Flow (Sequence)

```mermaid
sequenceDiagram
    actor Rider
    participant RSS as RideSharingSystem
    participant RS as RideService
    participant MS as MatchingStrategy
    participant PS as PricingStrategy
    participant Ride
    participant Observer as Observers
    participant PaySvc as PaymentService

    Rider->>RSS: bookRide(pickup, dropoff, vehicleType)
    RSS->>RS: requestRide(rider, pickup, dropoff, vehicleType)
    RS->>MS: matchDriver(pickup, availableDrivers, vehicleType)
    MS-->>RS: bestDriver
    RS->>PS: calculateFare(pickup, dropoff, vehicleType)
    PS-->>RS: fare
    RS->>Ride: new Ride(id, rider, driver, fare)
    RS->>Ride: addObserver(RiderNotificationObserver)
    RS->>Ride: addObserver(DriverNotificationObserver)
    RS->>Ride: updateStatus(DRIVER_ASSIGNED)
    Ride->>Observer: notifyObservers()
    Observer-->>Rider: "Driver Ravi assigned!"
    Observer-->>Rider: (Driver notified)

    Note over RS,Ride: ...later...
    RS->>Ride: updateStatus(RIDE_STARTED)
    RS->>Ride: updateStatus(COMPLETED)
    Ride->>Observer: notifyObservers()
    Observer-->>Rider: "Ride complete! Fare ₹350"

    Rider->>PaySvc: processPayment(ride, UPI)
    PaySvc-->>Rider: Payment COMPLETED
```

---

## Design Patterns Used

| Pattern | Where Applied | Purpose |
|---|---|---|
| **Singleton** | `RideSharingSystem` | Single system-wide entry point; safe for concurrent access (double-checked locking) |
| **Facade** | `RideSharingSystem` | Hides service wiring complexity behind a clean API |
| **Strategy** | `PricingStrategy`, `DriverMatchingStrategy` | Swap pricing/matching algorithms at runtime without modifying core logic |
| **Decorator** | `SurgePricingStrategy` | Wraps `BasePricingStrategy` and applies a multiplier — open for extension |
| **Observer** | `Ride` → `RideStatusObserver` | Push-based notifications to rider and driver on every state change |
| **Repository** | `RiderRepository`, `DriverRepository`, `RideRepository` | Decouples business logic from data storage; swappable for a real DB |

---

## Package Structure

```
ridesharingSystem/
├── enums/
│   ├── RideStatus.java          # REQUESTED → COMPLETED/CANCELLED lifecycle
│   ├── PaymentStatus.java       # PENDING, COMPLETED, FAILED, REFUNDED
│   ├── VehicleType.java         # BIKE, AUTO, SEDAN, SUV
│   └── PaymentMethod.java       # CASH, CREDIT_CARD, UPI, WALLET
│
├── models/
│   ├── User.java                # Abstract base: name, email, running-average rating
│   ├── Rider.java               # Extends User + location + activeRide flag
│   ├── Driver.java              # Extends User + vehicle + availability + location
│   ├── Vehicle.java             # make, model, licensePlate, vehicleType
│   ├── Location.java            # lat/lon + Haversine distanceTo()
│   ├── Ride.java                # Core aggregate: state machine + Observable
│   └── Payment.java             # Transaction record per ride
│
├── strategy/
│   ├── PricingStrategy.java          # Interface
│   ├── BasePricingStrategy.java      # baseFare + ratePerKm × distance
│   ├── SurgePricingStrategy.java     # Decorator: base × surgeMultiplier
│   ├── DriverMatchingStrategy.java   # Interface
│   ├── NearestDriverStrategy.java    # Min Haversine distance
│   └── HighestRatedDriverStrategy.java # Max rating
│
├── observer/
│   ├── RideStatusObserver.java         # Interface
│   ├── RiderNotificationObserver.java  # Notifies rider on each status change
│   └── DriverNotificationObserver.java # Notifies driver on each status change
│
├── repository/
│   ├── RiderRepository.java    # In-memory HashMap store
│   ├── DriverRepository.java   # + findAvailableDrivers()
│   └── RideRepository.java     # + findByRiderId / driverId / status
│
├── service/
│   ├── RideService.java         # Core orchestrator: match + fare + state machine
│   ├── DriverService.java       # Registration, location, availability, ratings
│   ├── RiderService.java        # Registration, retrieval, ratings
│   ├── PaymentService.java      # processPayment, refund, gateway simulation
│   └── NotificationService.java # SMS, email, receipts
│
├── RideSharingSystem.java       # Singleton Facade — wires everything
└── Main.java                    # Demo: full lifecycle, surge, cancel, no-driver
```

---

## Key Design Decisions (Interview Discussion Points)

### 1. Why Strategy for Pricing and Matching?
Both algorithms need to be **swappable at runtime** (e.g., enable surge pricing during peak hours, or switch to quality-first matching for premium rides) without changing `RideService`. The Strategy pattern enables open/closed principle compliance.

### 2. Why Observer for Notifications?
Ride status changes must propagate to **multiple parties** (rider, driver, billing) without `Ride` needing to know who's listening. New notification channels (e.g., SMS gateway, analytics) can be added without touching `Ride` or `RideService`.

### 3. Why Decorator for Surge Pricing?
`SurgePricingStrategy` wraps any `PricingStrategy`, so surge logic is **isolated** and doesn't duplicate base fare rules. This makes it composable (e.g., wrap surge on top of a special event pricing strategy).

### 4. Why Repository Pattern?
Decouples business services from storage. In production, swap `HashMap` stores with JPA/Hibernate repositories without touching service code.

### 5. Thread Safety Consideration
`RideSharingSystem` uses **double-checked locking** for thread-safe singleton initialization. In production, driver availability updates would be in a distributed lock or DB transaction to prevent double-booking.

---

## Scalability Notes (For System Design Discussion)

| Concern | Production Solution |
|---|---|
| Driver location updates | Write to Redis Geo (O(log n) radius queries) |
| Driver matching | Use PostGIS or Elasticsearch geo-nearest queries |
| Ride state machine | Distribute across services; use Kafka events for state transitions |
| Surge pricing | ML model or real-time demand/supply ratio from metrics |
| Notifications | Firebase FCM (mobile push), Twilio (SMS), SendGrid (email) |
| Payments | Stripe/Razorpay SDK integration |
| Scalability | Microservices: Ride Service, Driver Service, Payment Service, Notification Service |
