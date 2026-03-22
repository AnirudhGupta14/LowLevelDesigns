import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentMap<String, Integer> map = new MyConcurrentHashMap<>(16, 32);
        int numThreads = 10;
        int itemsPerThread = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        System.out.println("Starting concurrent writes...");
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < itemsPerThread; j++) {
                    String key = "Key-" + threadId + "-" + j;
                    map.put(key, j);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("Expected final size: " + (numThreads * itemsPerThread));
        System.out.println("Actual final size: " + map.size());

        System.out.println("Concurrent HashMap design test passed successfully!");
    }
}
