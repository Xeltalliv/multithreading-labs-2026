package io.github.xeltalliv.lab1.transpose;

import java.util.Random;

public class Matrix {
	double[][] data;
	int width;
	int height;
	
	public Matrix(int width, int height) {
		this.width = width;
		this.height = height;
		this.data = new double[height][width]; 
	}
	
	public void initRandom() {
		Random random = new Random();
		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {
				data[i][j] = random.nextDouble();
			}
		}
	}
	
	public Matrix transposeSingleThreaded() {
		Matrix transposed = new Matrix(height, width);
		
		final double[][] indata = this.data;
		final double[][] outdata = transposed.data;
		final int w = this.width;
		final int h = this.height;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				outdata[j][i] = indata[i][j];
			}
		}
		return transposed;
	}
	public Matrix transposeMultiThreaded(int numThreads) {
		Matrix transposed = new Matrix(height, width);
		Thread[] ths =  new Thread[numThreads];
		
		final double[][] indata = this.data;
		final double[][] outdata = transposed.data;
		final int w = this.width;
		final int h = this.height;
		for(int k=0; k<numThreads; k++) {
			final int start = k;
			ths[k] = new Thread(() -> {
				for(int i = start; i<h; i+=numThreads) {
					for(int j=0; j<w; j++) {
						outdata[j][i] = indata[i][j]; 
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
		return transposed;
	}
	
	public void print() {
		System.out.println();
		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {
				System.out.print(data[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}
	}
}
