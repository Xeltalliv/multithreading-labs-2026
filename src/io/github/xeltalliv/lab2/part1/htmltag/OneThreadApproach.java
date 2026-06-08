package io.github.xeltalliv.lab2.part1.htmltag;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OneThreadApproach {
	public static String compute(String directoryPath) {
		File folder = new File(directoryPath);
		File[] listOfFiles = folder.listFiles();

		if (listOfFiles == null) return "no files!";

		Map<String, Integer> tagCounts = new HashMap<>();
		for (File file : listOfFiles) {
			if (!file.isFile()) continue;
			countTagsInFile(file, tagCounts);
		}
		return stringifyTagCounts(tagCounts);
	}
	
	public static String stringifyTagCounts(Map<String, Integer> tagCounts) {
		return tagCounts.entrySet()
				.stream()
				.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
				.limit(20)
				.map(entry -> "<" + entry.getKey() + "> x " + entry.getValue())
				.collect(Collectors.joining(", "));
	}

	public static void countTagsInFile(File file, Map<String, Integer> tagCounts) {
		//System.out.println("Processing: " + file.getName());
		try {
			Document doc = Jsoup.parse(file, "UTF-8");
			Elements allElements = doc.getAllElements();
			for (Element element : allElements) {
				String tagName = element.tagName(); 
				tagCounts.put(tagName, tagCounts.getOrDefault(tagName, 0) + 1);
			}
		} catch (IOException e) {
			System.err.println("Could not parse file " + file.getName() + ": " + e.getMessage());
		}
	}
}
