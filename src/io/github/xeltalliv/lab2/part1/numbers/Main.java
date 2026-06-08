package io.github.xeltalliv.lab2.part1.numbers;
import java.util.Random;

import java.util.concurrent.ExecutionException;

import io.github.xeltalliv.lab2.SpeedTest;

public class Main {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		SpeedTest.repeatCount = 1;
		double[] numbers = new double[100_000_000];
		fillGaussian(numbers, 2.0, 4.0);
		
		// 0. One thread
		SpeedTest.test(() -> {
			Result r0 = OneThreadApproach.compute(numbers);
			System.out.printf("One Thread  -> Min: %.4f, Max: %.4f, Median: %.4f, Avg: %.4f%n", r0.min, r0.max, r0.median, r0.average);
		});
		
		// 1. Map-Reduce
        SpeedTest.test(() -> {
        	Result r1 = MapReduceApproach.compute(numbers, 12);
        	System.out.printf("Map-Reduce  -> Min: %.4f, Max: %.4f, Median: %.4f, Avg: %.4f%n", r1.min, r1.max, r1.median, r1.average);
        });

        // 2. Fork-Join
        SpeedTest.test(() -> {
        	Result r2 = ForkJoinApproach.compute(numbers, 12, 10_000);
        	System.out.printf("Fork-Join   -> Min: %.4f, Max: %.4f, Median: %.4f, Avg: %.4f%n", r2.min, r2.max, r2.median, r2.average);
        });

        // 3. Worker Pool
        SpeedTest.test(() -> {
        	Result r3 = WorkerPoolApproach.compute(numbers, 12, 12);
        	System.out.printf("Worker Pool -> Min: %.4f, Max: %.4f, Median: %.4f, Avg: %.4f%n", r3.min, r3.max, r3.median, r3.average);
        });
	}
	
	private static void fillGaussian(double[] numbers, double mean, double std) {
        Random random = new Random();
        random.setSeed(1234L);
		for(int i=0; i<numbers.length; i++) {
			numbers[i] = mean + (random.nextGaussian() * std);
		}
	}
}
