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

public class QueryFileProcsesor {
    private Map<String, SearchResult> resultMap;

    public QueryFileProcsesor() {
        this.resultMap = new HashMap<>();
    }

    public void addResult(String location, int totalWords, int count) {
        SearchResult result = resultMap.getOrDefault(location, new SearchResult(location, totalWords, 0, 0.0));
        result.updateCount(count);
        result.setScore(calculateScore(result.getCount(), totalWords));
        resultMap.put(location, result);
    }

    public Map<String, SearchResult> getResultMap() {
        return resultMap;
    }

    private double calculateScore(int matches, int totalWords) {
        return (double) matches / totalWords;
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
}
