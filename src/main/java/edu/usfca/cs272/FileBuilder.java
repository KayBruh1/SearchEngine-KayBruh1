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
	private InvertedIndex indexer; // TODO Missing keyword---either static or final... which one?

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
		if (inputPath != null && Files.isDirectory(inputPath)) { // TODO Remove null check, an exception SHOULD happen
																	// if values are null
			processDirectory(inputPath, false);
		} else {
			processFile(inputPath);
		}
	}

	/*
	 * TODO There is still a bit more complexity in here than recommended last time:
	 * https://github.com/usf-cs272-spring2024/project-KayBruh1/blob/
	 * 497875dba651eba029bc70ec23a5d7d3882cf766/src/main/java/edu/usfca/cs272/
	 * FileBuilder.java#L21-L37
	 * 
	 * There should NOT be separate steps for processing the index or the counts.
	 * Both should always be done. So processIndexFiles, processCountFiles, and
	 * processFile all need to be combined into 1 method. And it needs to work with
	 * the indexer now, not creating its own data structures. Something like:
	 * 
	 * var location = file.toString(); var stems = FileStemmer.listStems(file);
	 * indexer.addCount(location, stems.size());
	 * 
	 * for (...) { ... indexer.addWord(stem, location, ...) }
	 */

	/**
	 * Processes the files in the specified directory to generate word counts and
	 * the inverted index
	 *
	 * @param directory The directory to process
	 * @param both      A boolean indicating whether to build both structures
	 * @throws IOException If an I/O error occurs
	 */
	public void processDirectory(Path directory, boolean both) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			HashMap<String, Integer> wordCounts = null;
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					processDirectory(path, both);
				} else {
					String relativePath = directory.resolve(path.getFileName()).toString();
					if (relativePath.toLowerCase().endsWith(".txt") || relativePath.toLowerCase().endsWith(".text")) {
						processFile(Path.of(relativePath));
						int totalWords = wordCounts.values().stream().mapToInt(Integer::intValue).sum();
						if (totalWords > 0) {
							indexer.getFileWordCounts().put(relativePath, totalWords);
						}
					}
				}
			}
		}
	}

	/**
	 * Processes file to generate word counts and build the inverted index
	 *
	 * @param location The path of file to be processed
	 * @return A HashMap containing word counts for the file
	 * @throws IOException If an I/O error occurs
	 */
	public HashMap<String, Integer> processIndexFiles(Path location) throws IOException {
		TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex = this.indexer.getInvertedIndex();

		List<String> lines = Files.readAllLines(location);
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
				if (!fileMap.containsKey(location.toString())) {
					fileMap.put(location.toString(), new TreeSet<>());
				}

				TreeSet<Integer> positions = fileMap.get(location.toString());
				positions.add(position);
			}
		}
		return wordCounts;
	}

	/**
	 * Processes file to generate word counts
	 * 
	 * @param location The path of the file to be processed
	 * @return A HashMap containing the word counts for the file
	 * @throws IOException If an I/O error occurs
	 */
	public HashMap<String, Integer> processCountsFiles(Path location) throws IOException {
		List<String> lines = Files.readAllLines(location);
		HashMap<String, Integer> wordCounts = new HashMap<>();

		for (String line : lines) {
			List<String> wordStems = FileStemmer.listStems(line);

			for (String stemmedWord : wordStems) {
				if (wordCounts.containsKey(stemmedWord)) {
					int count = wordCounts.get(stemmedWord);
					wordCounts.put(stemmedWord, count + 1);
				} else {
					wordCounts.put(stemmedWord, 1);
				}
			}
		}
		return wordCounts;
	}

	/**
	 * Processes the specified file to generate word counts and an inverted index
	 *
	 * @param location The path of the file to process
	 * @throws IOException If an I/O error occurs
	 */
	public void processFile(Path location) throws IOException {
		if (location != null) {
			List<String> lines = Files.readAllLines(location);

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
					if (!fileMap.containsKey(location.toString())) {
						fileMap.put(location.toString(), new TreeSet<>());
					}

					TreeSet<Integer> wordPosition = fileMap.get(location.toString());
					wordPosition.add(position);
				}
			}

			InvertedIndex indexer = getIndexer();
			indexer.setFileWordCounts(new TreeMap<>(wordCounts));
			indexer.setInvertedIndex(new TreeMap<>(invertedIndexMap));
		}
	}
}
