package com.walintukai.derpteam;

public class Member {

	private String posterFbId; 
	private String targetFbId;
	private String imageUrl;
	private String title;
	private String caption;
	private int picId;
	private int views;
	
	public Member(String posterFbId, String targetFbId, String imageUrl, String title, 
				  String caption, int picId, int views) {
		this.posterFbId = posterFbId;
		this.targetFbId = targetFbId;
		this.imageUrl = "http://" + imageUrl;
		this.title = title;
		this.caption = caption;
		this.picId = picId;
		this.views = views;
	}
	
	public Member(String imageUrl, int picId) {
		this.posterFbId = "";
		this.targetFbId = "";
		this.imageUrl = "http://" + imageUrl;
		this.title = "";
		this.caption = "";
		this.picId = picId;
		this.views = 0;
	}
	
	public String getPosterFbId() { return posterFbId; }
	public String getTargetFbId() { return targetFbId; }
	public String getImageUrl() { return imageUrl; }
	public String getTitle() { return title; }
	public String getCaption() { return caption; }
	public int getPicId() { return picId; }
	public int getViews() { return views; }
	
}
