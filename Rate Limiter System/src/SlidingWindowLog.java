import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SlidingWindowLog implements RateLimiter {
    private final int maxRequests;
    private final long windowSizeInMillis;
    private final Queue<Long> slidingWindow;

    public SlidingWindowLog(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
        this.slidingWindow = new ConcurrentLinkedQueue<>();
    }

    @Override
    public boolean grantAccess() {
        long currentTime = System.currentTimeMillis();

        // Evict all purely outdated logs that fall outside the trailing window
        while (!slidingWindow.isEmpty() && slidingWindow.peek() < (currentTime - windowSizeInMillis)) {
            slidingWindow.poll();
        }

        // Because ConcurrentLinkedQueue.size() is O(N), for a massive system this gets
        // slow,
        // but it is mathematically precise.
        if (slidingWindow.size() < maxRequests) {
            slidingWindow.offer(currentTime);
            return true;
        }

        return false;
    }
}
