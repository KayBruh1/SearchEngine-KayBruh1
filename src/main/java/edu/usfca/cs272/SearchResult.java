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
	public int compareTo(SearchResult o) {
		// TODO Auto-generated method stub
		return 0;
	}
}

