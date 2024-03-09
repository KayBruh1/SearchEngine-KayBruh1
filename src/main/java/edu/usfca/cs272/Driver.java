package edu.usfca.cs272;

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
		Path inputPath = null; // TODO Declare these where they are defined (inside the if statements)
		String countsPath = null;
		String indexPath = null;

		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex indexer = new InvertedIndex();
		FileBuilder fileBuilder = new FileBuilder(indexer); // TODO Can declare this in if (-text) block too since not sued in multiple blocks

		if (parser.hasFlag("-text")) {
			inputPath = parser.getPath("-text");
			try {
				fileBuilder.buildStructures(inputPath);
			} catch (Exception e) {
				System.out.println("Error building the structures " + inputPath);
			}
		}

		if (parser.hasFlag("-counts")) {
			countsPath = parser.getString("-counts", ("counts.json"));
			try {
				indexer.writeCounts(countsPath);
			} catch (Exception e) {
				System.out.println("Error building the file word counts " + inputPath);
			}
		}

		if (parser.hasFlag("-index")) {
			indexPath = parser.getString("-index", ("index.json"));
			try {
				indexer.writeIndex(indexPath);
			} catch (Exception e) {
				System.out.println("Error building the inverted index " + inputPath);
			}
		}
		
		/*
		 * TODO The style of declaring all the variables at the start of code works when
		 * code is small, but starts to be difficult to manage when code gets larger and
		 * more complex. Try to declare the variables in the smallest scope possible and
		 * maintain functionality. For example, for the paths, declare the variable
		 * inside the if blocks where the path value is defined and used. The inverted
		 * index data structure and argument parser are both used in multiple blocks, so
		 * keep those declared at the start.
		 */

	}
}
