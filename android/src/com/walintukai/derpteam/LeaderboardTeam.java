package com.walintukai.derpteam;

public class LeaderboardTeam {
	
	private String fbId;
	private String firstName;
	private int upVote;
	private String picUrl1;
	private String picUrl2;
	private String picUrl3;
	
	public LeaderboardTeam(String fbId, String firstName, int upVote, String picUrl1, String picUrl2, String picUrl3) {
		this.fbId = fbId;
		this.firstName = firstName;
		this.upVote = upVote;
		this.picUrl1 = "http://dev.darthyogurt.com:8001" + picUrl1;
		this.picUrl2 = "http://dev.darthyogurt.com:8001" + picUrl2;
		this.picUrl3 = "http://dev.darthyogurt.com:8001" + picUrl3;
	}
	
	public String getFbId() { return fbId; }
	public String getFirstName() { return firstName; }
	public int getUpVote() { return upVote; }
	public String getPicUrl1() { return picUrl1; }
	public String getPicUrl2() { return picUrl2; }
	public String getPicUrl3() { return picUrl3; }
	
}
