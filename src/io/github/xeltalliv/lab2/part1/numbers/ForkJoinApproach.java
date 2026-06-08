package io.github.xeltalliv.lab2.part1.numbers;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinApproach {
    private static class ArrayTask extends RecursiveTask<Result> {
        private static final long serialVersionUID = 1L; // To prevent warning
		private final double[] numbers;
        private final int start;
        private final int end;
        private final int threshold;

        public ArrayTask(double[] numbers, int threshold, int start, int end) {
            this.numbers = numbers;
            this.threshold = threshold;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Result compute() {
        	int count = end - start;
            if (count <= threshold) {
                double min = Double.MAX_VALUE;
                double max = Double.MIN_VALUE;
                double sum = 0;
                for (int i = start; i < end; i++) {
                    if (numbers[i] < min) min = numbers[i];
                    if (numbers[i] > max) max = numbers[i];
                    sum += numbers[i];
                }
                return new Result(count, min, max, sum / count);
            } else {
            	int mid = start + (end - start) / 2;
                ArrayTask leftTask = new ArrayTask(numbers, threshold, start, mid);
                ArrayTask rightTask = new ArrayTask(numbers, threshold, mid, end);
                leftTask.fork(); // Run asynchronously
                Result rightResult = rightTask.compute(); // Run synchronously in current thread
                Result leftResult = leftTask.join(); // Wait for fork result
                return new Result(leftResult, rightResult);
            }
        }
    }

    @SuppressWarnings("resource")
    public static Result compute(double[] numbers, int threadCount, int threshold) {
    	ForkJoinPool pool = new ForkJoinPool(threadCount);
    	try {
    		return pool.invoke(new ArrayTask(numbers, threshold, 0, numbers.length));
    	} finally {
    		pool.shutdown();
    	}
    }
}
