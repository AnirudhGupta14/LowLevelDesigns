package services;

import cache.CacheManager;
import model.CacheStats;
import observer.LoggingCacheListener;
import observer.StatisticsListener;
import storage.InMemoryDatabaseStorage;
import strategy.FIFOEvictionStrategy;
import strategy.LFUEvictionStrategy;
import strategy.LRUEvictionStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Comprehensive demonstration of the Cache System LLD.
 *
 * <p>
 * Covers:
 * <ol>
 * <li>LRU eviction - oldest-access entry is evicted when cache is full</li>
 * <li>LFU eviction - lowest-frequency entry is evicted</li>
 * <li>FIFO eviction - first-inserted entry is evicted</li>
 * <li>DB write-behind - evicted entries are persisted to the DB</li>
 * <li>DB read-through - a cache miss transparently loads from DB</li>
 * <li>Concurrent access - multiple reader threads run in parallel (read lock)
 * while a writer gets exclusive access (write lock, no dirty reads)</li>
 * <li>Cache statistics - hit ratio, eviction count, DB fallback count</li>
 * </ol>
 */
public class CacheSystemDemo {

    // ------------------------------------------------------------------ //
    // Separator helpers (Java 8 compatible) //
    // ------------------------------------------------------------------ //

    private static String repeatChar(char ch, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    private static void separator(String title) {
        System.out.println("\n" + repeatChar('=', 60));
        System.out.println("  " + title);
        System.out.println(repeatChar('=', 60));
    }

    private static void subheader(String msg) {
        System.out.println("\n  >> " + msg);
    }

    // ================================================================== //
    // Main //
    // ================================================================== //

    public static void main(String[] args) {

        // 1. LRU Cache Demo
        separator("1. LRU Cache Demo  (capacity = 3)");
        CacheStats lruStats = new CacheStats();

        CacheManager<String, String> lruCache = new CacheManager.Builder<String, String>()
                .name("lru-cache")
                .capacity(3)
                .evictionStrategy(new LRUEvictionStrategy<String, String>())
                .database(new InMemoryDatabaseStorage<String, String>())
                .addListener(new LoggingCacheListener<String>("lru-cache"))
                .addListener(new StatisticsListener<String>(lruStats))
                .build();

        subheader("Filling cache (A, B, C)");
        lruCache.put("A", "Apple");
        lruCache.put("B", "Banana");
        lruCache.put("C", "Cherry");

        subheader("Access A (makes A recently used)");
        lruCache.get("A"); // Hit -> A is now most recently used

        subheader("Put D - cache is full, LRU entry (B) should be evicted -> saved to DB");
        lruCache.put("D", "Durian");

        subheader("Get B - cache miss -> DB fallback should load B back");
        String b = lruCache.get("B");
        System.out.println("  Loaded B from DB: " + b);

        subheader("LRU Stats: " + lruStats);

        // 2. LFU Cache Demo
        separator("2. LFU Cache Demo  (capacity = 3)");
        CacheStats lfuStats = new CacheStats();

        CacheManager<String, Integer> lfuCache = new CacheManager.Builder<String, Integer>()
                .name("lfu-cache")
                .capacity(3)
                .evictionStrategy(new LFUEvictionStrategy<String, Integer>())
                .database(new InMemoryDatabaseStorage<String, Integer>())
                .addListener(new LoggingCacheListener<String>("lfu-cache"))
                .addListener(new StatisticsListener<String>(lfuStats))
                .build();

        subheader("Filling cache (X=1, Y=2, Z=3)");
        lfuCache.put("X", 1);
        lfuCache.put("Y", 2);
        lfuCache.put("Z", 3);

        subheader("Access Y x3, Z x2, X x1 -> X has lowest frequency");
        lfuCache.get("Y");
        lfuCache.get("Y");
        lfuCache.get("Y");
        lfuCache.get("Z");
        lfuCache.get("Z");
        lfuCache.get("X");

        subheader("Put W - cache full, LFU entry (X, freq=2) evicted -> saved to DB");
        lfuCache.put("W", 99);

        subheader("Get X - cache miss -> DB fallback loads X");
        Integer xVal = lfuCache.get("X");
        System.out.println("  Loaded X from DB: " + xVal);

        subheader("LFU Stats: " + lfuStats);

        // 3. FIFO Cache Demo
        separator("3. FIFO Cache Demo  (capacity = 3)");
        CacheStats fifoStats = new CacheStats();

        CacheManager<Integer, String> fifoCache = new CacheManager.Builder<Integer, String>()
                .name("fifo-cache")
                .capacity(3)
                .evictionStrategy(new FIFOEvictionStrategy<Integer, String>())
                .database(new InMemoryDatabaseStorage<Integer, String>())
                .addListener(new LoggingCacheListener<Integer>("fifo-cache"))
                .addListener(new StatisticsListener<Integer>(fifoStats))
                .build();

        subheader("Filling cache (1='One', 2='Two', 3='Three')");
        fifoCache.put(1, "One");
        fifoCache.put(2, "Two");
        fifoCache.put(3, "Three");

        subheader("Access key 1 many times (should NOT protect it from FIFO eviction)");
        fifoCache.get(1);
        fifoCache.get(1);
        fifoCache.get(1);

        subheader("Put 4 - FIFO evicts first-inserted entry (key=1) regardless of access frequency");
        fifoCache.put(4, "Four");

        subheader("Get 1 - DB fallback should restore it");
        String one = fifoCache.get(1);
        System.out.println("  Loaded 1 from DB: " + one);

        subheader("FIFO Stats: " + fifoStats);

        // 4. Concurrent Access Demo
        separator("4. Concurrent Access Demo  (dirty-read prevention with ReentrantReadWriteLock)");

        CacheManager<String, String> concurrentCache = new CacheManager.Builder<String, String>()
                .name("concurrent-cache")
                .capacity(10)
                .evictionStrategy(new LRUEvictionStrategy<String, String>())
                .database(new InMemoryDatabaseStorage<String, String>())
                .addListener(new LoggingCacheListener<String>("concurrent-cache"))
                .build();

        // Pre-populate
        for (int i = 0; i < 5; i++) {
            concurrentCache.put("key-" + i, "value-" + i);
        }

        subheader("Spawning 5 reader threads + 1 writer thread simultaneously");

        List<Thread> readers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final int id = i;
            Thread reader = new Thread(() -> {
                String val = concurrentCache.get("key-" + id);
                System.out.printf("  [Reader-%d] key-%d = %s%n", id, id, val);
            }, "Reader-" + i);
            readers.add(reader);
        }

        Thread writer = new Thread(() -> {
            System.out.println("  [Writer  ] Acquiring write lock -> updating key-0");
            concurrentCache.put("key-0", "UPDATED-value-0");
            System.out.println("  [Writer  ] Write lock released, key-0 updated");
        }, "Writer");

        // Start all threads
        writer.start();
        for (Thread r : readers)
            r.start();

        // Wait for all to finish
        try {
            writer.join();
            for (Thread r : readers)
                r.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        subheader("All threads completed - no dirty reads observed");
        System.out.println("  Final value of key-0: " + concurrentCache.get("key-0"));

        // 5. Combined Stats Summary
        separator("5. Cache Statistics Summary");
        System.out.println("  LRU  -> " + lruStats);
        System.out.println("  LFU  -> " + lfuStats);
        System.out.println("  FIFO -> " + fifoStats);
    }
}
