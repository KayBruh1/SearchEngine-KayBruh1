package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Multithreaded class for building and processing files/directories to generate
 * word counts and an inverted index
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

	/**
	 * @param indexer    Inverted index instance for processing
	 * @param workQueue Number of threads for the work queue
	 */
	public ThreadedFileBuilder(ThreadSafeInvertedIndex indexer, CustomWorkQueue workQueue) {
		this.mtIndexer = indexer;
		this.workQueue = new CustomWorkQueue();
	}

	/**
	 * Builds word count and inverted index structures for the specified input path.
	 *
	 * @param inputPath The path of the file or directory to be processed
	 * @throws IOException If an i/o error occurs
	 */
	public void buildStructures(Path inputPath) throws IOException {
		if (Files.isDirectory(inputPath)) {
			processDirectory(inputPath);
		} else {
			workQueue.execute(new FileTask(inputPath));
		}
		workQueue.finish();
	}

	/**
	 * Processes the files in the specified directory to generate word counts and
	 * the inverted index
	 *
	 * @param directory The directory to process
	 * @throws IOException If an i/o error occurs
	 */
	private void processDirectory(Path directory) throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
			for (Path path : stream) {
				if (Files.isDirectory(path)) {
					processDirectory(path);
				} else if (FileBuilder.isTextFile(path)) {
					workQueue.execute(new FileTask(path));
				}
			}
		}
	}

	/**
	 * Class to help process files
	 */
	private class FileTask implements Runnable {
		/**
		 * Location to process
		 */
		private final Path location;

		/**
		 * @param location Path to process
		 */
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

	/**
	 * Processes the specified file to generate word counts and an inverted index
	 *
	 * @param location The path of the file to process
	 * @throws IOException If an I/O error occurs
	 */
	private void processFile(Path location) throws IOException {
		
		/* TODO Try this instead:
		InvertedIndex local = new InvertedIndex();
		FileBuilder.processFile(location, local);
		mtIndexer.addAll(local);
		*/
		
		
		String locationString = location.toString();
		List<String> stems = FileStemmer.listStems(location);
		int position = 0;
		for (String stem : stems) {
			position++;
			mtIndexer.addWord(stem, locationString, position);
		}
	}
}