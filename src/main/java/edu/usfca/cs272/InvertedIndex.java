package edu.usfca.cs272;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class InvertedIndex {
	/** TreeMap storing word counts for each file */
	public TreeMap<String, Integer> fileWordCounts = new TreeMap<>();

	/** TreeMap storing inverted index for files and word positions */
	public TreeMap<String, TreeMap<String, List<Integer>>> invertedIndex = new TreeMap<>();


	public void addWordCount(String filePath, Integer count) {
		fileWordCounts.put(filePath, count);
	}

	public void addWord(String word, String filePath, Integer position) {
		if (!invertedIndex.containsKey(word)) {
			invertedIndex.put(word, new TreeMap<>());
		}

		TreeMap<String, List<Integer>> wordMap = invertedIndex.get(word);

		if (!wordMap.containsKey(filePath)) {
			wordMap.put(filePath, new ArrayList<>());
		}

		List<Integer> wordPosition = wordMap.get(filePath);
		wordPosition.add(position);
	}
}
