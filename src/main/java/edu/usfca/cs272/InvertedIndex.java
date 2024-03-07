package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class representing an inverted index to add word counts, positions, and to a
 * JSON file, and write *
 */
public class InvertedIndex {
	/** TreeMap storing word counts for each file */
	private final TreeMap<String, Integer> counts;

	/** TreeMap storing inverted index for files and word positions */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex;

	/**
	 * Constructs a new InvertedIndex for counts and invertedIndex
	 */
	public InvertedIndex() {
		this.counts = new TreeMap<>();
		this.invertedIndex = new TreeMap<>();
	}

	/**
	 * Returns the word counts
	 *
	 * @return the TreeMap containing word counts
	 */
	public SortedMap<String, Integer> getWordCounts() {
		return Collections.unmodifiableSortedMap(counts);
	}

	/**
	 * Returns the InvertedIndex
	 *
	 * @return the TreeMap containing the inverted index
	 */
	public SortedMap<String, TreeMap<String, TreeSet<Integer>>> getInvertedIndex() {
		return Collections.unmodifiableSortedMap(invertedIndex);
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
	public int getCountSize() {
		return counts.size();
	}

	/**
	 * Looks for a word in the inverted index
	 *
	 * @param word The word to add
	 * @return The findings of the word
	 */
	public Map<String, TreeSet<Integer>> getWordInfo(String word) {
		TreeMap<String, TreeSet<Integer>> wordMap = invertedIndex.get(word);
		if (wordMap != null) {
			return Collections.unmodifiableMap(new TreeMap<>(wordMap));
		} else {
			return Collections.emptyMap();
		}
	}

	/**
	 * Retrieves the file locations and their positions for a word
	 *
	 * @param word The word to get location information for
	 * @return A map containing file locations
	 */
	public Map<String, TreeSet<Integer>> getLocationInfo(String word) {
		TreeMap<String, TreeSet<Integer>> locationInfo = invertedIndex.get(word);
		if (locationInfo != null) {
			return Collections.unmodifiableMap(new TreeMap<>(locationInfo));
		} else {
			return Collections.emptyMap();
		}
	}

	/**
	 * Adds the word count for a file to the inverted index
	 *
	 * @param location The path of the file
	 * @param count    The count of words in the file
	 */
	public void addWordCount(String location, Integer count) {
		if (count > 0) {
			counts.put(location, count);
		}
	}

	/**
	 * Adds a word with its position in a file to the inverted index
	 *
	 * @param word      The word to add
	 * @param location  The path of the file
	 * @param positions The positions of the word in the file
	 */
	public void addWord(String word, String location, TreeSet<Integer> positions) {
		TreeMap<String, TreeSet<Integer>> fileMap = invertedIndex.get(word);
		if (fileMap == null) {
			fileMap = new TreeMap<>();
			invertedIndex.put(word, fileMap);
		}
		TreeSet<Integer> current = fileMap.get(location);
		if (current == null) {
			current = new TreeSet<>();
			fileMap.put(location, current);
		}
		current.addAll(positions);
	}

	/**
	 * Adds the word counts for a given location
	 *
	 * @param location   The path of the file
	 * @param wordCounts A map containing word counts for the specified location
	 */
	public void addWordCounts(String location, HashMap<String, Integer> wordCounts) {
		int totalCount = counts.getOrDefault(location, 0);
		int newCount = 0;
		for (int count : wordCounts.values()) {
			newCount += count;
		}
		counts.put(location, totalCount + newCount);
	}

	/**
	 * Adds to the inverted index for a given file location
	 *
	 * @param location      The path of the file to be added
	 * @param invertedIndex The inverted index map containing words to file
	 *                      locations and positions
	 */
	public void addInvertedIndex(String location, TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex) {
		for (Map.Entry<String, TreeMap<String, TreeSet<Integer>>> entry : invertedIndex.entrySet()) {
			String word = entry.getKey();
			TreeMap<String, TreeSet<Integer>> fileMap = entry.getValue();
			for (Map.Entry<String, TreeSet<Integer>> fileEntry : fileMap.entrySet()) {
				String fileLocation = fileEntry.getKey();
				TreeSet<Integer> positions = fileEntry.getValue();
				addWord(word, fileLocation, positions);
			}
		}
	}

	/**
	 * Returns an unmodifiable view of the word counts
	 *
	 * @return an unmodifiable view of the word counts
	 */
	public Map<String, Integer> viewCounts() {
		return Collections.unmodifiableMap(counts);
	}

	/**
	 * Returns an unmodifiable view of the inverted index
	 *
	 * @return an unmodifiable view of the inverted index
	 */
	public Map<String, TreeMap<String, TreeSet<Integer>>> viewIndex() {
		return Collections.unmodifiableMap(invertedIndex);
	}

	/**
	 * Returns an unmodifiable view of the word positions for a location
	 *
	 * @param location The location to get word positions for
	 * @return an unmodifiable view of the word positions for the location
	 */
	public Map<String, TreeSet<Integer>> viewWords(String location) {
		TreeMap<String, TreeSet<Integer>> wordPositions = invertedIndex.getOrDefault(location, new TreeMap<>());
		return Collections.unmodifiableMap(wordPositions);
	}

	/**
	 * Returns an unmodifiable view of the locations for a word
	 *
	 * @param word The word to get locations for
	 * @return an unmodifiable view of the locations for the word
	 */
	public Map<String, TreeSet<Integer>> viewLocations(String word) {
		return Collections.unmodifiableMap(invertedIndex.getOrDefault(word, new TreeMap<>()));
	}

	/**
	 * Check if the location exists in the word counts
	 *
	 * @param location The location to check
	 * @return True if the location exists, false otherwise
	 */
	public boolean hasLocation(String location) {
		return counts.containsKey(location);
	}

	/**
	 * Check if the word exists in the inverted index
	 *
	 * @param word The word to check
	 * @return True if the word exists, false otherwise
	 */
	public boolean hasWord(String word) {
		return invertedIndex.containsKey(word);
	}

	/*
	 * TODO Still missing many methods. Try to make:
	 * 
	 * get or view methods, viewCounts, viewWords, viewLocations, etc. has or
	 * contains methods, hasWord, etc. num or size methods, numWords, etc.
	 * 
	 * (each of the above usually has the same number of methods to make sure all
	 * data is safely accessible)
	 * 
	 * toString addAll etc.
	 */

	/**
	 * Writes the word counts to a JSON file
	 *
	 * @param inputPath  the input path of the file or directory
	 * @param countsPath the output path of the JSON file
	 * @throws IOException if an I/O error occurs
	 */
	public void writeCounts(String countsPath) throws IOException {
		JsonWriter.writeObject(counts, Path.of(countsPath));
	}

	/**
	 * Writes the inverted index to a JSON file
	 *
	 * @param inputPath the input path of the file or directory
	 * @param indexPath the output path of the JSON file
	 * @param indexer   the InvertedIndex object
	 * @throws IOException if an I/O error occurs
	 */
	public void writeIndex(String indexPath) throws IOException {
		JsonWriter.writeIndex(invertedIndex, indexPath, 0);
	}
}