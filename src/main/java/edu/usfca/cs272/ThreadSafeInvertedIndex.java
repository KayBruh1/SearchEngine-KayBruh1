package edu.usfca.cs272;

import java.util.List;
import java.util.Set;

public class ThreadSafeInvertedIndex extends InvertedIndex {
    private final CustomReadWriteLock lock;
    private final InvertedIndex indexer;

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
    
	@Override
	public List<InvertedIndex.SearchResult> search(Set<String> queries, boolean partial) {
        lock.writeLock().lock();
        try {
        	return partial ? partialSearch(queries) : exactSearch(queries);
        } finally {
            lock.writeLock().unlock();
        }
	}
}