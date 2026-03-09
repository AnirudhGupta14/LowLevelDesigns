package observer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Concrete observer that prints a timestamped log line for every cache event.
 *
 * @param <K> Key type
 */
public class LoggingCacheListener<K> implements CacheEventListener<K> {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private final String cacheName;

    public LoggingCacheListener(String cacheName) {
        this.cacheName = cacheName;
    }

    @Override
    public void onHit(K key) {
        log("HIT        key=" + key);
    }

    @Override
    public void onMiss(K key) {
        log("MISS       key=" + key);
    }

    @Override
    public void onEvict(K key) {
        log("EVICT      key=" + key + "  → written to DB");
    }

    @Override
    public void onDbFallback(K key) {
        log("DB-FALLBACK key=" + key + "  → loaded into cache");
    }

    private void log(String message) {
        System.out.printf("[%s] [%s] %s%n",
                LocalDateTime.now().format(FMT), cacheName, message);
    }
}
