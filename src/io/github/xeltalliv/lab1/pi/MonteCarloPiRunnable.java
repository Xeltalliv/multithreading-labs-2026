package io.github.xeltalliv.lab1.pi;

import java.util.concurrent.ThreadLocalRandom;

public class MonteCarloPiRunnable implements Runnable {
	private final long iterations;
	private long insideCount = 0;

	public MonteCarloPiRunnable(long iterations) {
		this.iterations = iterations;
	}

	@Override
	public void run() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int hits = 0;
		for (long i = 0; i < iterations; i++) {
			double x = random.nextDouble();
			double y = random.nextDouble();
			if (x * x + y * y <= 1)
				hits++;
		}
		insideCount = hits;
	}

	public long getInsideCount() {
		return insideCount;
	}
}