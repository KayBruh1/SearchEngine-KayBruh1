package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class for building and processing files/directories to generate word counts
 * and an inverted index to write to JSON file
 */
public class FileBuilder {
	/**
	 * The InvertedIndex class used for storing word counts and the inverted index
	 */
	private final InvertedIndex indexer;

	/**
	 * Creates a new FileBuilder object with the InvertedIndex
	 *
	 * @param indexer the InvertedIndex object
	 */
	public FileBuilder(InvertedIndex indexer) {
		this.indexer = indexer;
	}

	/**
	 * Returns the InvertedIndex
	 *
	 * @return the InvertedIndex object
	 */
	public InvertedIndex getIndexer() {
		return indexer;
	}

	/**
	 * Builds word count and inverted index structures for the specified input path.
	 *
	 * @param inputPath The path of the file or directory to be processed
	 * @throws IOException If an I/O error occurs
	 */
	public void buildStructures(Path inputPath) throws IOException {
		if (Files.isDirectory(inputPath)) {
			processDirectory(inputPath);
		} else {
			processFile(inputPath);
		}
	}

	/**
	 * Processes the files in the specified directory to generate word counts and
	 * the inverted index
	 *
	 * @param directory The directory to process
	 * @throws IOException If an I/O error occurs
	 */
	public void processDirectory(Path directory) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					processDirectory(path);
				} else {
					if (isTextFile(path)) {
						processFile(path);
					}
				}
			}
		}
	}

	/**
	 * Processes the specified file to generate word counts and an inverted index
	 *
	 * @param location The path of the file to process
	 * @throws IOException If an I/O error occurs
	 */
	public void processFile(Path location) throws IOException {
		int position = 0;
		String locationString = location.toString();
		try (BufferedReader reader = Files.newBufferedReader(location)) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] words = FileStemmer.parse(line);
				for (String word : words) {
					List<String> stems = FileStemmer.listStems(word);
					for (String stemmedWord : stems) {
						position += 1;
						indexer.addWord(stemmedWord, locationString, position);
					}
				}
			}
		}
		indexer.addWordCount(locationString, position);
	}

	/**
	 * Determines if given a valid file
	 * 
	 * @param file The file to be checked
	 * @return True for a valid file, false otherwise
	 */
	public static boolean isTextFile(Path file) {
		String fileName = file.getFileName().toString().toLowerCase();
		return Files.isRegularFile(file) && (fileName.endsWith(".txt") || fileName.endsWith(".text"));
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
	 * Performs an exact search based on the processed queries
	 *
	 * @param processedQueries A list of processed search queries
	 * @return A map containing search results for each query
	 */
	public Map<String, List<SearchResult>> exactSearch(List<List<String>> processedQueries) {
		Map<String, List<SearchResult>> searchResultsMap = new HashMap<>();

		for (List<String> query : processedQueries) {
			if (query.isEmpty()) {
				continue;
			}

			String queryWord = String.join(" ", query);
			Map<String, SearchResult> resultMap = new HashMap<>();

			for (String word : query) {
				Map<String, TreeSet<Integer>> locations = indexer.getInvertedIndex().getOrDefault(word,
						new TreeMap<>());

				for (Map.Entry<String, TreeSet<Integer>> entry : locations.entrySet()) {
					String location = entry.getKey();
					TreeSet<Integer> positions = entry.getValue();
					int totalWords = indexer.getTotalWordCount(location);
					int count = countPositions(positions);
					SearchResult result = resultMap.getOrDefault(location,
							new SearchResult(location, totalWords, 0, 0.00000000));
					result.updateCount(count);
					result.setScore(calculateScore(result.getCount(), totalWords));
					resultMap.put(location, result);
				}
			}

			List<SearchResult> searchResults = new ArrayList<>(resultMap.values());
			searchResultsMap.put(queryWord, searchResults);
		}

		searchResultsMap = SearchResult.sortResults(searchResultsMap);

		return searchResultsMap;
	}

	/**
	 * Performs a partial based on the processed queries
	 *
	 * @param processedQueries A list of processed search queries
	 * @return A map containing search results for each query
	 */
	public Map<String, List<SearchResult>> partialSearch(List<List<String>> processedQueries) {
		Map<String, List<SearchResult>> searchResultsMap = new HashMap<>();

		for (List<String> query : processedQueries) {
			if (query.isEmpty()) {
				continue;
			}

			String queryWord = String.join(" ", query);
			Map<String, SearchResult> resultMap = new HashMap<>();

			for (String word : query) {
				for (Map.Entry<String, TreeMap<String, TreeSet<Integer>>> entry : indexer.getInvertedIndex()
						.entrySet()) {
					String checkWord = entry.getKey();
					if (checkWord.startsWith(word)) {
						TreeMap<String, TreeSet<Integer>> locations = entry.getValue();

						for (Map.Entry<String, TreeSet<Integer>> locationEntry : locations.entrySet()) {
							String location = locationEntry.getKey();
							TreeSet<Integer> positions = locationEntry.getValue();
							int totalWords = indexer.getTotalWordCount(location);
							int count = countPositions(positions);
							SearchResult result = resultMap.getOrDefault(location,
									new SearchResult(location, totalWords, 0, 0.00000000));
							result.updateCount(count);
							result.setScore(calculateScore(result.getCount(), totalWords));
							resultMap.put(location, result);
						}
					}
				}
			}

			List<SearchResult> searchResults = new ArrayList<>(resultMap.values());
			searchResultsMap.put(queryWord, searchResults);
		}

		searchResultsMap = SearchResult.sortResults(searchResultsMap);

		return searchResultsMap;
	}

	/**
	 * Counts the number of word positions
	 *
	 * @param positions A set of word positions
	 * @return The number of positions
	 */
	public int countPositions(TreeSet<Integer> positions) {
		return positions.size();
	}

	/**
	 * Calculates the score for a search result
	 *
	 * @param matches    The number of matches
	 * @param totalWords The total number of words
	 * @return The calculated score
	 */
	public static double calculateScore(int matches, int totalWords) {
		double score = (double) matches / totalWords;
		return score;
	}
}