# 🗄️ Cache System — Architecture

## Overview

A Java-based, multi-layered **Cache System** implementing three pluggable eviction policies (LRU, LFU, FIFO), a transparent DB storage fallback, and a `ReentrantReadWriteLock` for safe concurrent access. Designed around four core design patterns: **Strategy**, **Observer**, **Builder (Singleton)**, and **Factory**.

---

## Block Diagram

```mermaid
graph TB
    subgraph Entry["Entry Point"]
        M["Main.java"]
    end

    subgraph Services["Demo / Services"]
        Demo["CacheSystemDemo"]
    end

    subgraph Core["Core Cache"]
        CM["CacheManager\n(Builder · RW Lock)"]
    end

    subgraph Model["Model Layer"]
        CE["CacheEntry&lt;K,V&gt;"]
        CS["CacheStats"]
    end

    subgraph Enums["Enums"]
        CP["CachePolicy\n(LRU | LFU | FIFO)"]
        ST["StorageType\n(IN_MEMORY_DB | FILE_DB)"]
    end

    subgraph Strategy["Eviction Strategy Pattern"]
        ES["«interface»\nEvictionStrategy&lt;K,V&gt;"]
        LRU["LRUEvictionStrategy"]
        LFU["LFUEvictionStrategy"]
        FIFO["FIFOEvictionStrategy"]
    end

    subgraph Observer["Observer Pattern"]
        CEL["«interface»\nCacheEventListener&lt;K&gt;"]
        LOG["LoggingCacheListener"]
        STAT["StatisticsListener"]
    end

    subgraph Storage["Storage Layer"]
        DB["«interface»\nDatabaseStorage&lt;K,V&gt;"]
        IMDB["InMemoryDatabaseStorage"]
    end

    M --> Demo
    Demo --> CM

    CM --> CE
    CM --> ES
    CM --> DB
    CM --> CEL

    LRU --> ES
    LFU --> ES
    FIFO --> ES

    LOG --> CEL
    STAT --> CEL
    STAT --> CS

    IMDB --> DB
```

---

## Design Patterns

| Pattern | Where | Why |
|---------|-------|-----|
| **Strategy** | `EvictionStrategy` → `LRUEvictionStrategy`, `LFUEvictionStrategy`, `FIFOEvictionStrategy` | Swap eviction algorithm at construction time without changing `CacheManager` |
| **Observer** | `CacheEventListener` → `LoggingCacheListener`, `StatisticsListener` | Decouple logging/metrics from cache logic; add new listeners with zero code change |
| **Builder** | `CacheManager.Builder` | Fluent, validated construction; enforces required fields (strategy, DB) |
| **Factory** (implicit) | `Builder.build()` | Centralises `CacheManager` instantiation, hiding constructor |

---

## Concurrency Design — Dirty-Read Prevention

```mermaid
sequenceDiagram
    participant T1 as Thread 1 (Reader)
    participant T2 as Thread 2 (Reader)
    participant T3 as Thread 3 (Writer)
    participant CM as CacheManager

    T1 ->> CM: get("key-A") → acquires READ lock
    T2 ->> CM: get("key-B") → acquires READ lock (parallel ✓)
    T3 ->> CM: put("key-A", newVal) → waits for WRITE lock
    CM -->> T1: returns "value-A" (clean read)
    CM -->> T2: returns "value-B" (clean read)
    T1 -->> CM: releases READ lock
    T2 -->> CM: releases READ lock
    T3 ->> CM: acquires WRITE lock (exclusive)
    CM -->> T3: writes "key-A" = newVal
    T3 -->> CM: releases WRITE lock
```

**Rule**: `get()` uses `ReentrantReadWriteLock.readLock()` — N readers run concurrently with no blocking.  
**Rule**: `put()` / `remove()` use `writeLock()` — exclusive access; all readers blocked until write completes.  
**Result**: No thread can ever observe a half-written cache entry (dirty read eliminated).

---

## Cache Read/Write Flow

```mermaid
flowchart TD
    A["Client calls get(key)"] --> B{In cache?}
    B -->|Yes| C["recordAccess() ← updates LRU/LFU metadata"]
    C --> D["notifyListeners: onHit"]
    D --> R1["Return value"]

    B -->|No| E["notifyListeners: onMiss"]
    E --> F{In DB?}
    F -->|Yes| G["Load into cache + notifyListeners: onDbFallback"]
    G --> R1
    F -->|No| R2["Return null"]

    A2["Client calls put(key, value)"] --> P1{Key exists?}
    P1 -->|Yes| P2["Update value in-place"]
    P1 -->|No| P3{Cache full?}
    P3 -->|Yes| P4["EvictionStrategy.evict()"]
    P4 --> P5["Write evicted value to DB (write-behind)"]
    P5 --> P6["notifyListeners: onEvict"]
    P6 --> P7["Insert new CacheEntry"]
    P3 -->|No| P7
```

---

## Class Diagram

