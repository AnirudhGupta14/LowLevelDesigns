import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiterMain {
    public static void main(String[] args) throws InterruptedException {
        UserRateLimiter rateLimiterManager = new UserRateLimiter();

        // Policy: Allow exactly 5 requests per 1000ms window for the User
        RateLimiter fixedWindowAlgo = new FixedWindowCounter(5, 1000);
        rateLimiterManager.registerUser("User_123", fixedWindowAlgo);

        System.out.println("Starting heavy multithreaded barrage on Fixed Window (Limit: 5)...");
        ExecutorService executor = Executors.newFixedThreadPool(10);
        AtomicInteger successCounter = new AtomicInteger();
        AtomicInteger dropCounter = new AtomicInteger();

        // Spawn 20 simultaneous requests at the exact same millisecond
        for (int i = 0; i < 20; i++) {
            executor.submit(() -> {
                boolean attempt = rateLimiterManager.isAllowed("User_123");
                if (attempt) {
                    successCounter.incrementAndGet();
                    System.out.println(Thread.currentThread().getName() + " -> Allowed");
                } else {
                    dropCounter.incrementAndGet();
                    System.out.println(Thread.currentThread().getName() + " -> HTTP 429 Too Many Requests");
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);

        System.out.println("=========================================");
        System.out.println("Requests Allowed (Should clearly be 5): " + successCounter.get());
        System.out.println("Requests Dropped (Should be 15): " + dropCounter.get());
        System.out.println("=========================================");
    }
}
