package Helpers;

public class Pair<U, V> {
    public final U first;
    public final V second;

    public U getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }

    public Pair(U first, V second) {
        this.first = first;
        this.second = second;
    }
}
