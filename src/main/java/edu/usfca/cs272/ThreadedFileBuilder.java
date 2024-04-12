package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Multithreaded class for building and processing files/directories to generate word counts
 * and an inverted index
 */
public class ThreadedFileBuilder {
	/**
	 * Thread safe inverted index instance for searching
	 */
	private final ThreadSafeInvertedIndex mtIndexer;
	
	/**
	 * Work queue instance for multithreading
	 */
	private final CustomWorkQueue workQueue;

	public ThreadedFileBuilder(InvertedIndex indexer, int numThreads) {
		this.mtIndexer = new ThreadSafeInvertedIndex(indexer);
		this.workQueue = new CustomWorkQueue(numThreads);
	}

	public void buildStructures(Path inputPath) throws InterruptedException {
		if (Files.isDirectory(inputPath)) {
			processDirectory(inputPath);
		} else {
			workQueue.execute(new FileTask(inputPath));
		}
		workQueue.finish();
		workQueue.shutdown();
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
		} catch (Exception e) {
			System.out.println("Error processing files in directory: " + directory);
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
			} catch (Exception e) {
				System.out.println("Error processing file: " + location);
			}
		}
	}

	private void processFile(Path location) throws IOException {
		String locationString = location.toString();
		List<String> stems = FileStemmer.listStems(location);
		int position = 0;
		for (String stem : stems) {
			position++;
			mtIndexer.addWord(stem, locationString, position);
		}
	}

	private static boolean isTextFile(Path file) {
		String fileName = file.getFileName().toString().toLowerCase();
		return Files.isRegularFile(file) && (fileName.endsWith(".txt") || fileName.endsWith(".text"));
	}
}