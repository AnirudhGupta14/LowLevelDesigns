package storage;

import java.util.Optional;

/**
 * Abstraction over the persistent storage backend (DB layer).
 *
 * <p>
 * The cache treats this as a write-through / read-on-miss store:
 * <ul>
 * <li>When an entry is <em>evicted</em> from the cache it is persisted
 * here.</li>
 * <li>When the cache <em>misses</em>, it checks here before returning
 * null.</li>
 * </ul>
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public interface DatabaseStorage<K, V> {

    /**
     * Persist a key-value entry.
     *
     * @param key   cache key
     * @param value value to store
     */
    void save(K key, V value);

    /**
     * Retrieve an entry by key.
     *
     * @param key cache key
     * @return {@link Optional} containing the value if found, empty otherwise
     */
    Optional<V> get(K key);

    /**
     * Remove an entry from persistent storage.
     *
     * @param key cache key
     */
    void delete(K key);

    /**
     * Check whether a key exists in persistent storage.
     *
     * @param key cache key
     * @return {@code true} if the key exists
     */
    boolean exists(K key);

    /** @return total number of entries currently stored */
    int size();
}
