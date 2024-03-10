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
		return Collections.unmodifiableSortedMap(new TreeMap<>(invertedIndex));
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

	/*
	 * TODO There are 4 types of information stored, the counts, the words, the
	 * locations per word, and the positions per word. I should see 4 size methods,
	 * 4 has methods, 4 view methods, etc.
	 * 
	 * Make sure the view methods are returning DIFFERENT data, not duplicating what
	 * a different method did with different names.
	 */

	/**
	 * Looks for a word in the inverted index
	 *
	 * @param word The word to add
	 * @return The findings of the word
	 */
	public Map<String, TreeSet<Integer>> getWordInfo(String word) {
		TreeMap<String, TreeSet<Integer>> wordMap = invertedIndex.getOrDefault(word, new TreeMap<>());
		return Collections.unmodifiableMap(new TreeMap<>(wordMap));
	}

	/**
	 * Retrieves the file locations and their positions for a word
	 *
	 * @param word The word to get location information for
	 * @return A map containing file locations
	 */
	public Map<String, TreeSet<Integer>> getLocationInfo(String word) { // TODO Breaking encapsulation, and identical to
																		// the other method above?
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

	/*
	 * TODO Always start with adding the smallest "item" first, then add methods for
	 * multiple items. Here, that means 1 word, 1 location, 1 position. I'm not sure
	 * what happened, as you had this before:
	 * 
	 * https://github.com/usf-cs272-spring2024/project-KayBruh1/blob/
	 * 497875dba651eba029bc70ec23a5d7d3882cf766/src/main/java/edu/usfca/cs272/
	 * InvertedIndex.java#L52-L60
	 */
	/**
	 * Adds a word with its position in a file to the inverted index
	 *
	 * @param word      The word to add
	 * @param location  The path of the file
	 * @param positions The positions of the word in the file
	 */
	public void addWord(String word, String location, int position) {
		TreeMap<String, TreeSet<Integer>> fileMap = invertedIndex.get(word);
		if (fileMap == null) {
			fileMap = new TreeMap<>();
			invertedIndex.put(word, fileMap);
		}
		TreeSet<Integer> positions = fileMap.get(location);
		if (positions == null) {
			positions = new TreeSet<>();
			fileMap.put(location, positions);
		}
		positions.add(position);
	}

	/**
	 * Adds the word counts for a given location
	 *
	 * @param location   The path of the file
	 * @param wordCounts A map containing word counts for the specified location
	 */
	public void addWordCounts(String location, HashMap<String, Integer> wordCounts) { // TODO Remove, not quite how a
																						// method like this should work
																						// (the keys of wordCounts are
																						// ignored) and won't be needed
																						// in future
		int totalCount = counts.getOrDefault(location, 0);
		int newCount = 0;
		for (int count : wordCounts.values()) {
			newCount += count;
		}
		counts.put(location, totalCount + newCount);
	}

	/**
	 * Update the inverted index and word counts
	 *
	 * @param word     The word to add
	 * @param location The path of the file
	 * @param position The position of the word in the file
	 */
	public void updateStructures(String word, String location, int position) {
		updateIndex(word, location, position);
		updateCounts(location, word);
	}

	/**
	 * Adds a word to the inverted index
	 *
	 * @param word     The word to add
	 * @param location The path of the file
	 * @param position The position of the word in the file
	 */
	private void updateIndex(String word, String location, int position) {
		TreeMap<String, TreeSet<Integer>> fileMap = invertedIndex.get(word);
		if (fileMap == null) {
			fileMap = new TreeMap<>();
			invertedIndex.put(word, fileMap);
		}
		TreeSet<Integer> positions = fileMap.get(location);
		if (positions == null) {
			positions = new TreeSet<>();
			fileMap.put(location, positions);
		}
		positions.add(position);
	}

	/**
	 * Updates word counts for a given file.
	 *
	 * @param location The path of the file
	 * @param word     The word to update counts for
	 */
	private void updateCounts(String location, String word) {
		int count = counts.getOrDefault(location, 0);
		counts.put(location, count + 1);
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
		return Collections.unmodifiableMap(new TreeMap<>(invertedIndex));
	}

	/**
	 * Returns an unmodifiable view of the word positions for a location
	 *
	 * @param location The location to get word positions for
	 * @return an unmodifiable view of the word positions for the location
	 */
	public Map<String, TreeSet<Integer>> viewWords(String location) { // TODO Remove, encapsulation
		TreeMap<String, TreeSet<Integer>> wordPositions = invertedIndex.getOrDefault(location, new TreeMap<>());
		return Collections.unmodifiableMap(wordPositions);
	}

	/**
	 * Returns an unmodifiable view of the locations for a word
	 *
	 * @param word The word to get locations for
	 * @return an unmodifiable view of the locations for the word
	 */
	public Map<String, TreeSet<Integer>> viewLocations(String word) { // TODO Remove... encapsulation, duplicate logic
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

	/**
	 * Writes the word counts to a JSON file
	 *
	 * @param countsPath the output path of the JSON file
	 * @throws IOException if an I/O error occurs
	 */
	public void writeCounts(String countsPath) throws IOException {
		JsonWriter.writeObject(counts, Path.of(countsPath));
	}

	/**
	 * Writes the inverted index to a JSON file
	 *
	 * @param indexPath the output path of the JSON file
	 * @throws IOException if an I/O error occurs
	 */
	public void writeIndex(String indexPath) throws IOException {
		String indexWrite = JsonWriter.writeIndex(invertedIndex, Path.of(indexPath));
	}
}
