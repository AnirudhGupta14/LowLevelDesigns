# 🎬 Movie Seat Booking System — Architecture

## Overview

A Java-based Movie Seat Booking System with **seat locking & timeout**, **multiple payment methods**, **multi-theatre/multi-screen** support, and **observer notifications**. Built using **Singleton**, **Strategy**, **Factory**, and **Observer** design patterns with thread-safe booking operations.

---

## Block Diagram

```mermaid
graph TB
    subgraph Entry Point
        Main["Main.java"]
    end

    subgraph Models
        Movie["Movie"]
        User["User"]
        Screen["Screen"]
        Show["Show"]
        Theatre["Theatre"]
        Booking["Booking"]
    end

    subgraph Seats ["Seats (Inheritance + Factory)"]
        Seat["Seat (abstract)"]
        SS["SilverSeat"]
        GS["GoldSeat"]
        PS["PlatinumSeat"]
        SF["SeatFactory"]
    end

    subgraph Services
        BM["BookingManager (Singleton)"]
        SLM["SeatLockManager (Singleton)"]
        SM["ShowManager"]
        TM["TheatreManager"]
    end

    subgraph Payment ["Payment (Strategy Pattern)"]
        PMT["«interface» Payment"]
        Cash["CashPayment"]
        CC["CreditCardPayment"]
        UPI["UPIPayment"]
    end

    subgraph Observer ["Observer Pattern"]
        BO["«interface» BookingObserver"]
        NS["NotificationService"]
    end

    subgraph Enums
        SC["SeatCategory"]
        BS["BookingStatus"]
        PS2["PaymentStatus"]
    end

    Main -->|creates| Theatre
    Main -->|creates| Movie
    Main -->|uses| TM
    Main -->|uses| SM
    Main -->|uses| BM

    Theatre -->|has many| Screen
    Screen -->|has many| Seat
    SF -->|creates| Seat
    SS -->|extends| Seat
    GS -->|extends| Seat
    PS -->|extends| Seat

    Show -->|links| Movie
    Show -->|links| Screen
    Show -->|tracks availability of| Seat

    SM -->|manages| Show
    TM -->|manages| Theatre

    BM -->|uses| SLM
    BM -->|creates| Booking
    BM -->|notifies| BO
    NS -->|implements| BO

    SLM -->|locks/unlocks| Seat
    SLM -->|auto-expires via| Timer["⏰ Background Timer"]

    Booking -->|references| Show
    Booking -->|references| User
    Booking -->|contains| Seat

    BM -->|uses| PMT
    Cash -->|implements| PMT
    CC -->|implements| PMT
    UPI -->|implements| PMT
```

---

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Singleton** | `BookingManager`, `SeatLockManager` | Single coordinator for bookings and locks across the system |
| **Strategy** | `Payment` → `CashPayment`, `CreditCardPayment`, `UPIPayment` | Pluggable payment methods without modifying booking logic |
| **Factory** | `SeatFactory` | Creates `SilverSeat`, `GoldSeat`, `PlatinumSeat` based on category enum |
| **Observer** | `BookingObserver` → `NotificationService` | Decoupled notifications on booking confirmed/expired/cancelled |
| **Inheritance** | `Seat` → `SilverSeat`, `GoldSeat`, `PlatinumSeat` | Each category has its own pricing with shared base behavior |

---

## Class Diagram

```mermaid
classDiagram
    class Movie {
        -String id
        -String title
        -int durationMinutes
        -String genre
    }

    class User {
        -String id
        -String name
        -String email
    }

    class Seat {
        <<abstract>>
        -String id
        -int row, col
        -SeatCategory category
        -double price
    }

    class SilverSeat { }
    class GoldSeat { }
    class PlatinumSeat { }

    class Screen {
        -String id
        -String name
        -List~Seat~ seats
        +addSeat(seat)
    }

    class Theatre {
        -String id
        -String name
        -String city
        -List~Screen~ screens
        +addScreen(screen)
    }

    class Show {
        -String id
        -Movie movie
        -Screen screen
        -LocalDateTime startTime
        -Map~Seat,Boolean~ seatAvailability
        +getAvailableSeats()
        +markSeatBooked(seat)
        +markSeatAvailable(seat)
    }

    class Booking {
        -String id
        -Show show
        -List~Seat~ seats
        -User user
        -BookingStatus status
        -double totalAmount
        -LocalDateTime bookingTime
    }

    class SeatLockManager {
        -static SeatLockManager instance
        -Duration lockTimeout
        -Map~String,SeatLock~ locks
        -ScheduledExecutorService scheduler
        +lockSeats(show, seats, user) boolean
        +unlockSeats(show, seats)
        +isLocked(show, seat) boolean
        +getRemainingLockSeconds(show, seat) long
        +forceExpireAllLocks(show)
    }

    class BookingManager {
        -static BookingManager instance
        -SeatLockManager seatLockManager
        -List~BookingObserver~ observers
        +initiateBooking(show, seats, user) Booking
        +confirmBooking(booking, payment) boolean
        +cancelBooking(booking)
    }

    class ShowManager {
        -List~Show~ shows
        +addShow(show)
        +getShowsForMovie(movie)
        +getShowsInTheatre(theatre)
    }

    class TheatreManager {
        -List~Theatre~ theatres
        +addTheatre(theatre)
        +getTheatresByCity(city)
    }

    class Payment {
        <<interface>>
        +pay(amount) PaymentStatus
    }

    class CashPayment { }
    class CreditCardPayment { }
    class UPIPayment { }

    class BookingObserver {
        <<interface>>
        +onBookingConfirmed(booking)
        +onBookingExpired(booking)
        +onBookingCancelled(booking)
    }

    class NotificationService { }

    class SeatFactory {
        +static createSeat(category, id, row, col) Seat
    }

    Theatre --> Screen
    Screen --> Seat
    Show --> Movie
    Show --> Screen
    Booking --> Show
    Booking --> User
    Booking --> Seat
    SilverSeat --|> Seat
    GoldSeat --|> Seat
    PlatinumSeat --|> Seat
    SeatFactory --> Seat : creates
    BookingManager --> SeatLockManager
    BookingManager --> BookingObserver : notifies
    BookingManager --> Payment : uses
    NotificationService ..|> BookingObserver
    CashPayment ..|> Payment
    CreditCardPayment ..|> Payment
    UPIPayment ..|> Payment
```

