package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

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
	 * SnowballStemmer instance for stemming
	 */
	private final SnowballStemmer stemmer;

	/**
	 * Creates a new FileBuilder object with the InvertedIndex
	 *
	 * @param indexer the InvertedIndex object
	 */
	public FileBuilder(InvertedIndex indexer) {
		this.indexer = indexer;
		this.stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
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
					String stemmedWord = stemmer.stem(word).toString();
					position += 1;
					indexer.addWord(stemmedWord, locationString, position);
				}
			}
		}
	}
	
	/*
	 * TODO It can be helpful to have a static version of processFile when directory
	 * traversing is not needed. I suggest this minor tweak to the design:
	 * 
	 * 1) Change your current processFile declaration as follows (make no changes to
	 * the method implementation):
	 * 
	 * public static void processFile(Path location, InvertedIndex indexer) throws IOException
	 * 
	 * 2) Make a NEW version of processFile as follows:
	 * 
	 * public void processFile(Path location) throws IOException {
	 *     processFile(location, this.indexer);
	 * }
	 * 
	 * This gives you the benefits of an instance-based approach with the quick
	 * reusability of a static approach.
	 */

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
}