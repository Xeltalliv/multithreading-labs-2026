package io.github.xeltalliv.lab1.pi;

import io.github.xeltalliv.lab1.SpeedTest;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		System.out.println("PI = " + computePiSingleThreaded(1000000000));
		System.out.println("PI = " + computePiMultiThreaded(1000000000, 10));
		
		SpeedTest.test(() -> {computePiSingleThreaded(1000000000);}, 0);
		SpeedTest.test(() -> {computePiMultiThreaded(1000000000, 2);}, 2);
		SpeedTest.test(() -> {computePiMultiThreaded(1000000000, 4);}, 4);
		SpeedTest.test(() -> {computePiMultiThreaded(1000000000, 6);}, 6);
		SpeedTest.test(() -> {computePiMultiThreaded(1000000000, 8);}, 8);
		SpeedTest.test(() -> {computePiMultiThreaded(1000000000, 10);}, 10);
	}

	public static double computePiSingleThreaded(int iterations) {
        MonteCarloPiRunnable runnable = new MonteCarloPiRunnable(iterations);
        runnable.run();
        long sumInside = runnable.getInsideCount();
        return 4.0 * sumInside / iterations;
    }
	
	public static double computePiMultiThreaded(int iterations, int numThreads) {
        long iterationsPerThread = iterations / numThreads;

        MonteCarloPiRunnable[] runnables = new MonteCarloPiRunnable[numThreads];
        Thread[] ths = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
        	runnables[i] = new MonteCarloPiRunnable(iterationsPerThread);
            ths[i] = new Thread(runnables[i]);
        }
		try {
			for(Thread th : ths) th.start();
			for(Thread th : ths) th.join();
		} catch(InterruptedException ex) {
			ex.printStackTrace();
		}
        
        long sumInside = 0;
        for (int i = 0; i < numThreads; i++) {
            sumInside += runnables[i].getInsideCount();
        }
        return 4.0 * sumInside / iterations;
    }
}
