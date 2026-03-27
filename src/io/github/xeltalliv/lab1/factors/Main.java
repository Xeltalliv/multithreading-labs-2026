package io.github.xeltalliv.lab1.factors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.xeltalliv.lab1.SpeedTest;

public class Main {
	private volatile static long divValue;
	private volatile static long limit;
	
	public static void main(String[] args) {
		Random random = new Random();
		final long value = random.nextLong();
		System.out.println("input: "+value);
		List<Long> factors1 = computeFactorsSingleThreaded(value);
		System.out.println(factors1);
		List<Long> factors2 = computeFactorsMultiThreaded(value, 10);
		System.out.println(factors2);
		
		SpeedTest.test(() -> {computeFactorsSingleThreaded(value);}, 0);
		SpeedTest.test(() -> {computeFactorsMultiThreaded(value, 2);}, 2);
		SpeedTest.test(() -> {computeFactorsMultiThreaded(value, 4);}, 4);
		SpeedTest.test(() -> {computeFactorsMultiThreaded(value, 6);}, 6);
		SpeedTest.test(() -> {computeFactorsMultiThreaded(value, 8);}, 8);
		SpeedTest.test(() -> {computeFactorsMultiThreaded(value, 10);}, 10);
	}
	
	public static ArrayList<Long> computeFactorsSingleThreaded(long value) {
		ArrayList<Long> factors = new ArrayList<>();

		divValue = value;
		if (divValue < 0) {
			factors.add(-1L);
			divValue *= -1L;
		}
		limit = (long) Math.ceil(Math.sqrt(divValue));

		for (long i = 2; i <= limit; i++) {
			if (divValue % i == 0 && isPrime(i)) {
				divValue = divValue / i;
				limit = (long) Math.ceil(Math.sqrt(divValue));
				factors.add(i);
				i--;
			}
		}
		if (isPrime(divValue)) {
			factors.add(divValue);
			divValue = 1;
		}
		if (divValue != 1) {
			// in theory, this shall never trigger
			System.out.println("Error: divValue = " + divValue);
		}
		return factors;
	}
		
	public static ArrayList<Long> computeFactorsMultiThreaded(long value, int numThreads) {
		ArrayList<Long> factors = new ArrayList<>();
		Thread[] ths =  new Thread[numThreads];

		divValue = value;
		if (divValue < 0) {
			factors.add(-1L);
			divValue *= -1L;
		}
		limit = (long) Math.ceil(Math.sqrt(divValue));

		for(int k=0; k<numThreads; k++) {
			final long start = k + 2;
			ths[k] = new Thread(() -> {
				for(long i = start; i<=limit; i+=numThreads) {
					if (divValue % i == 0 && isPrime(i)) {
						synchronized(factors) {
							divValue = divValue / i;
							limit = (long) Math.ceil(Math.sqrt(divValue));
							factors.add(i);
							i-=numThreads;
						}
					}
				}
			});
		}
		try {
			for(Thread th : ths) th.start();
			for(Thread th : ths) th.join();
			if (isPrime(divValue)) {
				factors.add(divValue);
				divValue = 1;
			}
			if (divValue != 1) {
				// in theory, this shall never trigger
				System.out.println("Error: divValue = "+divValue);
			}
		} catch(InterruptedException ex) {
			ex.printStackTrace();
		}
		return factors;
	}
	
	public static boolean isPrime(long value) {
		if (value <= 1) return false;
		if (value == 2 || value == 3) return true;
		if (value % 2 == 0) return false;
		long limit = (long) Math.ceil(Math.sqrt(value));
		for(long i=3; i<=limit; i+=2) {
			if (value % i == 0) return false;
		}
		return true;
	}
}
