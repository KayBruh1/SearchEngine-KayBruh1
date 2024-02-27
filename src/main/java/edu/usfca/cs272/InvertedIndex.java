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

/**
 * Class representing an inverted index to add word counts, 
 * positions, and to a JSON file, and write * 
 */
public class InvertedIndex {
	// TODO Make these non-static, private final
	
	
	/** TreeMap storing word counts for each file */
	public static TreeMap<String, Integer> fileWordCounts = new TreeMap<>();

	/** TreeMap storing inverted index for files and word positions */
	// TODO public static TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex
	public static TreeMap<String, TreeMap<String, List<Integer>>> invertedIndex = new TreeMap<>();

	/* TODO 
	public InvertexIndex() {
		init the members here instead
	}
	*/
	
	/**
	 * Adds the word count for a file to the inverted index
	 *
	 * @param filePath The path of the file
	 * @param count    The count of words in the file
	 */
	public void addWordCount(String filePath, Integer count) {
		// TODO Only put the count if it is > 0
		fileWordCounts.put(filePath, count);
	}

	/**
	 * Adds a word with its position in a file to the inverted index
	 *
	 * @param word     The word to add
	 * @param filePath The path of the file
	 * @param position The position of the word in the file
	 */
	// TODO public void addWord(String word, String location, Integer position) {
	public void addWord(String word, String filePath, Integer position) {
		/* TODO 
		invertedIndex.putIfAbsent(word, new TreeMap<>());
		invertedIndex.get(word).putIfAbsent(filePath, new ArrayList<>());
		invertedIndex.get(word).get(filePath).add(position);
		
		Make sure everything in this class is compact in size 
		*/
		
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

	// TODO Remove
	/**
	 * Outputs word counts to a JSON file
	 *
	 * @param wordCounts The word counts map to write to file
	 * @param inputPath  The input path of the file
	 * @param outputPath The output path of the JSON file
	 * @throws IOException If an I/O error occurs
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
	 * Writes the inverted index to a JSON file
	 *
	 * @param indexPath     The output path of the JSON file
	 * @param invertedIndex The inverted index to write to file
	 * @throws IOException If an I/O error occurs
	 */
	public static void writeInvertedIndex(String indexPath, TreeMap<String, TreeMap<String, List<Integer>>> invertedIndex) throws IOException {
		TreeMap<String, TreeMap<String, List<Integer>>> convertedIndex = new TreeMap<>(invertedIndex);

		try (BufferedWriter writer = Files.newBufferedWriter(Path.of(indexPath), StandardCharsets.UTF_8)) {
			JsonWriter.writeIndex(convertedIndex, writer, 0);
		}
	}

	/**
	 * Writes to a JSON file
	 *
	 * @param indexPath     The output path of the JSON file
	 * @throws IOException If an I/O error occurs
	 */
	public static void writeEmpty(Path indexPath) throws IOException { // TODO Rename to writeCounts(...)
		fileWordCounts.put("No input provided", 0); // TODO Remove
		JsonWriter.writeObject(fileWordCounts, indexPath);
	}
	
	/*
	 * TODO 
	 * Add some more generally useful functionality
	 */
}
