package io.github.xeltalliv.lab1.wordcount;

import java.util.concurrent.RecursiveTask;

public class DocumentSearchTask extends RecursiveTask<Long> {
    private static final long serialVersionUID = -6757761263192957037L;
	private final Document document;
    
	public DocumentSearchTask(Document document) {
		this.document = document;
	}
	
	private String[] wordsIn(String line) {
		return line.trim().split("(\\s|\\p{Punct})+");
	}
	private Long countWordsInDocument(Document document) {
		long count = 0;
		for (String line : document.getLines()) {
			count += wordsIn(line).length;
		}
		return count;
	} 
    
    @Override
    protected Long compute() {
        return countWordsInDocument(document);
    }
}
