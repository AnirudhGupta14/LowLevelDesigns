package eviction;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DoublyLinkedList<K> {
    private final DoublyLinkedListNode<K> head;
    private final DoublyLinkedListNode<K> tail;
    private final ConcurrentHashMap<K, DoublyLinkedListNode<K>> nodeMap;
    private final ReentrantReadWriteLock lock;

    public DoublyLinkedList() {
        this.head = new DoublyLinkedListNode<>(null);
        this.tail = new DoublyLinkedListNode<>(null);
        this.head.setNext(tail);
        this.tail.setPrev(head);
        this.nodeMap = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock();
    }

    public void addToHead(K key) {
        lock.writeLock().lock();
        try {
            DoublyLinkedListNode<K> node = new DoublyLinkedListNode<>(key);
            DoublyLinkedListNode<K> next = head.getNext();
            
            head.setNext(node);
            node.setPrev(head);
            node.setNext(next);
            next.setPrev(node);
            
            nodeMap.put(key, node);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void moveToHead(K key) {
        lock.writeLock().lock();
        try {
            DoublyLinkedListNode<K> node = nodeMap.get(key);
            if (node != null) {
                removeNode(node);
                addToHead(key);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    

    public void removeKey(K key) {
        lock.writeLock().lock();
        try {
            DoublyLinkedListNode<K> node = nodeMap.remove(key);
            if (node != null) {
                removeNode(node);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public DoublyLinkedListNode<K> removeTail() {
        lock.writeLock().lock();
        try {
            if (tail.getPrev() == head) {
                return null; // List is empty
            }
            
            DoublyLinkedListNode<K> lastNode = tail.getPrev();
            K key = lastNode.getKey();
            nodeMap.remove(key);
            removeNode(lastNode);
            return lastNode;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean contains(K key) {
        lock.readLock().lock();
        try {
            return nodeMap.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try {
            return nodeMap.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public K getTailKey() {
        lock.readLock().lock();
        try {
            if (tail.getPrev() == head) {
                return null; // List is empty
            }
            return tail.getPrev().getKey();
        } finally {
            lock.readLock().unlock();
        }
    }

    private void removeNode(DoublyLinkedListNode<K> node) {
        DoublyLinkedListNode<K> prev = node.getPrev();
        DoublyLinkedListNode<K> next = node.getNext();
        
        prev.setNext(next);
        next.setPrev(prev);
    }
}
