package com.walintukai.derpteam;

import java.util.List;

import android.widget.LinearLayout;

public class Member {

	private String posterFbId; 
	private String posterFbPicUrl;
	private String posterFirstName;
	private String targetFbId;
	private String targetFirstName;
	private String targetFbPicUrl;
	private String imageUrl;
	private String title;
	private String caption;
	private int picId;
	private int views;
	private int upVote;
	private int downVote;
	private String userVoted;
	private List<Comment> comments;
	 
	// For team page
	public Member(String posterFbId, String posterFirstName, String targetFbId, String targetFirstName, 
				  String imageUrl, String title, String caption, int picId, int views, int upVote, 
				  int downVote, List<Comment> comments) {
		this.posterFbId = posterFbId;
		this.posterFbPicUrl = "http://graph.facebook.com/" + posterFbId + "/picture";
		this.posterFirstName = posterFirstName;
		this.targetFbId = targetFbId;
		this.targetFirstName = targetFirstName;
		this.imageUrl = "http://" + imageUrl;
		this.title = title;
		this.caption = caption;
		this.picId = picId;
		this.views = views;
		this.upVote = upVote;
		this.downVote = downVote;
		this.comments = comments;
	}
	
	// For member page
	public Member(String posterFbId, String targetFbId, String targetFirstName, String imageUrl, String title, 
				  String caption, int picId, int views, int upVote, int downVote, String userVoted) {
		this.posterFbId = posterFbId;
		this.posterFirstName = "";
		this.targetFbId = targetFbId;
		this.targetFirstName = targetFirstName;
		this.targetFbPicUrl = "http://graph.facebook.com/" + targetFbId + "/picture";
		this.imageUrl = "http://" + imageUrl;
		this.title = title;
		this.caption = caption;
		this.picId = picId;
		this.views = views;
		this.upVote = upVote;
		this.downVote = downVote;
		this.userVoted = userVoted;
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
	
	// For stats
	public Member(String targetFbId, String imageUrl, String title, String caption, int picId, 
				  int upVote, int downVote) {
		this.targetFbId = targetFbId;
		this.imageUrl = "http://dev.darthyogurt.com:8001" + imageUrl;
		this.title = title;
		this.caption = caption;
		this.picId = picId;
		this.upVote = upVote;
		this.downVote = downVote;
	}
	
	public String getPosterFbId() { return posterFbId; }
	public String getPosterFbPicUrl() { return posterFbPicUrl; }
	public String getPosterFirstName() { return posterFirstName; }
	public String getTargetFbId() { return targetFbId; }
	public String getTargetFirstName() { return targetFirstName; }
	public String getTargetFbPicUrl() { return targetFbPicUrl; }
	public String getImageUrl() { return imageUrl; }
	public String getTitle() { return title; }
	public String getCaption() { return caption; }
	public int getPicId() { return picId; }
	public int getViews() { return views; }
	public int getUpVote() { return upVote; }
	public int getDownVote() { return downVote; }
	public String getUserVoted() { return userVoted; }
	public List<Comment> getComments() { return comments; }
	
}
