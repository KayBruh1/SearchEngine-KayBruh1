package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * Interface for query processing
 */
public interface QueryFileProcessorInterface {
    /**
     * Processes search queries from a path
     *
     * @param queryPath The path containing search queries
     * @throws IOException If an I/O error occurs
     */
	default void processQueries(Path queryPath) throws IOException {
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
    void processQueries(String queryLine);

    /**
     * Process query line to a stemmed query
     *
     * @param queryLine The query line to process
     * @return The stemmed query
     */
    String processQueryLine(String queryLine);

    /**
     * Checks if search results exist for a query
     *
     * @param queryLine The query to check results for
     * @return True if search results exist, false otherwise
     */
    boolean hasSearchResults(String queryLine);
    
    /**
     * Gets the search results for a query line
     *
     * @param queryLine The query line for search results
     * @return The search results for the query line
     */
    List<InvertedIndex.SearchResult> getQueryLineResults(String queryLine);

    /**
     * Returns the total number processed queries
     *
     * @return The total number of processed queries
     */
    int getTotalQueries();
    
    /**
     * Returns an unmodifiable set containing the queries for search results
     *
     * @return An unmodifiable set containing the queries for search results
     */
    Set<String> viewQueryResults();

    /**
     * Writes the search results to a JSON file
     *
     * @param resultsPath the output path of the JSON file
     * @throws IOException if an I/O error occurs
     */
    void writeResults(Path resultsPath) throws IOException;
}
