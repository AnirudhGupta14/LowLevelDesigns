package observer;

import model.CacheStats;

/**
 * Concrete observer that records hit / miss / eviction / DB-fallback counts
 * into a {@link CacheStats} object.
 *
 * @param <K> Key type
 */
public class StatisticsListener<K> implements CacheEventListener<K> {

    private final CacheStats stats;

    public StatisticsListener(CacheStats stats) {
        this.stats = stats;
    }

    @Override
    public void onHit(K key) {
        stats.incrementHits();
    }

    @Override
    public void onMiss(K key) {
        stats.incrementMisses();
    }

    @Override
    public void onEvict(K key) {
        stats.incrementEvictions();
    }

    @Override
    public void onDbFallback(K key) {
        stats.incrementDbFallbacks();
    }
}
