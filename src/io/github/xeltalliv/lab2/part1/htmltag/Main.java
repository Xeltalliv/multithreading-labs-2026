package io.github.xeltalliv.lab2.part1.htmltag;

import java.util.concurrent.ExecutionException;

import io.github.xeltalliv.lab2.SpeedTest;

public class Main {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		SpeedTest.repeatCount = 1;
		String path = "src/io/github/xeltalliv/lab2/part1/dataset.nobackup";
		
		// 0. One thread
		SpeedTest.test(() -> {
			String r0 = OneThreadApproach.compute(path);
			System.out.printf("One Thread  -> %s%n", r0);
        });
		
		// 1. Map-Reduce
		SpeedTest.test(() -> {
			String r1 = MapReduceApproach.compute(path, 12);
        	System.out.printf("Map-Reduce  -> %s%n", r1);
        });

        // 2. Fork-Join
        SpeedTest.test(() -> {
        	String r2 = ForkJoinApproach.compute(path, 12, 20);
        	System.out.printf("Fork-Join   -> %s%n", r2);
        });
        
        // 3. Worker Pool
        SpeedTest.test(() -> {
			String r3 = WorkerPoolApproach.compute(path, 12, 12);
	    	System.out.printf("Worker Pool -> %s%n", r3);
        });
	}
}
