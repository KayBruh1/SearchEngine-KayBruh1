package edu.usfca.cs272;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
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
		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex indexer = new InvertedIndex();
		FileBuilder fileBuilder = new FileBuilder(indexer);
		// TODO QueryFileProcessor ... = new QueryFileProcessor(indexer);

		if (parser.hasFlag("-text")) {
			Path inputPath = parser.getPath("-text");
			try {
				fileBuilder.buildStructures(inputPath);
			} catch (Exception e) {
				System.out.println("Error building the structures " + inputPath);
			}
		}

		if (parser.hasFlag("-counts")) {
			Path countsPath = parser.getPath("-counts", Path.of("counts.json"));
			try {
				indexer.writeCounts(countsPath);
			} catch (Exception e) {
				System.out.println("Error building the file word counts " + countsPath);
			}
		}

		if (parser.hasFlag("-index")) {
			Path indexPath = parser.getPath("-index", Path.of("index.json"));
			try {
				indexer.writeIndex(indexPath);
			} catch (Exception e) {
				System.out.println("Error building the inverted index " + indexPath);
			}
		}

		Map<String, List<InvertedIndex.SearchResult>> searchResultsMap = new HashMap<>();
		if (parser.hasFlag("-query")) {
			Path queryPath = parser.getPath("-query");
			try {
				if (Files.exists(queryPath)) {
					List<List<String>> processedQueries = QueryFileProcsesor.processQueries(queryPath);
					boolean partial = false;
					if (parser.hasFlag("-partial")) {
						partial = true;
					}
					InvertedIndex.conductSearch(processedQueries, searchResultsMap, indexer, partial);
					searchResultsMap = InvertedIndex.SearchResult.sortResults(searchResultsMap);
				}
			} catch (Exception e) {
				System.out.println("Error reading the query file " + queryPath);
			}
		}

		if (parser.hasFlag("-results")) {
			String resultsPath = parser.getString("-results", "results.json");
			try {
				// TODO processor.writeResults(...)
				indexer.writeResults(searchResultsMap, resultsPath);
			} catch (Exception e) {
				System.out.println("Error writing results to file " + resultsPath);
			}
		}
	}
}