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
		return counts.size(); // TODO Might as well reuse the view here too.
	}

	/**
	 * Finds the amount of different words
	 * 
	 * @return The number of words
	 */
	public int numWords() {
		return invertedIndex.size(); // TODO Might as well reuse the view here too.
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
		TreeMap<String, TreeSet<Integer>> fileMap = invertedIndex.get(word);
		if (fileMap == null) {
			fileMap = new TreeMap<>();
		}
		TreeSet<Integer> positions = fileMap.get(location);
		if (positions == null) {
			positions = new TreeSet<>();
		}
		return positions.size();
		
		/*
		 * TODO This method unnecessarily creates new empty instances when 0 can be
		 * returned instead.
		 * 
		 * Since you have efficient view methods, and are using it in your
		 * numWordLocations above, you can actually switch ALL of the num and has
		 * methods for the inverted index to use your view methods instead. It would be
		 * more efficient, reuse more code, and make sure everything is consistently
		 * implemented using the same approach. For example: 
		 * 
		 * return viewPositions(word, location).size();
		 */
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
		/*
		 * TODO This can be implemented using your view too. For example:
		 * 
		 * return viewLocations(word).contains(location);
		 */
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
		// TODO Same issue
		return hasWordLocation(word, location) && invertedIndex.get(word).get(location).contains(position);
	}

	/**
	 * Returns an unmodifiable view of the word counts
	 *
	 * @return an unmodifiable view of the word counts
	 */
	public Map<String, Integer> viewCounts() { // TODO Either keep getCounts or viewCounts, and keep a consistent naming scheme.
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
	 * Adds all entries from another InvertedIndex to the current one.
	 *
	 * @param other The InvertedIndex entries to add
	 */
	public void addAll(InvertedIndex other) {
		for (Map.Entry<String, TreeMap<String, TreeSet<Integer>>> entry : other.invertedIndex.entrySet()) {
			String word = entry.getKey();
			TreeMap<String, TreeSet<Integer>> locations = entry.getValue();
			TreeMap<String, TreeSet<Integer>> thisLocations = this.invertedIndex.get(word);
			if (thisLocations == null) {
				this.invertedIndex.put(word, locations);
			} else {
				
				for (Map.Entry<String, TreeSet<Integer>> locationEntry : locations.entrySet()) {
					String location = locationEntry.getKey();
					TreeSet<Integer> positions = locationEntry.getValue();
					
					/*
					 * TODO This is not always safe to do if the other index has some of the same
					 * locations as this index. (Imagine this index has the first half of a file
					 * indexed and the other index has the entire file indexed, so there is overlap
					 * between the two.) This needs an if/else check similar to the one for
					 * thisLocations, but for a different level of nesting within the data
					 * structure.
					 */
					thisLocations.put(location, positions);
				}
			}
		}
		
		for (Map.Entry<String, Integer> entry : other.counts.entrySet()) {
			String location = entry.getKey();
			int count = entry.getValue();
			
			// TODO This is where a getOrDefault is great, because it doesn't need to create a new instance...
			this.counts.putIfAbsent(location, 0); // TODO Remove
			int checkCount = this.counts.get(location); // TODO getOrDefault(location, 0);
			if (count > checkCount) {
				this.counts.put(location, count);
			}
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
		int checkCount = counts.getOrDefault(location, 0);
		if (position > checkCount) {
			counts.put(location, position);
		}
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
	 * Searches the inverted index for the specified queries
	 * 
	 * @param queries The set of queries to search for
	 * @param partial Boolean for whether or not to partial search
	 * @return A list of search results
	 */
	public List<InvertedIndex.SearchResult> search(Set<String> queries, boolean partial) {
		return partial ? partialSearch(queries) : exactSearch(queries);
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