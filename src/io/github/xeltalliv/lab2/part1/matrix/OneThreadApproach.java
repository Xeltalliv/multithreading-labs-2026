package io.github.xeltalliv.lab2.part1.matrix;

public class OneThreadApproach {
	public static double[][] compute(double[][] matrixA, double[][] matrixB) {
	    int rowsA = matrixA.length;
	    int colsA = matrixA[0].length;
	    int rowsB = matrixB.length;
	    int colsB = matrixB[0].length;

	    if (colsA != rowsB) {
	        throw new IllegalArgumentException("Matrix dimensions do not match: " +
	                "Columns of A (" + colsA + ") must equal Rows of B (" + rowsB + ").");
	    }

	    double[][] result = new double[rowsA][colsB];
	    for (int i = 0; i < rowsA; i++) {
	        for (int j = 0; j < colsB; j++) {
	            for (int k = 0; k < colsA; k++) {
	                result[i][j] += matrixA[i][k] * matrixB[k][j];
	            }
	        }
	    }
	    return result;
	}
}


