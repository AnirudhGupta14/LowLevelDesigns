package eviction;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DoublyLinkedListNode<K> {
    private K key;
    private DoublyLinkedListNode<K> prev;
    private DoublyLinkedListNode<K> next;

    public DoublyLinkedListNode(K key) {
        this.key = key;
        this.prev = null;
        this.next = null;
    }
}
