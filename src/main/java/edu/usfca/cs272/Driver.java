package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Kayvan Zahiri
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class Driver {
	static TreeMap<String, Integer> fileWordCounts = new TreeMap<>();
	static String inputPath;
	static String outputPath;
	static boolean counts = false;

	public static void main(String[] args) throws IOException {
		Instant start = Instant.now();

		fileWordCounts.clear();

		ArgumentParser parser = new ArgumentParser(args);

		inputPath = parser.getString("-text");

		for (String arg : args) {
			if (arg.contains("-counts")) {
				outputPath = parser.getString("-counts", "counts.json");
				counts = true;
			}
		}

		if (inputPath != null) {
			Path path = Path.of(inputPath);
			processPath(path);
		} else {
			System.out.println("No input text files provided");
		}

		long elapsed = Duration.between(start, Instant.now()).toMillis();
		double seconds = (double) elapsed / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}


	private static void processPath(Path path) throws IOException {
		if (Files.isDirectory(path)) {
			processDirectoryOutput(path);
		} else if (Files.exists(path)){
			processFile(path, inputPath, counts);
		}
	}

	private static void processDirectoryOutput(Path directory) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					processDirectoryOutput(path);
				} else {
					// @CITE StackOverflow 
					String relativePath = directory.resolve(path.getFileName()).toString();

					if (relativePath.toLowerCase().endsWith(".txt") || relativePath.toLowerCase().endsWith(".text")) {
						HashMap<String, Integer> wordCounts = processFile(path);

						// @CITE StackOverflow
						int totalWords = wordCounts.values().stream().mapToInt(Integer::intValue).sum();
						if (totalWords > 0) {
							fileWordCounts.put(relativePath, totalWords);
						}
					}
				}
			}
		}

		JsonWriter.writeObject(fileWordCounts, Path.of(outputPath));
		System.out.println("Word counts have been written to: " + outputPath);
	}

	private static HashMap<String, Integer> processFile(Path filePath) throws IOException {
		List<String> lines = Files.readAllLines(filePath);
		HashMap<String, Integer> wordCounts = new HashMap<>();

		for (String line : lines) {
			List<String> wordStems = FileStemmer.listStems(line);

			for (String stemmedWord : wordStems) {
				if (wordCounts.containsKey(stemmedWord)) {
					int currentCount = wordCounts.get(stemmedWord);
					wordCounts.put(stemmedWord, currentCount + 1);
				} else {
					wordCounts.put(stemmedWord, 1);
				}
			}
		}

		return wordCounts;
	}

	private static void processFile(Path filePath, String inputPath, boolean counts) throws IOException {
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

		if (counts == true) {
			outputWordCounts(wordCounts, inputPath, outputPath);
		}
	}

	private static void outputWordCounts(HashMap<String, Integer> wordCounts, String inputPath, String outputPath) {
		try {
			if (wordCounts.isEmpty()) {
				System.out.println("Word counts map is empty"); 
				HashMap<String, Integer> pathWordCount = new HashMap<>();

				JsonWriter.writeObject(pathWordCount, Path.of(outputPath));
				System.out.println("Empty word counts have been written to: " + outputPath);
			} else {
				HashMap<String, Integer> pathWordCount = new HashMap<>();
				int totalWords = 0;

				for (int count : wordCounts.values()) {
					totalWords += count;
				}
				System.out.println("Total words: " + totalWords);
				pathWordCount.put(inputPath, totalWords);

				JsonWriter.writeObject(pathWordCount, Path.of(outputPath));
				System.out.println("Word counts have been written to: " + outputPath);
			}
		} catch (Exception e) {
			System.err.println("Error writing word counts to file: " + e.getMessage());
		}
	}
}
