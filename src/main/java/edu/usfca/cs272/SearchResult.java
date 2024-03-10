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
		System.out.println("here ");
		int scoreComparison = Double.compare(other.score, this.score);
		if (scoreComparison != 0) {
			System.out.println("1 ");

			return scoreComparison;
		}

		int countComparison = Integer.compare(other.count, this.count);
		if (countComparison != 0) {
			System.out.println("2 ");

			return countComparison;
		}

		System.out.println("3 ");

		return this.location.compareToIgnoreCase(other.location);
	}
}
