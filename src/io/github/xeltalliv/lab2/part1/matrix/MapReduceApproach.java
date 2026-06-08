package io.github.xeltalliv.lab2.part1.matrix;

public class MapReduceApproach {
    public static double[][] compute(double[][] matrixA, double[][] matrixB, int threadCount) throws InterruptedException {
        int rows = matrixA.length;
        int colsB = matrixB[0].length;
        double[][] result = new double[rows][colsB];
        
        int chunkSize = (int) Math.ceil((double) rows / threadCount);
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int startRow = i * chunkSize;
            final int endRow = Math.min(startRow + chunkSize, rows);

            threads[i] = new Thread(() -> multiplyRowRange(matrixA, matrixB, result, startRow, endRow));
            threads[i].start();
        }

        for (Thread thread : threads) thread.join();
        return result;
    }

    public static void multiplyRowRange(double[][] A, double[][] B, double[][] result, int startRow, int endRow) {
        int colsA = A[0].length;
        int colsB = B[0].length;
        for (int i = startRow; i < endRow; i++) {
            for (int k = 0; k < colsA; k++) {
                double valA = A[i][k];
                for (int j = 0; j < colsB; j++) {
                    result[i][j] += valA * B[k][j];
                }
            }
        }
    }
}