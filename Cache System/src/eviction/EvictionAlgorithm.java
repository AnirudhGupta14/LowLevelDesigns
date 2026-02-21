package eviction;

import java.util.concurrent.CompletableFuture;

public interface EvictionAlgorithm<K> {

    CompletableFuture<Void> keyAccessed(K key);

    CompletableFuture<K> evictKey();

    CompletableFuture<Void> removeKey(K key);
}
