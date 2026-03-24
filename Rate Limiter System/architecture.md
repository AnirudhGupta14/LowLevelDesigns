# Rate Limiter System Design Architecture

This module implements a Thread-Safe Rate Limiter leveraging the **Strategy Pattern**. It defines an overarching `RateLimiter` interface which allows dynamic switching between 4 industry-standard algorithms depending on exact system requirements, such as allowing burst traffic versus enforcing a strict uniform outflow.

## Block Diagram

```mermaid
classDiagram
    class RateLimiter {
        <<interface>>
        +grantAccess() boolean
    }
    
    class TokenBucket {
        -int bucketCapacity
        -int refreshRate
        -AtomicInteger currentCapacity
        -AtomicLong lastUpdatedTime
        +grantAccess() boolean
    }
    
    class LeakyBucket {
        -BlockingQueue queue
        +grantAccess() boolean
    }
    
    class FixedWindowCounter {
        -int maxRequests
        -long windowSizeInMillis
        -AtomicInteger counter
        -AtomicLong windowStartTime
        +grantAccess() boolean
    }
    
    class SlidingWindowLog {
        -int maxRequests
        -long windowSizeInMillis
        -Queue~Long~ slidingWindow
        +grantAccess() boolean
    }

    class UserRateLimiter {
        -Map~String, RateLimiter~ userLimits
        +isAllowed(userId) boolean
    }

    RateLimiter <|.. TokenBucket
    RateLimiter <|.. LeakyBucket
    RateLimiter <|.. FixedWindowCounter
    RateLimiter <|.. SlidingWindowLog
    UserRateLimiter "1" *-- "many" RateLimiter : manages
```
