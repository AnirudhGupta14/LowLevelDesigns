package strategy;

import model.CacheEntry;

import java.util.Map;
import java.util.Optional;

/**
 * First-In First-Out (FIFO) eviction strategy.
 *
 * <p>
 * Evicts the entry that was inserted into the cache earliest, regardless of
 * how often or how recently it was accessed. Uses
 * {@code CacheEntry#getCreatedAt()}
 * as the ordering key.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class FIFOEvictionStrategy<K, V> implements EvictionStrategy<K, V> {

    @Override
    public K evict(Map<K, CacheEntry<K, V>> cacheMap) {
        Optional<Map.Entry<K, CacheEntry<K, V>>> oldest = cacheMap.entrySet()
                .stream()
                .min((a, b) -> a.getValue().getCreatedAt()
                        .compareTo(b.getValue().getCreatedAt()));
        return oldest.map(Map.Entry::getKey).orElse(null);
    }
}
