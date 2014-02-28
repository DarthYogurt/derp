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
	
	public Member(String posterFbId, String posterFirstName, String targetFbId, String imageUrl, 
				  String title, String caption, int picId, int views) {
		this.posterFbId = posterFbId;
		this.posterFirstName = posterFirstName;
		this.targetFbId = targetFbId;
		this.imageUrl = "http://" + imageUrl;
		this.title = title;
		this.caption = caption;
		this.picId = picId;
		this.views = views;
	}
	
	public Member(String posterFbId, String targetFbId, String imageUrl, String title, 
				  String caption, int picId, int views) {
		this.posterFbId = posterFbId;
		this.posterFirstName = "";
		this.targetFbId = targetFbId;
		this.imageUrl = "http://" + imageUrl;
		this.title = title;
		this.caption = caption;
		this.picId = picId;
		this.views = views;
	}
	
	public Member(String imageUrl, int picId) {
		this.posterFbId = "";
		this.posterFirstName = "";
		this.targetFbId = "";
		this.imageUrl = "http://" + imageUrl;
		this.title = "";
		this.caption = "";
		this.picId = picId;
		this.views = 0;
	}
	
	public String getPosterFbId() { return posterFbId; }
	public String getPosterFirstName() { return posterFirstName; }
	public String getTargetFbId() { return targetFbId; }
	public String getImageUrl() { return imageUrl; }
	public String getTitle() { return title; }
	public String getCaption() { return caption; }
	public int getPicId() { return picId; }
	public int getViews() { return views; }
	
}
