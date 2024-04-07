package edu.usfca.cs272;

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
}