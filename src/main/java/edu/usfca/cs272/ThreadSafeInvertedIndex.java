package edu.usfca.cs272;

import java.util.List;
import java.util.Set;

/**
 * Class for thread safe methods
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {
	/** The lock used to protect concurrent access*/
	private final CustomReadWriteLock lock;
	
	/**
	 * The InvertedIndex instance for adding and searching
	 */
	private final InvertedIndex indexer;

	/**
	 * @param indexer The instance to use for adding and searching
	 */
	public ThreadSafeInvertedIndex(InvertedIndex indexer) {
		this.lock = new CustomReadWriteLock();
		this.indexer = indexer;
	}

	@Override
	public void addWord(String word, String location, int position) {
		lock.writeLock().lock();
		try {
			indexer.addWord(word, location, position);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Searches the inverted index for the specified queries
	 * 
	 * @param queries The set of queries to search for
	 * @param partial Boolean for whether or not to partial search
	 * @return A list of search results
	 */
	@Override
	public List<InvertedIndex.SearchResult> search(Set<String> queries, boolean partial) {;
		lock.writeLock().lock();
		try {
			return partial ? partialSearch(queries) : exactSearch(queries);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Performs an exact search based on the provided set of queries.
	 *
	 * @param queries The set of queries to search for
	 * @return A list of search results for each query
	 */
	@Override
	public List<SearchResult> exactSearch(Set<String> queries) {
		lock.writeLock().lock();
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
		lock.writeLock().lock();
		try {
			return indexer.partialSearch(queries);
		} finally {
			lock.writeLock().unlock();
		}
	}
}