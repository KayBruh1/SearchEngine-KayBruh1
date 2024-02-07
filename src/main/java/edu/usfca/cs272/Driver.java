package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

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
			processPath(path, outputPath);
		} else {
			System.out.println("No input text files provided.");
		}

		long elapsed = Duration.between(start, Instant.now()).toMillis();
		double seconds = (double) elapsed / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	private static void processPath(Path path, String outputPath) throws IOException {
		if (Files.isDirectory(path)) {
			processDirectory(path, outputPath);
		} else {
			processFile(path);
		}
	}

	private static void processDirectory(Path directory, String outputPath) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path path : listing) {
				processPath(path, outputPath);
			}
		}
	}

	private static void processFile(Path file) throws IOException {
		// Implement logic to process the file
		System.out.println("Processing file: " + file);
	}
}