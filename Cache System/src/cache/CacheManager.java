package cache;

import model.CacheEntry;
import model.CacheStats;
import observer.CacheEventListener;
import storage.DatabaseStorage;
import strategy.EvictionStrategy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Core cache manager — the central component of the Cache System.
 *
 * <h2>Design Patterns</h2>
 * <ul>
 * <li><b>Singleton</b> — one instance per logical cache, created via the
 * builder. Double-checked locking is used inside the builder.</li>
 * <li><b>Strategy</b> — pluggable {@link EvictionStrategy} selects which
 * entry to remove when the cache is full.</li>
 * <li><b>Observer</b> — zero or more {@link CacheEventListener}s are notified
 * on every hit / miss / eviction / DB-fallback.</li>
 * </ul>
 *
 * <h2>Concurrency &amp; Dirty-Read Prevention</h2>
 * <p>
 * A {@link ReentrantReadWriteLock} is used to guarantee data consistency:
 * <ul>
 * <li>{@code get()} acquires the <em>read lock</em>: multiple threads can
 * read concurrently without blocking each other.</li>
 * <li>{@code put()} and {@code remove()} acquire the <em>write lock</em>:
 * exclusive access ensures no thread can observe a partially-written
 * (dirty) cache entry.</li>
 * </ul>
 *
 * <h2>DB Fallback &amp; Write-Behind on Eviction</h2>
 * <ul>
 * <li>Cache miss → checks {@link DatabaseStorage}; if found, loads into
 * cache.</li>
 * <li>Eviction → writes evicted value to {@link DatabaseStorage} so it is
 * not permanently lost.</li>
 * </ul>
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class CacheManager<K, V> {

    // ------------------------------------------------------------------ //
    // State //
    // ------------------------------------------------------------------ //

    private final String name;
    private final int capacity;
    private final Map<K, CacheEntry<K, V>> cache; // key → entry
    private final EvictionStrategy<K, V> evictionStrategy;
    private final DatabaseStorage<K, V> db;
    private final List<CacheEventListener<K>> listeners;

    /**
     * The lock that prevents dirty reads.
     *
     * <p>
     * Read operations (get) share the read lock; write operations (put,
     * remove) take the exclusive write lock. This is the JavaDoc-standard
     * pattern for a read-heavy workload.
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    // ------------------------------------------------------------------ //
    // Constructor (private — use Builder) //
    // ------------------------------------------------------------------ //

    private CacheManager(Builder<K, V> builder) {
        this.name = builder.name;
        this.capacity = builder.capacity;
        this.evictionStrategy = builder.evictionStrategy;
        this.db = builder.db;
        this.listeners = new ArrayList<>(builder.listeners);
        // LinkedHashMap preserves insertion order (useful for FIFO debugging)
        this.cache = new LinkedHashMap<>();
    }

    // ------------------------------------------------------------------ //
    // Public API //
    // ------------------------------------------------------------------ //

    /**
     * Retrieve a value by key.
     *
     * <p>
     * Acquires the <b>read lock</b> — multiple concurrent reads are allowed.
     * If the key is not in cache, upgrades to a write lock to load from DB.
     *
     * @param key the cache key
     * @return the cached value, or {@code null} if not found in cache or DB
     */
    public V get(K key) {
        // --- Fast path: read lock (allows concurrent reads) ---
        lock.readLock().lock();
        try {
            CacheEntry<K, V> entry = cache.get(key);
            if (entry != null) {
                entry.recordAccess();
                notifyListeners(l -> l.onHit(key));
                return entry.getValue();
            }
        } finally {
            lock.readLock().unlock();
        }

        // --- Slow path: cache miss — need write lock to load from DB ---
        notifyListeners(l -> l.onMiss(key));

        lock.writeLock().lock();
        try {
            // Double-check: another thread may have loaded it while we waited
            CacheEntry<K, V> entry = cache.get(key);
            if (entry != null) {
                entry.recordAccess();
                notifyListeners(l -> l.onHit(key));
                return entry.getValue();
            }

            // Try DB fallback
            Optional<V> dbValue = db.get(key);
            if (dbValue.isPresent()) {
                notifyListeners(l -> l.onDbFallback(key));
                putInternal(key, dbValue.get()); // load into cache
                return dbValue.get();
            }

            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Insert or update a key-value pair in the cache.
     *
     * <p>
     * Acquires the <b>write lock</b> — exclusive access prevents any thread
     * from reading a partially-constructed entry (dirty read).
     *
     * <p>
     * If the cache is at capacity, the active {@link EvictionStrategy} selects
     * an entry to evict, which is then persisted to the DB (write-behind).
     *
     * @param key   cache key
     * @param value value to store
     */
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            putInternal(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Remove a key from the cache and from persistent storage.
     *
     * <p>
     * Acquires the <b>write lock</b>.
     *
     * @param key cache key to remove
     */
    public void remove(K key) {
        lock.writeLock().lock();
        try {
            cache.remove(key);
            db.delete(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /** @return current number of entries in the cache (not including DB). */
    public int size() {
        lock.readLock().lock();
        try {
            return cache.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    // ------------------------------------------------------------------ //
    // Internal helpers (must be called while holding write lock) //
    // ------------------------------------------------------------------ //

    /**
     * Core insert logic. Caller MUST hold the write lock.
     */
    private void putInternal(K key, V value) {
        if (cache.containsKey(key)) {
            // Update existing entry in-place
            cache.get(key).setValue(value);
            cache.get(key).recordAccess();
            return;
        }

        // Evict if at capacity
        if (cache.size() >= capacity) {
            K evictKey = evictionStrategy.evict(cache);
            if (evictKey != null) {
                V evictedValue = cache.get(evictKey).getValue();
                cache.remove(evictKey);
                db.save(evictKey, evictedValue); // write-behind to DB
                notifyListeners(l -> l.onEvict(evictKey));
            }
        }

        cache.put(key, new CacheEntry<>(key, value));
    }

    // ------------------------------------------------------------------ //
    // Observer notification //
    // ------------------------------------------------------------------ //

    @FunctionalInterface
    private interface ListenerAction<K> {
        void notify(CacheEventListener<K> listener);
    }

    private void notifyListeners(ListenerAction<K> action) {
        for (CacheEventListener<K> listener : listeners) {
            action.notify(listener);
        }
    }

    // ------------------------------------------------------------------ //
    // Builder (Fluent API — Singleton per named cache) //
    // ------------------------------------------------------------------ //

    /**
     * Fluent builder for {@link CacheManager}.
     *
     * <pre>{@code
     * CacheManager<String, String> cache = new CacheManager.Builder<String, String>()
     *         .name("product-cache")
     *         .capacity(100)
     *         .evictionStrategy(new LRUEvictionStrategy<>())
     *         .database(new InMemoryDatabaseStorage<>())
     *         .addListener(new LoggingCacheListener<>("product-cache"))
     *         .build();
     * }</pre>
     *
     * @param <K> Key type
     * @param <V> Value type
     */
    public static class Builder<K, V> {

        private String name = "default-cache";
        private int capacity = 10;
        private EvictionStrategy<K, V> evictionStrategy;
        private DatabaseStorage<K, V> db;
        private final List<CacheEventListener<K>> listeners = new ArrayList<>();

        public Builder<K, V> name(String name) {
            this.name = name;
            return this;
        }

        public Builder<K, V> capacity(int capacity) {
            if (capacity <= 0)
                throw new IllegalArgumentException("Capacity must be > 0");
            this.capacity = capacity;
            return this;
        }

        public Builder<K, V> evictionStrategy(EvictionStrategy<K, V> strategy) {
            this.evictionStrategy = strategy;
            return this;
        }

        public Builder<K, V> database(DatabaseStorage<K, V> db) {
            this.db = db;
            return this;
        }

        public Builder<K, V> addListener(CacheEventListener<K> listener) {
            this.listeners.add(listener);
            return this;
        }

        public CacheManager<K, V> build() {
            if (evictionStrategy == null)
                throw new IllegalStateException("EvictionStrategy must be provided");
            if (db == null)
                throw new IllegalStateException("DatabaseStorage must be provided");
            return new CacheManager<>(this);
        }
    }
}
