package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
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
		if (inputPath != null && Files.isDirectory(inputPath)) { // TODO Remove null check, an exception SHOULD happen
																	// if values are null
			processDirectory(inputPath);
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
	public void processDirectory(Path directory) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					processDirectory(path);
				} else {
					processFile(path);
				}
			}
		}
	}

	public void processFile(Path location) throws IOException {
		String fileName = location.getFileName().toString();
		if ((fileName.toLowerCase().endsWith(".txt") || fileName.toLowerCase().endsWith(".text"))
				&& Files.size(location) > 0) {
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
}