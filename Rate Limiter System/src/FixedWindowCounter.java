import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FixedWindowCounter implements RateLimiter {
    private final int maxRequests;
    private final long windowSizeInMillis;
    private final AtomicInteger counter;
    private final AtomicLong windowStartTime;

    public FixedWindowCounter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
        this.counter = new AtomicInteger(0);
        this.windowStartTime = new AtomicLong(System.currentTimeMillis());
    }

    @Override
    public boolean grantAccess() {
        long currentTime = System.currentTimeMillis();
        long startTime = windowStartTime.get();

        // If current time breached the absolute window boundary, reset it atomically
        if (currentTime - startTime >= windowSizeInMillis) {
            if (windowStartTime.compareAndSet(startTime, currentTime)) {
                counter.set(0);
            }
        }

        // Fast thread-safe counter incrementation
        return counter.incrementAndGet() <= maxRequests;
    }
}
