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
	static InvertedIndex indexer = new InvertedIndex();
	
	/** Path to input text files */
	static Path inputPath;
	
	
	static boolean dir = false;

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

		indexer.fileWordCounts.clear();
		indexer.invertedIndex.clear();
		ArgumentParser parser = new ArgumentParser(args);
		FileBuilder builder = new FileBuilder();

		if (parser.hasFlag("-text")) {
			inputPath = parser.getPath("-text");
			if (inputPath != null && Files.isDirectory(inputPath)) {
				dir = true;
			}
		}

		if (parser.hasFlag("-counts")) {
			Path countsPath = parser.getPath("-counts", Path.of("counts.json"));
			try {
				builder.processCountsDirectory(inputPath, countsPath.toString(), dir);
			}
			catch (Exception e) {
				indexer.fileWordCounts.put("No input provided", 0);
				JsonWriter.writeObject(indexer.fileWordCounts, Path.of("counts.json"));
				System.out.println("Error building the file counts");
			}
		}
		
		if (parser.hasFlag("-index")) {
			Path indexPath = parser.getPath("-index", Path.of("index.json"));
			try {
				builder.processIndexDirectory(inputPath, indexPath.toString(), dir);
			}
			catch (Exception e) {
				indexer.fileWordCounts.put("No input provided", 0);
				JsonWriter.writeObject(indexer.fileWordCounts, Path.of("index.json"));	
				System.out.println("Error building the inverted index");
			}
		}
	}

}
