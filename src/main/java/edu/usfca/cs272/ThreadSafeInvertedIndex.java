package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * Class for thread safe methods
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {
	/** The lock used to protect concurrent access */
	private final CustomReadWriteLock lock;

	public ThreadSafeInvertedIndex() {
		this.lock = new CustomReadWriteLock();
	}

	/**
	 * Adds a word with its position in a file to the inverted index
	 *
	 * @param word     The word to add
	 * @param location The path of the file
	 * @param position The position of the word in the file
	 */
	@Override
	public void addWord(String word, String location, int position) {
		lock.writeLock().lock();
		try {
			super.addWord(word, location, position);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	// TODO Need to override and lock more methods

	/**
	 * Performs an exact search based on the provided set of queries.
	 *
	 * @param queries The set of queries to search for
	 * @return A list of search results for each query
	 */
	@Override
	public List<SearchResult> exactSearch(Set<String> queries) {
		lock.readLock().lock();
		try {
			return super.exactSearch(queries);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Performs a partial search based on the provided set of queries.
	 *
	 * @param queries The set of queries to search for
	 * @return A list of search results for each query
	 */
	@Override
	public List<SearchResult> partialSearch(Set<String> queries) {
		lock.readLock().lock();
		try {
			return super.partialSearch(queries);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Writes the word counts to a JSON file
	 *
	 * @param countsPath the output path of the JSON file
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	public void writeCounts(Path countsPath) throws IOException {
		System.out.println("o");
		super.writeCounts(countsPath);;
	}

	/**
	 * Writes the inverted index to a JSON file
	 *
	 * @param indexPath the output path of the JSON file
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	public void writeIndex(Path indexPath) throws IOException {
		super.writeIndex(indexPath);
	}
}