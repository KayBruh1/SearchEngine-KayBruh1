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

    public void buildStructures(Path inputPath, ArgumentParser parser) throws IOException {
		if (inputPath != null && Files.isDirectory(inputPath)) {
			processDirectory(inputPath, parser, 0);
		} else {
			processFile(inputPath);
		}
    }
    
	/**
	 * Recursively processes a directory to generate word counts for files
	 *
	 * @param directory  The directory to process
	 * @param parser 
	 * @throws IOException If an I/O error occurs
	 */
	public void processDirectory(Path directory, ArgumentParser parser, int counter) throws IOException {
		String countsPath = null;
		boolean counts = false;
		boolean index = false;
		
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			HashMap<String, Integer> wordCounts = null;
			if (parser.hasFlag("-counts")) {
				countsPath = parser.getString("-counts", ("counts.json"));
				counts = true;
				if (index) {
					counts = false;
				}
			}
			
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					processDirectory(path, parser, counter);
				} else {
					String relativePath = directory.resolve(path.getFileName()).toString();
					if (counter > 0) {
						counts = false;
					}
					
					if (relativePath.toLowerCase().endsWith(".txt") || relativePath.toLowerCase().endsWith(".text")) {
						if (counts) {
							wordCounts = processCountsFiles(path);
				            System.out.println("yo ");
						} else {
				            System.out.println("here ");
							counts = false;
							wordCounts = processIndexFiles(path);
						}

						int totalWords = wordCounts.values().stream().mapToInt(Integer::intValue).sum();
						if (totalWords > 0) {
							indexer.getFileWordCounts().put(relativePath, totalWords);
						}
					}
				}
			}
		}
		if (counts) {
            counter += 1;
            if (counter >= 2) {
            	return;
            }
			JsonWriter.writeObject(indexer.getFileWordCounts(), Path.of(countsPath));
			if (parser.hasFlag("-index")) {
				counter += 1;
				processDirectory(directory, parser, counter);
			}
		}
	}

	/**
	 * Processes file to generate word counts and build the inverted index
	 *
	 * @param filePath The path of file to be processed
	 * @return A HashMap containing word counts for the file
	 * @throws IOException If an I/O error occurs
	 */
	public HashMap<String, Integer> processIndexFiles(Path filePath) throws IOException {
		TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex = this.indexer.getInvertedIndex();

		List<String> lines = Files.readAllLines(filePath);
		HashMap<String, Integer> wordCounts = new HashMap<>();
		int position = 0;

		for (String line : lines) {
			List<String> wordStems = FileStemmer.listStems(line);

			for (String stemmedWord : wordStems) {
				position += 1;
				wordCounts.put(stemmedWord, wordCounts.getOrDefault(stemmedWord, 0) + 1);

				if (!invertedIndex.containsKey(stemmedWord)) {
					invertedIndex.put(stemmedWord, new TreeMap<>());
				}

				TreeMap<String, TreeSet<Integer>> fileMap = invertedIndex.get(stemmedWord);
				if (!fileMap.containsKey(filePath.toString())) {
					fileMap.put(filePath.toString(), new TreeSet<>());
				}

				TreeSet<Integer> positions = fileMap.get(filePath.toString());
				positions.add(position);
			}
		}
		return wordCounts;
	}

	/**
	 * Processes file to generate word counts
	 * 
	 * @param filePath The path of the file to be processed
	 * @return A HashMap containing the word counts for the file
	 * @throws IOException If an I/O error occurs
	 */
	public HashMap<String, Integer> processCountsFiles(Path filePath) throws IOException {
		List<String> lines = Files.readAllLines(filePath);
		HashMap<String, Integer> wordCounts = new HashMap<>();

		for (String line : lines) {
			List<String> wordStems = FileStemmer.listStems(line);

			for (String stemmedWord : wordStems) {
				if (wordCounts.containsKey(stemmedWord)) {
					int currentCount = wordCounts.get(stemmedWord);
					wordCounts.put(stemmedWord, currentCount + 1);
				} else {
					wordCounts.put(stemmedWord, 1);
				}
			}
		}
		return wordCounts;
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
