package edu.usfca.cs272;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a single search result containing information on location, total
 * words, count, and score
 */
public class SearchResult implements Comparable<SearchResult> {
	/**
	 * The location of the search result
	 */
	private String location;

	/**
	 * The total number of words in the search result
	 */
	private int totalWords;

	/**
	 * The count of matches for the search query
	 */
	private int count;

	/**
	 * The representing score of the search result
	 */
	private double score;

	/**
	 * Constructs a search result with the location, word count, match count, and
	 * score
	 *
	 * @param location   the location of the search result
	 * @param totalWords the total number of words at the location
	 * @param count      the number of matches at in the location
	 * @param score      the score of the search result
	 */
	public SearchResult(String location, int totalWords, int count, double score) {
		this.location = location;
		this.totalWords = totalWords;
		this.count = count;
		this.score = score;
	}

	/**
	 * Updates the match count
	 *
	 * @param matches the number of matches to add
	 */
	public void updateCount(int matches) {
		this.count += matches;
	}

	/**
	 * Sets the score of the search result
	 *
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * Gets the count of matches for the search result
	 *
	 * @return the count of matches
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Gets the score for the search result
	 *
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * Gets the location for the search result
	 *
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Gets the total number of words at the location
	 *
	 * @return the total number of words
	 */
	public int getTotalWords() {
		return totalWords;
	}

	/**
	 * Compares one search result with another for sorting
	 *
	 * @param other the other search result to compare with
	 * @return a negative, positive, or zero based on the comparison
	 */
	@Override
	public int compareTo(SearchResult other) {
		int scoreComparison = Double.compare(other.score, this.score);
		if (scoreComparison != 0) {
			return scoreComparison;
		}
		int countComparison = Integer.compare(other.count, this.count);
		if (countComparison != 0) {
			return countComparison;
		}
		return this.location.compareToIgnoreCase(other.location);
	}

	/**
	 * Sorts a map of search results
	 *
	 * @param unsortedMap the unsorted map of search results
	 * @return a sorted map of search results
	 */
	public static Map<String, List<SearchResult>> sortResults(Map<String, List<SearchResult>> unsortedMap) {
		Map<String, List<SearchResult>> sortedMap = new LinkedHashMap<>();
		List<Map.Entry<String, List<SearchResult>>> entryList = new ArrayList<>(unsortedMap.entrySet());
		Collections.sort(entryList, new Comparator<Map.Entry<String, List<SearchResult>>>() {
			@Override
			public int compare(Map.Entry<String, List<SearchResult>> entry1,
					Map.Entry<String, List<SearchResult>> entry2) {
				return entry1.getKey().compareTo(entry2.getKey());
			}
		});
		for (Map.Entry<String, List<SearchResult>> entry : entryList) {
			Collections.sort(entry.getValue());
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}
