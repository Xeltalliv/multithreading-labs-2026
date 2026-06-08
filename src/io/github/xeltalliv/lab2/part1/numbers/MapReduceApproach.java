package io.github.xeltalliv.lab2.part1.numbers;

public class MapReduceApproach {
    public static Result compute(double[] numbers, int threadCount) throws InterruptedException {
        int chunkSize = (int) Math.ceil((double) numbers.length / threadCount);
        
        final Thread[] threads = new Thread[threadCount];
        final Result[] results = new Result[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, numbers.length);

            threads[threadIndex] = new Thread(() -> {
                int count = end - start;
                double min = Double.MAX_VALUE;
                double max = Double.MIN_VALUE;
                double sum = 0;
                
                for (int j = start; j < end; j++) {
                    if (numbers[j] < min) min = numbers[j];
                    if (numbers[j] > max) max = numbers[j];
                    sum += numbers[j];
                }
                results[threadIndex] = new Result(count, min, max, sum / count);
            });
            threads[threadIndex].start();
        }
        for (Thread thread : threads) thread.join();

        Result finalResult = results[0];
        for (int i = 1; i < results.length; i++) {
            finalResult = new Result(finalResult, results[i]);
        }

        return finalResult;
    }
}