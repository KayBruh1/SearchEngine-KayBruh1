
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
public class ThreadedQueryFileProcsesor {
	/**
	 * Map to store search results
	 */
	private final Map<String, List<InvertedIndex.SearchResult>> searchResultsMap;

	/**
	 * Inverted index instance for searching
	 */
	private final ThreadSafeInvertedIndex mtIndexer;
	private final CustomWorkQueue workQueue;

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
	public ThreadedQueryFileProcsesor(InvertedIndex indexer, boolean partial, int numThreads) {
		this.mtIndexer = new ThreadSafeInvertedIndex(indexer);
		this.searchResultsMap = new TreeMap<>();
		this.workQueue = new CustomWorkQueue(numThreads);
		this.stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		this.partial = partial;
	}

	public void processQueries(Path queryPath) throws InterruptedException {
		try (BufferedReader reader = Files.newBufferedReader(queryPath)) {
			String line;
			while ((line = reader.readLine()) != null) {
				workQueue.execute(new QueryTask(line));
			}
		} catch (IOException e) {
			System.out.println("Error reading query file: " + e.getMessage());
		}
		workQueue.finish();
		workQueue.shutdown();
	}

	private class QueryTask implements Runnable {
		private final String queryLine;

		public QueryTask(String queryLine) {
			this.queryLine = queryLine;
		}

		@Override
		public void run() {
			processQuery(queryLine);
		}
	}

	private synchronized void processQuery(String queryLine) {
		TreeSet<String> query = FileStemmer.uniqueStems(queryLine, stemmer);
		if (query.isEmpty()) {
			return;
		}
		String queryVal = String.join(" ", query);
		if (searchResultsMap.get(queryVal) != null) {
			return;
		}
		List<InvertedIndex.SearchResult> searchResults = mtIndexer.search(query, partial);
		searchResultsMap.put(queryVal, searchResults);
	}

	/**
	 * Process query line to a stemmed query
	 * 
	 * @param queryLine The query line to process
	 * @return The stemmed query
	 */
	public String processQueryLine(String queryLine) {
		TreeSet<String> query = FileStemmer.uniqueStems(queryLine, stemmer);
		return String.join(" ", query);
	}

	/**
	 * Checks if search results exist for a query
	 *
	 * @param queryLine The query to check results for
	 * @return True if search results exist, false otherwise
	 */
	public boolean hasSearchResults(String queryLine) {
		String queryVal = processQueryLine(queryLine);
		return searchResultsMap.containsKey(queryVal);
	}

	/**
	 * Gets the search results for a query line
	 *
	 * @param queryLine The query line for search results
	 * @return The search results for the query line
	 */
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

	/**
	 * Writes the search results to a JSON file
	 * 
	 * @param resultsPath the output path of the JSON file
	 * @throws IOException if an I/O error occurs
	 */
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