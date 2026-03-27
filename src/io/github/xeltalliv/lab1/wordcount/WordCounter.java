package io.github.xeltalliv.lab1.wordcount;

import java.util.concurrent.ForkJoinPool;

public class WordCounter {
	private final ForkJoinPool forkJoinPool;
	public WordCounter(int numThreads) {
		if (numThreads > 0) {
			forkJoinPool = new ForkJoinPool(numThreads);
		} else {
			forkJoinPool = null;
		}
	}
	public String[] wordsIn(String line) {
		return line.trim().split("(\\s|\\p{Punct})+");
	}
	private Long countWordsInDocument(Document document) {
		long count = 0;
		for (String line : document.getLines()) {
			count += wordsIn(line).length;
		}
		return count;
	} 
	public Long countWordsSingleThreaded(Folder folder) {
		long count = 0;
		for (Folder subFolder : folder.getSubFolders()) {
			count = count + countWordsSingleThreaded(subFolder);
		}
		for (Document document : folder.getDocuments()) {
			count = count + countWordsInDocument(document);
		}
		return count;
	}
	public Long countWordsMultiThreaded(Folder folder){
		return forkJoinPool.invoke(new FolderSearchTask(folder));
	}
}