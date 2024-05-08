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
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex indexer;
		FileBuilder builder;
		QueryFileProcessorInterface processor;
		CustomWorkQueue workQueue = null;
		WebCrawler crawler = null;
		SearchEngine engine = null;
		boolean threaded = false;

		if (parser.hasFlag("-threads") || parser.hasFlag("-html") || parser.hasFlag("-server")) {
			threaded = true;
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

			workQueue = new CustomWorkQueue(numThreads);
			ThreadSafeInvertedIndex threadSafe = new ThreadSafeInvertedIndex();
			builder = new ThreadedFileBuilder(threadSafe, workQueue);
			processor = new ThreadedQueryFileProcessor(threadSafe, workQueue, parser.hasFlag("-partial"));
			crawler = new WebCrawler(threadSafe, workQueue);
			engine = new SearchEngine(threadSafe);
			indexer = threadSafe;
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

		if (parser.hasFlag("-html")) {
			String seed = parser.getString("-html");
			int total = 1;
			try {
				total = Integer.parseInt(parser.getString("-crawl"));
			} catch (Exception e) {
				System.out.println("Invalid total. Using default value.");
			}
			if (total < 1) {
				System.out.println("Invalid total. Using default value.");
				total = 1;
			}
			try {
				crawler.startCrawl(seed, total);
			} catch (Exception e) {
				System.out.println("Error crawling the html content " + seed);
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
		
		if (parser.hasFlag("-server")) {
			int port = 8080;
			try {
				port = Integer.parseInt(parser.getString("-server"));
			} catch (Exception e) {
				System.out.println("Invalid port. Using default value.");
			}
			if (port < 0) {
				System.out.println("Invalid port. Using default value.");
				port = 8080;
			}
			engine.startEngine(port);
		}

		if (threaded) {
			workQueue.shutdown();
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

		if (parser.hasFlag("-html")) {
			Path htmlPath = parser.getPath("-html", Path.of("html.json"));
			try {
				processor.writeResults(htmlPath);
			} catch (Exception e) {
				System.out.println("Error writing html results to file " + htmlPath);
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