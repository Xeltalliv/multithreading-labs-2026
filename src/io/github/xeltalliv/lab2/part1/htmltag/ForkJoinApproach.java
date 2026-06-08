package io.github.xeltalliv.lab2.part1.htmltag;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinApproach {
    private static class ArrayTask extends RecursiveTask<Map<String, Integer>> {
        private static final long serialVersionUID = 1L; // To prevent warning
		private final File[] listOfFiles;
        private final int start;
        private final int end;
        private final int threshold;

        public ArrayTask(File[] listOfFiles, int threshold, int start, int end) {
            this.listOfFiles = listOfFiles;
            this.threshold = threshold;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Map<String, Integer> compute() {
        	int count = end - start;
            if (count <= threshold) {
            	Map<String, Integer> tagCounts = new HashMap<>();
                for (int j = start; j < end; j++) {
                	File file = listOfFiles[j];
        			if (!file.isFile()) continue;
        			OneThreadApproach.countTagsInFile(file, tagCounts);
                }
                return tagCounts;
            } else {
            	int mid = start + (end - start) / 2;
                ArrayTask leftTask = new ArrayTask(listOfFiles, threshold, start, mid);
                ArrayTask rightTask = new ArrayTask(listOfFiles, threshold, mid, end);
                leftTask.fork(); // Run asynchronously
                Map<String, Integer> rightResult = rightTask.compute(); // Run synchronously in current thread
                Map<String, Integer> leftResult = leftTask.join(); // Wait for fork result
                
                // Merge both Maps
                leftResult.forEach((key, value) -> rightResult.merge(key, value, Integer::sum));
                return rightResult;
            }
        }
    }

    @SuppressWarnings("resource")
    public static String compute(String directoryPath, int threadCount, int threshold) {
		File folder = new File(directoryPath);
		File[] listOfFiles = folder.listFiles();
		
		if (listOfFiles == null) return "no files!";
		
    	ForkJoinPool pool = new ForkJoinPool(threadCount);
    	try {
    		Map<String, Integer> finalCount = pool.invoke(new ArrayTask(listOfFiles, threshold, 0, listOfFiles.length));
    		return OneThreadApproach.stringifyTagCounts(finalCount);
    	} finally {
    		pool.shutdown();
    	}
    }
}
