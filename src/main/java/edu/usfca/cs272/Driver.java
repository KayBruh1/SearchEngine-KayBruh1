package edu.usfca.cs272;

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
	/**
	 * Main method
	 *
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		Path inputPath = null;
		boolean dir = false;
		String countsPath = null;
		String indexPath = null;

		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex indexer = new InvertedIndex();

		if (parser.hasFlag("-text")) {
			inputPath = parser.getPath("-text");

			// TODO Just build in here (no output)

			if (inputPath != null && Files.isDirectory(inputPath)) {
				dir = true;
			}
		}

		if (parser.hasFlag("-counts")) {
			countsPath = parser.getString("-counts", ("counts.json"));
			try {

				// TODO Just output here
				// TODO indexer.writeCounts(countsPath)
				FileBuilder fileBuilder = new FileBuilder(indexer);
				if (dir) {
					fileBuilder.processCountsDirectory(inputPath, countsPath, dir);
				} else {
					fileBuilder.processFile(inputPath, countsPath, indexPath);
					//fileBuilder.processFileCounts(inputPath, countsPath.toString());
				}
			}
			catch (Exception e) {
				System.out.println("Error building the word counts " + e);
			}
		}

		if (parser.hasFlag("-index")) {
			indexPath = parser.getString("-index", ("index.json"));
			try {
				FileBuilder fileBuilder = new FileBuilder(indexer);
				if (dir) {
					fileBuilder.processIndexDirectory(inputPath, indexPath.toString(), dir);
				} else {
					fileBuilder.processFile(inputPath, countsPath, indexPath);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
