package writePolicy;

import cache.CacheStorage;
import db.DBStorage;

import java.util.concurrent.CompletableFuture;

public class WriteThroughPolicy<K, V> implements WritePolicy<K, V> {
    
    @Override
    public CompletableFuture<Void> write(K key, V value, CacheStorage<K, V> cacheStorage, DBStorage<K, V> dbStorage) {
        // Write to cache and database in parallel
        CompletableFuture<Void> cacheWrite = cacheStorage.put(key, value);
        CompletableFuture<Void> dbWrite = dbStorage.write(key, value);
        
        // Wait for both operations to complete
        return CompletableFuture.allOf(cacheWrite, dbWrite);
    }
}
