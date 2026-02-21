package eviction;

import cache.CacheStorage;

import java.util.concurrent.CompletableFuture;

public class LRUEvictionAlgorithm<K> implements EvictionAlgorithm<K> {
    private final DoublyLinkedList<K> accessOrder;
    private final CacheStorage<K, ?> cacheStorage;

    public LRUEvictionAlgorithm(CacheStorage<K, ?> cacheStorage) {
        this.cacheStorage = cacheStorage;
        this.accessOrder = new DoublyLinkedList<>();
    }
    
    @Override
    public CompletableFuture<Void> keyAccessed(K key) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Check if key exists in cache
                if (cacheStorage.containsKey(key).get()) {
                    // Move to head (most recently used)
                    accessOrder.moveToHead(key);
                } else {
                    // Add new key to head
                    accessOrder.addToHead(key);
                    
                    // Check if cache is full and evict if necessary
                    if (cacheStorage.isFull().get()) {
                        evictKey().get();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to process key access for: " + key, e);
            }
        });
    }
    
    @Override
    public CompletableFuture<Void> removeKey(K key) {
        return CompletableFuture.runAsync(() -> {
            try {
                accessOrder.removeKey(key);
            } catch (Exception e) {
                throw new RuntimeException("Failed to remove key from eviction tracking: " + key, e);
            }
        });
    }

    @Override
    public CompletableFuture<K> evictKey() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get the least recently used key (tail of the list)
                K lruKey = accessOrder.getTailKey();
                if (lruKey != null) {
                    // Remove from cache
                    cacheStorage.remove(lruKey).get();
                    // Remove from access order tracking
                    accessOrder.removeKey(lruKey);
                    System.out.println("Evicted LRU key: " + lruKey);
                }
                return lruKey;
            } catch (Exception e) {
                throw new RuntimeException("Failed to evict LRU key", e);
            }
        });
    }
}
