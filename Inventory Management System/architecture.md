# 📦 Inventory Management System — Architecture

## Overview

A Java-based Inventory Management System built using **Observer**, **Strategy**, and **Singleton** design patterns. Supports product CRUD, order lifecycle management (place → ship → deliver / cancel), low-stock alert notifications, and pluggable restocking strategies.

---

## Block Diagram

```mermaid
graph TB
    subgraph Entry Point
        Main["Main.java"]
    end

    subgraph Entities
        Product["Product"]
        Order["Order"]
        Category["Category (enum)"]
        OrderStatus["OrderStatus (enum)"]
    end

    subgraph Services
        IM["InventoryManager (Singleton)"]
        OS["OrderService"]
        WS["WarehouseService"]
    end

    subgraph Observer ["Observer Pattern"]
        IS["«interface» InventorySubject"]
        IO["«interface» InventoryObserver"]
        LSA["LowStockAlertObserver"]
    end

    subgraph Strategy ["Restock Strategy Pattern"]
        RS["«interface» RestockStrategy"]
        JIT["JustInTimeRestock"]
        BR["BulkRestock"]
    end

    Main -->|uses| IM
    Main -->|uses| OS
    Main -->|uses| WS

    IM -->|implements| IS
    LSA -->|implements| IO
    IM -->|notifies| IO

    OS -->|deducts/restores stock via| IM
    OS -->|creates| Order
    Order -->|has| OrderStatus

    WS -->|restocks via| IM
    WS -->|delegates to| RS
    JIT -->|implements| RS
    BR -->|implements| RS

    IM -->|manages| Product
    Product -->|has| Category
```

---

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Observer** | `InventorySubject` / `InventoryObserver` | `LowStockAlertObserver` auto-notifies when product stock drops below threshold |
| **Strategy** | `RestockStrategy` → `JustInTimeRestock`, `BulkRestock` | Pluggable restocking algorithms — easily switch between lean and bulk replenishment |
| **Singleton** | `InventoryManager` | Single global inventory ensures data consistency across all services |

---

## Class Diagram

```mermaid
classDiagram
    class Category {
        <<enum>>
        ELECTRONICS
        CLOTHING
        FOOD
        FURNITURE
        SPORTS
    }

    class OrderStatus {
        <<enum>>
        PENDING
        CONFIRMED
        SHIPPED
        DELIVERED
        CANCELLED
    }

    class Product {
        -String id
        -String name
        -Category category
        -double price
        -int quantity
        -int lowStockThreshold
        +getId() String
        +getName() String
        +getQuantity() int
        +getLowStockThreshold() int
        +setQuantity(qty)
    }

    class Order {
        -String id
        -String productId
        -int quantity
        -OrderStatus status
        -double totalAmount
        -LocalDateTime timestamp
        +getId() String
        +getStatus() OrderStatus
        +setStatus(status)
    }

    class InventorySubject {
        <<interface>>
        +addObserver(observer)
        +removeObserver(observer)
        +notifyObservers(product)
    }

    class InventoryObserver {
        <<interface>>
        +onStockUpdate(product)
    }

    class LowStockAlertObserver {
        +onStockUpdate(product)
    }

    class RestockStrategy {
        <<interface>>
        +restock(product) int
    }

    class JustInTimeRestock {
        +restock(product) int
    }

    class BulkRestock {
        +restock(product) int
    }

    class InventoryManager {
        -static InventoryManager instance
        -Map products
        -List observers
        +static getInstance() InventoryManager
        +addProduct(product)
        +removeProduct(productId)
        +getProduct(productId) Product
        +updateStock(productId, qty)
        +hasStock(productId, qty) boolean
    }

    class OrderService {
        -InventoryManager inventoryManager
        -List orders
        +placeOrder(productId, qty) Order
        +cancelOrder(orderId)
        +shipOrder(orderId)
        +deliverOrder(orderId)
    }

    class WarehouseService {
        -RestockStrategy strategy
        +setStrategy(strategy)
        +restockProduct(product, inventoryManager)
    }

    InventoryManager ..|> InventorySubject
    LowStockAlertObserver ..|> InventoryObserver
    InventoryManager --> InventoryObserver : notifies
    InventoryManager --> Product : manages

    JustInTimeRestock ..|> RestockStrategy
    BulkRestock ..|> RestockStrategy
    WarehouseService --> RestockStrategy : delegates to

    OrderService --> InventoryManager : uses
    OrderService --> Order : creates
    WarehouseService --> InventoryManager : restocks via

    Product --> Category
    Order --> OrderStatus
```

