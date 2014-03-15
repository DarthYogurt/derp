package com.walintukai.derpteam;

public class Notification {

	private String type;
	private String text;
	private int picId;
	
	public Notification(String type, String text) {
		this.type = type;
		this.text = text;
	}
	
	public Notification(String type, String text, int picId) {
		this.type = type;
		this.text = text;
		this.picId = picId;
	}

	public String getType() { return type; }
	public String getText() { return text; }
	public int getPicId() { return picId; }
	
}
