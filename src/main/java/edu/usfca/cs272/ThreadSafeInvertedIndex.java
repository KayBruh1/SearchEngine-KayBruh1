package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

/**
 * Class for thread safe methods
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {
	/** The lock used to protect concurrent access */
	private final CustomReadWriteLock lock;

	/**
	 * 
	 */
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

	/**
	 * Adds all entries to the InvertedIndex
	 *
	 * @param localIndex The InvertedIndex entries to add
	 */
	@Override
	public void addAll(InvertedIndex localIndex) {
		lock.writeLock().lock();
		try {
			super.addAll(localIndex);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Returns the word counts
	 *
	 * @return the TreeMap containing word counts
	 */
	@Override
	public SortedMap<String, Integer> getCounts() {
		lock.readLock().lock();
		try {
			return super.getCounts();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Gets the total word count for a specific location
	 *
	 * @param location The location of the document
	 * @return The total word count at the location
	 */
	@Override
	public int getTotalWordCount(String location) {
		lock.readLock().lock();
		try {
			return super.getTotalWordCount(location);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Finds the amount of different files
	 * 
	 * @return The number of files
	 */
	@Override
	public int numCounts() {
		lock.readLock().lock();
		try {
			return super.numCounts();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Finds the amount of different words
	 * 
	 * @return The number of words
	 */
	@Override
	public int numWords() {
		lock.readLock().lock();
		try {
			return super.numWords();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Returns the number of locations of a word
	 *
	 * @param word The word to get locations for
	 * @return The number of locations the word appears
	 */
	@Override
	public int numWordLocations(String word) {
		lock.readLock().lock();
		try {
			return super.numWordLocations(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Returns the number of positions a word appears in a file
	 *
	 * @param word     The word to get positions for
	 * @param location The location to get positions for
	 * @return The number of positions the word appears in the location
	 */
	@Override
	public int numWordPositions(String word, String location) {
		lock.readLock().lock();
		try {
			return super.numWordPositions(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Check if the location exists in the word counts
	 *
	 * @param location The location to check
	 * @return True if the location exists, false otherwise
	 */
	@Override
	public boolean hasLocation(String location) {
		lock.readLock().lock();
		try {
			return super.hasLocation(location);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Check if the word exists in the inverted index
	 *
	 * @param word The word to check
	 * @return True if the word exists, false otherwise
	 */
	@Override
	public boolean hasWord(String word) {
		lock.readLock().lock();
		try {
			return super.hasWord(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Checks if a word at a specific location exists
	 *
	 * @param word     The word to check
	 * @param location The location to check
	 * @return True if the word at location exists, false otherwise
	 */
	@Override
	public boolean hasWordLocation(String word, String location) {
		lock.readLock().lock();
		try {
			return super.hasWordLocation(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Checks if a word exists at a specific location position
	 *
	 * @param word     The word to check
	 * @param location The location to check
	 * @param position The position of the word
	 * @return True if the word exists at the location position, false otherwise
	 */
	@Override
	public boolean hasWordPosition(String word, String location, int position) {
		lock.readLock().lock();
		try {
			return super.hasWordPosition(word, location, position);
		} finally {
			lock.readLock().unlock();
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
		lock.writeLock().lock();
		try {
			super.writeCounts(countsPath);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Writes the inverted index to a JSON file
	 *
	 * @param indexPath the output path of the JSON file
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	public void writeIndex(Path indexPath) throws IOException {
		lock.writeLock().lock();
		try {
			super.writeIndex(indexPath);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Returns a string representation of the inverted index
	 * 
	 * @return a string representation of the inverted index
	 */
	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		} finally {
			lock.readLock().unlock();
		}
	}
}