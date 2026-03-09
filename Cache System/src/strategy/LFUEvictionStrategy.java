package strategy;

import model.CacheEntry;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

/**
 * Least Frequently Used (LFU) eviction strategy.
 *
 * <p>
 * Evicts the entry with the lowest access frequency. When two entries share
 * the same frequency the one with the older {@code lastAccessTime} is chosen
 * (i.e. LRU tie-breaking among equally frequent entries).
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class LFUEvictionStrategy<K, V> implements EvictionStrategy<K, V> {

    @Override
    public K evict(Map<K, CacheEntry<K, V>> cacheMap) {
        Optional<Map.Entry<K, CacheEntry<K, V>>> candidate = cacheMap.entrySet()
                .stream()
                .min(Comparator.<Map.Entry<K, CacheEntry<K, V>>>comparingInt(e -> e.getValue().getFrequency())
                        .thenComparing(e -> e.getValue().getLastAccessTime()));
        return candidate.map(Map.Entry::getKey).orElse(null);
    }
}
