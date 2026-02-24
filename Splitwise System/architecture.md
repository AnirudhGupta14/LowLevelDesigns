# 💰 Splitwise System — Architecture

## Overview

A Java-based expense-splitting system that lets users share expenses with **equal** or **percentage-based** splits. Features an **Observer pattern** to auto-update balances, a **Factory pattern** for split creation, and **DP-based optimal settlement** calculation.

---

## Block Diagram

```mermaid
graph TB
    subgraph Entry Point
        Main["SplitwiseSystem (main)"]
    end

    subgraph Entities
        User["User"]
        Expense["Expense"]
        Transaction["Transaction"]
        UserPair["UserPair"]
    end

    subgraph Services
        EM["ExpenseManager"]
        BS["BalanceSheet"]
    end

    subgraph Observer ["Observer Pattern"]
        ES["«interface» ExpenseSubject"]
        EO["«interface» ExpenseObserver"]
    end

    subgraph SplitWays ["Split Strategy (Factory Pattern)"]
        SF["SplitFactory"]
        SP["«interface» Split"]
        EQ["EqualSplit"]
        PS["PercentageSplit"]
    end

    Main -->|creates| User
    Main -->|uses| SF
    SF -->|creates| SP
    EQ -->|implements| SP
    PS -->|implements| SP
    SP -->|calculates shares for| Expense
    Main -->|creates| Expense
    Main -->|adds expense to| EM

    EM -->|implements| ES
    BS -->|implements| EO
    EM -->|notifies| EO

    BS -->|maintains balances using| UserPair
    BS -->|generates| Transaction
```

---

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Observer** | `ExpenseSubject` / `ExpenseObserver` | `BalanceSheet` auto-updates when expenses are added/modified via `ExpenseManager` |
| **Factory** | `SplitFactory` | Creates `EqualSplit` or `PercentageSplit` based on a string type — avoids hardcoded `if/else` in client code |

---

## Class Diagram

```mermaid
classDiagram
    class User {
        -String id
        -String name
        -String email
        +getId() String
        +getName() String
        +getEmail() String
    }

    class Expense {
        -String id
        -String description
        -double amount
        -User payer
        -List~User~ participants
        -Map~User,Double~ shares
    }

    class Transaction {
        -User from
        -User to
        -double amount
    }

    class UserPair {
        -User user1
        -User user2
    }

    class ExpenseSubject {
        <<interface>>
        +addObserver(observer)
        +removeObserver(observer)
        +notifyExpenseAdded(expense)
        +notifyExpenseUpdated(expense)
    }

    class ExpenseObserver {
        <<interface>>
        +onExpenseAdded(expense)
        +onExpenseUpdated(expense)
    }

    class ExpenseManager {
        -List~ExpenseObserver~ observers
        -List~Expense~ expenses
        +addExpense(expense)
        +updateExpense(expense)
        +getAllExpenses()
    }

    class BalanceSheet {
        -Map~UserPair,Double~ balances
        +onExpenseAdded(expense)
        +onExpenseUpdated(expense)
        +getBalance(user1, user2)
        +getTotalBalance(user)
        +getSimplifiedSettlements()
        +getOptimalMinimumSettlements()
    }

    class Split {
        <<interface>>
        +calculateSplit(amount, participants, details)
    }

    class EqualSplit {
        +calculateSplit(amount, participants, details)
    }

    class PercentageSplit {
        +calculateSplit(amount, participants, details)
    }

    class SplitFactory {
        +static createSplit(type) Split
    }

    ExpenseManager ..|> ExpenseSubject
    BalanceSheet ..|> ExpenseObserver
    ExpenseManager --> ExpenseObserver : notifies
    BalanceSheet --> UserPair
    BalanceSheet --> Transaction
    Expense --> User
    EqualSplit ..|> Split
    PercentageSplit ..|> Split
    SplitFactory --> Split : creates
```

---

## Component Responsibilities

### Entities

| Class | Responsibility |
|-------|---------------|
| `User` | Stores user id, name, email. Overrides `equals` & `hashCode` by id |
| `Expense` | Immutable record of an expense — payer, participants, and per-user shares |
| `Transaction` | Represents a "from → to" settlement with amount |
| `UserPair` | Key for the balance map — tracks balance between two users |

### Services

| Class | Responsibility |
|-------|---------------|
| `ExpenseManager` | Stores expenses, implements `ExpenseSubject` to notify observers on add/update |
| `BalanceSheet` | Observes expenses, maintains pairwise balances, computes simplified & optimal settlements |

### Split Strategies

| Class | Responsibility |
|-------|---------------|
| `SplitFactory` | Factory method — creates `EqualSplit` or `PercentageSplit` by type string |
| `EqualSplit` | Divides amount equally among all participants |
| `PercentageSplit` | Splits amount based on user-specified percentages |

---

## Expense Flow

```mermaid
flowchart TD
    A[User creates Expense] --> B[Choose Split Type]
    B --> C{SplitFactory}
    C -->|EQUAL| D[EqualSplit.calculateSplit]
    C -->|PERCENTAGE| E[PercentageSplit.calculateSplit]
    D --> F[Create Expense with shares]
    E --> F
    F --> G["ExpenseManager.addExpense()"]
    G --> H["notify → BalanceSheet.onExpenseAdded()"]
    H --> I[Update pairwise balances]
    I --> J[Query: getSimplifiedSettlements]
    J --> K[List of Transactions]
```

---

## Settlement Algorithms

| Algorithm | Method | Complexity | Approach |
|-----------|--------|------------|----------|
| **Greedy** | `getSimplifiedSettlements()` | O(n²) | Match max creditor with max debtor iteratively |
| **Backtracking** | `getSubOptimalMinimumSettlements()` | Exponential | DFS trying all settlement combinations |
| **DP Bitmask** | `getOptimalMinimumSettlements()` | O(3ⁿ) | Finds max balanced subgroups using bitmask DP |

---

## Folder Structure

```
Splitwise System/
└── src/
    ├── Main.java
    ├── entities/
    │   ├── Expense.java
    │   ├── Transaction.java
    │   ├── User.java
    │   └── UserPair.java
    ├── Observer/
    │   ├── ExpenseObserver.java    (interface)
    │   └── ExpenseSubject.java     (interface)
    ├── Services/
    │   ├── BalanceSheet.java       (observer)
    │   ├── ExpenseManager.java     (subject)
    │   └── SplitwiseSystem.java    (entry point)
    └── splitWays/
        ├── EqualSplit.java
        ├── PercentageSplit.java
        ├── Split.java              (interface)
        └── SplitFactory.java       (factory)
```