```mermaid
classDiagram
    class CacheEntry~K V~ {
        -K key
        -V value
        -int frequency
        -LocalDateTime lastAccessTime
        -LocalDateTime createdAt
        +recordAccess()
        +getKey() K
        +getValue() V
        +getFrequency() int
        +getLastAccessTime() LocalDateTime
        +getCreatedAt() LocalDateTime
    }

    class CacheStats {
        -AtomicInteger hits
        -AtomicInteger misses
        -AtomicInteger evictions
        -AtomicInteger dbFallbacks
        +incrementHits()
        +incrementMisses()
        +incrementEvictions()
        +incrementDbFallbacks()
        +hitRatio() double
    }

    class CachePolicy {
        <<enum>>
        LRU
        LFU
        FIFO
    }

    class StorageType {
        <<enum>>
        IN_MEMORY_DB
        FILE_DB
    }

    class EvictionStrategy~K V~ {
        <<interface>>
        +evict(Map) K
    }

    class LRUEvictionStrategy~K V~ {
        +evict(Map) K
    }

    class LFUEvictionStrategy~K V~ {
        +evict(Map) K
    }

    class FIFOEvictionStrategy~K V~ {
        +evict(Map) K
    }

    class CacheEventListener~K~ {
        <<interface>>
        +onHit(K key)
        +onMiss(K key)
        +onEvict(K key)
        +onDbFallback(K key)
    }

    class LoggingCacheListener~K~ {
        -String cacheName
        +onHit(K key)
        +onMiss(K key)
        +onEvict(K key)
        +onDbFallback(K key)
    }

    class StatisticsListener~K~ {
        -CacheStats stats
        +onHit(K key)
        +onMiss(K key)
        +onEvict(K key)
        +onDbFallback(K key)
    }

    class DatabaseStorage~K V~ {
        <<interface>>
        +save(K key, V value)
        +get(K key) Optional~V~
        +delete(K key)
        +exists(K key) boolean
        +size() int
    }

    class InMemoryDatabaseStorage~K V~ {
        -Map store
        +save(K, V)
        +get(K) Optional~V~
        +delete(K)
        +exists(K) boolean
        +size() int
    }

    class CacheManager~K V~ {
        -String name
        -int capacity
        -Map cache
        -ReadWriteLock lock
        -EvictionStrategy evictionStrategy
        -DatabaseStorage db
        -List~CacheEventListener~ listeners
        +get(K key) V
        +put(K key, V value)
        +remove(K key)
        +size() int
    }

    class Builder~K V~ {
        +name(String) Builder
        +capacity(int) Builder
        +evictionStrategy(EvictionStrategy) Builder
        +database(DatabaseStorage) Builder
        +addListener(CacheEventListener) Builder
        +build() CacheManager
    }

    LRUEvictionStrategy ..|> EvictionStrategy
    LFUEvictionStrategy ..|> EvictionStrategy
    FIFOEvictionStrategy ..|> EvictionStrategy

    LoggingCacheListener ..|> CacheEventListener
    StatisticsListener ..|> CacheEventListener
    StatisticsListener --> CacheStats

    InMemoryDatabaseStorage ..|> DatabaseStorage

    CacheManager --> EvictionStrategy : delegates eviction
    CacheManager --> DatabaseStorage : write-behind / read-through
    CacheManager --> CacheEventListener : notifies
    CacheManager --> CacheEntry : stores

    Builder --> CacheManager : builds

    StatisticsListener --> CacheStats
```

---

## Eviction Policy Comparison

| Policy | Evicts | Ideal Workload |
|--------|--------|----------------|
| **LRU** | Entry with oldest `lastAccessTime` | General-purpose; good temporal locality |
| **LFU** | Entry with lowest `frequency` (LRU tie-break) | Workloads with hot-key patterns |
| **FIFO** | Entry with oldest `createdAt` | Streams / time-series where old data is stale |

---

## Project Structure

```
Cache System/
├── src/
│   ├── Main.java                          ← Entry point
│   ├── cache/
│   │   └── CacheManager.java             ← Singleton (Builder), RW Lock, eviction, DB fallback
│   ├── model/
│   │   ├── CacheEntry.java               ← Generic K/V entry with LRU/LFU/FIFO metadata
│   │   └── CacheStats.java               ← Thread-safe hit/miss/eviction counters
│   ├── enums/
│   │   ├── CachePolicy.java              ← LRU | LFU | FIFO
│   │   └── StorageType.java              ← IN_MEMORY_DB | FILE_DB
│   ├── strategy/
│   │   ├── EvictionStrategy.java         ← «interface»
│   │   ├── LRUEvictionStrategy.java
│   │   ├── LFUEvictionStrategy.java
│   │   └── FIFOEvictionStrategy.java
│   ├── observer/
│   │   ├── CacheEventListener.java       ← «interface»
│   │   ├── LoggingCacheListener.java     ← Timestamped console logger
│   │   └── StatisticsListener.java       ← Delegates to CacheStats
│   ├── storage/
│   │   ├── DatabaseStorage.java          ← «interface»
│   │   └── InMemoryDatabaseStorage.java  ← HashMap-backed DB simulation
│   └── services/
│       └── CacheSystemDemo.java          ← End-to-end demo
└── ARCHITECTURE.md                       ← This file
```

---

## Component Responsibilities

| Component | Responsibility |
|-----------|---------------|
| `CacheManager` | Orchestrates all operations; holds the RW lock, strategy, DB, and listeners |
| `CacheEntry<K,V>` | Stores value + access metadata (`frequency`, `lastAccessTime`, `createdAt`) |
| `CacheStats` | Thread-safe counter bag for observability (hit ratio, evictions, DB fallbacks) |
| `EvictionStrategy` | Selects which key to remove when cache is full |
| `LRUEvictionStrategy` | Picks entry with oldest last-access time |
| `LFUEvictionStrategy` | Picks entry with lowest frequency, LRU tie-break |
| `FIFOEvictionStrategy` | Picks entry with earliest creation time |
| `DatabaseStorage` | Interface for the persistent storage backend |
| `InMemoryDatabaseStorage` | HashMap simulation of a real DB |
| `CacheEventListener` | Observer interface for `onHit`, `onMiss`, `onEvict`, `onDbFallback` |
| `LoggingCacheListener` | Prints timestamped event logs |
| `StatisticsListener` | Increments `CacheStats` counters |
| `CacheSystemDemo` | Demonstrates LRU/LFU/FIFO/concurrency/stats in one runnable class |
