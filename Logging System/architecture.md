# 📝 Logging System — Architecture

## Overview

A Java-based Logging Framework built from scratch using **Chain of Responsibility**, **Strategy**, and **Singleton** design patterns. Supports multiple log levels, pluggable output destinations (console, file), dynamic level changes at runtime, and thread-safe logging.

---

## Block Diagram

```mermaid
graph TB
    subgraph Entry Point
        Main["Main.java"]
    end

    subgraph Services
        Logger["Logger (Singleton)"]
        Config["LoggerConfig"]
    end

    subgraph LogHandlers ["Log Handlers (Chain of Responsibility)"]
        LH["LogHandler (abstract)"]
        DL["DebugLogger"]
        IL["InfoLogger"]
        WL["WarnLogger"]
        EL["ErrorLogger"]
        FL["FatalLogger"]
    end

    subgraph Appenders ["Appenders (Strategy Pattern)"]
        LA["«interface» LogAppender"]
        CA["ConsoleAppender"]
        FA["FileAppender"]
    end

    subgraph Constants
        LL["LogLevel (enum)"]
    end

    Main -->|uses| Logger
    Logger -->|reads config from| Config
    Config -->|holds| LA
    Config -->|has min| LL

    Logger -->|delegates to chain| DL
    DL -->|next| IL
    IL -->|next| WL
    WL -->|next| EL
    EL -->|next| FL

    DL -->|extends| LH
    IL -->|extends| LH
    WL -->|extends| LH
    EL -->|extends| LH
    FL -->|extends| LH

    LH -->|writes to| LA
    CA -->|implements| LA
    FA -->|implements| LA
```

---

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Chain of Responsibility** | `LogHandler` → `DebugLogger` → `InfoLogger` → `WarnLogger` → `ErrorLogger` → `FatalLogger` | Each handler checks if the message meets its severity threshold, processes it, and passes to the next |
| **Strategy** | `LogAppender` → `ConsoleAppender`, `FileAppender` | Pluggable output destinations — easily add database, network, or custom appenders |
| **Singleton** | `Logger` | Single global logger instance ensures consistent logging across the application |

---

## Class Diagram

```mermaid
classDiagram
    class LogLevel {
        <<enum>>
        DEBUG = 1
        INFO = 2
        WARN = 3
        ERROR = 4
        FATAL = 5
        +getSeverity() int
    }

    class LogAppender {
        <<interface>>
        +append(level, message)
    }

    class ConsoleAppender {
        +append(level, message)
    }

    class FileAppender {
        -String filePath
        +append(level, message)
        +getFilePath() String
    }

    class LogHandler {
        <<abstract>>
        #LogLevel level
        -LogHandler nextHandler
        -List~LogAppender~ appenders
        +setNextHandler(handler) LogHandler
        +setAppenders(appenders)
        +handleLog(level, message)
    }

    class DebugLogger { }
    class InfoLogger { }
    class WarnLogger { }
    class ErrorLogger { }
    class FatalLogger { }

    class LoggerConfig {
        -LogLevel minLogLevel
        -List~LogAppender~ appenders
        +addAppender(appender)
        +removeAppender(appender)
        +setMinLogLevel(level)
    }

    class Logger {
        -static Logger instance
        -LoggerConfig config
        -LogHandler chainHead
        +static getInstance(config) Logger
        +debug(message)
        +info(message)
        +warn(message)
        +error(message)
        +fatal(message)
        +log(level, message)
        +refreshConfig()
    }

    ConsoleAppender ..|> LogAppender
    FileAppender ..|> LogAppender
    DebugLogger --|> LogHandler
    InfoLogger --|> LogHandler
    WarnLogger --|> LogHandler
    ErrorLogger --|> LogHandler
    FatalLogger --|> LogHandler
    LogHandler --> LogAppender : writes to
    LogHandler --> LogHandler : next in chain
    Logger --> LoggerConfig
    Logger --> LogHandler : delegates to chain
    LoggerConfig --> LogAppender
    LoggerConfig --> LogLevel
```

---

## Chain of Responsibility Flow

