package model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe statistics container for a cache instance.
 *
 * <p>
 * All counters use {@link AtomicInteger} so they can safely be incremented
 * from the {@code StatisticsListener} while the cache is under concurrent load.
 */
public class CacheStats {

    private final AtomicInteger hits = new AtomicInteger(0);
    private final AtomicInteger misses = new AtomicInteger(0);
    private final AtomicInteger evictions = new AtomicInteger(0);
    private final AtomicInteger dbFallbacks = new AtomicInteger(0);

    public void incrementHits() {
        hits.incrementAndGet();
    }

    public void incrementMisses() {
        misses.incrementAndGet();
    }

    public void incrementEvictions() {
        evictions.incrementAndGet();
    }

    public void incrementDbFallbacks() {
        dbFallbacks.incrementAndGet();
    }

    public int getHits() {
        return hits.get();
    }

    public int getMisses() {
        return misses.get();
    }

    public int getEvictions() {
        return evictions.get();
    }

    public int getDbFallbacks() {
        return dbFallbacks.get();
    }

    public double hitRatio() {
        int total = hits.get() + misses.get();
        return total == 0 ? 0.0 : (double) hits.get() / total * 100.0;
    }

    @Override
    public String toString() {
        return String.format(
                "CacheStats{hits=%d, misses=%d, evictions=%d, dbFallbacks=%d, hitRatio=%.1f%%}",
                getHits(), getMisses(), getEvictions(), getDbFallbacks(), hitRatio());
    }
}
