package io.github.xeltalliv.lab2.part1.htmltag;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapReduceApproach {
    public static String compute(String directoryPath, int threadCount) throws InterruptedException {
		File folder = new File(directoryPath);
		File[] listOfFiles = folder.listFiles();

		if (listOfFiles == null) return "no files!";
    	
        int chunkSize = (int) Math.ceil((double) listOfFiles.length / threadCount);
        
        final Thread[] threads = new Thread[threadCount];
        List<Map<String, Integer>> results = new ArrayList<>(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, listOfFiles.length);
            
            threads[threadIndex] = new Thread(() -> {
            	Map<String, Integer> tagCounts = new HashMap<>();
                for (int j = start; j < end; j++) {
                	File file = listOfFiles[j];
        			if (!file.isFile()) continue;
        			OneThreadApproach.countTagsInFile(file, tagCounts);
                }
                results.add(tagCounts);
            });
            threads[threadIndex].start();
        }
        for (Thread thread : threads) thread.join();
        
        Map<String, Integer> finalCount = new HashMap<String, Integer>();
        for (Map<String, Integer> perThreadCount : results) {
        	perThreadCount.forEach((key, value) -> finalCount.merge(key, value, Integer::sum));
        }

        return OneThreadApproach.stringifyTagCounts(finalCount);
    }
}