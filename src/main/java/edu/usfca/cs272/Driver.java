package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

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
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) throws IOException {
		Instant start = Instant.now();

		ArgumentParser parser = new ArgumentParser(args);
		String inputPath = parser.getString("-text");
		String outputPath = parser.getString("-counts", "counts.json");

		if (inputPath != null) {
			Path path = Path.of(inputPath);
			processPath(path, inputPath, outputPath);
		} else {
			System.out.println("No input text files provided");
		}

		long elapsed = Duration.between(start, Instant.now()).toMillis();
		double seconds = (double) elapsed / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	private static void processPath(Path path, String inputPath, String outputPath) throws IOException {
		if (Files.isDirectory(path)) {
			processDirectory(path, inputPath, outputPath);
		} else {
			processFile(path, inputPath, outputPath);
		}
	}

	private static void processDirectory(Path directory, String inputPath, String outputPath) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path path : listing) {
				processPath(path, inputPath, outputPath);
			}
		}
	}

	private static void processFile(Path filePath, String inputPath, String outputPath) throws IOException {
		System.out.println("Processing file: " + filePath);

		List<String> lines = Files.readAllLines(filePath);
		HashMap<String, Integer> wordCounts = new HashMap<>();

		for (String line : lines) {
			List<String> wordStems = FileStemmer.listStems(line);

			for (String stemmedWord : wordStems) {
				if (wordCounts.containsKey(stemmedWord)) {
					wordCounts.put(stemmedWord, wordCounts.get(stemmedWord) + 1);
				} else {
					wordCounts.put(stemmedWord, 1);
				}
			}
		}

		outputWordCounts(wordCounts, inputPath, outputPath);
	}

	private static void outputWordCounts(HashMap<String, Integer> wordCounts, String inputPath, String outputPath) {
		HashMap<String, Integer> pathWordCount = new HashMap<>();
		int totalWords = 0;
		
		for (int count : wordCounts.values()) {
			totalWords += count;
		}
		System.out.println(totalWords);
		pathWordCount.put(inputPath, totalWords);

		try {
			JsonWriter.writeObject(pathWordCount, Path.of(outputPath));
			System.out.println("Word counts have been written to: " + outputPath);
		} catch (Exception e) {
			System.err.println("Error writing word counts to file: " + e.getMessage());
		}
	}

}