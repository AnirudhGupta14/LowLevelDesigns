package observer;

/**
 * Observer interface for cache lifecycle events.
 *
 * <p>
 * Implementations are notified synchronously after each cache operation.
 * Any number of listeners can be registered on a {@code CacheManager} instance.
 *
 * @param <K> Key type
 */
public interface CacheEventListener<K> {

    /**
     * Called when a key is found in the cache (cache hit).
     *
     * @param key the accessed key
     */
    void onHit(K key);

    /**
     * Called when a key is NOT found in the cache (cache miss before DB check).
     *
     * @param key the requested key
     */
    void onMiss(K key);

    /**
     * Called when an entry is evicted from the cache to make room.
     *
     * @param key the evicted key
     */
    void onEvict(K key);

    /**
     * Called when a cache miss is recovered from the DB fallback.
     *
     * @param key the key read from DB
     */
    void onDbFallback(K key);
}
