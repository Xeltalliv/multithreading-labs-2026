package io.github.xeltalliv.lab1.wordcount;

import java.io.File;
import java.io.IOException;

import io.github.xeltalliv.lab1.SpeedTest;

public class Main {
	public static void main(String[] args) throws IOException {
		Folder folder = Folder.fromDirectory(new File("/dir/here"));
		
		WordCounter wordCounter = new WordCounter(10);
		long words1 = wordCounter.countWordsSingleThreaded(folder);
		long words2 = wordCounter.countWordsMultiThreaded(folder);
		System.out.println("words = "+words1);
		System.out.println("words = "+words2);
		
		SpeedTest.test(() -> {(new WordCounter(0)).countWordsSingleThreaded(folder);}, 0);
		SpeedTest.test(() -> {(new WordCounter(2)).countWordsMultiThreaded(folder);}, 2);
		SpeedTest.test(() -> {(new WordCounter(4)).countWordsMultiThreaded(folder);}, 4);
		SpeedTest.test(() -> {(new WordCounter(6)).countWordsMultiThreaded(folder);}, 6);
		SpeedTest.test(() -> {(new WordCounter(8)).countWordsMultiThreaded(folder);}, 8);
		SpeedTest.test(() -> {(new WordCounter(10)).countWordsMultiThreaded(folder);}, 10);
	}
}
