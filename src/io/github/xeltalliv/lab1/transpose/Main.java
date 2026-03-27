package io.github.xeltalliv.lab1.transpose;

import io.github.xeltalliv.lab1.SpeedTest;

public class Main {
	public static void main(String[] args) {
		Matrix m1 = new Matrix(3, 8);
		m1.initRandom();
		m1.print();
		Matrix m1t1 = m1.transposeSingleThreaded();
		m1t1.print();
		Matrix m1t2 = m1.transposeMultiThreaded(10);
		m1t2.print();

		Matrix m2 = new Matrix(10000, 10000);
		m2.initRandom();
		
		SpeedTest.test(() -> {m2.transposeSingleThreaded();}, 0);
		SpeedTest.test(() -> {m2.transposeMultiThreaded(2);}, 2);
		SpeedTest.test(() -> {m2.transposeMultiThreaded(4);}, 4);
		SpeedTest.test(() -> {m2.transposeMultiThreaded(6);}, 6);
		SpeedTest.test(() -> {m2.transposeMultiThreaded(8);}, 8);
		SpeedTest.test(() -> {m2.transposeMultiThreaded(10);}, 10);
	}
}
