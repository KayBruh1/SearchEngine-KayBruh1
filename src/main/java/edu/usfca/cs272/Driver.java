package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
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
			try {
				processDirectory(Path.of(inputPath), outputPath);
			} catch (IOException e) {
				System.err.println("Error processing directory: " + e.getMessage());
			}
		} else {
			System.out.println("No input text files provided.");
		}

		long elapsed = Duration.between(start, Instant.now()).toMillis();
		double seconds = (double) elapsed / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	public static void processDirectory(Path directory, String outputPath) throws IOException {
		Map<String, Integer> wordCounts = new HashMap<>();
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					processDirectory(path, outputPath);
				} else {
					if (isTextFile(path)) {
						processTextFile(path, wordCounts);
					}
				}
			}
		}
	}

	public static boolean isTextFile(Path file) {
		String fileName = file.getFileName().toString().toLowerCase();
		return fileName.endsWith(".txt") || fileName.endsWith(".text");
	}

}