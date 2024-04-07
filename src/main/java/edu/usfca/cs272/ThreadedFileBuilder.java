package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class ThreadedFileBuilder {
	private final ThreadSafeInvertedIndex mtIndexer;
	private final SnowballStemmer stemmer;
	private final CustomWorkQueue workQueue;

	public ThreadedFileBuilder(InvertedIndex indexer, int numThreads) {
		this.mtIndexer = new ThreadSafeInvertedIndex(indexer);
		this.stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		this.workQueue = new CustomWorkQueue(numThreads);
	}

	public void buildStructures(Path inputPath) throws InterruptedException {
		if (Files.isDirectory(inputPath)) {
			processDirectory(inputPath);
		} else {
			workQueue.execute(new FileTask(inputPath));
		}

		workQueue.finish();
	}

	private void processDirectory(Path directory) throws InterruptedException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
			for (Path path : stream) {
				if (Files.isDirectory(path)) {
					processDirectory(path);
				} else if (isTextFile(path)) {
					workQueue.execute(new FileTask(path));
				}
			}
		} catch (IOException e) {
			System.out.println("Error listing files in directory: " + directory);
		}
	}

	private class FileTask implements Runnable {
		private final Path location;

		public FileTask(Path location) {
			this.location = location;
		}

		@Override
		public void run() {
			try {
				processFile(location);
			} catch (IOException e) {
				System.out.println("Error processing file: " + location);
			}
		}
	}

	private synchronized void processFile(Path location) throws IOException {
		int position = 0;
		String locationString = location.toString();
		try (BufferedReader reader = Files.newBufferedReader(location)) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] words = FileStemmer.parse(line);
				for (String word : words) {
					String stemmedWord = stemmer.stem(word).toString();
					position++;
					mtIndexer.addWord(stemmedWord, locationString, position);
				}
			}
		}
	}

	private static boolean isTextFile(Path file) {
		String fileName = file.getFileName().toString().toLowerCase();
		return Files.isRegularFile(file) && (fileName.endsWith(".txt") || fileName.endsWith(".text"));
	}
}