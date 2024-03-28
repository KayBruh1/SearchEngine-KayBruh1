package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class responsible for query handling and adding search results
 */
public class QueryFileProcsesor {
	/**
	 * Map to store search results
	 */
	Map<String, List<InvertedIndex.SearchResult>> searchResultsMap;

	/**
	 * Inverted index instance for searching
	 */
	InvertedIndex indexer;

	public QueryFileProcsesor(InvertedIndex indexer) {
		this.indexer = indexer;
		this.searchResultsMap = new TreeMap<>();
		new HashMap<>();
	}

	/**
	 * Processes search queries from a path
	 *
	 * @param queryPath The path containing search queries
	 * @throws IOException If an I/O error occurs
	 */
	public void processQueries(Path queryPath, boolean partial) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(queryPath)) {
			String line;
			while ((line = reader.readLine()) != null) {
				processQueries(line, partial);
			}
		}
	}

	/**
	 * Processes a single search query line
	 *
	 * @param queryLine The query line to process
	 */
	public void processQueries(String queryLine, boolean partial) {
		List<String> stemmedWords = FileStemmer.listStems(queryLine);
		List<String> query = new ArrayList<>(new HashSet<>(stemmedWords));
		if (query.isEmpty()) {
			return;
		}
		Collections.sort(query);
		List<InvertedIndex.SearchResult> searchResults = null;
		if (partial) {
			searchResults = indexer.partialSearch(new HashSet<>(query));
		} else {
			searchResults = indexer.exactSearch(new HashSet<>(query));
		}
		searchResultsMap.put(String.join(" ", query), searchResults);
	}

	/**
	 * Writes the search results to a JSON file
	 * 
	 * @param resultsPath the output path of the JSON file
	 * @throws IOException if an I/O error occurs
	 */
	public void writeResults(String resultsPath) throws IOException {
		JsonWriter.writeResults(searchResultsMap, resultsPath);
	}
}
