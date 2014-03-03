package com.walintukai.derpteam;

public class Comment {

	private String posterFbId;
	private String posterFirstName;
	private String comment;
	
	public Comment(String posterFbId, String posterFirstName, String comment) {
		this.posterFbId = posterFbId;
		this.posterFirstName = posterFirstName;
		this.comment = comment;
	}
	
	public String getPosterFbId() { return posterFbId; }
	public String getPosterFirstName() { return posterFirstName; }
	public String getComment() { return comment; }
	
}
