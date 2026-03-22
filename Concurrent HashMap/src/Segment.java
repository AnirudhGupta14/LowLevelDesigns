import java.util.concurrent.locks.ReentrantLock;

public class Segment<K, V> {
    private final HashNode<K, V>[] table;
    private final ReentrantLock lock;
    private int count;

    @SuppressWarnings("unchecked")
    public Segment(int capacity) {
        this.table = new HashNode[capacity];
        this.lock = new ReentrantLock();
        this.count = 0;
    }

    public void put(K key, V value, int hash) {
        lock.lock();
        try {
            int index = getIndex(hash);
            HashNode<K, V> head = table[index];
            HashNode<K, V> current = head;

            // Update existing key
            while (current != null) {
                if (current.key.equals(key)) {
                    current.value = value;
                    return;
                }
                current = current.next;
            }

            // Insert new key
            HashNode<K, V> newNode = new HashNode<>(key, value);
            newNode.next = head;
            table[index] = newNode;
            count++;
        } finally {
            lock.unlock();
        }
    }

    public V get(K key, int hash) {
        lock.lock();
        try {
            int index = getIndex(hash);
            HashNode<K, V> node = table[index];
            while (node != null) {
                if (node.key.equals(key)) {
                    return node.value;
                }
                node = node.next;
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    public void remove(K key, int hash) {
        lock.lock();
        try {
            int index = getIndex(hash);
            HashNode<K, V> current = table[index];
            HashNode<K, V> prev = null;

            while (current != null) {
                if (current.key.equals(key)) {
                    if (prev == null) {
                        table[index] = current.next;
                    } else {
                        prev.next = current.next;
                    }
                    count--;
                    return;
                }
                prev = current;
                current = current.next;
            }
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }

    private int getIndex(int hash) {
        return Math.abs(hash) % table.length;
    }
}
