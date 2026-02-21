package db;

import java.util.concurrent.CompletableFuture;

public interface DBStorage<K, V> {

    CompletableFuture<Void> write(K key, V value);

    CompletableFuture<V> read(K key);

    CompletableFuture<Void> delete(K key);
}
