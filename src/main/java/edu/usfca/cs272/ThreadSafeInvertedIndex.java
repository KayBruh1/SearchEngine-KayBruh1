
package edu.usfca.cs272;

import java.util.List;
import java.util.Set;

/**
 * Class for thread safe methods
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {
	/** The lock used to protect concurrent access */
	private final CustomReadWriteLock lock;

	/**
	 * The InvertedIndex instance for adding and searching
	 */
	private final InvertedIndex indexer; // TODO Remove

	/* TODO 
	public ThreadSafeInvertedIndex() {
		this.lock = new CustomReadWriteLock();
	}
	*/
	
	/**
	 * @param indexer The instance to use for adding and searching
	 */
	public ThreadSafeInvertedIndex(InvertedIndex indexer) { // TODO Remove
		this.lock = new CustomReadWriteLock();
		this.indexer = indexer;
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
			indexer.addWord(word, location, position);
		} finally {
			lock.writeLock().unlock();
		}
		/* TODO 
		lock.writeLock().lock();
		try {
			super.addWord(word, location, position);
		} finally {
			lock.writeLock().unlock();
		}
		*/
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
			return indexer.exactSearch(queries);
		} finally {
			lock.writeLock().unlock();
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
			return indexer.partialSearch(queries);
		} finally {
			lock.writeLock().unlock();
		}
	}
}
