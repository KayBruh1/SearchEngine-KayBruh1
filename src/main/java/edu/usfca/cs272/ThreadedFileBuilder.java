package edu.usfca.cs272;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

/**
 * Multithreaded class for building and processing files/directories to generate
 * word counts and an inverted index
 */
public class ThreadedFileBuilder extends FileBuilder {
	/**
	 * Thread safe inverted index instance for searching
	 */
	private final ThreadSafeInvertedIndex mtIndexer;

	/**
	 * Work queue instance for multithreading
	 */
	private final CustomWorkQueue workQueue;

	/**
	 * Constructs an indexer and work queue for building
	 * 
	 * @param indexer   Inverted index instance for processing
	 * @param workQueue The work queue for multithreading
	 */
	public ThreadedFileBuilder(ThreadSafeInvertedIndex indexer, CustomWorkQueue workQueue) {
		super(indexer);
		this.mtIndexer = indexer;
		this.workQueue = workQueue;
	}

	/**
	 * Builds word count and inverted index structures for the specified input path.
	 *
	 * @param inputPath The path of the file or directory to be processed
	 * @throws IOException If an i/o error occurs
	 */
	@Override
	public void buildStructures(Path inputPath) throws IOException {
		super.buildStructures(inputPath);
		workQueue.finish();
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
				InvertedIndex localIndex = new InvertedIndex();
				FileBuilder.processFile(location, localIndex);
				mtIndexer.addAll(localIndex);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	/**
	 * Processes the specified file to generate word counts and an inverted index
	 *
	 * @param location The path of the file to process
	 * @throws IOException If an I/O error occurs
	 */
	@Override
	public void processFile(Path location) throws IOException {
		workQueue.execute(new FileTask(location));
	}
}