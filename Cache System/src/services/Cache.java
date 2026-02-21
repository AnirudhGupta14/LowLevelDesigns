package services;

import cache.CacheStorage;
import cache.InMemoryCacheStorage;
import db.DBStorage;
import db.SimpleDBStorage;
import eviction.EvictionAlgorithm;
import eviction.LRUEvictionAlgorithm;
import lombok.Getter;
import lombok.Setter;
import writePolicy.WritePolicy;
import writePolicy.WriteThroughPolicy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
public class Cache<K, V> {
    private final CacheStorage<K, V> cacheStorage;
    private final DBStorage<K, V> dbStorage;
    private final WritePolicy<K, V> writePolicy;
    private final EvictionAlgorithm<K> evictionAlgorithm;
    private final KeyBasedExecutor keyBasedExecutor;
    private final ExecutorService executorService;
    
    // Performance monitoring
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);
    private final AtomicLong dbHits = new AtomicLong(0);
    private final AtomicLong dbMisses = new AtomicLong(0);

    public Cache(int capacity, int threadPoolSize) {
        this.cacheStorage = new InMemoryCacheStorage<>(capacity);
        this.dbStorage = new SimpleDBStorage<>();
        this.writePolicy = new WriteThroughPolicy<>();
        this.evictionAlgorithm = new LRUEvictionAlgorithm<>(cacheStorage);
        this.keyBasedExecutor = new KeyBasedExecutor(threadPoolSize);
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    public CompletableFuture<Void> updateData(K key, V value) {
        return keyBasedExecutor.execute(key, () -> {
            try {
                // Write through to both cache and database
                writePolicy.write(key, value, cacheStorage, dbStorage).get();

                // Update eviction algorithm
                evictionAlgorithm.keyAccessed(key).get();

                return null;
            } catch (Exception e) {
                throw new RuntimeException("Failed to update data for key: " + key, e);
            }
        });
    }

    public CompletableFuture<V> accessData(K key) {
        return keyBasedExecutor.execute(key, () -> {
            try {
                // Try cache first
                V value = cacheStorage.get(key).get();
                if (value != null) {
                    cacheHits.incrementAndGet();
                    evictionAlgorithm.keyAccessed(key).get();
                    return value;
                }
                
                // services.Cache miss - try database
                cacheMisses.incrementAndGet();
                value = dbStorage.read(key).get();
                if (value != null) {
                    dbHits.incrementAndGet();
                    // Load into cache
                    cacheStorage.put(key, value).get();
                    evictionAlgorithm.keyAccessed(key).get();
                    return value;
                }
                
                // Not found anywhere
                dbMisses.incrementAndGet();
                return null;
                
            } catch (Exception e) {
                throw new RuntimeException("Failed to access data for key: " + key, e);
            }
        });
    }

    public CompletableFuture<V> removeData(K key) {
        return keyBasedExecutor.execute(key, () -> {
            try {
                // Remove from cache
                V cacheValue = cacheStorage.remove(key).get();
                
                // Remove from database
                dbStorage.delete(key).get();
                
                // Update eviction algorithm
                evictionAlgorithm.removeKey(key).get();
                
                // Return the value that was found
                return cacheValue;
                
            } catch (Exception e) {
                throw new RuntimeException("Failed to remove data for key: " + key, e);
            }
        });
    }

    public CompletableFuture<CacheStats> getStats() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int cacheSize = cacheStorage.size().get();
                int cacheCapacity = cacheStorage.getCapacity();
                boolean isFull = cacheStorage.isFull().get();
                
                return new CacheStats(
                    cacheHits.get(),
                    cacheMisses.get(),
                    dbHits.get(),
                    dbMisses.get(),
                    cacheSize,
                    cacheCapacity,
                    isFull
                );
            } catch (Exception e) {
                throw new RuntimeException("Failed to get cache statistics", e);
            }
        }, executorService);
    }

    public void shutdown() {
        try {
            keyBasedExecutor.shutdown();
            executorService.shutdown();
        } catch (Exception e) {
            System.err.println("Error during cache shutdown: " + e.getMessage());
        }
    }
}
