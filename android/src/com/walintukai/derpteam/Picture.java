package com.walintukai.derpteam;

public class Picture {

	private String posterUserId;
	private String posterFbId; 
	private String targetUserId;
	private String targetFbId;
	private String imageUrl;
	private String title;
	private String caption;
	private int picId;
	private int views;
	
	public Picture(String posterUserId, String posterFbId, String targetUserId, String targetFbId,
				   String imageUrl, String title, String caption, int picId, int views) {
		this.posterUserId = posterUserId;
		this.posterFbId = posterFbId;
		this.targetUserId = targetUserId;
		this.targetFbId = targetFbId;
		this.imageUrl = "http://" + imageUrl;
		this.title = title;
		this.caption = caption;
		this.picId = picId;
		this.views = views;
	}
	
	public Picture(String imageUrl, int picId) {
		this.posterUserId = "";
		this.posterFbId = "";
		this.targetUserId = "";
		this.targetFbId = "";
		this.imageUrl = "http://" + imageUrl;
		this.title = "";
		this.caption = "";
		this.picId = picId;
		this.views = 0;
	}
	
	public String getPosterUserId() { return posterUserId; }
	public String getPosterFbId() { return posterFbId; }
	public String getTargetUserId() { return targetUserId; }
	public String getTargetFbId() { return targetFbId; }
	public String getImageUrl() { return imageUrl; }
	public String getTitle() { return title; }
	public String getCaption() { return caption; }
	public int getPicId() { return picId; }
	public int getViews() { return views; }
	
}
