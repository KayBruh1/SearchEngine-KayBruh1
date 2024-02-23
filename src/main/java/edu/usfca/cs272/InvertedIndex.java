package edu.usfca.cs272;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class InvertedIndex {
	/** TreeMap storing word counts for each file */
	public static TreeMap<String, Integer> fileWordCounts = new TreeMap<>();

	/** TreeMap storing inverted index for files and word positions */
	public static TreeMap<String, TreeMap<String, List<Integer>>> invertedIndex = new TreeMap<>();


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
	 * Outputs word counts to JSON file

	 * @param wordCounts The word counts map to be written to file
	 * @param inputPath  The input path of the text file
	 * @param outputPath The output path of the JSON file
	 * @throws IOException 
	 */
	public static void outputWordCounts(HashMap<String, Integer> wordCounts, String inputPath, String outputPath) throws IOException {

		if (wordCounts.isEmpty()) {
			HashMap<String, Integer> pathWordCount = new HashMap<>();

			JsonWriter.writeObject(pathWordCount, Path.of(outputPath));
		} else {
			HashMap<String, Integer> pathWordCount = new HashMap<>();
			int totalWords = 0;

			for (int count : wordCounts.values()) {
				totalWords += count;
			}
			pathWordCount.put(inputPath, totalWords);

			JsonWriter.writeObject(pathWordCount, Path.of(outputPath));
		}
	}

	/**
	 * Writes inverted index to JSON file
	 * @throws IOException 
	 */
	public static void writeInvertedIndex(String indexPath, TreeMap<String, TreeMap<String, List<Integer>>> invertedIndex) throws IOException {
		TreeMap<String, TreeMap<String, List<Integer>>> convertedIndex = new TreeMap<>(invertedIndex);

		try (BufferedWriter writer = Files.newBufferedWriter(Path.of(indexPath), StandardCharsets.UTF_8)) {
			JsonWriter.writeIndex(convertedIndex, writer, 0);
		}
	}
}
