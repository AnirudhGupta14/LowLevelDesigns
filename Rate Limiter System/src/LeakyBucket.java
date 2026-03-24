import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LeakyBucket implements RateLimiter {
    private final BlockingQueue<Integer> queue;

    public LeakyBucket(int capacity) {
        // LinkedBlockingQueue dictates strict FIFO queuing with a hard capacity bound.
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    @Override
    public boolean grantAccess() {
        // The queue represents the "bucket". If it is full, 'offer' instantly returns
        // false.
        // If it returns true, the request "entered the bucket" and will leak at a
        // stable rate.
        return queue.offer(1);
    }

    // In a full production system, a background Thread executor
    // runs continuously and purely takes items out of the queue using
    // `queue.take()`
    // at a mathematically constant fixed rate.
}
