package io.github.xeltalliv.lab1.wordcount;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Folder {
	private final String name;
    private final List<Folder> subFolders;
    private final List<Document> documents;
    
    public Folder(String name, List<Folder> subFolders, List<Document> documents) {
    	this.name = name;
    	this.subFolders = subFolders;
    	this.documents = documents;
    }
    
    public String getName() {
    	return name;
    }
    
    public List<Folder> getSubFolders() {
    	return subFolders;
    }
    
    public List<Document> getDocuments() {
    	return documents;
    }
    
    static Folder fromDirectory(File dir) throws IOException {
    	List<Document> documents = new LinkedList<>();
    	List<Folder> subFolders = new LinkedList<>();

    	for (File entry : dir.listFiles()) {
    		if (entry.isDirectory()) {
    			subFolders.add(Folder.fromDirectory(entry));
    		} else {
    			documents.add(Document.fromFile(entry));
    		}
    	}
    	return new Folder(dir.getName(), subFolders, documents);
    }
}