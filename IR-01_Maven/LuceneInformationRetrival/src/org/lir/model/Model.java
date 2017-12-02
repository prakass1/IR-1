package org.lir.model;

public class Model {

	private String title;
	private String summary;
	private int rank;
	private int relScore;
	private String path;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getRelScore() {
		return relScore;
	}
	public void setRelScore(int relScore) {
		this.relScore = relScore;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	@Override
	public String toString() {
		return "Model [title=" + title + ", summary=" + summary + ", rank=" + rank + ", relScore=" + relScore + "]";
	}
	
	
}
