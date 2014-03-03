package com.walintukai.derpteam;

public class Member {

	private String posterFbId; 
	private String posterFirstName;
	private String targetFbId;
	private String imageUrl;
	private String title;
	private String caption;
	private int picId;
	private int views;
	private int upVote;
	private int downVote;
	 
	// For team page
	public Member(String posterFbId, String posterFirstName, String targetFbId, String imageUrl, 
				  String title, String caption, int picId, int views, int upVote, int downVote) {
		this.posterFbId = posterFbId;
		this.posterFirstName = posterFirstName;
		this.targetFbId = targetFbId;
		this.imageUrl = "http://" + imageUrl;
		this.title = title;
		this.caption = caption;
		this.picId = picId;
		this.views = views;
		this.upVote = upVote;
		this.downVote = downVote;
	}
	
	// For view member page
	public Member(String posterFbId, String targetFbId, String imageUrl, String title, 
				  String caption, int picId, int views, int upVote, int downVote) {
		this.posterFbId = posterFbId;
		this.posterFirstName = "";
		this.targetFbId = targetFbId;
		this.imageUrl = "http://" + imageUrl;
		this.title = title;
		this.caption = caption;
		this.picId = picId;
		this.views = views;
		this.upVote = upVote;
		this.downVote = downVote;
	}
	
	// For gallery
	public Member(String imageUrl, int picId) {
		this.posterFbId = "";
		this.posterFirstName = "";
		this.targetFbId = "";
		this.imageUrl = "http://" + imageUrl;
		this.title = "";
		this.caption = "";
		this.picId = picId;
		this.views = 0;
		this.upVote = 0;
		this.downVote = 0;
	}
	
	public String getPosterFbId() { return posterFbId; }
	public String getPosterFirstName() { return posterFirstName; }
	public String getTargetFbId() { return targetFbId; }
	public String getImageUrl() { return imageUrl; }
	public String getTitle() { return title; }
	public String getCaption() { return caption; }
	public int getPicId() { return picId; }
	public int getViews() { return views; }
	public int getUpVote() { return upVote; }
	public int getDownVote() { return downVote; }
	
}
