package cache;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCacheStorage<K, V> implements CacheStorage<K, V> {
    private final ConcurrentHashMap<K, V> cache;
    private final int capacity;

    public InMemoryCacheStorage(int capacity) {
        this.capacity = capacity;
        this.cache = new ConcurrentHashMap<>(capacity);
    }
    
    @Override
    public CompletableFuture<Void> put(K key, V value) {
        return CompletableFuture.runAsync(() -> cache.put(key, value));
    }
    
    @Override
    public CompletableFuture<V> get(K key) {
        return CompletableFuture.supplyAsync(() -> cache.get(key));
    }
    
    @Override
    public CompletableFuture<V> remove(K key) {
        return CompletableFuture.supplyAsync(() -> cache.remove(key));
    }
    
    @Override
    public CompletableFuture<Boolean> containsKey(K key) {
        return CompletableFuture.supplyAsync(() -> cache.containsKey(key));
    }
    
    @Override
    public CompletableFuture<Integer> size() {
        return CompletableFuture.supplyAsync(() -> cache.size());
    }
    
    @Override
    public int getCapacity() {
        return capacity;
    }
    
    @Override
    public CompletableFuture<Boolean> isFull() {
        return CompletableFuture.supplyAsync(() -> cache.size() >= capacity);
    }
}
