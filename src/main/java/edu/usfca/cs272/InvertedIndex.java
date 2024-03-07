package edu.usfca.cs272;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class representing an inverted index to add word counts, positions, and to a
 * JSON file, and write *
 */
public class InvertedIndex {
	/*
	 * TODO Can use either the static -or- final keywords on the two private
	 * members below without having to change any other code. Which one should
	 * you use?
	 */
	
	/** TreeMap storing word counts for each file */
	private TreeMap<String, Integer> fileWordCounts; // TODO Call just counts or wordCounts? We won't always work with files

	/** TreeMap storing inverted index for files and word positions */
	private TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

	/**
	 * Constructs a new InvertedIndex for fileWordCounts and invertedIndex
	 */
	public InvertedIndex() {
		this.fileWordCounts = new TreeMap<>();
		this.invertedIndex = new TreeMap<>();
	}

	/*
	 * TODO The get methods here are breaking encapsulation. It is now time to fix
	 * this problem. The PrefixMap example from the lectures illustrates how to fix
	 * this problem efficiently.
	 */
	
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

	/*
	 * TODO Remove these set methods---it will allow for invalid data to be placed
	 * into our data structure and break encapsulation.
	 */
	
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
	public void addWord(String word, String location, TreeSet<Integer> positions) {
	    invertedIndex.putIfAbsent(word, new TreeMap<>());
	    TreeMap<String, TreeSet<Integer>> fileMap = invertedIndex.get(word);
	    fileMap.putIfAbsent(location, new TreeSet<>());
	    TreeSet<Integer> current = fileMap.get(location);
	    current.addAll(positions);
		/*
		 * TODO If you are interested in making this more efficient, there is a
		 * better way than using putIfAbsent. Otherwise, try to make this as compact
		 * as possible. (Choose one to be more important than the other in this class.)
		 */
	}

	/**
	 * Looks for a word in the inverted index
	 *
	 * @param word The word to add
	 * @return The findings of the word
	 */
	public List<String> findWord(String word) { // TODO Looks like a get method, not a find method? Not efficient way of doing this (copying from one type to the other). See PrefixMap for a better approach!
		if (invertedIndex.containsKey(word)) {
			TreeMap<String, TreeSet<Integer>> wordMap = invertedIndex.get(word);
			return new ArrayList<>(wordMap.keySet());
		}
		return Collections.emptyList();
	}

	/**
	 * Finds the amount of different words
	 * 
	 * @return The number of words
	 */
	public int getIndexSize() {
		return invertedIndex.size();
	}

	/**
	 * Finds the amount of different files
	 * 
	 * @return The number of files
	 */
	public int getFileCount() { // TODO getCountSize
		return fileWordCounts.size();
	}
	
	/*
	 * TODO Still missing many methods. Try to make:
	 * 
	 * get or view methods, viewCounts, viewWords, viewLocations, etc.
	 * has or contains methods, hasWord, etc.
	 * num or size methods, numWords, etc.
	 * 
	 * (each of the above usually has the same number of methods to make sure
	 * all data is safely accessible)
	 * 
	 * toString
	 * addAll
	 * etc.
	 */

	/*
	 * TODO This method should take ONLY the file PATH to produce the output
	 * (to avoid too much string to path to string conversion back and forth)
	 * 
	 * writeCounts(Path output) throws IOException
	 */
	/**
	 * Writes the word counts to a JSON file
	 *
	 * @param inputPath  the input path of the file or directory
	 * @param countsPath the output path of the JSON file
	 * @throws IOException if an I/O error occurs
	 */
	public void writeCounts(Path inputPath, String countsPath) throws IOException {
		// TODO Should only need to call 1 JsonWriter method here with nothing else
		// TODO Let exceptions happen if the parameter is an issue
		// TODO JsonWriter.writeObject(fileWordCounts, output);
		
		if (inputPath != null && Files.isDirectory(inputPath)) {
			JsonWriter.writeObject(fileWordCounts, Path.of(countsPath));
		} else if (inputPath != null) {
			outputWordCounts(fileWordCounts, inputPath.toString(), countsPath);
		} else {
			fileWordCounts.put("No input provided", 0);
			JsonWriter.writeObject(fileWordCounts, Path.of(countsPath));
		}
	}

	/*
	 * TODO Same as writeCounts, should only need 1 output Path parameter
	 * and 1 JsonWriter call (should create the bufferd writer in JsonWriter)
	 */
	/**
	 * Writes the inverted index to a JSON file
	 *
	 * @param inputPath the input path of the file or directory
	 * @param indexPath the output path of the JSON file
	 * @param indexer   the InvertedIndex object
	 * @throws IOException if an I/O error occurs
	 */
	public void writeIndex(Path inputPath, String indexPath, InvertedIndex indexer) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(Path.of(indexPath), StandardCharsets.UTF_8)) {
			FileBuilder fileBuilder = new FileBuilder(indexer);
			;
			if (Files.isDirectory(inputPath)) {
				fileBuilder.processDirectory(inputPath);
			}
			JsonWriter.writeIndex(invertedIndex, writer, 0);
		}
	}

	// TODO Remove this one, should not be needed anymore
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
}
