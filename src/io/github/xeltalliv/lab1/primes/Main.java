package io.github.xeltalliv.lab1.primes;

import java.util.ArrayList;
import java.util.List;

import io.github.xeltalliv.lab1.SpeedTest;

public class Main {
	public static void main(String[] args) {
		List<Integer> primes1 = computePrimesSingleThreaded(10000000);
		System.out.println(primes1.size());
		System.out.println(primes1.subList(0, 100));
		List<Integer> primes2 = computePrimesMultiThreaded(10000000, 10);
		System.out.println(primes2.size());
		System.out.println(primes2.subList(0, 100));
		
		SpeedTest.test(() -> {computePrimesSingleThreaded(10000000);}, 0);
		SpeedTest.test(() -> {computePrimesMultiThreaded(10000000, 2);}, 2);
		SpeedTest.test(() -> {computePrimesMultiThreaded(10000000, 4);}, 4);
		SpeedTest.test(() -> {computePrimesMultiThreaded(10000000, 6);}, 6);
		SpeedTest.test(() -> {computePrimesMultiThreaded(10000000, 8);}, 8);
		SpeedTest.test(() -> {computePrimesMultiThreaded(10000000, 10);}, 10);
	}
	
	public static ArrayList<Integer> computePrimesSingleThreaded(int limit) {
		ArrayList<Integer> primes = new ArrayList<>();
		
		for(int i = 0; i<limit; i++) {
			if (isPrime(i)) {
				synchronized(primes) {
					primes.add(i);
				}
			}
		}
		return primes;
	}
	
	public static ArrayList<Integer> computePrimesMultiThreaded(int limit, int numThreads) {
		ArrayList<Integer> primes = new ArrayList<>();
		Thread[] ths =  new Thread[numThreads];

		for(int k=0; k<numThreads; k++) {
			final int start = k;
			ths[k] = new Thread(() -> {
				for(int i = start; i<limit; i+=numThreads) {
					if (isPrime(i)) {
						synchronized(primes) {
							primes.add(i);
						}
					}
				}
			});
		}
		try {
			for(Thread th : ths) th.start();
			for(Thread th : ths) th.join();
		} catch(InterruptedException ex) {
			ex.printStackTrace();
		}
		return primes;
	}
	
	public static boolean isPrime(int value) {
		if (value <= 1) return false;
		if (value == 2 || value == 3) return true;
		if (value % 2 == 0) return false;
		int limit = (int) Math.ceil(Math.sqrt(value));
		for(int i=3; i<=limit; i+=2) {
			if (value % i == 0) return false;
		}
		return true;
	}
}
