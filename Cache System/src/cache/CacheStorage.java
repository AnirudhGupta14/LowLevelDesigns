package cache;

import java.util.concurrent.CompletableFuture;


public interface CacheStorage<K, V> {

    CompletableFuture<Void> put(K key, V value);

    CompletableFuture<V> get(K key);

    CompletableFuture<V> remove(K key);

    CompletableFuture<Boolean> containsKey(K key);

    CompletableFuture<Integer> size();

    int getCapacity();

    CompletableFuture<Boolean> isFull();
}
