package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Kayvan Zahiri
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */

public class Driver {
	/*
	 * TODO Move fileWordCounts and invertedIndex into an InvertedIndex data structure class
	 *
	 * Keep the members public and non-static for now
	 * Have at least an add method...
	 * addWordCount(String location, Integer count) --> fileWordCounts.put(locations, count);
	 * addWord(String word, String location (filePath), Integer position) --> add to the invertedIndex here
	 *
	 * ---
	 *
	 * Move the traversing and stemming etc. into a builder or parser class.
	 */
	
	static InvertedIndex indexer = new InvertedIndex();
	static FileBuilder parser = new FileBuilder();
	
	/** Path to input text files */
	static String inputPath;

	/** Path to write word counts JSON file */
	static String countsPath;

	/** Path to write inverted index JSON file */
	static String indexPath;

	/** Flag indicating to write word counts or not */
	static boolean counts = false;

	/** Flag indicating to write inverted index or not */
	static boolean index = false;

	/**
	 * Main method
	 *
	 * @param args Command line arguments
	 * @throws IOException If an I/O error occurs
	 */
	public static void main(String[] args) throws IOException {
		/* TODO
		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex index = new InvertedIndex();

		if (parser.hasFlag("-text")) {
			Path input = parser.getPath("-text");

			try {
				1 or 2 lines of code calling other classes
			}
			catch ( ... ) {
				System.out.println("Unable to build the inverted index from path: " + input.toString());
			}
		}

		if (parser.hasFlag("-counts")) {
			Path output = parser.getPath("-counts", Path.of("counts.json"));

			try {
				index.writeCounts(output);
			}
			catch ( ) {

			}
		}

		etc.
		 */

		try {
			counts = false;
			index = false;
			indexer.fileWordCounts.clear();
			indexer.invertedIndex.clear();

			ArgumentParser parser = new ArgumentParser(args);

			inputPath = parser.getString("-text");

			for (String arg : args) {
				if (arg.contains("-counts")) {
					countsPath = parser.getString("-counts", "counts.json");
					counts = true;
				}
				if (arg.contains("-index")) {
					indexPath = parser.getString("-index", "index.json");
					index = true;
				}
			}

			if (inputPath != null) {
				Path path = Path.of(inputPath);
				processPath(path);
			} else if (inputPath == null && counts == true && index == false) {
				indexer.fileWordCounts.put("No input provided", 0);
				JsonWriter.writeObject(indexer.fileWordCounts, Path.of(countsPath));
			} else if (inputPath == null && index == true && counts == false) {
				indexer.fileWordCounts.put("No input provided", 0);
				JsonWriter.writeObject(indexer.fileWordCounts, Path.of(indexPath));
			}
		} catch (Exception e) {
			System.err.println("Error" + e.getMessage());
		}
	}
	/**
	 * Processes the given path as file or directory
	 *
	 * @param path The path to process
	 * @throws IOException If an I/O error occurs
	 */
	private static void processPath(Path path) throws IOException {
		if (Files.isDirectory(path) && index && !counts) {
			parser.processIndexDirectory(path, indexPath);
		} else if (Files.isDirectory(path) && counts && !index) {
			parser.processCountsDirectory(path, countsPath);
		} else if (Files.isDirectory(path) && counts && index) {
			parser.processCountsDirectory(path, countsPath);
			parser.processIndexDirectory(path, indexPath);
		} else if (Files.exists(path) && index && !counts){
			parser.processFileIndex(path, counts, indexPath);
		} else if (Files.exists(path) && counts && !index){
			parser.processFileCounts(path, inputPath, countsPath, counts);
		}
	}
}