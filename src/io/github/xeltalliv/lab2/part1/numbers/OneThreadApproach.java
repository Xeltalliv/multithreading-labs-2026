package io.github.xeltalliv.lab2.part1.numbers;

public class OneThreadApproach {
	public static Result compute(double[] numbers) {
		int count = numbers.length;
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		double sum = 0;

		for (int j = 0; j < count; j++) {
			if (numbers[j] < min) min = numbers[j];
			if (numbers[j] > max) max = numbers[j];
			sum += numbers[j];
		}
		return new Result(count, min, max, sum / count);
	}
}
