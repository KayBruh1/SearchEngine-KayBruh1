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
		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex indexer;
		FileBuilder builder;
		QueryFileProcessorInterface processor;

		if (parser.hasFlag("-threads")) {
			int numThreads = 5;
			try {
				numThreads = Integer.parseInt(parser.getString("-threads"));
			} catch (Exception e) {
				System.out.println("Invalid number of threads Using default value.");
			}
			if (numThreads < 1) {
				System.out.println("Invalid number of threads. Using default value.");
				numThreads = 5;
			}

			CustomWorkQueue workQueue = new CustomWorkQueue(numThreads);
			indexer = new ThreadSafeInvertedIndex();
			builder = new ThreadedFileBuilder((ThreadSafeInvertedIndex) indexer, workQueue);
			processor = new ThreadedQueryFileProcessor((ThreadSafeInvertedIndex) indexer, workQueue,
					parser.hasFlag("-partial"));
		} else {
			indexer = new InvertedIndex();
			builder = new FileBuilder(indexer);
			processor = new QueryFileProcessor(indexer, parser.hasFlag("-partial"));
		}

		if (parser.hasFlag("-text")) {
			Path inputPath = parser.getPath("-text");
			try {
				builder.buildStructures(inputPath);
			} catch (Exception e) {
				System.out.println("Error building the structures " + inputPath);
			}
		}

		if (parser.hasFlag("-query")) {
			Path queryPath = parser.getPath("-query");
			try {
				processor.processQueries(queryPath);
			} catch (Exception e) {
				System.out.println("Error reading the query file " + queryPath);
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

		if (parser.hasFlag("-results")) {
			Path resultsPath = parser.getPath("-results", Path.of("results.json"));
			try {
				processor.writeResults(resultsPath);
			} catch (Exception e) {
				System.out.println("Error writing results to file " + resultsPath);
			}
		}
	}
}