---

## Seat Locking & Timeout Flow

```mermaid
flowchart TD
    A[User selects seats] --> B["BookingManager.initiateBooking()"]
    B --> C{All seats available?}
    C -->|No| D["❌ Booking rejected"]
    C -->|Yes| E["SeatLockManager.lockSeats()"]
    E --> F{Lock acquired?}
    F -->|No — locked by another user| D
    F -->|Yes| G["Create PENDING Booking"]
    G --> H["⏰ Timeout timer starts"]

    H --> I{User completes payment in time?}
    I -->|Yes| J["BookingManager.confirmBooking()"]
    J --> K["Verify locks still valid"]
    K --> L{Locks expired?}
    L -->|Yes| M["❌ Booking EXPIRED\nSeats released"]
    L -->|No| N["Payment.pay()"]
    N --> O{Payment success?}
    O -->|No| P["Payment failed\nBooking stays PENDING"]
    O -->|Yes| Q["Mark seats BOOKED in Show"]
    Q --> R["Unlock seats + Status = CONFIRMED"]
    R --> S["Notify observers"]

    I -->|No — timeout| T["Background scheduler\ncleanupExpiredLocks()"]
    T --> U["Locks auto-removed\nSeats available again"]
```

---

## Booking Flow

```mermaid
flowchart LR
    A["🎬 Browse Movies"] --> B["🏛️ Select Theatre"]
    B --> C["📺 Choose Screen/Show"]
    C --> D["💺 Pick Seats"]
    D --> E["🔒 Seats Locked"]
    E --> F["💳 Make Payment"]
    F --> G["✅ Booking Confirmed"]

    E -->|Timeout| H["⏰ Lock Expired"]
    H --> D
    G --> I["📧 Notification Sent"]
```

---

## Component Responsibilities

### Models

| Class | Responsibility |
|-------|---------------|
| `Movie` | Movie details: title, duration, genre |
| `User` | User identity: id, name, email |
| `Seat` _(abstract)_ | Base seat with position, category, price |
| `SilverSeat` / `GoldSeat` / `PlatinumSeat` | Category-specific seats with preset pricing ($150/$250/$400) |
| `Screen` | A hall with a collection of seats |
| `Theatre` | A cinema complex in a city with multiple screens |
| `Show` | A specific movie screening on a screen at a time, tracking per-seat availability |
| `Booking` | Links user + show + seats, tracks lifecycle status and total amount |

### Services

| Class | Responsibility |
|-------|---------------|
| `SeatLockManager` | **Thread-safe** seat locking with configurable timeout. Background scheduler auto-releases expired locks |
| `BookingManager` | Singleton orchestrator: initiate → confirm/expire → cancel. Notifies observers |
| `ShowManager` | Manages shows, query by movie or theatre |
| `TheatreManager` | Manages theatres, query by city |

### Payment (Strategy Pattern)

| Class | Description |
|-------|------------|
| `CashPayment` | Simulates cash payment |
| `CreditCardPayment` | Simulates card charge with masked number |
| `UPIPayment` | Simulates UPI transaction via UPI ID |

---

## Seat Pricing

| Category | Class | Price |
|----------|-------|-------|
| 🥈 Silver | `SilverSeat` | $150 |
| 🥇 Gold | `GoldSeat` | $250 |
| 💎 Platinum | `PlatinumSeat` | $400 |

---

## Folder Structure

```
Movie Booking System/
├── architecture.md
└── src/
    ├── Main.java                         (entry point + demo)
    ├── enums/
    │   ├── BookingStatus.java
    │   ├── PaymentStatus.java
    │   └── SeatCategory.java
    ├── factory/
    │   └── SeatFactory.java
    ├── models/
    │   ├── Booking.java
    │   ├── GoldSeat.java
    │   ├── Movie.java
    │   ├── PlatinumSeat.java
    │   ├── Screen.java
    │   ├── Seat.java                     (abstract)
    │   ├── Show.java
    │   ├── SilverSeat.java
    │   ├── Theatre.java
    │   └── User.java
    ├── observer/
    │   ├── BookingObserver.java           (interface)
    │   └── NotificationService.java
    ├── payment/
    │   ├── CashPayment.java
    │   ├── CreditCardPayment.java
    │   ├── Payment.java                  (interface)
    │   └── UPIPayment.java
    └── services/
        ├── BookingManager.java           (Singleton)
        ├── SeatLockManager.java          (Singleton + Timer)
        ├── ShowManager.java
        └── TheatreManager.java
```
