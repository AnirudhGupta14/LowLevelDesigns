package db;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class SimpleDBStorage<K, V> implements DBStorage<K, V> {
    private final ConcurrentHashMap<K, V> database;
    

    public SimpleDBStorage() {
        this.database = new ConcurrentHashMap<>();
    }
    
    @Override
    public CompletableFuture<Void> write(K key, V value) {
        return CompletableFuture.runAsync(() -> {
            // Simulate database write delay
            simulateDatabaseDelay();
            database.put(key, value);
            System.out.println("DB: Written key=" + key + ", value=" + value);
        });
    }
    
    @Override
    public CompletableFuture<V> read(K key) {
        return CompletableFuture.supplyAsync(() -> {
            // Simulate database read delay
            simulateDatabaseDelay();
            V value = database.get(key);
            System.out.println("DB: Read key=" + key + ", value=" + value);
            return value;
        });
    }
    
    @Override
    public CompletableFuture<Void> delete(K key) {
        return CompletableFuture.runAsync(() -> {
            // Simulate database delete delay
            simulateDatabaseDelay();
            database.remove(key);
            System.out.println("DB: Deleted key=" + key);
        });
    }
    
    /**
     * Simulates database operation delay.
     * In a real system, this would be the actual database latency.
     */
    private void simulateDatabaseDelay() {
        try {
            // Random delay between 10-50ms to simulate database operations
            Thread.sleep(ThreadLocalRandom.current().nextInt(10, 51));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
