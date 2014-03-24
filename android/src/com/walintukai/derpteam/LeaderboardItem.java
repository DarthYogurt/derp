package com.walintukai.derpteam;

public class LeaderboardItem {
	
	private String fbId;
	private String fbName;
	private int points;
	private String picUrl1;
	private String picUrl2;
	private String picUrl3;
	
	public LeaderboardItem(String fbId, String fbName, int points, String picUrl1, String picUrl2, String picUrl3) {
		this.fbId = fbId;
		this.fbName = fbName;
		this.points = points;
		this.picUrl1 = "http://dev.darthyogurt.com:8001" + picUrl1;
		this.picUrl2 = "http://dev.darthyogurt.com:8001" + picUrl2;
		this.picUrl3 = "http://dev.darthyogurt.com:8001" + picUrl3;
	}
	
	public LeaderboardItem(String fbId, String fbName, int points) {
		this.fbId = fbId;
		this.fbName = fbName;
		this.points = points;
	}
	
	public String getFbId() { return fbId; }
	public String getFbName() { return fbName; }
	public int getPoints() { return points; }
	public String getPicUrl1() { return picUrl1; }
	public String getPicUrl2() { return picUrl2; }
	public String getPicUrl3() { return picUrl3; }
	
}
