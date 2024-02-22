package edu.usfca.cs272;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class InvertedIndex {
	/** TreeMap storing word counts for each file */
	public TreeMap<String, Integer> fileWordCounts = new TreeMap<>();

	/** TreeMap storing inverted index for files and word positions */
	public TreeMap<String, TreeMap<String, List<Integer>>> invertedIndex = new TreeMap<>();


	public void addWordCount(String filePath, Integer count) {
		fileWordCounts.put(filePath, count);
	}

	public void addWord(String word, String filePath, Integer position) {
		if (!invertedIndex.containsKey(word)) {
			invertedIndex.put(word, new TreeMap<>());
		}

		TreeMap<String, List<Integer>> wordMap = invertedIndex.get(word);

		if (!wordMap.containsKey(filePath)) {
			wordMap.put(filePath, new ArrayList<>());
		}

		List<Integer> wordPosition = wordMap.get(filePath);
		wordPosition.add(position);
	}
	
	/**
	 * Writes inverted index to JSON file
	 * @throws IOException 
	 */
	private static void writeInvertedIndex(String indexPath) throws IOException {
		TreeMap<String, TreeMap<String, List<Integer>>> convertedIndex = new TreeMap<String, TreeMap<String, List<Integer>>>();

		try (BufferedWriter writer = Files.newBufferedWriter(Path.of(indexPath), StandardCharsets.UTF_8)) {
			JsonWriter.writeIndex(convertedIndex, writer, 0);
		}
	}
}
