package com.walintukai.derpteam;

import java.io.Serializable;

public class Friend implements Serializable {

	private static final long serialVersionUID = 6367256067569323807L;
	
	private String fbId;
	private String fbName;
	private String fbFirstName;
	private String fbPicUrl;
	
	public Friend(String fbId, String fbName, String fbFirstName) {
		this.fbId = fbId;
		this.fbName = fbName;
		this.fbFirstName = fbFirstName;
		this.fbPicUrl = "http://graph.facebook.com/" + fbId + "/picture";
	}
	
	public String getFbId() { return fbId; }
	public String getFbName() { return fbName; }
	public String getFbFirstName() { return fbFirstName; }
	public String getFbPicUrl() { return fbPicUrl; }
}
