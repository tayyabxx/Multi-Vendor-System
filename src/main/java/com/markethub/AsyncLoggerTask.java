package com.markethub;

import org.springframework.stereotype.Component;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class AsyncLoggerTask {
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public void logActivity(String actionDescription) {
        executorService.submit(() -> {
            try {
                // Simulate background processing (e.g., writing to file or audit DB)
                System.out.println("[ASYNC THREAD " + Thread.currentThread().getName() + "] Log recorded: " + actionDescription);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}