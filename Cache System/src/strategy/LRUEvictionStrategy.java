package strategy;

import model.CacheEntry;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

/**
 * Least Recently Used (LRU) eviction strategy.
 *
 * <p>
 * Evicts the entry whose {@code lastAccessTime} is the oldest among all
 * current cache entries. Ties are broken arbitrarily (first found).
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class LRUEvictionStrategy<K, V> implements EvictionStrategy<K, V> {

    @Override
    public K evict(Map<K, CacheEntry<K, V>> cacheMap) {
        Optional<Map.Entry<K, CacheEntry<K, V>>> oldest = cacheMap.entrySet()
                .stream()
                .min(Comparator.comparing(a -> a.getValue().getLastAccessTime()));
        return oldest.map(Map.Entry::getKey).orElse(null);
    }
}
