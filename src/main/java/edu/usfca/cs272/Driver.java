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

	/** Instance of the InvertedIndex class */
	static InvertedIndex indexer = new InvertedIndex();

	/** Path to input text files */
	static Path inputPath;

	/** Boolean flag indicating whether the input is a directory or not*/
	static boolean dir = false;

	/**
	 * Main method
	 *
	 * @param args Command line arguments
	 * @throws IOException If an I/O error occurs
	 */
	public static void main(String[] args) throws IOException {
		InvertedIndex.fileWordCounts.clear();
		InvertedIndex.invertedIndex.clear();
		inputPath = null;
		ArgumentParser parser = new ArgumentParser(args);

		if (parser.hasFlag("-text")) {
			inputPath = parser.getPath("-text");
			if (inputPath != null && Files.isDirectory(inputPath)) {
				dir = true;
			}
		}

		if (parser.hasFlag("-counts")) {
			System.out.println(inputPath);
			Path countsPath = parser.getPath("-counts", Path.of("counts.json"));
			try {
				FileBuilder.processCountsDirectory(inputPath, countsPath.toString(), dir);
			}
			catch (Exception e) {
				InvertedIndex.writeEmpty(countsPath, null);
				e.printStackTrace();
			}
		}

		if (parser.hasFlag("-index")) {
			Path indexPath = parser.getPath("-index", Path.of("index.json"));
			try {
				FileBuilder.processIndexDirectory(inputPath, indexPath.toString(), dir);
			}
			catch (Exception e) {
				InvertedIndex.writeEmpty(null, indexPath);	
				System.out.println("here ");
			}
		}
	}

}