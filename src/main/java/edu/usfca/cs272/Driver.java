package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
		Path queryFilePath = null;
		String resultsOutputPath = null;

		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex indexer = new InvertedIndex();
		FileBuilder fileBuilder = new FileBuilder(indexer);

		if (parser.hasFlag("-text")) {
			inputPath = parser.getPath("-text");
			try {
				fileBuilder.buildStructures(inputPath);
			} catch (IOException e) {
				System.out.println("Error building the structures " + inputPath);
			}
		}

		if (parser.hasFlag("-counts")) {
			countsPath = parser.getString("-counts", "counts.json");
			try {
				indexer.writeCounts(inputPath, countsPath);
			} catch (Exception e) {
				System.out.println("Error building the file word counts " + inputPath);
			}
		}

		if (parser.hasFlag("-index")) {
			indexPath = parser.getString("-index", "index.json");
			try {
				indexer.writeIndex(inputPath, indexPath, indexer);
			} catch (Exception e) {
				System.out.println("Error building the inverted index " + inputPath);
			}
		}

		List<List<String>> processedQueries = new ArrayList<>();

		if (parser.hasFlag("-query")) {
		    queryFilePath = parser.getPath("-query");
		    if (Files.exists(queryFilePath)) {
		        try {
		            List<String> queryLines = Files.readAllLines(queryFilePath);

		            for (String queryLine : queryLines) {
		                List<String> stemmedWords = FileStemmer.listStems(queryLine);
		                processedQueries.add(stemmedWords);
		            }
		        } catch (Exception e) {
		            System.out.println("Error reading the query file " + queryFilePath);
		        }
		    } else {
		        System.out.println("Query file does not exist: " + queryFilePath);
		    }
		}

		if (parser.hasFlag("-results")) {
		    resultsOutputPath = parser.getString("-results", "results.json");
		    try {
		        fileBuilder.processDirectory(inputPath, true);
		        List<List<Map<String, Object>>> searchResults = fileBuilder.conductSearch(processedQueries, indexer);
		        
		        for (List<Map<String, Object>> queryResult : searchResults) {
		            for (Map<String, Object> result : queryResult) {
		                System.out.println("Location: " + result.get("where"));
		                System.out.println("Count: " + result.get("count"));
		                System.out.println("Score: " + result.get("score"));
		                System.out.println();
		            }
		        }
		    } catch (Exception e) {
		        System.out.println("Error writing results to file: " + resultsOutputPath);
		    }
		}
		
	}
}