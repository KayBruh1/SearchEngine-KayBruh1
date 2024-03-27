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

/**
 * Class responsible for query handling and adding search results
 */
public class QueryFileProcsesor {
	/** The map to store search results */
	private final Map<String, InvertedIndex.SearchResult> resultMap;

	/** The InvertedIndex instance to help with search operations */
	InvertedIndex indexer = new InvertedIndex();
	
	/* TODO 
	Map<String, List<InvertedIndex.SearchResult>> searchResultsMap;
	InvertedIndex indexer;
	
	public QueryFileProcsesor(InvertedIndex indexer) {
		this.indexer = indexer;
		this.searchResultMap = new TreeMap<>();
	}
	*/

	/**
	 * Constructs a new QueryFileProcsesor with an empty result map
	 */
	public QueryFileProcsesor() {
		this.resultMap = new HashMap<>();
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
	
	/* TODO 
	public void processQueries(Path queryPath) throws IOException {
		buffered reader
		read line by line
		call the other processQueries on the line
	}
	
	public void processQueries(String queryLine) {
		stem the line... List<String> stemmedWords = FileStemmer.listStems(queryLine);
		ask the index for the search results
		
		if (partial) call index.partialSearch etc.
		
		store the search results
	}
	*/

}
