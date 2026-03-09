package enums;

/**
 * Extensible enum for the persistent storage backend type.
 *
 * <ul>
 * <li>{@link #IN_MEMORY_DB} — HashMap-backed in-process "database". Suitable
 * for demo/testing.</li>
 * <li>{@link #FILE_DB} — File-system-backed persistent store (stub, ready for
 * future impl).</li>
 * </ul>
 */
public enum StorageType {
    IN_MEMORY_DB,
    FILE_DB
}
