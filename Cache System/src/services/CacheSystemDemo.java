package services;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CacheSystemDemo {
    
    public static void main(String[] args) {
        System.out.println("=== services.Cache System Low-Level Design Demo ===\n");
        
        // Create cache with capacity of 3 and 4 executor threads
        Cache<String, String> cache = new Cache<>(3, 4);
        
        try {
            // Test 1: Basic Operations
            testBasicOperations(cache);
            
            // Test 2: services.Cache Eviction (LRU)
            testCacheEviction(cache);
            
            // Test 3: Concurrent Access
            testConcurrentAccess(cache);
            
            // Test 4: Performance Monitoring
            testPerformanceMonitoring(cache);
            
            // Test 5: "Read Your Own Writes" Consistency
            testReadYourOwnWrites(cache);
            
        } finally {
            // Cleanup
            cache.shutdown();
            System.out.println("\n=== services.Cache System Demo Completed ===");
        }
    }
    
    /**
     * Test basic cache operations: put, get, remove
     */
    private static void testBasicOperations(Cache<String, String> cache) {
        System.out.println("--- Test 1: Basic Operations ---");
        
        try {
            // Update data and .get() will stop for any further operation
            cache.updateData("user1", "John Doe").get();
            cache.updateData("user2", "Jane Smith").get();
            cache.updateData("user3", "Bob Johnson").get();
            
            // Access data
            String user1 = cache.accessData("user1").get();
            String user2 = cache.accessData("user2").get();
            String user3 = cache.accessData("user3").get();
            
            System.out.println("Retrieved: " + user1 + ", " + user2 + ", " + user3);
            
            // Remove data
            String removed = cache.removeData("user2").get();
            System.out.println("Removed: " + removed);
            
            // Try to access removed data
            String notFound = cache.accessData("user2").get();
            System.out.println("Access after removal: " + notFound);
            
        } catch (Exception e) {
            System.err.println("Error in basic operations test: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Test LRU eviction when cache reaches capacity
     */
    private static void testCacheEviction(Cache<String, String> cache) {
        System.out.println("--- Test 2: services.Cache Eviction (LRU) ---");
        
        try {
            // Fill cache to capacity
            cache.updateData("key1", "value1").get();
            cache.updateData("key2", "value2").get();
            cache.updateData("key3", "value3").get();
            
            System.out.println("services.Cache filled to capacity (3/3)");
            
            // Access key1 to make it most recently used
            cache.accessData("key1").get();
            System.out.println("Accessed key1 to make it MRU");
            
            // Add new key - should evict key2 (least recently used)
            cache.updateData("key4", "value4").get();
            System.out.println("Added key4 - should evict key2 (LRU)");
            
            // Check what's still in cache
            String key1 = cache.accessData("key1").get();
            String key2 = cache.accessData("key2").get(); // Should be cache miss, then DB hit
            String key3 = cache.accessData("key3").get();
            String key4 = cache.accessData("key4").get();
            
            System.out.println("services.Cache contents after eviction:");
            System.out.println("key1: " + key1);
            System.out.println("key2: " + key2);
            System.out.println("key3: " + key3);
            System.out.println("key4: " + key4);
            
        } catch (Exception e) {
            System.err.println("Error in eviction test: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Test concurrent access to the cache
     */
    private static void testConcurrentAccess(Cache<String, String> cache) {
        System.out.println("--- Test 3: Concurrent Access ---");
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        try {
            // Create multiple concurrent operations
            @SuppressWarnings("unchecked")
            CompletableFuture<Void>[] futures = new CompletableFuture[10];
            
            for (int i = 0; i < 10; i++) {
                final int threadId = i;
                futures[i] = CompletableFuture.runAsync(() -> {
                    try {
                        String key = "concurrent_key_" + (threadId % 3); // Only 3 different keys
                        String value = "value_from_thread_" + threadId;
                        
                        // Update data
                        cache.updateData(key, value).get();
                        
                        // Access data
                        String retrieved = cache.accessData(key).get();
                        
                        System.out.println("Thread " + threadId + ": " + key + " = " + retrieved);
                        
                    } catch (Exception e) {
                        System.err.println("Error in thread " + threadId + ": " + e.getMessage());
                    }
                }, executor);
            }
            
            // Wait for all operations to complete
            CompletableFuture.allOf(futures).get(5, TimeUnit.SECONDS);
            System.out.println("All concurrent operations completed");
            
        } catch (Exception e) {
            System.err.println("Error in concurrent access test: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
        
        System.out.println();
    }
    
    /**
     * Test performance monitoring and statistics
     */
    private static void testPerformanceMonitoring(Cache<String, String> cache) {
        System.out.println("--- Test 4: Performance Monitoring ---");
        
        try {
            // Clear cache for clean test
            cache.removeData("key1").get();
            cache.removeData("key2").get();
            cache.removeData("key3").get();
            cache.removeData("key4").get();
            
            // Perform operations to generate hits and misses
            cache.updateData("perf1", "value1").get();
            cache.updateData("perf2", "value2").get();
            
            // services.Cache hits
            cache.accessData("perf1").get();
            cache.accessData("perf2").get();
            cache.accessData("perf1").get();
            
            // services.Cache miss (not in cache or DB)
            cache.accessData("perf3").get();
            
            // Get and display statistics
            CacheStats stats = cache.getStats().get();
            System.out.println("services.Cache Statistics: " + stats);
            
        } catch (Exception e) {
            System.err.println("Error in performance monitoring test: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Test "read your own writes" consistency
     */
    private static void testReadYourOwnWrites(Cache<String, String> cache) {
        System.out.println("--- Test 5: Read Your Own Writes Consistency ---");
        
        try {
            // Update data
            cache.updateData("consistency_key", "initial_value").get();
            
            // Immediately read - should get the updated value due to thread affinity
            String value1 = cache.accessData("consistency_key").get();
            System.out.println("Read after write: " + value1);
            
            // Update again
            cache.updateData("consistency_key", "updated_value").get();
            
            // Read again - should get the latest value
            String value2 = cache.accessData("consistency_key").get();
            System.out.println("Read after second write: " + value2);
            
            // Verify consistency
            if ("updated_value".equals(value2)) {
                System.out.println("✓ Read your own writes consistency maintained");
            } else {
                System.out.println("✗ Read your own writes consistency failed");
            }
            
        } catch (Exception e) {
            System.err.println("Error in consistency test: " + e.getMessage());
        }
        
        System.out.println();
    }
}
