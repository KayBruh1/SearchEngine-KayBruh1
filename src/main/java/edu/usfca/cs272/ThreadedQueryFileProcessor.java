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
public class ThreadedQueryFileProcessor implements QueryFileProcessorInterface {
	/**
	 * Map to store search results
	 */
	private final Map<String, List<InvertedIndex.SearchResult>> searchResultsMap;

	/**
	 * Inverted index instance for searching
	 */
	private final ThreadSafeInvertedIndex mtIndexer;

	/**
	 * Work queue instance for multithreading
	 */
	private final CustomWorkQueue workQueue;

	/**
	 * A boolean indicating whether or not to partial search
	 */
	private final boolean partial;

	/**
	 * Constructs a new QueryFileProcsesor with the InvertedIndex
	 *
	 * @param indexer   The InvertedIndex instance for searching
	 * @param workQueue The work queue for multithreading
	 * @param partial   boolean for partial search or not
	 */
	public ThreadedQueryFileProcessor(ThreadSafeInvertedIndex indexer, CustomWorkQueue workQueue, boolean partial) {
		this.searchResultsMap = new TreeMap<>();
		this.mtIndexer = indexer;
		this.workQueue = workQueue;
		this.partial = partial;
	}

	/**
	 * Processes search queries from a path
	 *
	 * @param queryPath The path containing search queries
	 * @throws IOException If an I/O error occurs
	 */
	@Override
	public void processQueries(Path queryPath) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(queryPath)) {
			String line;
			while ((line = reader.readLine()) != null) {
				workQueue.execute(new QueryTask(line));
			}
		}
		workQueue.finish();
	}

	/**
	 * Class to help process queries
	 */
	private class QueryTask implements Runnable {
		/**
		 * The query line to process
		 */
		private final String queryLine;

		/**
		 * @param queryLine The query line to process
		 */
		public QueryTask(String queryLine) {
			this.queryLine = queryLine;
		}

		@Override
		public void run() {
			processQueries(queryLine);
		}
	}

	/**
	 * Processes a single search query line
	 *
	 * @param queryLine The query line to process
	 */
	@Override
	public void processQueries(String queryLine) {
		SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		TreeSet<String> query = FileStemmer.uniqueStems(queryLine, stemmer);
		if (query.isEmpty()) {
			return;
		}
		String queryVal = String.join(" ", query);
		synchronized (this) {
			if (searchResultsMap.containsKey(queryVal)) {
				return;
			}
		}
		List<InvertedIndex.SearchResult> searchResults = mtIndexer.search(query, partial);
		synchronized (this) {
			searchResultsMap.put(queryVal, searchResults);
		}
	}

	/**
	 * Process query line to a stemmed query
	 * 
	 * @param queryLine The query line to process
	 * @return The stemmed query
	 */
	@Override
	public synchronized String processQueryLine(String queryLine) {
		TreeSet<String> query = FileStemmer.uniqueStems(queryLine);
		return String.join(" ", query);
	}

	/**
	 * Checks if search results exist for a query
	 *
	 * @param queryLine The query to check results for
	 * @return True if search results exist, false otherwise
	 */
	@Override
	public synchronized boolean hasSearchResults(String queryLine) {
		String queryVal = processQueryLine(queryLine);
		return searchResultsMap.containsKey(queryVal);
	}

	/**
	 * Gets the search results for a query line
	 *
	 * @param queryLine The query line for search results
	 * @return The search results for the query line
	 */
	@Override
	public synchronized List<InvertedIndex.SearchResult> getQueryLineResults(String queryLine) {
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
	public synchronized int getTotalQueries() {
		return searchResultsMap.size();
	}

	/**
	 * Returns an unmodifiable set containing the queries for search results
	 *
	 * @return An unmodifiable set containing the queries for search results
	 */
	@Override
	public synchronized Set<String> viewQueryResults() {
		return Collections.unmodifiableSet(searchResultsMap.keySet());
	}

	/**
	 * Writes the search results to a JSON file
	 * 
	 * @param resultsPath the output path of the JSON file
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	public synchronized void writeResults(Path resultsPath) throws IOException {
		JsonWriter.writeResults(searchResultsMap, resultsPath);
	}

	/**
	 * Returns a string representation of the search results map
	 * 
	 * @return a string representation of the search results map
	 */
	@Override
	public synchronized String toString() {
		return searchResultsMap.toString();
	}
}