package io.github.xeltalliv.lab2.part1.matrix;
import java.util.Random;

import java.util.concurrent.ExecutionException;

import io.github.xeltalliv.lab2.SpeedTest;

public class Main {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		SpeedTest.repeatCount = 1;
		double[][] matrixA = generateRandomMatrix(2000, 2000, 0.0, 4.0);
		double[][] matrixB = generateRandomMatrix(2000, 2000, 0.0, 1.0);
		
		// 0. One thread
		SpeedTest.test(() -> {
			double[][] matrixC = OneThreadApproach.compute(matrixA, matrixB);
			System.out.printf("One Thread  -> Checksum: %.4f%n", getChecksum(matrixC));
		});
		
		// 1. Map-Reduce
		SpeedTest.test(() -> {
			double[][] matrixC = MapReduceApproach.compute(matrixA, matrixB, 12);
        	System.out.printf("Map-Reduce  -> Checksum: %.4f%n", getChecksum(matrixC));
		});

        // 2. Fork-Join
        SpeedTest.test(() -> {
        	double[][] matrixC = ForkJoinApproach.compute(matrixA, matrixB, 12, 100);
        	System.out.printf("Fork-Join   -> Checksum: %.4f%n", getChecksum(matrixC));
        });
		
        // 3. Worker Pool
        SpeedTest.test(() -> {
        	double[][] matrixC = WorkerPoolApproach.compute(matrixA, matrixB, 12, 12);
        	System.out.printf("Worker Pool -> Checksum: %.4f%n", getChecksum(matrixC));
        });
	}
	
	private static double[][] generateRandomMatrix(int rows, int cols, double mean, double std) {
        Random random = new Random(1234L);
        double[][] matrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = mean + (random.nextGaussian() * std);
            }
        }
        return matrix;
    }

    private static double getChecksum(double[][] matrix) {
    	int rows = matrix.length;
    	int cols = matrix[0].length;
        double sum = 0;
        for (int i = 0; i < rows; i++) {
        	for (int j = 0; j < cols; j++) {
        		sum += matrix[i][j];
        	}
        }
        return sum;
    }
}
