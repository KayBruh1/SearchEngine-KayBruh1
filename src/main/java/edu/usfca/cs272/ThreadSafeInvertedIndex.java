package edu.usfca.cs272;

public class ThreadSafeInvertedIndex extends InvertedIndex {
    private final CustomrReadWriteLock lock;

    public ThreadSafeInvertedIndex() {
        this.lock = new CustomrReadWriteLock();
    }

    @Override
	public void addWord(String word, String location, int position) {
        lock.writeLock().lock();
        try {
            super.addWord(word, location, position);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
