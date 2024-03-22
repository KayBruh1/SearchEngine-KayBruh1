package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import edu.usfca.cs272.InvertedIndex.SearchResult;

/**
 * Class responsible for query handling and adding search results
 */
public class QueryFileProcsesor {
	/** The map to store search results */
	private final Map<String, InvertedIndex.SearchResult> resultMap;

	/** The InvertedIndex instance to help with search operations */
	InvertedIndex indexer = new InvertedIndex();

	/**
	 * Constructs a new QueryFileProcsesor with an empty result map
	 */
	public QueryFileProcsesor() {
		this.resultMap = new HashMap<>();
	}

	/**
	 * Gets the map containing search results
	 *
	 * @return The map containing search results
	 */
	public Map<String, InvertedIndex.SearchResult> getResultMap() {
		return resultMap;
	}

	public void addResult(String location, int totalWords, int count) {
		SearchResult result = resultMap.getOrDefault(location, indexer.new SearchResult(location, 0, 0.0));
		result.updateCount(count);
		result.setScore(calculateScore(result.getCount(), totalWords));
		resultMap.put(location, result);
	}

	/**
	 * Processes search queries from a location
	 *
	 * @param queryPath The path containing search queries
	 * @return A list of processed search queries
	 * @throws IOException If an I/O error occurs
	 */
	public static List<List<String>> processQueries(Path queryPath) throws IOException {
		List<List<String>> processedQueries = new ArrayList<>();
		List<String> queryLines = Files.readAllLines(queryPath);
		for (String queryLine : queryLines) {
			List<String> stemmedWords = FileStemmer.listStems(queryLine);
			List<String> processedQuery = new ArrayList<>(new HashSet<>(stemmedWords));
			Collections.sort(processedQuery);
			processedQueries.add(processedQuery);
		}
		return processedQueries;
	}

	/**
	 * Calculates the score for a search result
	 *
	 * @param matches    The number of matches
	 * @param totalWords The total number of words
	 * @return The calculated score
	 */
	private static double calculateScore(int matches, int totalWords) {
		return (double) matches / totalWords;
	}
}
