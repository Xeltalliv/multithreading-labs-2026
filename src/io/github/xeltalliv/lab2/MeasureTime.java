package io.github.xeltalliv.lab2;

public class MeasureTime {
	private static long startTimeInNano = 0;
	public static void start() {
		startTimeInNano = System.nanoTime();
	}
	public static void end() {
		long durationInNano = System.nanoTime() - startTimeInNano;
		long durationInMillis = durationInNano / 1_000_000;
		System.out.println("Execution time: " + durationInMillis + " ms");
	}
}
