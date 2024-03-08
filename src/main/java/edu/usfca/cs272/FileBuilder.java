package edu.usfca.cs272;

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
		if (Files.size(location) > 0) {
			List<String> lines = Files.readAllLines(location);
			HashMap<String, Integer> wordCounts = new HashMap<>();
			TreeMap<String, TreeSet<Integer>> invertedIndex = new TreeMap<>();
			int position = 0;

			for (String line : lines) {
				List<String> wordStems = FileStemmer.listStems(line);
				for (String stemmedWord : wordStems) {
					position += 1;
					wordCounts.put(stemmedWord, wordCounts.getOrDefault(stemmedWord, 0) + 1);

					TreeSet<Integer> positions = invertedIndex.get(stemmedWord);
					if (positions == null) {
						positions = new TreeSet<>();
						invertedIndex.put(stemmedWord, positions);
					}
					positions.add(position);
				}
			}

			TreeMap<String, TreeMap<String, TreeSet<Integer>>> index = new TreeMap<>();
			for (Map.Entry<String, TreeSet<Integer>> entry : invertedIndex.entrySet()) {
				String word = entry.getKey();
				TreeSet<Integer> positions = entry.getValue();
				TreeMap<String, TreeSet<Integer>> fileMap = new TreeMap<>();
				fileMap.put(location.toString(), positions);
				index.put(word, fileMap);
			}

			indexer.addWordCounts(location.toString(), wordCounts);
			indexer.addInvertedIndex(location.toString(), index);
		}
	}

	/**
	 * Determines if given a valid file
	 * 
	 * @param file The file to be checked
	 * @return True for a valid file, false otherwise
	 */
	private static boolean isTextFile(Path file) {
		String fileName = file.getFileName().toString().toLowerCase();
		return Files.isRegularFile(file) && (fileName.endsWith(".txt") || fileName.endsWith(".text"));
	}

	public Map<String, List<Map<String, Object>>> conductSearch(List<List<String>> processedQueries)
			throws IOException {
		HashMap<String, List<Map<String, Object>>> searchResults = new HashMap<>();

		for (List<String> query : processedQueries) {
			String queryWord = String.join(" ", query);
			List<Map<String, Object>> results = new ArrayList<>();

			for (String word : query) {
				Map<String, TreeSet<Integer>> locations = indexer.getInvertedIndex().getOrDefault(word,
						new TreeMap<>());

				for (Map.Entry<String, TreeSet<Integer>> entry : locations.entrySet()) {
					String location = entry.getKey();
					int count = entry.getValue().size();
					double score = calculateScore(count);

					HashMap<String, Object> resultMap = new HashMap<>();
					resultMap.put("count", count);
					resultMap.put("score", score);
					resultMap.put("where", location);

					results.add(resultMap);
				}
			}
			searchResults.put(queryWord, results);
		}

		return searchResults;
	}


    public static List<List<String>> processQuery(Path queryPath) throws IOException {
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

	private static int calculateWordCount(InvertedIndex indexer) {
		int totalWordCount = 0;
		for (String word : indexer.getInvertedIndex().keySet()) {
			TreeMap<String, TreeSet<Integer>> wordMap = indexer.getInvertedIndex().get(word);
			for (String location : wordMap.keySet()) {
				totalWordCount += wordMap.get(location).size();
			}
		}
		return totalWordCount;
	}

	private static double calculateScore(int count) {
		return count;
	}
}
