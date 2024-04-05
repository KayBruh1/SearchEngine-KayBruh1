package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class ThreadedFileBuilder {
	/**
	 * The InvertedIndex class used for storing word counts and the inverted index
	 */
	private final ThreadSafeInvertedIndex indexer;

	/**
	 * SnowballStemmer instance for stemming
	 */
	private final SnowballStemmer stemmer;

	/**
	 * Creates a new FileBuilder object with the InvertedIndex
	 *
	 * @param indexer the InvertedIndex object
	 */
	public ThreadedFileBuilder(InvertedIndex indexer) {
		this.indexer = new ThreadSafeInvertedIndex(indexer);
		this.stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
	}

    public void buildStructures(Path inputPath) throws IOException, InterruptedException {
        if (Files.isDirectory(inputPath)) {
            processDirectory(inputPath);
        } else {
            processFile(inputPath);
        }
    }

    public void processDirectory(Path directory) throws IOException, InterruptedException {
        try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
            List<Thread> threads = new ArrayList<>();
            for (Path path : listing) {
                if (Files.isDirectory(path)) {
                    processDirectory(path);
                } else {
                    if (isTextFile(path)) {
                        Thread thread = new Thread(() -> {
                            try {
                                processFile(path);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        threads.add(thread);
                        thread.start();
                        thread.join();
                    }
                }
            }
        }
    }

    public void processFile(Path location) throws IOException {
        String locationString = location.toString();
        int position = 0;
        try (BufferedReader reader = Files.newBufferedReader(location)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = FileStemmer.parse(line);
                for (String word : words) {
                    String stemmedWord = stemmer.stem(word).toString();
                    position++;
                    indexer.addWord(stemmedWord, locationString, position);
                }
            }
        }
    }

    public boolean isTextFile(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        return Files.isRegularFile(file) && (fileName.endsWith(".txt") || fileName.endsWith(".text"));
    }
}
