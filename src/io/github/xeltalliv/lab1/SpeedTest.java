package io.github.xeltalliv.lab1;

public class SpeedTest {
	public static int repeatCount = 5;
	public static void test(Runnable runnable, int numThreads) {
		long startTime, stopTime, averTime = 0;    
		for (int i = 0; i < repeatCount; i++) {
			startTime = System.currentTimeMillis();
			runnable.run();
			stopTime = System.currentTimeMillis();
			averTime += stopTime - startTime;
		}
		if (numThreads < 1) {
			System.out.println("Singlethreaded took " + averTime/repeatCount+ "ms");
		} else {
			System.out.println("Multithreaded ("+numThreads+" threads) took " + averTime/repeatCount+ "ms");
		}
	}
}
