# 🍔 Food Ordering System — Architecture

## Overview

A Java-based Food Ordering System built using **Observer**, **Strategy**, and **Singleton** design patterns. Supports restaurant & menu management, multi-item order placement, pluggable payment methods (Credit Card / UPI / Cash on Delivery), and full order lifecycle with real-time status notifications.

---

## Block Diagram

```mermaid
graph TB
    subgraph Entry Point
        Main["Main.java"]
    end

    subgraph Entities
        MI["MenuItem"]
        MC["MenuCategory (enum)"]
        R["Restaurant"]
        C["Customer"]
        OI["OrderItem"]
        O["Order"]
        OS["OrderStatus (enum)"]
    end

    subgraph Services
        RS["RestaurantService (Singleton)"]
        ORS["OrderService"]
        PS["PaymentService"]
    end

    subgraph Observer ["Observer Pattern"]
        OSub["«interface» OrderSubject"]
        OObs["«interface» OrderObserver"]
        OSN["OrderStatusNotifier"]
    end

    subgraph Strategy ["Payment Strategy Pattern"]
        PSt["«interface» PaymentStrategy"]
        CC["CreditCardPayment"]
        UPI["UpiPayment"]
        COD["CashOnDelivery"]
    end

    Main -->|uses| RS
    Main -->|uses| ORS
    Main -->|uses| PS

    RS -->|manages| R
    R -->|has| MI
    MI -->|has| MC

    ORS -->|implements| OSub
    OSN -->|implements| OObs
    ORS -->|notifies| OObs

    ORS -->|creates| O
    O -->|contains| OI
    OI -->|references| MI
    O -->|has| OS
    O -->|belongs to| C
    O -->|from| R

    PS -->|delegates to| PSt
    CC -->|implements| PSt
    UPI -->|implements| PSt
    COD -->|implements| PSt
```

---

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Observer** | `OrderSubject` / `OrderObserver` | `OrderStatusNotifier` auto-notifies customers when order status changes (PLACED → CONFIRMED → PREPARING → DELIVERED) |
| **Strategy** | `PaymentStrategy` → `CreditCardPayment`, `UpiPayment`, `CashOnDelivery` | Pluggable payment methods — switch at runtime without modifying client code |
| **Singleton** | `RestaurantService` | Single global restaurant catalog ensures data consistency |

---

## Class Diagram

```mermaid
classDiagram
    class MenuCategory {
        <<enum>>
        STARTER
        MAIN_COURSE
        DESSERT
        BEVERAGE
        SNACK
    }

    class OrderStatus {
        <<enum>>
        PLACED
        CONFIRMED
        PREPARING
        OUT_FOR_DELIVERY
        DELIVERED
        CANCELLED
    }

    class MenuItem {
        -String id
        -String name
        -MenuCategory category
        -double price
        -boolean available
        +getId() String
        +getName() String
        +getCategory() MenuCategory
        +getPrice() double
        +isAvailable() boolean
        +setAvailable(available)
    }

    class Customer {
        -String id
        -String name
        -String email
        -String address
        +getId() String
        +getName() String
        +getEmail() String
        +getAddress() String
    }

    class Restaurant {
        -String id
        -String name
        -String address
        -List~MenuItem~ menu
        +addMenuItem(item)
        +removeMenuItem(itemId)
        +getMenuItem(itemId) MenuItem
        +displayMenu()
    }

    class OrderItem {
        -MenuItem menuItem
        -int quantity
        -double subtotal
        +getMenuItem() MenuItem
        +getQuantity() int
        +getSubtotal() double
    }

    class Order {
        -String id
        -Customer customer
        -Restaurant restaurant
        -List~OrderItem~ items
        -OrderStatus status
        -double totalAmount
        -LocalDateTime timestamp
        +getId() String
        +getStatus() OrderStatus
        +setStatus(status)
        +getTotalAmount() double
    }

    class OrderSubject {
        <<interface>>
        +addObserver(observer)
        +removeObserver(observer)
        +notifyObservers(order)
    }

    class OrderObserver {
        <<interface>>
        +onOrderStatusChanged(order)
    }

    class OrderStatusNotifier {
        +onOrderStatusChanged(order)
    }

    class PaymentStrategy {
        <<interface>>
        +pay(amount) boolean
        +getPaymentMethod() String
    }

    class CreditCardPayment {
        -String cardNumber
        +pay(amount) boolean
        +getPaymentMethod() String
    }

    class UpiPayment {
        -String upiId
        +pay(amount) boolean
        +getPaymentMethod() String
    }

    class CashOnDelivery {
        +pay(amount) boolean
        +getPaymentMethod() String
    }

    class RestaurantService {
        -static RestaurantService instance
        -Map restaurants
        +static getInstance() RestaurantService
        +addRestaurant(restaurant)
        +getRestaurant(id) Restaurant
        +searchByName(keyword) List
        +displayAllRestaurants()
    }

    class OrderService {
        -Map orders
        -List observers
        -int orderCounter
        +placeOrder(customer, restaurant, items) Order
        +confirmOrder(orderId)
        +prepareOrder(orderId)
        +outForDelivery(orderId)
        +deliverOrder(orderId)
        +cancelOrder(orderId)
        +displayOrders()
    }

    class PaymentService {
        -PaymentStrategy strategy
        +setStrategy(strategy)
        +processPayment(order) boolean
    }

    OrderService ..|> OrderSubject
    OrderStatusNotifier ..|> OrderObserver
    OrderService --> OrderObserver : notifies

    CreditCardPayment ..|> PaymentStrategy
    UpiPayment ..|> PaymentStrategy
    CashOnDelivery ..|> PaymentStrategy
    PaymentService --> PaymentStrategy : delegates to

    RestaurantService --> Restaurant : manages
    Restaurant --> MenuItem : has
    MenuItem --> MenuCategory

    OrderService --> Order : creates
    Order --> OrderItem : contains
    Order --> Customer
    Order --> Restaurant
    Order --> OrderStatus
    OrderItem --> MenuItem
```

