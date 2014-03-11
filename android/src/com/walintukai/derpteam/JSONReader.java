package com.walintukai.derpteam;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class JSONReader {
	
	private static final String KEY_POSTER_USER_ID = "posterUserId";
	private static final String KEY_POSTER_FB_ID = "posterFbId";
	private static final String KEY_POSTER_FB_NAME = "posterFbName";
	private static final String KEY_TARGET_USER_ID = "targetUserId";
	private static final String KEY_TARGET_FB_ID = "targetFbId";
	private static final String KEY_TARGET_FB_NAME = "targetFbName";
	private static final String KEY_IMAGE_URL = "imageUrl";
	private static final String KEY_TITLE = "title";
	private static final String KEY_CAPTION = "caption";
	private static final String KEY_PIC_ID = "picId";
	private static final String KEY_VIEWS = "views";
	private static final String KEY_UP_VOTE = "upVote";
	private static final String KEY_DOWN_VOTE = "downVote";
	private static final String KEY_USER_VOTED = "userVotedUp";
	private static final String KEY_TOTAL_PAGES = "totalPages";
	private static final String KEY_COMMENT = "comment";
	private static final String KEY_PIC_CAPTION = "picCaption";

	private Context context;
	
	public JSONReader(Context context) {
		this.context = context;
	}
	
	public String[] getActiveFriendsArray(String jsonString) {
		try {
			JSONObject jObject = new JSONObject(jsonString);
			JSONArray jArray = jObject.getJSONArray("activeFriends");
			
			int length = jArray.length();
			String[] activeFriends = new String[length];
			if (length > 0) {
				for (int i = 0; i < length; i++) { 
					activeFriends[i] = jArray.getString(i); 
					Log.v("ACTIVE FRIEND", activeFriends[i]);
				}
			}
			return activeFriends;
		} 
		catch (JSONException e) { e.printStackTrace(); }
		return null;
	}
	
	public Member getMemberObject(String jsonString) {
		try {
			JSONObject jObject = new JSONObject(jsonString);
			
			String posterFbId = jObject.getString(KEY_POSTER_FB_ID);
	    	String targetFbId = jObject.getString(KEY_TARGET_FB_ID);
	    	String targetFbName = jObject.getString(KEY_TARGET_FB_NAME);
	    	String imageUrl = jObject.getString(KEY_IMAGE_URL);
	    	String title = jObject.getString(KEY_TITLE);
	    	String caption = jObject.getString(KEY_CAPTION);
	    	int picId = jObject.getInt(KEY_PIC_ID);
	    	int views = jObject.getInt(KEY_VIEWS);
	    	int upVote = jObject.getInt(KEY_UP_VOTE);
	    	int downVote = jObject.getInt(KEY_DOWN_VOTE);
	    	String userVoted = jObject.getString(KEY_USER_VOTED);
	    	
	    	String targetFirstName = targetFbName.substring(0, targetFbName.indexOf(" "));
		
	    	Member member = new Member(posterFbId, targetFbId, targetFirstName, imageUrl, title, caption, picId, 
	    							   views, upVote, downVote, userVoted);
	    	return member;
		} 
		catch (JSONException e) { e.printStackTrace(); }
		return null;
	}
	
	public List<Member> getMembersArray(String jsonString) {
		List<Member> membersArray = new ArrayList<Member>();
		
		try {
            JSONObject jObject = new JSONObject(jsonString);
            JSONArray jArray = jObject.getJSONArray("gallery");
            
            for (int i = 0; i < jArray.length(); i++) {
            	String imageUrl = jArray.getJSONObject(i).getString(KEY_IMAGE_URL);
            	int picId = jArray.getJSONObject(i).getInt(KEY_PIC_ID);
                
                Member member = new Member(imageUrl, picId);
                membersArray.add(member);
            }
        } 
		catch (Exception e) { e.printStackTrace(); }
		return membersArray;
	}
	
	public int getGalleryTotalPages(String jsonString) {
		int totalPages = 0;
		try {
            JSONObject jObject = new JSONObject(jsonString);
            totalPages = jObject.getInt(KEY_TOTAL_PAGES);
        } 
		catch (Exception e) { e.printStackTrace(); }
		return totalPages;
	}
	
	public List<Member> getTeamMembersArray(String jsonString) {
		final List<Member> teamMembersArray = new ArrayList<Member>();
		try {
            JSONObject jObject = new JSONObject(jsonString);
            JSONArray jArray = jObject.getJSONArray("teamGallery");
            
            String targetFbId = jObject.getString(KEY_TARGET_FB_ID);
            String targetFbName = jObject.getString(KEY_TARGET_FB_NAME);
            String targetFirstName = targetFbName.substring(0, targetFbName.indexOf(" "));
            
            for (int i = 0; i < jArray.length(); i++) {
    			String posterFbId = jArray.getJSONObject(i).getString(KEY_POSTER_FB_ID);
    			String posterFbName = jArray.getJSONObject(i).getString(KEY_POSTER_FB_NAME);
    	    	String imageUrl = jArray.getJSONObject(i).getString(KEY_IMAGE_URL);
    	    	String title = jArray.getJSONObject(i).getString(KEY_TITLE);
    	    	String caption = jArray.getJSONObject(i).getString(KEY_CAPTION);
    	    	int picId = jArray.getJSONObject(i).getInt(KEY_PIC_ID);
    	    	int views = jArray.getJSONObject(i).getInt(KEY_VIEWS);
    	    	int upVote = jArray.getJSONObject(i).getInt(KEY_UP_VOTE);
    	    	int downVote = jArray.getJSONObject(i).getInt(KEY_DOWN_VOTE);
    	    	
    	    	String posterFirstName = posterFbName.substring(0, posterFbName.indexOf(" "));
    	    	
    	    	List<Comment> commentsArray = new ArrayList<Comment>();
    	    	JSONArray jArrayComment = jArray.getJSONObject(i).getJSONArray("comments");
    	    	for (int c = 0; c < jArrayComment.length(); c++) {
    	    		String commentFbId = jArrayComment.getJSONObject(c).getString(KEY_POSTER_FB_ID);
    	    		String commentFbName = jArrayComment.getJSONObject(c).getString(KEY_POSTER_FB_NAME);
        	    	String commentString = jArrayComment.getJSONObject(c).getString(KEY_COMMENT);
        	    	
        	    	String commentFirstName = commentFbName.substring(0, commentFbName.indexOf(" "));
        	    	
        	    	Comment comment = new Comment(commentFbId, commentFirstName, commentString);
	    	    	commentsArray.add(comment);
    	    	}
    	    	
    	    	Member member = new Member(posterFbId, posterFirstName, targetFbId, targetFirstName, imageUrl, 
    	    							   title, caption, picId, views, upVote, downVote, commentsArray);
    	    	teamMembersArray.add(member);
            }
        } 
		catch (Exception e) { e.printStackTrace(); }
		return teamMembersArray;
	}
	
	public List<Comment> getCommentsArray(String jsonString) {
		final List<Comment> commentsArray = new ArrayList<Comment>();
		try {
            JSONObject jObject = new JSONObject(jsonString);
            JSONArray jArray = jObject.getJSONArray("comments");
            
            for (int i = 0; i < jArray.length(); i++) {
    			String posterFbId = jArray.getJSONObject(i).getString(KEY_POSTER_FB_ID);
    			String posterFbName = jArray.getJSONObject(i).getString(KEY_POSTER_FB_NAME);
    	    	String comment = jArray.getJSONObject(i).getString(KEY_COMMENT);
    	    	
    	    	String posterFirstName = posterFbName.substring(0, posterFbName.indexOf(" "));
    	    	
    	    	Comment c = new Comment(posterFbId, posterFirstName, comment);
    	    	commentsArray.add(c);
            }
        }
		catch (Exception e) { e.printStackTrace(); }
		return commentsArray;
	}
	
	public List<Member> getStatsArray(String jsonString) {
		List<Member> statsArray = new ArrayList<Member>();
		try {
            JSONObject jObject = new JSONObject(jsonString);
            JSONArray jArray = jObject.getJSONArray("postedPics");
            
            for (int i = 0; i < jArray.length(); i++) {
    			String targetFbId = jArray.getJSONObject(i).getString(KEY_TARGET_FB_ID);
    			String imageUrl = jArray.getJSONObject(i).getString(KEY_IMAGE_URL);
    	    	String title = jArray.getJSONObject(i).getString(KEY_TITLE);
    	    	String caption = jArray.getJSONObject(i).getString(KEY_CAPTION);
    	    	int picId = jArray.getJSONObject(i).getInt(KEY_PIC_ID);
    	    	int upVote = jArray.getJSONObject(i).getInt(KEY_UP_VOTE);
    	    	int downVote = jArray.getJSONObject(i).getInt(KEY_DOWN_VOTE);
    	    	
    	    	Member member = new Member(targetFbId, imageUrl, title, caption, picId, upVote, downVote);
    	    	statsArray.add(member);
            }
        }
		catch (Exception e) { e.printStackTrace(); }
		return statsArray;
	}
	
	public Notification getNotificationObject(String jsonString) {
		try {
			JSONObject jObject = new JSONObject(jsonString);
			
			String posterFbName = jObject.getString(KEY_POSTER_FB_NAME);
	    	String targetFbName = jObject.getString(KEY_TARGET_FB_NAME);
	    	int picId = jObject.getInt(KEY_PIC_ID);
	    	String picCaption = jObject.getString(KEY_PIC_CAPTION);
		
	    	Notification notification = new Notification(posterFbName, targetFbName, picId, picCaption);
	    	return notification;
		} 
		catch (JSONException e) { e.printStackTrace(); }
		return null;
	}
	
	public String getImageUrlForFbPost(String jsonString) {
		try {
			JSONObject jObject = new JSONObject(jsonString);
			String imageUrl = jObject.getString(KEY_IMAGE_URL);
	    	return "http://dev.darthyogurt.com:8001" + imageUrl;
		} 
		catch (JSONException e) { e.printStackTrace(); }
		return "";
	}
	
	public String getLinkUrlForFbPost(String jsonString) {
		try {
			JSONObject jObject = new JSONObject(jsonString);
			String picId = jObject.getString(KEY_PIC_ID);
	    	return "http://dev.darthyogurt.com:8001/external/" + picId;
		} 
		catch (JSONException e) { e.printStackTrace(); }
		return "";
	}

}
