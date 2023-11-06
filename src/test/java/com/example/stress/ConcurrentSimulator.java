package com.example.stress;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ConcurrentSimulator {

    static void doConcurrently(int concurrency, Consumer<Integer> code) throws InterruptedException {
        var executorService = Executors.newFixedThreadPool(concurrency);
        var countDownLatch = new CountDownLatch(concurrency);
        for (int i = 0; i < concurrency; i++) {
            int finalI = i;
            executorService.submit(() -> {
                awaitThreads(countDownLatch);
                code.accept(finalI);
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.HOURS);
    }

    private static void awaitThreads(CountDownLatch countDownLatch) {
        countDownLatch.countDown();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
