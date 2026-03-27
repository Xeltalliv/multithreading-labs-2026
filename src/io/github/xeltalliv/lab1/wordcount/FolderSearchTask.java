package io.github.xeltalliv.lab1.wordcount;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class FolderSearchTask extends RecursiveTask<Long> {
	private static final long serialVersionUID = -8103465808686779620L;
	private final Folder folder;
	
	public FolderSearchTask(Folder folder) {
		this.folder = folder;
	}
	
	@Override
	protected Long compute() {
		long count = 0L;
		List<RecursiveTask<Long>> tasks = new LinkedList<>();

		for (Document document : folder.getDocuments()) {
			DocumentSearchTask task = new DocumentSearchTask(document);
			tasks.add(task);
			task.fork();
		}
		for (Folder folder : folder.getSubFolders()) {
			FolderSearchTask task = new FolderSearchTask(folder);
			tasks.add(task);
			task.fork();
		}

		for (RecursiveTask<Long> task : tasks) {
			count = count + task.join();
		}
		return count;
	}
}
