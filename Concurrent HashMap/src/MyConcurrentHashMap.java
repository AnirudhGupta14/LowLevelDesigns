public class MyConcurrentHashMap<K, V> implements ConcurrentMap<K, V> {
    private final Segment<K, V>[] segments;
    private final int numSegments;

    @SuppressWarnings("unchecked")
    public MyConcurrentHashMap(int numSegments, int segmentCapacity) {
        this.numSegments = numSegments;
        this.segments = new Segment[numSegments];
        for (int i = 0; i < numSegments; i++) {
            this.segments[i] = new Segment<>(segmentCapacity);
        }
    }

    private int hash(K key) {
        return key.hashCode();
    }

    private Segment<K, V> getSegment(K key) {
        int hash = hash(key);
        int segmentIndex = Math.abs(hash) % numSegments;
        return segments[segmentIndex];
    }

    @Override
    public void put(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Null keys or values are not supported");
        }
        int hash = hash(key);
        getSegment(key).put(key, value, hash);
    }

    @Override
    public V get(K key) {
        if (key == null)
            return null;
        int hash = hash(key);
        return getSegment(key).get(key, hash);
    }

    @Override
    public void remove(K key) {
        if (key == null)
            return;
        int hash = hash(key);
        getSegment(key).remove(key, hash);
    }

    @Override
    public int size() {
        int totalSize = 0;
        for (Segment<K, V> segment : segments) {
            totalSize += segment.size();
        }
        return totalSize;
    }
}