---

## Order Flow

```mermaid
flowchart TD
    A["Client calls orderService.placeOrder(productId, qty)"] --> B["Lookup Product"]
    B --> C{Product exists?}
    C -->|No| D["❌ Error: Product not found"]
    C -->|Yes| E{Sufficient stock?}
    E -->|No| F["❌ Error: Insufficient stock"]
    E -->|Yes| G["Create Order (CONFIRMED)"]
    G --> H["inventoryManager.updateStock(-qty)"]
    H --> I["notifyObservers(product)"]
    I --> J{qty <= threshold?}
    J -->|Yes| K["⚠️ LowStockAlertObserver fires"]
    J -->|No| L["No alert"]
    K --> M["Return Order"]
    L --> M
```

---

## Restock Flow

```mermaid
flowchart LR
    A["warehouse.restockProduct(product)"] --> B{"Which Strategy?"}
    B -->|JIT| C["threshold × 2 units"]
    B -->|Bulk| D["threshold × 10 units"]
    C --> E["inventoryManager.updateStock(+qty)"]
    D --> E
    E --> F["notifyObservers(product)"]
    F --> G["Stock replenished ✅"]
```

---

## Order Lifecycle

```mermaid
stateDiagram-v2
    [*] --> PENDING : Order created
    PENDING --> CONFIRMED : Stock validated & deducted
    CONFIRMED --> SHIPPED : shipOrder()
    SHIPPED --> DELIVERED : deliverOrder()
    CONFIRMED --> CANCELLED : cancelOrder() → stock restored
    CANCELLED --> [*]
    DELIVERED --> [*]
```

---

## Component Responsibilities

### Entities

| Class | Responsibility |
|-------|---------------|
| `Product` | Stores id, name, category, price, quantity, lowStockThreshold. Equals/hash by id |
| `Category` | Enum — ELECTRONICS, CLOTHING, FOOD, FURNITURE, SPORTS |
| `Order` | Immutable record of an order — productId, quantity, status, totalAmount, timestamp |
| `OrderStatus` | Enum — PENDING → CONFIRMED → SHIPPED → DELIVERED / CANCELLED |

### Observer

| Class | Responsibility |
|-------|---------------|
| `InventorySubject` | Interface — add/remove/notify observers |
| `InventoryObserver` | Interface — `onStockUpdate(product)` callback |
| `LowStockAlertObserver` | Prints alert when `product.quantity <= product.lowStockThreshold` |

### Strategy (Restock)

| Class | Responsibility |
|-------|---------------|
| `RestockStrategy` | Interface — `restock(product)` returns quantity to add |
| `JustInTimeRestock` | Lean restock — adds `threshold × 2` units |
| `BulkRestock` | Heavy restock — adds `threshold × 10` units |

### Services

| Class | Responsibility |
|-------|---------------|
| `InventoryManager` | Singleton — product CRUD, stock updates, observer notifications, thread-safe |
| `OrderService` | Order lifecycle — place, cancel, ship, deliver. Validates stock, deducts/restores on cancel |
| `WarehouseService` | Delegates restocking to pluggable `RestockStrategy`. Strategy switchable at runtime |

---

## Key Features

| Feature | Implementation |
|---------|---------------|
| **Product CRUD** | Add, remove, get, list, filter by category |
| **Order lifecycle** | PENDING → CONFIRMED → SHIPPED → DELIVERED / CANCELLED |
| **Low-stock alerts** | Observer pattern — auto-fires when stock drops below threshold |
| **Pluggable restock** | Strategy pattern — JIT (× 2) or Bulk (× 10), switchable at runtime |
| **Thread-safe** | `synchronized` stock updates in InventoryManager |
| **Stock validation** | Orders rejected if insufficient stock |
| **Cancel & restore** | Cancelling an order restores deducted stock |

---

## Folder Structure

```
Inventory Management System/
├── architecture.md
└── src/
    ├── Main.java                              (entry point + demo)
    ├── entities/
    │   ├── Category.java                      (enum)
    │   ├── Order.java
    │   ├── OrderStatus.java                   (enum)
    │   └── Product.java
    ├── Observer/
    │   ├── InventoryObserver.java             (interface)
    │   ├── InventorySubject.java              (interface)
    │   └── LowStockAlertObserver.java
    ├── Strategy/
    │   ├── BulkRestock.java
    │   ├── JustInTimeRestock.java
    │   └── RestockStrategy.java               (interface)
    └── Services/
        ├── InventoryManager.java              (Singleton + Subject)
        ├── OrderService.java
        └── WarehouseService.java
```
