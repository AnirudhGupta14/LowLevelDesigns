package storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory {@link HashMap}-backed implementation of {@link DatabaseStorage}.
 *
 * <p>
 * This simulates a real persistent database for the purposes of the demo.
 * In production this would be replaced by a JDBC, Redis, or file-based store.
 *
 * <p>
 * <strong>Thread-safety note:</strong> The {@code CacheManager} holds a write
 * lock before calling {@code save()} or {@code delete()} and a read lock before
 * calling {@code get()}, so this class itself does not need additional
 * synchronisation.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class InMemoryDatabaseStorage<K, V> implements DatabaseStorage<K, V> {

    private final Map<K, V> store = new HashMap<>();

    @Override
    public void save(K key, V value) {
        store.put(key, value);
        System.out.println("    [DB] WRITE  key=" + key + "  value=" + value);
    }

    @Override
    public Optional<V> get(K key) {
        V value = store.get(key);
        if (value != null) {
            System.out.println("    [DB] READ   key=" + key + "  value=" + value);
        }
        return Optional.ofNullable(value);
    }

    @Override
    public void delete(K key) {
        store.remove(key);
        System.out.println("    [DB] DELETE key=" + key);
    }

    @Override
    public boolean exists(K key) {
        return store.containsKey(key);
    }

    @Override
    public int size() {
        return store.size();
    }

    @Override
    public String toString() {
        return "InMemoryDatabaseStorage" + store.toString();
    }
}
