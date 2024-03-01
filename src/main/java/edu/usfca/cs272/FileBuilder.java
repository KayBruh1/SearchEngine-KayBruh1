package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
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
	private InvertedIndex indexer;

	public FileBuilder(InvertedIndex indexer) {
		this.indexer = indexer;
	}

	// Other methods of the FileBuilder class

	public InvertedIndex getIndexer() {
		return indexer;
	}

	public void buildStructures(Path inputPath) throws IOException {
		if (inputPath != null && Files.isDirectory(inputPath)) {
			processDirectory(inputPath);
		} else {
			processFile(inputPath);
		}
	}

	/**
	 * Recursively processes the directory to build and write the inverted index.
	 *
	 * @param directory The directory to process
	 * @param indexPath The output path for the inverted index JSON file
	 * @param dir       A boolean indicating whether the given path is a directory
	 *                  or not
	 * @throws IOException If an I/O error occurs
	 */
	public void processIndexDirectory(Path directory, String indexPath, boolean dir) throws IOException {
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
							this.indexer.getFileWordCounts().put(relativePath, totalWords);
						}
					}
				}
			}
		}
		this.indexer.writeInvertedIndex(indexPath, this.indexer.getInvertedIndex());
	}

	/**
	 * Processes file to generate word counts and build the inverted index
	 *
	 * @param filePath The path of file to be processed
	 * @return A HashMap containing word counts for the file
	 * @throws IOException If an I/O error occurs
	 */
	public HashMap<String, Integer> processDirIndex(Path filePath) throws IOException {
		TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex = this.indexer.getInvertedIndex();

		List<String> lines = Files.readAllLines(filePath);
		HashMap<String, Integer> wordCounts = new HashMap<>();
		int position = 0;

		for (String line : lines) {
			List<String> wordStems = FileStemmer.listStems(line);

			for (String stemmedWord : wordStems) {
				position += 1;
				wordCounts.put(stemmedWord, wordCounts.getOrDefault(stemmedWord, 0) + 1);

				if (!invertedIndex.containsKey(stemmedWord)) {
					invertedIndex.put(stemmedWord, new TreeMap<>());
				}

				TreeMap<String, TreeSet<Integer>> fileMap = invertedIndex.get(stemmedWord);
				if (!fileMap.containsKey(filePath.toString())) {
					fileMap.put(filePath.toString(), new TreeSet<>());
				}

				TreeSet<Integer> positions = fileMap.get(filePath.toString());
				positions.add(position);
			}
		}
		return wordCounts;
	}

	/**
	 * Recursively processes a directory to generate word counts for files
	 *
	 * @param directory  The directory to process
	 * @param countsPath The output path for the word counts JSON file
	 * @param dir        A boolean indicating whether the given path is a directory
	 *                   or not
	 * @throws IOException If an I/O error occurs
	 */
	public void processCountsDirectory(Path directory, String countsPath, boolean dir) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					processCountsDirectory(path, countsPath, dir);
				} else {
					String relativePath = directory.resolve(path.getFileName()).toString();

					if (relativePath.toLowerCase().endsWith(".txt") || relativePath.toLowerCase().endsWith(".text")) {
						HashMap<String, Integer> wordCounts = processDirCounts(path);

						int totalWords = wordCounts.values().stream().mapToInt(Integer::intValue).sum();
						if (totalWords > 0) {
							indexer.getFileWordCounts().put(relativePath, totalWords);
						}
					}
				}
			}
		}
		JsonWriter.writeObject(indexer.getFileWordCounts(), Path.of(countsPath));
	}

	/**
	 * Processes file to generate word counts
	 * 
	 * @param filePath The path of the file to be processed
	 * @return A HashMap containing the word counts for the file
	 * @throws IOException If an I/O error occurs
	 */
	public HashMap<String, Integer> processDirCounts(Path filePath) throws IOException {
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

	public void processDirectory(Path directory) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					processDirectory(path);
				} else {
					String relativePath = directory.resolve(path.getFileName()).toString();

					if (relativePath.toLowerCase().endsWith(".txt") || relativePath.toLowerCase().endsWith(".text")) {
						HashMap<String, Integer> wordCounts = processDirCounts(path);

						int totalWords = wordCounts.values().stream().mapToInt(Integer::intValue).sum();
						if (totalWords > 0) {
							indexer.getFileWordCounts().put(relativePath, totalWords);
						}
					}
				}
			}
		}
	}

	public void processFile(Path filePath) throws IOException {
		if (filePath != null) {
			List<String> lines = Files.readAllLines(filePath);

			HashMap<String, Integer> wordCounts = new HashMap<>();
			TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndexMap = new TreeMap<>();

			int position = 0;

			for (String line : lines) {
				List<String> wordStems = FileStemmer.listStems(line);

				for (String stemmedWord : wordStems) {
					position += 1;

					wordCounts.put(stemmedWord, wordCounts.getOrDefault(stemmedWord, 0) + 1);

					if (!invertedIndexMap.containsKey(stemmedWord)) {
						invertedIndexMap.put(stemmedWord, new TreeMap<>());
					}

					TreeMap<String, TreeSet<Integer>> fileMap = invertedIndexMap.get(stemmedWord);
					if (!fileMap.containsKey(filePath.toString())) {
						fileMap.put(filePath.toString(), new TreeSet<>());
					}

					TreeSet<Integer> wordPosition = fileMap.get(filePath.toString());
					wordPosition.add(position);
				}
			}

			InvertedIndex indexer = getIndexer();
			indexer.setFileWordCounts(new TreeMap<>(wordCounts));
			indexer.setInvertedIndex(new TreeMap<>(invertedIndexMap));

			System.out.println("Processing completed");
		} else {
			System.out.println("File path is null");
		}
	}

}
