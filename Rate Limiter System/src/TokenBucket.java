import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TokenBucket implements RateLimiter {
    private final int bucketCapacity;
    private final int refreshRatePerSecond;
    private final AtomicInteger currentCapacity;
    private final AtomicLong lastUpdatedTime;

    public TokenBucket(int bucketCapacity, int refreshRatePerSecond) {
        this.bucketCapacity = bucketCapacity;
        this.refreshRatePerSecond = refreshRatePerSecond;
        this.currentCapacity = new AtomicInteger(bucketCapacity);
        this.lastUpdatedTime = new AtomicLong(System.currentTimeMillis());
    }

    @Override
    public boolean grantAccess() {
        refillTokens();

        // Optimistic Concurrency to decrement tokens safely
        while (true) {
            int currentTokens = currentCapacity.get();
            if (currentTokens == 0) {
                return false; // Fast fail: Empty Bucket
            }
            if (currentCapacity.compareAndSet(currentTokens, currentTokens - 1)) {
                return true; // Fast win: Decremented atomically
            }
        }
    }

    private void refillTokens() {
        long currentTime = System.currentTimeMillis();
        long lastTime = lastUpdatedTime.get();
        double elapsedTimeInSeconds = (currentTime - lastTime) / 1000.0;

        int tokensToAdd = (int) (elapsedTimeInSeconds * refreshRatePerSecond);

        if (tokensToAdd > 0) {
            int currentTokens = currentCapacity.get();
            int newTokens = Math.min(bucketCapacity, currentTokens + tokensToAdd);

            // Atomically update capacity and timestamp
            if (currentCapacity.compareAndSet(currentTokens, newTokens)) {
                lastUpdatedTime.compareAndSet(lastTime, currentTime);
            }
        }
    }
}