---

## Order Flow

```mermaid
flowchart TD
    A["Customer selects restaurant & items"] --> B["orderService.placeOrder(customer, restaurant, items)"]
    B --> C{All items exist & available?}
    C -->|No| D["❌ Error: Item not found / unavailable"]
    C -->|Yes| E["Create Order (PLACED)"]
    E --> F["notifyObservers(order)"]
    F --> G["🔔 OrderStatusNotifier fires"]
    G --> H["Return Order"]
```

---

## Payment Flow

```mermaid
flowchart LR
    A["paymentService.processPayment(order)"] --> B{Which Strategy?}
    B -->|CreditCard| C["💳 Charge card via masked number"]
    B -->|UPI| D["📱 Debit via UPI ID"]
    B -->|CashOnDelivery| E["💵 Payment collected at door"]
    C --> F["✅ Payment Successful"]
    D --> F
    E --> F
```

---

## Order Lifecycle

```mermaid
stateDiagram-v2
    [*] --> PLACED : Customer places order
    PLACED --> CONFIRMED : Restaurant confirms
    CONFIRMED --> PREPARING : Kitchen starts cooking
    PREPARING --> OUT_FOR_DELIVERY : Rider picks up
    OUT_FOR_DELIVERY --> DELIVERED : Delivered to customer
    PLACED --> CANCELLED : Customer cancels
    CONFIRMED --> CANCELLED : Customer cancels
    PREPARING --> CANCELLED : Customer cancels
    CANCELLED --> [*]
    DELIVERED --> [*]
```

---

## Component Responsibilities

### Entities

| Class | Responsibility |
|-------|---------------|
| `MenuItem` | Food item with id, name, category, price, availability. Equals/hash by id |
| `MenuCategory` | Enum — STARTER, MAIN_COURSE, DESSERT, BEVERAGE, SNACK |
| `Restaurant` | Has id, name, address, menu (list of MenuItems). CRUD on menu items |
| `Customer` | Stores id, name, email, delivery address |
| `OrderItem` | Links MenuItem with quantity, auto-calculates subtotal |
| `Order` | Full order — customer, restaurant, items, status, total, timestamp |
| `OrderStatus` | Enum — PLACED → CONFIRMED → PREPARING → OUT_FOR_DELIVERY → DELIVERED / CANCELLED |

### Observer

| Class | Responsibility |
|-------|---------------|
| `OrderSubject` | Interface — add/remove/notify observers on order status change |
| `OrderObserver` | Interface — `onOrderStatusChanged(order)` callback |
| `OrderStatusNotifier` | Prints notification with emoji when order status changes |

### Strategy (Payment)

| Class | Responsibility |
|-------|---------------|
| `PaymentStrategy` | Interface — `pay(amount)` returns boolean, `getPaymentMethod()` returns name |
| `CreditCardPayment` | Simulates credit card payment — displays masked card number |
| `UpiPayment` | Simulates UPI payment — displays UPI ID |
| `CashOnDelivery` | Cash on delivery — always succeeds |

### Services

| Class | Responsibility |
|-------|---------------|
| `RestaurantService` | Singleton — manages restaurant catalog, search by name, display all |
| `OrderService` | Order lifecycle — place, confirm, prepare, dispatch, deliver, cancel. Validates items, notifies observers |
| `PaymentService` | Delegates to pluggable `PaymentStrategy`. Strategy switchable at runtime |

---

## Key Features

| Feature | Implementation |
|---------|---------------|
| **Restaurant Management** | Add restaurants with menu items, search, display catalog |
| **Menu Management** | Add/remove/toggle availability of menu items |
| **Multi-item Orders** | Place orders with multiple items and quantities |
| **Order Lifecycle** | PLACED → CONFIRMED → PREPARING → OUT_FOR_DELIVERY → DELIVERED / CANCELLED |
| **Status Notifications** | Observer pattern — auto-fires notification on every status change |
| **Pluggable Payments** | Strategy pattern — Credit Card / UPI / Cash on Delivery, switchable at runtime |
| **Validation** | Orders rejected if item not found or unavailable |
| **Cancel & Refund** | Cancel order at any pre-delivery stage, refund initiated |

---

## Folder Structure

```
Food Ordering System/
├── architecture.md
└── src/
    ├── Main.java                              (entry point + demo)
    ├── entities/
    │   ├── Customer.java
    │   ├── MenuCategory.java                  (enum)
    │   ├── MenuItem.java
    │   ├── Order.java
    │   ├── OrderItem.java
    │   ├── OrderStatus.java                   (enum)
    │   └── Restaurant.java
    ├── Observer/
    │   ├── OrderObserver.java                 (interface)
    │   ├── OrderSubject.java                  (interface)
    │   └── OrderStatusNotifier.java
    ├── Strategy/
    │   ├── CashOnDelivery.java
    │   ├── CreditCardPayment.java
    │   ├── PaymentStrategy.java               (interface)
    │   └── UpiPayment.java
    └── Services/
        ├── OrderService.java                  (Subject + lifecycle)
        ├── PaymentService.java                (Strategy delegator)
        └── RestaurantService.java             (Singleton)
```
