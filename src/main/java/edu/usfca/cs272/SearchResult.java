package edu.usfca.cs272;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SearchResult implements Comparable<SearchResult> {
    private String location;
    private int totalWords;
    private int count;
    private double score;

    public SearchResult(String location, int totalWords, int count, double score) {
        this.location = location;
        this.totalWords = totalWords;
        this.count = count;
        this.score = score;
    }
    
    public void updateMatchCount(int matches) {
        this.count += matches;
    }

	public int getCount() {
		return count;
	}

	public double getScore() {
		return score;
	}

	public String getLocation() {
		return location;
	}

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
	
	public static Map<String, List<SearchResult>> sortResults(Map<String, List<SearchResult>> unsortedMap) {
	    Map<String, List<SearchResult>> sortedMap = new LinkedHashMap<>();

	    List<Map.Entry<String, List<SearchResult>>> entryList = new ArrayList<>(unsortedMap.entrySet());
	    Collections.sort(entryList, new Comparator<Map.Entry<String, List<SearchResult>>>() {
	        @Override
	        public int compare(Map.Entry<String, List<SearchResult>> entry1, Map.Entry<String, List<SearchResult>> entry2) {
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
