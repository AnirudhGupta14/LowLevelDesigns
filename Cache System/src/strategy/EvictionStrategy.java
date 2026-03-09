package strategy;

import model.CacheEntry;

import java.util.Map;

/**
 * Strategy interface for cache eviction algorithms.
 *
 * <p>
 * Implementors decide <em>which key to evict</em> when the cache is full.
 * The decision is based purely on the metadata stored in each
 * {@link CacheEntry} (frequency, lastAccessTime, createdAt).
 *
 * <p>
 * Design note: keeping this interface generic and stateless means new
 * eviction algorithms can be plugged in with zero changes to
 * {@code CacheManager}.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public interface EvictionStrategy<K, V> {

    /**
     * Select the key to evict from the given cache map.
     *
     * @param cacheMap current snapshot of the cache (key → entry)
     * @return the key that should be removed, or {@code null} if the map is empty
     */
    K evict(Map<K, CacheEntry<K, V>> cacheMap);
}
