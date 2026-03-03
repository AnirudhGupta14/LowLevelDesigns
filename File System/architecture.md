# 📁 File System — Architecture

## Overview

A Java-based File System built using **Composite**, **Observer**, **Strategy**, and **Singleton** design patterns. Supports nested directory creation, file CRUD operations, pluggable search algorithms (by name, extension, or size), file move/delete, and real-time change notifications.

---

## Block Diagram

```mermaid
graph TB
    subgraph Entry Point
        Main["Main.java"]
    end

    subgraph Entities
        FSI["FileSystemItem (abstract)"]
        F["File"]
        D["Directory"]
        FT["FileType (enum)"]
    end

    subgraph Services
        FSS["FileSystemService (Singleton)"]
    end

    subgraph Observer ["Observer Pattern"]
        FCS["«interface» FileChangeSubject"]
        FCO["«interface» FileChangeObserver"]
        LO["LoggingObserver"]
    end

    subgraph Strategy ["Search Strategy Pattern"]
        SS["«interface» SearchStrategy"]
        NS["NameSearch"]
        ES["ExtensionSearch"]
        SzS["SizeSearch"]
    end

    Main -->|uses| FSS

    F -->|extends| FSI
    D -->|extends| FSI
    D -->|contains| FSI
    F -->|has| FT

    FSS -->|implements| FCS
    LO -->|implements| FCO
    FSS -->|notifies| FCO

    FSS -->|manages| D
    FSS -->|creates| F
    FSS -->|delegates to| SS
    NS -->|implements| SS
    ES -->|implements| SS
    SzS -->|implements| SS
```

---

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Composite** | `FileSystemItem` → `File`, `Directory` | Unified tree structure — directories recursively contain files and other directories |
| **Observer** | `FileChangeSubject` / `FileChangeObserver` | `LoggingObserver` auto-logs create, delete, and modify events with timestamps |
| **Strategy** | `SearchStrategy` → `NameSearch`, `ExtensionSearch`, `SizeSearch` | Pluggable search algorithms — switch at runtime without modifying client code |
| **Singleton** | `FileSystemService` | Single global file system root ensures data consistency |

---

## Class Diagram

```mermaid
classDiagram
    class FileType {
        <<enum>>
        TEXT
        IMAGE
        VIDEO
        AUDIO
        DOCUMENT
        CODE
        OTHER
    }

    class FileSystemItem {
        <<abstract>>
        -String name
        -LocalDateTime createdAt
        -Directory parent
        +getName() String
        +getPath() String
        +getSize() long*
        +isDirectory() boolean*
        +display(indent)*
        +setParent(parent)
    }

    class File {
        -String content
        -String extension
        -FileType fileType
        +getContent() String
        +getExtension() String
        +getFileType() FileType
        +setContent(content)
        +appendContent(text)
        +getSize() long
        +isDirectory() boolean
    }

    class Directory {
        -List~FileSystemItem~ children
        +addChild(item)
        +removeChild(name) boolean
        +getChild(name) FileSystemItem
        +getChildren() List
        +getChildCount() int
        +getSize() long
        +isDirectory() boolean
    }

    class FileChangeSubject {
        <<interface>>
        +addObserver(observer)
        +removeObserver(observer)
        +notifyCreated(item)
        +notifyDeleted(item)
        +notifyModified(item)
    }

    class FileChangeObserver {
        <<interface>>
        +onFileCreated(item)
        +onFileDeleted(item)
        +onFileModified(item)
    }

    class LoggingObserver {
        +onFileCreated(item)
        +onFileDeleted(item)
        +onFileModified(item)
    }

    class SearchStrategy {
        <<interface>>
        +search(root, criteria) List
    }

    class NameSearch {
        +search(root, criteria) List
    }

    class ExtensionSearch {
        +search(root, criteria) List
    }

    class SizeSearch {
        +search(root, criteria) List
    }

    class FileSystemService {
        -static FileSystemService instance
        -Directory root
        -List observers
        +static getInstance() FileSystemService
        +createDirectory(path) Directory
        +createFile(dir, name, ext, type) File
        +writeToFile(path, content)
        +delete(path)
        +move(source, dest)
        +search(strategy, criteria) List
        +displayTree()
    }

    File --|> FileSystemItem
    Directory --|> FileSystemItem
    Directory o-- FileSystemItem : contains

    File --> FileType

    FileSystemService ..|> FileChangeSubject
    LoggingObserver ..|> FileChangeObserver
    FileSystemService --> FileChangeObserver : notifies

    NameSearch ..|> SearchStrategy
    ExtensionSearch ..|> SearchStrategy
    SizeSearch ..|> SearchStrategy
    FileSystemService --> SearchStrategy : delegates to

    FileSystemService --> Directory : manages root
```

---

## File Operation Flow

```mermaid
flowchart TD
    A["Client calls createFile / writeToFile / delete / move"] --> B{"Validate path exists?"}
    B -->|No| C["❌ Print error message"]
    B -->|Yes| D{"Operation type?"}

    D -->|Create| E["Add to parent Directory"]
    D -->|Write| F["Set file content"]
    D -->|Delete| G["Remove from parent"]
    D -->|Move| H["Remove from source, add to dest"]

    E --> I["notifyCreated()"]
    F --> J["notifyModified()"]
    G --> K["notifyDeleted()"]
    H --> L["Print move confirmation"]

    I --> M["LoggingObserver logs event"]
    J --> M
    K --> M
```

---

## Search Strategy Flow

```mermaid
flowchart LR
    A["Client calls search(strategy, criteria)"] --> B["FileSystemService delegates to SearchStrategy"]
    B --> C{"Which strategy?"}

    C -->|NameSearch| D["Recursive name substring match"]
    C -->|ExtensionSearch| E["Recursive file extension match"]
    C -->|SizeSearch| F["Recursive size > threshold check"]

    D --> G["Return List of matches"]
    E --> G
    F --> G
```

---

## Component Responsibilities

| Component | Responsibility |
|-----------|---------------|
| `FileSystemItem` | Abstract base — provides common `name`, `path`, `size` contract |
| `File` | Leaf node — stores content, extension, type; calculates size from content length |
| `Directory` | Composite node — holds children, recursive size calculation, tree display |
| `FileType` | Categorization enum for files |
| `FileChangeSubject` | Subject interface for observer registration and notifications |
| `FileChangeObserver` | Observer interface for reacting to file events |
| `LoggingObserver` | Concrete observer — logs events with timestamps |
| `SearchStrategy` | Strategy interface for pluggable search algorithms |
| `NameSearch` | Searches by name substring (case-insensitive) |
| `ExtensionSearch` | Searches by file extension |
| `SizeSearch` | Searches for files above a size threshold |
| `FileSystemService` | Singleton — manages root, orchestrates all operations, notifies observers |

---

## Folder Structure

```
File System/
├── architecture.md
└── src/
    ├── Main.java
    ├── entities/
    │   ├── FileSystemItem.java
    │   ├── File.java
    │   ├── Directory.java
    │   └── FileType.java
    ├── Observer/
    │   ├── FileChangeObserver.java
    │   ├── FileChangeSubject.java
    │   └── LoggingObserver.java
    ├── Strategy/
    │   ├── SearchStrategy.java
    │   ├── NameSearch.java
    │   ├── ExtensionSearch.java
    │   └── SizeSearch.java
    └── Services/
        └── FileSystemService.java
```
