package com.walintukai.derpteam;

public class Notification {

	private String text;
	private String posterFbName;
	private String targetFbName;
	private int picId;
	private String picCaption;
	
	public Notification(String text) {
		this.text = text;
	}

	public String getText() { return text; }
	public String getPosterFbName() { return posterFbName; }
	public String getTargetFbName() { return targetFbName; }
	public int getPicId() { return picId; }
	public String getPicCaption() { return picCaption; }
	
}
