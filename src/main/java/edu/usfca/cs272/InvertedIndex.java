package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
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
	public SortedMap<String, Integer> getCounts() {
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
	 * Finds the amount of different files
	 * 
	 * @return The number of files
	 */
	public int countSize() {
		return counts.size();
	}

	/**
	 * Finds the amount of different words
	 * 
	 * @return The number of words
	 */
	public int indexSize() {
		return invertedIndex.size();
	}

	/**
	 * Finds the total count of words in all files
	 *
	 * @return The total count of words
	 */
	public int totalWordCount() {
		int totalCount = 0;
		for (int count : counts.values()) {
			totalCount += count;
		}
		return totalCount;
	}

	/**
	 * Finds the total count of positions for a word
	 *
	 * @param word The word to count positions for
	 * @return The total count of positions for the word
	 */
	public int totalPositionCount(String word) {
		int totalPositions = 0;
		TreeMap<String, TreeSet<Integer>> fileMap = invertedIndex.getOrDefault(word, new TreeMap<>());
		for (TreeSet<Integer> positions : fileMap.values()) {
			totalPositions += positions.size();
		}
		return totalPositions;
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
	 * Checks if a word at a specific location exists
	 *
	 * @param word     The word to check
	 * @param location The location to check
	 * @return True if the word at location exists, false otherwise
	 */
	public boolean hasWordLocation(String word, String location) {
		return invertedIndex.containsKey(word) && invertedIndex.get(word).containsKey(location);
	}

	/**
	 * Checks if a word exists at a specific location position
	 *
	 * @param word     The word to check
	 * @param location The location to check
	 * @param position The position of the word
	 * @return True if the word exists at the location position, false otherwise
	 */
	public boolean hasWordPosition(String word, String location, int position) {
		return hasWordLocation(word, location) && invertedIndex.get(word).get(location).contains(position);
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
	 * Returns an unmodifiable view of the word location
	 *
	 * @param word The word to retrieve locations
	 * @return An unmodifiable view of the word locations
	 */
	public Map<String, TreeSet<Integer>> viewWordLocations(String word) {
		TreeMap<String, TreeSet<Integer>> locations = invertedIndex.getOrDefault(word, new TreeMap<>());
		return Collections.unmodifiableMap(locations);
	}

	/**
	 * Returns an unmodifiable view of the positions for a word
	 *
	 * @param word     The word to retrieve positions for
	 * @param location The location to retrieve positions for
	 * @return An unmodifiable view of the positions for the word
	 */
	public TreeSet<Integer> viewWordPositions(String word, String location) {
		TreeMap<String, TreeSet<Integer>> fileMap = invertedIndex.getOrDefault(word, new TreeMap<>());
		SortedSet<Integer> positions = fileMap.getOrDefault(location, new TreeSet<>());
		return new TreeSet<>(positions);
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
	 * @param word     The word to add
	 * @param location The path of the file
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

	public int getTotalWordCount(String location) {
		return counts.getOrDefault(location, 0);
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
