package io.github.xeltalliv.lab2;

import java.util.concurrent.ExecutionException;

public class SpeedTest {
	@FunctionalInterface
	public interface ThrowingRunnable {
	    void run() throws InterruptedException, ExecutionException;
	}
	
	public static int repeatCount = 1;
	
	public static void test(ThrowingRunnable runnable) {
		long[] times = new long[repeatCount];
		for (int i = 0; i < repeatCount; i++) {
			long startTime = System.nanoTime();
			try {
				runnable.run();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			long stopTime = System.nanoTime();
			times[i] = (stopTime - startTime) / 1_000_000;
		}
		long timesSum = 0;
		for (int i = 0; i < repeatCount; i++) {
			timesSum += times[i];
		}
		long timesMean = timesSum / repeatCount;
		if (repeatCount < 2) {
			System.out.println("Execution time: " + timesMean+" ms");
			return;
		}
		long timesSqrDiffSum = 0;
		for (int i = 0; i < repeatCount; i++) {
			timesSqrDiffSum += (times[i] - timesMean) * (times[i] - timesMean);
		}
		long timesStd = (long) Math.sqrt((double) (timesSqrDiffSum / (repeatCount - 1L)));
		System.out.println("Execution time: " + timesMean + " (±"+timesStd+") ms");
	}
}
