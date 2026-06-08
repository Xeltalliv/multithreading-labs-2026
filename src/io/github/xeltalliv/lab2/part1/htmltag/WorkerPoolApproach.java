package io.github.xeltalliv.lab2.part1.htmltag;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

public class WorkerPoolApproach {
	public static String compute(String directoryPath, int threadCount, int taskCount) throws InterruptedException, ExecutionException {
		File folder = new File(directoryPath);
		File[] listOfFiles = folder.listFiles();

		if (listOfFiles == null) return "no files!";
		
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        
        int chunkSize = (int) Math.ceil((double) listOfFiles.length / taskCount);
        
        for (int i = 0; i < taskCount; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, listOfFiles.length);
            
            if (start >= listOfFiles.length) break;

            futures.add(executor.submit(() -> {
            	Map<String, Integer> tagCounts = new HashMap<>();
                for (int j = start; j < end; j++) {
                	File file = listOfFiles[j];
        			if (!file.isFile()) continue;
        			OneThreadApproach.countTagsInFile(file, tagCounts);
                }
                return tagCounts;
            }));
        }

        Map<String, Integer> finalCount = new HashMap<String, Integer>();
        for (Future<Map<String, Integer>> future : futures) {
        	Map<String, Integer> perThreadCount = future.get();
        	perThreadCount.forEach((key, value) -> finalCount.merge(key, value, Integer::sum));
        }

        executor.shutdown();
        return OneThreadApproach.stringifyTagCounts(finalCount);
    }
}