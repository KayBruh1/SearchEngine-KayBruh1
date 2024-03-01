package edu.usfca.cs272;

import java.io.IOException;
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
	/**
	 * Main method
	 *
	 * @param args Command line arguments
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		Path inputPath = null;
		boolean dir = false;
		String countsPath = null;
		String indexPath = null;

		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex indexer = new InvertedIndex();
		FileBuilder fileBuilder = new FileBuilder(indexer);

		if (parser.hasFlag("-text")) {
			inputPath = parser.getPath("-text");
			try {
				fileBuilder.buildStructures(inputPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (parser.hasFlag("-counts")) {
			countsPath = parser.getString("-counts", ("counts.json"));
			try {
				indexer.writeCounts(inputPath, countsPath);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (parser.hasFlag("-index")) {
			indexPath = parser.getString("-index", ("index.json"));
			try {
				indexer.writeInvertedIndex(indexPath, indexer.getInvertedIndex());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
