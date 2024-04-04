package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class responsible for query handling and adding search results
 */
public class QueryFileProcsesor {
	/**
	 * Map to store search results
	 */
	private final Map<String, List<InvertedIndex.SearchResult>> searchResultsMap;

	/**
	 * Inverted index instance for searching
	 */
	private final InvertedIndex indexer;

	/**
	 * SnowballStemmer instance for query processing word stems
	 */
	private final SnowballStemmer stemmer;

	/**
	 * A boolean indicating whether or not to partial search
	 */
	private final boolean partial;

	/**
	 * Constructs a new QueryFileProcsesor with the InvertedIndex
	 *
	 * @param indexer The InvertedIndex instance for searching
	 * @param partial boolean for partial search or not
	 */
	public QueryFileProcsesor(InvertedIndex indexer, boolean partial) {
		this.indexer = indexer;
		this.searchResultsMap = new TreeMap<>();
		this.stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		this.partial = partial;
	}

	/**
	 * Processes search queries from a path
	 *
	 * @param queryPath The path containing search queries
	 * @throws IOException If an I/O error occurs
	 */
	public void processQueries(Path queryPath) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(queryPath)) {
			String line;
			while ((line = reader.readLine()) != null) {
				processQueries(line);
			}
		}
	}

	/**
	 * Processes a single search query line
	 *
	 * @param queryLine The query line to process
	 */
	public void processQueries(String queryLine) {
		TreeSet<String> query = FileStemmer.uniqueStems(queryLine, stemmer);
		if (query.isEmpty()) {
			return;
		}
		/*
		 * TODO Do we need to re-search for the same query line multiple times?
		 * 
		 * Consider the respect.txt query file and its expected search results. How many
		 * query lines do you see in the query file? How many query lines do you see in
		 * the search results? Do you need to repeat search for every query line in that
		 * respect.txt query file?
		 * 
		 * The files in question are linked below. What could you do here to avoid
		 * reconducting a search that you already have results for?
		 */

		// TODO respect.txt: https://github.com/usf-cs272-spring2024/project-tests/blob/main/input/query/respect.txt
		// TODO results: https://github.com/usf-cs272-spring2024/project-tests/blob/main/expected-nix/exact/exact-respect-stems.json 
		
		List<InvertedIndex.SearchResult> searchResults = null; // TODO No need to do this on two lines, declare and define on the same line!
		searchResults = search(query);
		searchResultsMap.put(String.join(" ", query), searchResults);
	}

	/**
	 * Searches the inverted index for the specified queries
	 * 
	 * @param queries The set of queries to search for
	 * @return A list of search results
	 */
	public List<InvertedIndex.SearchResult> search(Set<String> queries) { // TODO This needs to be a method in the inverted index, not here. It has nothign to do with the query file, but everythign to do with the search methods implemented in the inverted index.
		return partial ? indexer.partialSearch(queries) : indexer.exactSearch(queries);
	}

	/**
	 * Adds search results for a specific query
	 * 
	 * @param query         The query to add results for
	 * @param searchResults The list of search results to add
	 */
	public void addSearchResults(String query, List<InvertedIndex.SearchResult> searchResults) {
		/*
		 * TODO Remove this. You already have a method that adds to your data
		 * structure... that is the processQueries method. This one allows for arbitrary
		 * results from any index to overwrite the correctly calculated results from the
		 * index.
		 */
		searchResultsMap.put(query, searchResults);
	}

	/**
	 * Checks if search results exist for a query
	 *
	 * @param query The query to check results for
	 * @return True if search results exist, false otherwise
	 */
	public boolean hasSearchResults(String query) {
		/*
		 * TODO: The processing done to a query line before storing the results in your
		 * map (using the joined unique stems as the key, not the original query line)
		 * is not visible outside of the method or to the user of this method.
		 * 
		 * For example a user might search for "44FOUR44 66SIX66" but that is stored as
		 * "four six" in your map. The user has no idea that processing happened to the
		 * query, so they are going to call this method with the unprocessed
		 * "44FOUR44 66SIX66" version of the query line.
		 * 
		 * That means your code needs to do one of the following:
		 * 
		 * 1) Make that process visible via another public method and update the Javadoc
		 * to make it clear it must be called first.
		 * 
		 * 2) Repeat that process to the line before accessing the results in this
		 * method and any other that accesses the map of results by a key. (Therefore,
		 * stem and join "44FOUR44 66SIX66" into "four six" instead before doing the
		 * containsKey.)
		 */

		return searchResultsMap.containsKey(query);
	}

	/**
	 * Returns the total number processed queries
	 *
	 * @return The total number of processed queries
	 */
	public int getTotalQueries() {
		return searchResultsMap.size();
	}

	/**
	 * Returns an unmodifiable set containing the queries for search results
	 *
	 * @return An unmodifiable set containing the queries for search results
	 */
	public Set<String> viewQueryResults() {
		return Collections.unmodifiableSet(searchResultsMap.keySet());
	}
	
	// TODO Add one to get search results for a query line

	/**
	 * Writes the search results to a JSON file
	 * 
	 * @param resultsPath the output path of the JSON file
	 * @throws IOException if an I/O error occurs
	 */
	public void writeResults(String resultsPath) throws IOException {
		JsonWriter.writeResults(searchResultsMap, resultsPath);
	}

	/**
	 * Returns a string representation of the search results map
	 * 
	 * @return a string representation of the search results map
	 */
	@Override
	public String toString() {
		return searchResultsMap.toString();
	}
}
