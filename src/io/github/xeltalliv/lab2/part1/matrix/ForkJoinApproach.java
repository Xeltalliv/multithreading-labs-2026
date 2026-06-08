package io.github.xeltalliv.lab2.part1.matrix;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinApproach {
    private static class MatrixTask extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final double[][] A;
        private final double[][] B;
        private final double[][] result;
        private final int startRow;
        private final int endRow;
        private final int threshold;

        public MatrixTask(double[][] A, double[][] B, double[][] result, int startRow, int endRow, int threshold) {
            this.A = A;
            this.B = B;
            this.result = result;
            this.startRow = startRow;
            this.endRow = endRow;
            this.threshold = threshold;
        }

        @Override
        protected void compute() {
        	int rowCount = endRow - startRow;
            if (rowCount <= threshold) {
                MapReduceApproach.multiplyRowRange(A, B, result, startRow, endRow);
            } else {
                int mid = startRow + (endRow - startRow) / 2;
                MatrixTask leftTask = new MatrixTask(A, B, result, startRow, mid, threshold);
                MatrixTask rightTask = new MatrixTask(A, B, result, mid, endRow, threshold);
                leftTask.fork();
                rightTask.fork();
                leftTask.join();
                rightTask.join();
            }
        }
    }

    @SuppressWarnings("resource")
    public static double[][] compute(double[][] A, double[][] B, int threadCount, int threshold) {
        double[][] result = new double[A.length][B[0].length];
        ForkJoinPool pool = new ForkJoinPool(threadCount);
        try {
            pool.invoke(new MatrixTask(A, B, result, 0, A.length, threshold));
        } finally {
            pool.shutdown();
        }
        return result;
    }
}