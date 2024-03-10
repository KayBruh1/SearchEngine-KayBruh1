package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
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

	/**
	 * Adds a word with its position in a file to the inverted index
	 *
	 * @param word      The word to add
	 * @param location  The path of the file
	 * @param position The position of the word in the file
	 */
	public void addWord(String word, String location, int position) {
	    TreeMap<String, TreeSet<Integer>> fileMap = invertedIndex.getOrDefault(word, new TreeMap<>());
	    TreeSet<Integer> positions = fileMap.getOrDefault(location, new TreeSet<>());
	    positions.add(position);
	    fileMap.put(location, positions);
	    invertedIndex.put(word, fileMap);
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
		JsonWriter.writeIndex(invertedIndex, Path.of(indexPath));
	}
}
