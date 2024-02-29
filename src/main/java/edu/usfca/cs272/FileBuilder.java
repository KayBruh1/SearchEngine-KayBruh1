package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class for building and processing files/directories to generate word counts
 * and an inverted index to write to JSON file
 */
public class FileBuilder {

	/**
	 * The InvertedIndex class used for storing word counts and the inverted index
	 */
    private InvertedIndex indexer;

    public FileBuilder(InvertedIndex indexer) {
        this.indexer = indexer;
    }

    // Other methods of the FileBuilder class

    public InvertedIndex getIndexer() {
        return indexer;
    }

	/*
	 * TODO * 
	 * public static void build(...) { if (dir) call traverse else processFile }
	 */


	public void processDirectory(Path directory) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					processDirectory(path);
				} else {
					// @CITE StackOverflow
					String relativePath = directory.resolve(path.getFileName()).toString();

					if (relativePath.toLowerCase().endsWith(".txt") || relativePath.toLowerCase().endsWith(".text")) {
						processFile(Path.of(relativePath));
					}
				}
			}
		}
	}

    public void processFile(Path filePath) throws IOException {
        if (filePath != null) {
            List<String> lines = Files.readAllLines(filePath);

            HashMap<String, Integer> wordCounts = new HashMap<>();
            TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndexMap = new TreeMap<>();

            int position = 0;

            for (String line : lines) {
                List<String> wordStems = FileStemmer.listStems(line);

                for (String stemmedWord : wordStems) {
                    position += 1;

                    wordCounts.put(stemmedWord, wordCounts.getOrDefault(stemmedWord, 0) + 1);

                    if (!invertedIndexMap.containsKey(stemmedWord)) {
                        invertedIndexMap.put(stemmedWord, new TreeMap<>());
                    }

                    TreeMap<String, TreeSet<Integer>> fileMap = invertedIndexMap.get(stemmedWord);
                    if (!fileMap.containsKey(filePath.toString())) {
                        fileMap.put(filePath.toString(), new TreeSet<>());
                    }

                    TreeSet<Integer> wordPosition = fileMap.get(filePath.toString());
                    wordPosition.add(position);
                }
            }

            InvertedIndex indexer = getIndexer();
            indexer.setFileWordCounts(new TreeMap<>(wordCounts));
            indexer.setInvertedIndex(new TreeMap<>(invertedIndexMap));

            System.out.println("Processing completed");
        } else {
            System.out.println("File path is null");
        }
    }

}
