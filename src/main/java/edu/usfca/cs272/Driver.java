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
	
	// TODO Instead of static members, make these local variables inside of main

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
	public static void main(String[] args) throws IOException { // TODO Remove throws IOException
		InvertedIndex.fileWordCounts.clear();
		InvertedIndex.invertedIndex.clear();
		inputPath = null;
		dir = false;
		ArgumentParser parser = new ArgumentParser(args);
		// TODO InvertedIndex indexer = new InvertedIndex();

		if (parser.hasFlag("-text")) {
			inputPath = parser.getPath("-text");
			
			// TODO Just build in here (no output)
			
			if (inputPath != null && Files.isDirectory(inputPath)) {
				dir = true;
			}
		}

		if (parser.hasFlag("-counts")) {
			String countsPath = parser.getString("-counts", ("counts.json"));
			try {
				
				// TODO Just output here
				// TODO indexer.writeCounts(countsPath)
				
				if (dir) {
					FileBuilder.processCountsDirectory(inputPath, countsPath, dir);
				} else {
					FileBuilder.processFileCounts(inputPath, countsPath.toString());
				}
			}
			catch (Exception e) {
				System.out.println("Error building the word counts " + e);
			}
		}

		if (parser.hasFlag("-index")) {
			String indexPath = parser.getString("-index", ("index.json"));
			try {
				// TODO indexer.writeInvertedIndex(indexPath);
				if (dir) {
					FileBuilder.processIndexDirectory(inputPath, indexPath.toString(), dir);
				} else {
					FileBuilder.processFileIndex(inputPath, indexPath.toString());
				}
			}
			catch (Exception e) {
				System.out.println("Error building the inverted index " + e);
			}
		}
	}

}