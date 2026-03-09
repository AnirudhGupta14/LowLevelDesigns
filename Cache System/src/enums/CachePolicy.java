package enums;

/**
 * Supported cache eviction policies.
 *
 * <ul>
 * <li>{@link #LRU} — Least Recently Used: evicts the entry with the oldest
 * last-access time.</li>
 * <li>{@link #LFU} — Least Frequently Used: evicts the entry accessed the
 * fewest times.</li>
 * <li>{@link #FIFO} — First In First Out: evicts the entry that was inserted
 * earliest.</li>
 * </ul>
 */
public enum CachePolicy {
    LRU,
    LFU,
    FIFO
}
