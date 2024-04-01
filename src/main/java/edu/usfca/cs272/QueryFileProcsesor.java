package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

	private final SnowballStemmer stemmer;

	/**
	 * Constructs a new QueryFileProcsesor with the InvertedIndex
	 *
	 * @param indexer The InvertedIndex instance for searching
	 */
	public QueryFileProcsesor(InvertedIndex indexer) {
		this.indexer = indexer;
		this.searchResultsMap = new TreeMap<>();
		this.stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
	}

	/**
	 * Processes search queries from a path
	 *
	 * @param queryPath The path containing search queries
	 * @param partial   A boolean indicating whether or not to partial search
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
	 * @param partial   A boolean indicating whether or not to partial search
	 */
	public void processQueries(String queryLine, boolean partial) {
		/*
		 * TODO Consider the query line "hello world". When does that query line
		 * generate the same results (and thus should be stored in the same map of
		 * results)? If we have two different inverted index instances (one built from
		 * text files, another built from web pages), do they return the same list of
		 * results? How about doing an exact versus partial search for that query line?
		 * 
		 * If it does not result in the same results, it should not be stored in the
		 * same query map. In that case, instead of making those values parameters of
		 * this method, it should be parameters sent to the constructor of this class
		 * and stored as final members.
		 */

		List<String> stemmedWords = FileStemmer.listStems(queryLine);
		List<String> query = new ArrayList<>(new HashSet<>(stemmedWords)); // TODO Can you think of better methods to
																			// reuse here in FileStemmer rather than
																			// converting your data from one type (a
																			// list) to another (a set) inefficiently
																			// multiple times?
		if (query.isEmpty()) {
			return;
		}
		Collections.sort(query); // TODO If you needed a sorted set, what is another way to do that?
		List<InvertedIndex.SearchResult> searchResults = null;
		if (partial) {
			/*
			 * TODO This is a common if statement, make a convenience method so it is
			 * reusable. This is similar to putIfAbsent or getOrDefault methods in maps.
			 * This means adding something like the method below to the inverted index
			 * class, and then calling that method here.
			 */

			/*-
			public (list of search results) search(Set<String> queries, boolean partial) {
				return partial ? (results from partial search) : (or results from exact search);
			}
			*/

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

	/*
	 * TODO You have some useful data stored in this class that we might want access
	 * to, but can only get if we write to a file. What are some other useful
	 * methods you might add to this class?
	 */

}
