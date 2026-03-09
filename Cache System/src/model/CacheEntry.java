package model;

import java.time.LocalDateTime;

/**
 * Generic wrapper for a single cache entry.
 *
 * <p>
 * Tracks:
 * <ul>
 * <li>key / value pair</li>
 * <li>frequency — incremented on every cache hit (used by LFU)</li>
 * <li>lastAccessTime — updated on every cache hit (used by LRU)</li>
 * <li>createdAt — set once at insertion (used by FIFO)</li>
 * </ul>
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class CacheEntry<K, V> {

    private final K key;
    private V value;
    private int frequency;
    private LocalDateTime lastAccessTime;
    private final LocalDateTime createdAt;

    public CacheEntry(K key, V value) {
        this.key = key;
        this.value = value;
        this.frequency = 1;
        this.createdAt = LocalDateTime.now();
        this.lastAccessTime = this.createdAt;
    }

    // ------------------------------------------------------------------ //
    // Accessors //
    // ------------------------------------------------------------------ //

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public int getFrequency() {
        return frequency;
    }

    public LocalDateTime getLastAccessTime() {
        return lastAccessTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // ------------------------------------------------------------------ //
    // Mutation helpers //
    // ------------------------------------------------------------------ //

    /** Called on every successful cache-hit to keep LRU / LFU metadata fresh. */
    public void recordAccess() {
        this.frequency++;
        this.lastAccessTime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "CacheEntry{key=" + key + ", value=" + value
                + ", freq=" + frequency
                + ", lastAccess=" + lastAccessTime
                + ", created=" + createdAt + "}";
    }
}
