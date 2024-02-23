package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class FileBuilder {
	static InvertedIndex indexer = new InvertedIndex();
	/**
	 * Recursively processes the directory to build and write the inverted index.
	 *
	 * @param directory The directory to process
	 * @throws IOException If an I/O error occurs
	 */
	public static void processIndexDirectory(Path directory, String indexPath, boolean dir) throws IOException {
		if (!dir) {
			processFileIndex(directory, indexPath);
		}
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					processIndexDirectory(path, indexPath, dir);
				} else {
					// @CITE StackOverflow
					String relativePath = directory.resolve(path.getFileName()).toString();

					if (relativePath.toLowerCase().endsWith(".txt") || relativePath.toLowerCase().endsWith(".text")) {
						HashMap<String, Integer> wordCounts = processDirIndex(path);

						// @CITE StackOverflow
						int totalWords = wordCounts.values().stream().mapToInt(Integer::intValue).sum();
						if (totalWords > 0) {
							indexer.fileWordCounts.put(relativePath, totalWords);
						}
					}
				}
			}
		}

		indexer.writeInvertedIndex(indexPath, indexer.invertedIndex);
	}

	/**
	 * Processes file to generate word counts and build the inverted index
	 *
	 * @param filePath The path of file to be processed
	 * @return A HashMap containing word counts for the file
	 * @throws IOException If an I/O error occurs
	 */
	public static HashMap<String, Integer> processDirIndex(Path filePath) throws IOException {
		List<String> lines = Files.readAllLines(filePath);
		HashMap<String, Integer> wordCounts = new HashMap<>();
		int position = 0;

		for (String line : lines) {

			List<String> wordStems = FileStemmer.listStems(line);

			for (String stemmedWord : wordStems) {
				position += 1;
				if (wordCounts.containsKey(stemmedWord)) {
					wordCounts.put(stemmedWord, wordCounts.get(stemmedWord) + 1);
				} else {
					wordCounts.put(stemmedWord, 1);
				}

				if (!indexer.invertedIndex.containsKey(stemmedWord)) {
					indexer.invertedIndex.put(stemmedWord, new TreeMap<>());
				}

				TreeMap<String, List<Integer>> fileMap = indexer.invertedIndex.get(stemmedWord);

				if (!fileMap.containsKey(filePath.toString())) {
					fileMap.put(filePath.toString(), new ArrayList<>());

				}

				List<Integer> wordPosition = fileMap.get(filePath.toString());
				wordPosition.add(position);

			}
		}
		return wordCounts;
	}

	/**
	 * Processes file to generate word counts and build and write the inverted index

	 * @param filePath The path of the file to be processed
	 * @param counts   A boolean indicating to generate word counts or not
	 * @throws IOException If an I/O error occurs
	 */
	public static void processFileIndex(Path filePath, String indexPath) throws IOException {
		List<String> lines = Files.readAllLines(filePath);
		HashMap<String, Integer> wordCounts = new HashMap<>();
		int position = 0;

		for (String line : lines) {

			List<String> wordStems = FileStemmer.listStems(line);

			for (String stemmedWord : wordStems) {
				position += 1;
				if (wordCounts.containsKey(stemmedWord)) {
					wordCounts.put(stemmedWord, wordCounts.get(stemmedWord) + 1);
				} else {
					wordCounts.put(stemmedWord, 1);
				}

				if (!indexer.invertedIndex.containsKey(stemmedWord)) {
					indexer.invertedIndex.put(stemmedWord, new TreeMap<>());
				}

				TreeMap<String, List<Integer>> fileMap = indexer.invertedIndex.get(stemmedWord);

				if (!fileMap.containsKey(filePath.toString())) {
					fileMap.put(filePath.toString(), new ArrayList<>());

				}

				List<Integer> wordPosition = fileMap.get(filePath.toString());
				wordPosition.add(position);

			}
		}
		indexer.writeInvertedIndex(indexPath, indexer.invertedIndex);
	}

	/**
	 * Recursively processes directory to generate word counts for files
	 *
	 * @param directory The directory to process
	 * @throws IOException If an I/O error occurs
	 */
	public static void processCountsDirectory(Path directory, String countsPath, boolean dir) throws IOException {
		if (!dir) {
			processFileCounts(directory, countsPath);
		}
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					processCountsDirectory(path, countsPath, dir);
				} else {
					// @CITE StackOverflow
					String relativePath = directory.resolve(path.getFileName()).toString();

					if (relativePath.toLowerCase().endsWith(".txt") || relativePath.toLowerCase().endsWith(".text")) {
						HashMap<String, Integer> wordCounts = processDirCounts(path);

						// @CITE StackOverflow
						int totalWords = wordCounts.values().stream().mapToInt(Integer::intValue).sum();
						if (totalWords > 0) {
							indexer.fileWordCounts.put(relativePath, totalWords);
						}
					}
				}
			}
		}
		JsonWriter.writeObject(indexer.fileWordCounts, Path.of(countsPath));
	}

	/**
	 * Processes file to generate word counts

	 * @param filePath The path of the file to be processed
	 * @return A HashMap containing the word counts for the file
	 * @throws IOException If an I/O error occurs
	 */
	public static HashMap<String, Integer> processDirCounts(Path filePath) throws IOException {
		List<String> lines = Files.readAllLines(filePath);
		HashMap<String, Integer> wordCounts = new HashMap<>();

		for (String line : lines) {
			List<String> wordStems = FileStemmer.listStems(line);

			for (String stemmedWord : wordStems) {
				if (wordCounts.containsKey(stemmedWord)) {
					int currentCount = wordCounts.get(stemmedWord);
					wordCounts.put(stemmedWord, currentCount + 1);
				} else {
					wordCounts.put(stemmedWord, 1);
				}
			}
		}
		return wordCounts;
	}

	/**
	 * Processes the file to generate and write word count
	 *
	 * @param filePath  The path of the file to be processed
	 * @param counts    A boolean indicating to output word counts or not
	 * @throws IOException If an I/O error occurs
	 */
	public static void processFileCounts(Path inputPath, String countsPath) throws IOException {
		List<String> lines = Files.readAllLines(inputPath);
		HashMap<String, Integer> wordCounts = new HashMap<>();

		for (String line : lines) {
			List<String> wordStems = FileStemmer.listStems(line);

			for (String stemmedWord : wordStems) {
				if (wordCounts.containsKey(stemmedWord)) {
					wordCounts.put(stemmedWord, wordCounts.get(stemmedWord) + 1);
				} else {
					wordCounts.put(stemmedWord, 1);
				}
			}
		}
		indexer.outputWordCounts(wordCounts, inputPath.toString(), countsPath);
	}
}