```mermaid
flowchart LR
    A["logger.error('msg')"] --> B["Logger.log()"]
    B --> C{Level >= minLevel?}
    C -->|No| D["Silently skip"]
    C -->|Yes| E["DebugLogger"]
    E -->|"severity >= DEBUG? → write"| F["InfoLogger"]
    F -->|"severity >= INFO? → write"| G["WarnLogger"]
    G -->|"severity >= WARN? → write"| H["ErrorLogger"]
    H -->|"severity >= ERROR? ✅ → write"| I["FatalLogger"]
    I -->|"severity >= FATAL? → skip"| J["End of chain"]
```

---

## Log Message Flow

```mermaid
flowchart TD
    A["Application calls logger.info('message')"] --> B["Logger checks minLogLevel"]
    B --> C{INFO >= minLevel?}
    C -->|No| D["Message silently dropped"]
    C -->|Yes| E["Pass to chain head"]
    E --> F["Handler checks its level threshold"]
    F --> G{Message level >= Handler level?}
    G -->|Yes| H["Write to all Appenders"]
    G -->|No| I["Skip writing"]
    H --> J["Pass to next handler"]
    I --> J
    J --> K{More handlers?}
    K -->|Yes| F
    K -->|No| L["Done"]

    H --> M["ConsoleAppender\n(stdout / stderr)"]
    H --> N["FileAppender\n(application.log)"]
```

---

## Component Responsibilities

### Constants

| Class | Responsibility |
|-------|---------------|
| `LogLevel` | Enum with severity ordering: DEBUG(1) < INFO(2) < WARN(3) < ERROR(4) < FATAL(5) |

### Appenders (Strategy)

| Class | Responsibility |
|-------|---------------|
| `LogAppender` | Interface — defines `append(level, message)` contract |
| `ConsoleAppender` | Writes to stdout (or stderr for ERROR/FATAL) with timestamp |
| `FileAppender` | Appends to a log file with timestamp formatting |

### Log Handlers (Chain of Responsibility)

| Class | Responsibility |
|-------|---------------|
| `LogHandler` | Abstract base — checks level threshold, writes to appenders, forwards to next handler |
| `DebugLogger` | Handles DEBUG+ messages |
| `InfoLogger` | Handles INFO+ messages |
| `WarnLogger` | Handles WARN+ messages |
| `ErrorLogger` | Handles ERROR+ messages |
| `FatalLogger` | Handles FATAL messages |

### Services

| Class | Responsibility |
|-------|---------------|
| `LoggerConfig` | Holds minimum log level and appender list. Supports runtime changes |
| `Logger` | Singleton facade — builds chain, filters by level, provides `debug/info/warn/error/fatal` convenience methods |

---

## Key Features

| Feature | Implementation |
|---------|---------------|
| **Multiple log levels** | DEBUG < INFO < WARN < ERROR < FATAL with severity filtering |
| **Pluggable appenders** | Strategy pattern — add Console, File, or custom appenders |
| **Dynamic level change** | Change `minLogLevel` at runtime via `LoggerConfig.setMinLogLevel()` + `Logger.refreshConfig()` |
| **Thread-safe** | All log methods are `synchronized` |
| **Timestamp formatting** | `yyyy-MM-dd HH:mm:ss` format in all appenders |
| **stderr for errors** | `ConsoleAppender` routes ERROR/FATAL to `System.err` |
| **Chain rebuilding** | Logger reconstructs handler chain on config change |

---

## Folder Structure

```
Logging System/
├── architecture.md
└── src/
    ├── Main.java                     (entry point + demo)
    ├── Appenders/
    │   ├── ConsoleAppender.java
    │   ├── FileAppender.java
    │   └── LogAppender.java          (interface)
    ├── Constants/
    │   └── LogLevel.java             (enum)
    ├── LogHandlers/
    │   ├── DebugLogger.java
    │   ├── ErrorLogger.java
    │   ├── FatalLogger.java
    │   ├── InfoLogger.java
    │   ├── LogHandler.java           (abstract — Chain of Responsibility)
    │   └── WarnLogger.java
    └── Services/
        ├── Logger.java               (Singleton)
        └── LoggerConfig.java
```
