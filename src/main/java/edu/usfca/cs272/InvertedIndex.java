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
 * Class representing an inverted index to add word counts, positions, and to a
 * JSON file, and write *
 */
public class InvertedIndex {
	/** TreeMap storing word counts for each file */
	private TreeMap<String, Integer> fileWordCounts;

	/** TreeMap storing inverted index for files and word positions */
	private TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

	/**
	 * Constructs a new InvertedIndex for fileWordCounts and invertedIndex
	 */
	public InvertedIndex() {
		this.fileWordCounts = new TreeMap<>();
		this.invertedIndex = new TreeMap<>();
	}

	/**
	 * Returns the file word counts
	 *
	 * @return the TreeMap containing file word counts
	 */
	public TreeMap<String, Integer> getFileWordCounts() {
		return fileWordCounts;
	}

	/**
	 * Returns the InvertedIndex
	 *
	 * @return the TreeMap containing the inverted index
	 */
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getInvertedIndex() {
		return invertedIndex;
	}

	/**
	 * Sets the TreeMap storing the word counts for each file
	 *
	 * @param fileWordCounts the TreeMap storing the word counts
	 */
	public void setFileWordCounts(TreeMap<String, Integer> fileWordCounts) {
		this.fileWordCounts = fileWordCounts;
	}

	/**
	 * Sets the TreeMap storing the inverted index
	 *
	 * @param invertedIndex the TreeMap storing the inverted index
	 */
	public void setInvertedIndex(TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex) {
		this.invertedIndex = invertedIndex;
	}

	/**
	 * Adds the word count for a file to the inverted index
	 *
	 * @param location The path of the file
	 * @param count    The count of words in the file
	 */
	public void addWordCount(String location, Integer count) {
		if (count > 0) {
			fileWordCounts.put(location, count);
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

	/**
	 * Writes the word counts to a JSON file
	 *
	 * @param inputPath  the input path of the file or directory
	 * @param countsPath the output path of the JSON file
	 * @throws IOException if an I/O error occurs
	 */
	public void writeCounts(Path inputPath, String countsPath) throws IOException {
		if (inputPath != null && Files.isDirectory(inputPath)) {
			JsonWriter.writeObject(fileWordCounts, Path.of(countsPath));
		} else if (inputPath != null) {
			outputWordCounts(fileWordCounts, inputPath.toString(), countsPath);
		} else {
			fileWordCounts.put("No input provided", 0);
			JsonWriter.writeObject(fileWordCounts, Path.of(countsPath));
		}
	}

	/**
	 * Outputs word counts to a JSON file
	 *
	 * @param wordCounts The word counts map to write to file
	 * @param inputPath  The input path of the file
	 * @param outputPath The output path of the JSON file
	 * @throws IOException If an I/O error occurs
	 */
	public void outputWordCounts(TreeMap<String, Integer> wordCounts, String inputPath, String outputPath)
			throws IOException {
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
	 * @param inputPath  the input path of the file or directory
	 * @param indexPath  the output path of the JSON file
	 * @param indexer    the InvertedIndex object
	 * @throws IOException if an I/O error occurs
	 */
	public void writeIndex(Path inputPath, String indexPath, InvertedIndex indexer) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(Path.of(indexPath), StandardCharsets.UTF_8)) {
			FileBuilder fileBuilder = new FileBuilder(indexer);
			;
			if (Files.isDirectory(inputPath)) {
				fileBuilder.processDirectory(inputPath, true);
			}
			JsonWriter.writeIndex(invertedIndex, writer, 0);
		}
	}

	/*
	 * TODO Add some more generally useful functionality
	 */
}
