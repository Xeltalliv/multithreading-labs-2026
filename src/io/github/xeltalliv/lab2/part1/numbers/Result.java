package io.github.xeltalliv.lab2.part1.numbers;

class Result {
	public long count;
	public double min;
	public double max;
	public double median;
	public double average;
	
	public Result(long count, double min, double max, double average) {
		this.count = count;
		this.min = min;
		this.max = max;
		this.median = (min + max) / 2.0;
		this.average = average;
	}
	
	public Result(Result r1, Result r2) {
		this.count = r1.count + r2.count;
		this.min = Math.min(r1.min, r2.min);
		this.max = Math.max(r1.max, r2.max);
		this.median = (min + max) / 2.0;
		this.average = ((r1.average * r1.count) + (r2.average * r2.count)) / count;
	}
}
