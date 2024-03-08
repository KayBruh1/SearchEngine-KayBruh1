package edu.usfca.cs272;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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
		String countsPath = null;
		String indexPath = null;
		Path queryPath = null;
		String resultsPath = null;

		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex indexer = new InvertedIndex();
		FileBuilder fileBuilder = new FileBuilder(indexer);

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

		Map<String, List<Map<String, Object>>> searchResults = null;
		if (parser.hasFlag("-query")) {
			queryPath = parser.getPath("-query");
			if (Files.exists(queryPath)) {
				try {
					List<List<String>> processedQueries = fileBuilder.processQuery(queryPath);
					searchResults = fileBuilder.conductSearch(processedQueries);

				} catch (Exception e) {
					System.out.println("Error reading the query file " + queryPath);
				}
			}
		}

		if (parser.hasFlag("-results")) {
			resultsPath = parser.getString("-results", "results.json");
			try {

			} catch (Exception e) {
				System.out.println("Error writing results to file " + resultsPath);
			}
		}

	}
}
