package edu.usfca.cs272;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class representing an inverted index to add word counts, 
 * positions, and to a JSON file, and write * 
 */
public class InvertedIndex {    
	/** TreeMap storing word counts for each file */
	private TreeMap<String, Integer> fileWordCounts; 

	/** TreeMap storing inverted index for files and word positions */
	private TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

	public InvertedIndex() {
		this.fileWordCounts = new TreeMap<>();
		this.invertedIndex = new TreeMap<>();
	}

	public TreeMap<String, Integer> getFileWordCounts() {
		return fileWordCounts;
	} 

	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getInvertedIndex() {
		return invertedIndex;
	}
	
	public void setFileWordCounts(TreeMap<String, Integer> fileWordCounts) {
	    this.fileWordCounts = fileWordCounts;
	}
	
	public void setInvertedIndex(TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex) {
	    this.invertedIndex = invertedIndex;
	}

	/**
	 * Adds the word count for a file to the inverted index
	 *
	 * @param filePath The path of the file
	 * @param count    The count of words in the file
	 */
	public void addWordCount(String filePath, Integer count) {
	    if (count > 0) {
	        fileWordCounts.put(filePath, count);
	    }
	}

	/**
	 * Adds a word with its position in a file to the inverted index
	 *
	 * @param word     The word to add
	 * @param location The path of the file
	 * @param position The position of the word in the file
	 */
	public void addWord(String word, String location, Integer position) {
	    invertedIndex.putIfAbsent(word, new TreeMap<>());
	    TreeMap<String, TreeSet<Integer>> wordMap = invertedIndex.get(word);
	    wordMap.putIfAbsent(location, new TreeSet<>());
	    TreeSet<Integer> wordPosition = wordMap.get(location);
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
	public void outputWordCounts(TreeMap<String, Integer> wordCounts, String inputPath, String outputPath) throws IOException {
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
	public void writeInvertedIndex(String indexPath, TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(Path.of(indexPath), StandardCharsets.UTF_8)) {
			JsonWriter.writeIndex(invertedIndex, writer, 0);
		}
	}

	/**
	 * Writes to a JSON file
	 *
	 * @param indexPath     The output path of the JSON file
	 * @throws IOException If an I/O error occurs
	 */
	public void writeEmpty(Path indexPath) throws IOException { // TODO Rename to writeCounts(...)
		fileWordCounts.put("No input provided", 0); // TODO Remove
		JsonWriter.writeObject(fileWordCounts, indexPath);
	}

	/*
	 * TODO 
	 * Add some more generally useful functionality
	 */
}
