package services;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class KeyBasedExecutor {

    private final Executor[] executors;
    private final int numThreads;

    public KeyBasedExecutor(int numExecutors) {
        this.numThreads = numExecutors;
        this.executors = new ExecutorService[numExecutors];

        for (int i = 0; i < numExecutors; i++) {
            executors[i] = Executors.newSingleThreadExecutor();
        }
    }

    public <T> CompletableFuture<T> execute(Object key, Supplier<T> task) {
        int index = getExecutorIndex(key);
        return CompletableFuture.supplyAsync(task, executors[index]);
    }

    private int getExecutorIndex(Object key) {
        return Math.abs(key.hashCode()) % numThreads;
    }

    public void shutdown() {
        for (Executor executor : executors) {
            if (executor instanceof java.util.concurrent.ExecutorService) {
                ((java.util.concurrent.ExecutorService) executor).shutdown();
            }
        }
    }
}
