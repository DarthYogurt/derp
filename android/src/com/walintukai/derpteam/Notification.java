package com.walintukai.derpteam;

public class Notification {

	private String posterFbName;
	private String targetFbName;
	private int picId;
	private String picCaption;
	
	public Notification(String posterFbName, String targetFbName, int picId, String picCaption) {
		this.posterFbName = posterFbName;
		this.targetFbName = targetFbName;
		this.picId = picId;
		this.picCaption = picCaption;
	}
	
	public String getPosterFbName() { return posterFbName; }
	public String getTargetFbName() { return targetFbName; }
	public int getPicId() { return picId; }
	public String getPicCaption() { return picCaption; }
	
}
