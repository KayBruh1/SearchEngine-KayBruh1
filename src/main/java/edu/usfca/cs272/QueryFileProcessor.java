package edu.usfca.cs272;

import java.io.IOException;
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
public class QueryFileProcessor implements QueryFileProcessorInterface {
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
	public QueryFileProcessor(InvertedIndex indexer, boolean partial) {
		this.indexer = indexer;
		this.searchResultsMap = new TreeMap<>();
		this.stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		this.partial = partial;
	}

	/**
	 * Processes a single search query line
	 *
	 * @param queryLine The query line to process
	 */
	@Override
	public void processQueries(String queryLine) {
		TreeSet<String> query = FileStemmer.uniqueStems(queryLine, stemmer);
		if (query.isEmpty()) {
			return;
		}
		String queryVal = String.join(" ", query);
		if (searchResultsMap.get(queryVal) != null) {
			return;
		}
		List<InvertedIndex.SearchResult> searchResults = indexer.search(query, partial);
		searchResultsMap.put(queryVal, searchResults);
	}

	/**
	 * Checks if search results exist for a query
	 *
	 * @param queryLine The query to check results for
	 * @return True if search results exist, false otherwise
	 */
	@Override
	public boolean hasSearchResults(String queryLine) {
		String queryVal = processQueryLine(queryLine);
		return searchResultsMap.containsKey(queryVal);
	}

	/*
	 * TODO Override processQueryLine in this class to reuse your
	 * stemmer member!
	 */
	
	/**
	 * Gets the search results for a query line
	 *
	 * @param queryLine The query line for search results
	 * @return The search results for the query line
	 */
	@Override
	public List<InvertedIndex.SearchResult> getQueryLineResults(String queryLine) {
		String queryVal = processQueryLine(queryLine);
		List<InvertedIndex.SearchResult> results = searchResultsMap.get(queryVal);
		if (results == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(results);
	}

	/**
	 * Returns the total number processed queries
	 *
	 * @return The total number of processed queries
	 */
	@Override
	public int getTotalQueries() { // TODO Remove, will be implemented in interface instead
		return searchResultsMap.size();
	}

	/**
	 * Returns an unmodifiable set containing the queries for search results
	 *
	 * @return An unmodifiable set containing the queries for search results
	 */
	@Override
	public Set<String> viewQueryResults() {
		return Collections.unmodifiableSet(searchResultsMap.keySet());
	}

	/**
	 * Writes the search results to a JSON file
	 * 
	 * @param resultsPath the output path of the JSON file
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	public void writeResults(Path resultsPath) throws IOException {
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
