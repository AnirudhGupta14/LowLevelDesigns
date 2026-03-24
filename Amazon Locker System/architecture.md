# Amazon Locker System Architecture Documentation

A consistent documentation style for Low-Level Design (LLD) projects using Markdown, Mermaid diagrams, and formatted tables for quick identification of patterns and system components.

## 1. Overview
This is the Low-Level Design (LLD) for the Amazon Locker System. The system manages package deliveries by agents, assigns appropriately sized lockers, and handles customer pickups with OTP validation. It utilizes Java and follows structural and behavioral design patterns: Strategy for locker assignment, Observer for customer notifications, and Singleton for service management.

## 2. Block Diagram (Mermaid)
```mermaid
graph TB
    subgraph Entry Point
        Main["AmazonLockerMain.java"]
    end
    subgraph Entities
        Locker["Locker"]
        Package["Package"]
        Customer["Customer"]
    end
    subgraph Services
        LockerService["LockerService (Singleton)"]
        DeliveryService["DeliveryService (Singleton)"]
        PickupService["PickupService (Singleton)"]
    end
    subgraph Strategy
        LockerAssignmentStrategy["LockerAssignmentStrategy"]
        SizeBasedLockerAssignmentStrategy["SizeBasedLockerAssignmentStrategy"]
    end
    subgraph Observer
        NotificationPublisher["NotificationPublisher"]
        NotificationSubscriber["NotificationSubscriber"]
        EmailNotificationService["EmailNotificationService"]
    end
    
    Main --> DeliveryService
    Main --> PickupService
    DeliveryService --> LockerService
    PickupService --> LockerService
    LockerService --> LockerAssignmentStrategy
    DeliveryService --> NotificationPublisher
```

## 3. Design Patterns Summary Table

| Pattern | Where | Why |
|---------|-------|-----|
| **Observer** | `NotificationPublisher` / `NotificationSubscriber` | Decouples components for automated notifications (e.g., sending OTP via email upon delivery). |
| **Strategy** | `LockerAssignmentStrategy` | Provides interchangeable algorithms for assigning a package to a locker (e.g., size-based matching). |
| **Singleton** | `LockerService`, `DeliveryService`, `PickupService` | Ensures a single source of truth across the application for managing orders and lockers. |

## 4. Class Diagram (Mermaid)
```mermaid
classDiagram
    class Customer {
        -String id
        -String name
        -String email
    }
    class Package {
        -String id
        -LockerSize size
        -PackageStatus status
        -Customer customer
    }
    class Locker {
        -String id
        -LockerSize size
        -LockerState state
        -Package currentPackage
        -String otp
        +assignPackage(Package, String otp)
        +removePackage()
    }
    class LockerService {
        -List~Locker~ lockers
        -LockerAssignmentStrategy strategy
        +addLocker(Locker)
        +assignLocker(Package) Locker
        +getLockerById(String) Locker
        +freeLocker(String)
    }
    class DeliveryService {
        -LockerService lockerService
        -List~NotificationSubscriber~ observers
        +deliverPackage(Package)
    }
    class PickupService {
        -LockerService lockerService
        +pickupPackage(String lockerId, String otp)
    }
```

## 5. Flow Diagrams
```mermaid
sequenceDiagram
    participant Main
    participant DeliveryService
    participant LockerService
    participant NotificationService
    participant PickupService
    participant Locker
    
    Main->>DeliveryService: deliverPackage(package)
    DeliveryService->>LockerService: assignLocker(package)
    LockerService-->>DeliveryService: locker
    DeliveryService->>Locker: assignPackage(package, generatedOtp)
    DeliveryService->>NotificationService: sendOTP(customer, otp, lockerId)
    Main->>PickupService: pickupPackage(lockerId, otp)
    PickupService->>LockerService: getLockerById(lockerId)
    LockerService-->>PickupService: locker
    PickupService->>Locker: check otp
    alt Valid OTP
        PickupService->>Locker: removePackage()
        Locker-->>PickupService: success
    else Invalid OTP
        PickupService-->>Main: error
    end
```

## 6. Project Structure Table

| Layer | Files |
|-------|-------|
| **Enums** | `LockerSize.java`, `LockerState.java`, `PackageStatus.java` |
| **Entities** | `Locker.java`, `Package.java`, `Customer.java` |
| **Strategy** | `LockerAssignmentStrategy.java`, `SizeBasedLockerAssignmentStrategy.java` |
| **Observer** | `NotificationPublisher.java`, `NotificationSubscriber.java`, `EmailNotificationService.java` |
| **Services** | `LockerService.java`, `DeliveryService.java`, `PickupService.java` |
| **Entry Point** | `AmazonLockerMain.java` |

## 7. Verification Results
- [x] Run `AmazonLockerMain.java` successfully with no compile errors.
