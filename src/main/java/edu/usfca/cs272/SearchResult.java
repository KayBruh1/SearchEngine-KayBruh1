package edu.usfca.cs272;

public class SearchResult implements Comparable<SearchResult> {
	private String location;
	private int count;
	private double score;

	public SearchResult(String location, int count, double score) {
		this.location = location;
		this.count = count;
		this.score = score;
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
}
