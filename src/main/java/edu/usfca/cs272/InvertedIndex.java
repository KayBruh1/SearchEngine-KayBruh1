package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	public SortedMap<String, Integer> getCounts() {
		return Collections.unmodifiableSortedMap(counts);
	}

	/**
	 * Gets the total word count for a specific location
	 *
	 * @param location The location of the document
	 * @return The total word count at the location
	 */
	public int getTotalWordCount(String location) {
		return counts.getOrDefault(location, 0);
	}

	/**
	 * Finds the amount of different files
	 * 
	 * @return The number of files
	 */
	public int numCounts() {
		return counts.size();
	}

	/**
	 * Finds the amount of different words
	 * 
	 * @return The number of words
	 */
	public int numWords() {
		return invertedIndex.size();
	}

	/**
	 * Returns the number of locations of a word
	 *
	 * @param word The word to get locations for
	 * @return The number of locations the word appears
	 */
	public int numWordLocations(String word) {
		return viewLocations(word).size();
	}

	/**
	 * Returns the number of positions a word appears in a file
	 *
	 * @param word     The word to get positions for
	 * @param location The location to get positions for
	 * @return The number of positions the word appears in the location
	 */
	public int numWordPositions(String word, String location) {
		/*
		 * TODO There was an old comment from v1.4 that discussed the inefficiency of
		 * using getOrDefault:
		 * 
		 * https://github.com/usf-cs272-spring2024/project-KayBruh1/blob/
		 * 9094d098e1f1d281ec9513742a6ddb699e518073/src/main/java/edu/usfca/cs272/
		 * InvertedIndex.java#L64-L98
		 * 
		 * ...and you are still using it in your code. When I make a comment, it is
		 * important to apply that concept to your code everywhere---not just in the one
		 * place it is discussed.
		 */
		TreeMap<String, TreeSet<Integer>> fileMap = invertedIndex.getOrDefault(word, new TreeMap<>());
		TreeSet<Integer> positions = fileMap.getOrDefault(location, new TreeSet<>());
		return positions.size();
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
	 * Returns an unmodifiable view of the positions of a word's location
	 *
	 * @param word     The word to get positions for
	 * @param location The location to get positions for
	 * @return An unmodifiable view of the word location's position
	 */
	public Set<Integer> viewPositions(String word, String location) {
		TreeMap<String, TreeSet<Integer>> wordInfo = invertedIndex.get(word);
		if (wordInfo != null) {
			TreeSet<Integer> positions = wordInfo.get(location);
			if (positions != null) {
				return Collections.unmodifiableSet(positions);
			}
		}
		return Collections.emptySet();
	}

	/**
	 * Returns an unmodifiable view of the inverted index words
	 *
	 * @return An unmodifiable view of the words in the inverted index
	 */
	public Set<String> viewWords() {
		return Collections.unmodifiableSet(invertedIndex.keySet());
	}

	/**
	 * Returns an unmodifiable view of an inverted index word location
	 *
	 * @param word The word to get locations for
	 * @return An unmodifiable view of the word locations
	 */
	public Set<String> viewLocations(String word) {
		TreeMap<String, TreeSet<Integer>> locations = invertedIndex.get(word);
		return locations != null ? Collections.unmodifiableSet(locations.keySet()) : Collections.emptySet();
	}

	/**
	 * Adds the word count for a file to the inverted index
	 *
	 * @param location The path of the file
	 * @param count    The count of words in the file
	 */
	public void addWordCount(String location, Integer count) { // TODO Remove or make private
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
		invertedIndex.putIfAbsent(word, new TreeMap<>());
		invertedIndex.get(word).putIfAbsent(location, new TreeSet<>());
		invertedIndex.get(word).get(location).add(position);

		/*
		 * TODO To ensure our search result scores and rankings are always correct, we
		 * need to update the word count here instead (see comments in your builder
		 * class). This will keep the index and the counts always in sync with each
		 * other and better encapsulated. There are two ways to go about this (choose
		 * one):
		 * 
		 * 1) Every time a NEW word, location, position is added, increase the count for
		 * that location by 1. If we accidentally add the same word, location, and
		 * position again later, it should NOT increment the word count because it did
		 * not add anything new to the index. This is more direct and easier to
		 * implement now, but slightly complicates multithreading later. For example:
		 * 
		 * add(hello, hello.txt, 12) --> new entry, increment count by one
		 * 
		 * add(hello, hello.txt, 12) --> old entry, do not increment count
		 * 
		 * 2) Keep the maximum position found for a location as the word count. Ignore
		 * positions less than what is already stored. This is harder to reason about
		 * now and not a direct measurement, but slightly easier to multithread.
		 * 
		 * add(hello, hello.txt, 12) --> set word count to 12
		 * 
		 * add(world, hello.txt, 73) --> since 73 > 12, set word count to 73
		 * 
		 * add(earth, hello.txt, 10) --> since 10 < 73, ignore
		 */

	}

	/**
	 * Writes the word counts to a JSON file
	 *
	 * @param countsPath the output path of the JSON file
	 * @throws IOException if an I/O error occurs
	 */
	public void writeCounts(Path countsPath) throws IOException {
		JsonWriter.writeObject(counts, countsPath);
	}

	/**
	 * Writes the inverted index to a JSON file
	 *
	 * @param indexPath the output path of the JSON file
	 * @throws IOException if an I/O error occurs
	 */
	public void writeIndex(Path indexPath) throws IOException {
		JsonWriter.writeIndex(invertedIndex, indexPath);
	}

	/**
	 * Returns a string representation of the inverted index
	 * 
	 * @return a string representation of the inverted index
	 */
	@Override
	public String toString() {
		return invertedIndex.toString();
	}

	/**
	 * Performs an exact search based on the provided set of queries.
	 *
	 * @param queries The set of queries to search for
	 * @return A list of search results for each query
	 */
	public List<SearchResult> exactSearch(Set<String> queries) {
		Map<String, InvertedIndex.SearchResult> resultMap = new HashMap<>();
		ArrayList<SearchResult> results = new ArrayList<>();
		for (String query : queries) {
			processLocations(query, resultMap, results, invertedIndex.get(query));
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Performs a partial search based on the provided set of queries.
	 *
	 * @param queries The set of queries to search for
	 * @return A list of search results for each query
	 */
	public List<SearchResult> partialSearch(Set<String> queries) {
		Map<String, InvertedIndex.SearchResult> resultMap = new HashMap<>();
		ArrayList<SearchResult> results = new ArrayList<>();
		for (String query : queries) {
			for (Map.Entry<String, TreeMap<String, TreeSet<Integer>>> entry : invertedIndex.tailMap(query).entrySet()) {
				String word = entry.getKey();
				if (word.startsWith(query)) {
					processLocations(query, resultMap, results, entry.getValue());
				} else {
					break;
				}
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Processes locations for a query
	 *
	 * @param query     the query being processed
	 * @param resultMap the map containing search results
	 * @param results   the list to store search results
	 * @param locations the locations with the query
	 */
	private void processLocations(String query, Map<String, SearchResult> resultMap, List<SearchResult> results,
			TreeMap<String, TreeSet<Integer>> locations) {
		if (locations != null) {
			for (Map.Entry<String, TreeSet<Integer>> entry : locations.entrySet()) {
				String location = entry.getKey();
				TreeSet<Integer> positions = entry.getValue();
				int count = positions.size();
				SearchResult result = resultMap.get(location);
				if (result == null) {
					result = new SearchResult(location);
					resultMap.put(location, result);
					results.add(result);
				}
				result.updateCount(count);
			}
		}
	}

	/**
	 * Represents a single search result containing information on location, total
	 * words, count, and score
	 */
	public class SearchResult implements Comparable<SearchResult> {
		/**
		 * The location of the search result
		 */
		private final String location;

		/**
		 * The count of matches for the search query
		 */
		private int count;

		/**
		 * The representing score of the search result
		 */
		private double score;

		/**
		 * Constructs a search result with the location, word count, match count, and
		 * score
		 *
		 * @param location the location of the search result
		 */
		public SearchResult(String location) {
			this.location = location;
			this.count = 0;
			this.score = 0.0;
		}

		/**
		 * Updates the match count
		 *
		 * @param matches the number of matches to add
		 */
		private void updateCount(int matches) {
			this.count += matches;
			this.score = (double) count / counts.get(this.location);
		}

		/**
		 * Gets the count of matches for the search result
		 *
		 * @return the count of matches
		 */
		public int getCount() {
			return count;
		}

		/**
		 * Gets the score for the search result
		 *
		 * @return the score
		 */
		public double getScore() {
			return score;
		}

		/**
		 * Gets the location for the search result
		 *
		 * @return the location
		 */
		public String getLocation() {
			return location;
		}

		/**
		 * Compares one search result with another for sorting
		 *
		 * @param other the other search result to compare with
		 * @return a negative, positive, or zero based on the comparison
		 */
		@Override
		public int compareTo(SearchResult other) {
			int scoreComparison = Double.compare(other.score, this.score);
			if (scoreComparison != 0) {
				return scoreComparison;
			}
			int countComparison = Integer.compare(other.count, this.count);
			if (countComparison != 0) {
				return countComparison;
			}
			return this.location.compareToIgnoreCase(other.location);
		}
	}
}