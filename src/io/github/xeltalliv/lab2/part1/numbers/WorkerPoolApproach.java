package io.github.xeltalliv.lab2.part1.numbers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

public class WorkerPoolApproach {
    public static Result compute(double[] numbers, int threadCount, int taskCount) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Result>> futures = new ArrayList<>();
        
        int chunkSize = (int) Math.ceil((double) numbers.length / taskCount);
        
        for (int i = 0; i < taskCount; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, numbers.length);
            
            if (start >= numbers.length) break;

            futures.add(executor.submit(() -> {
                int count = end - start;
                double min = Double.MAX_VALUE;
                double max = Double.MIN_VALUE;
                double sum = 0;
                for (int j = start; j < end; j++) {
                    if (numbers[j] < min) min = numbers[j];
                    if (numbers[j] > max) max = numbers[j];
                    sum += numbers[j];
                }
                return new Result(count, min, max, sum / count);
            }));
        }

        Result finalResult = futures.get(0).get();
        for (int i = 1; i < futures.size(); i++) {
            finalResult = new Result(finalResult, futures.get(i).get());
        }

        executor.shutdown();
        return finalResult;
    }
}