package io.github.xeltalliv.lab2.part1.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

public class WorkerPoolApproach {
    public static double[][] compute(double[][] A, double[][] B, int threadCount, int taskCount) throws InterruptedException, ExecutionException {
        int rows = A.length;
        int cols = B[0].length;
        double[][] result = new double[rows][cols];
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<?>> futures = new ArrayList<>();
        
        int chunkSize = (int) Math.ceil((double) rows / taskCount);
        
        for (int i = 0; i < taskCount; i++) {
            final int startRow = i * chunkSize;
            final int endRow = Math.min(startRow + chunkSize, rows);
            
            if (startRow >= rows) break;

            futures.add(executor.submit(() -> {
                MapReduceApproach.multiplyRowRange(A, B, result, startRow, endRow);
                return null; // Future needs to return value
            }));
        }
        for (Future<?> future : futures) future.get();

        executor.shutdown();
        return result;
    }
}