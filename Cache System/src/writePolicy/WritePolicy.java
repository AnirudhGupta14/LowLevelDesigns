package writePolicy;

import cache.CacheStorage;
import db.DBStorage;

import java.util.concurrent.CompletableFuture;

public interface WritePolicy<K, V> {

    CompletableFuture<Void> write(K key, V value, CacheStorage<K, V> cacheStorage, DBStorage<K, V> dbStorage);
}
