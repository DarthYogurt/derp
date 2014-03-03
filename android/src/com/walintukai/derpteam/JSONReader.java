package com.walintukai.derpteam;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

import android.content.Context;
import android.util.Log;

public class JSONReader {
	
	private static final String KEY_POSTER_USER_ID = "posterUserId";
	private static final String KEY_POSTER_FB_ID = "posterFbId";
	private static final String KEY_TARGET_USER_ID = "targetUserId";
	private static final String KEY_TARGET_FB_ID = "targetFbId";
	private static final String KEY_IMAGE_URL = "imageUrl";
	private static final String KEY_TITLE = "title";
	private static final String KEY_CAPTION = "caption";
	private static final String KEY_PIC_ID = "picId";
	private static final String KEY_VIEWS = "views";
	private static final String KEY_UP_VOTE = "upVote";
	private static final String KEY_DOWN_VOTE = "downVote";
	private static final String KEY_TOTAL_PAGES = "totalPages";
	private static final String KEY_COMMENT = "comment";

	private Context context;
	
	public JSONReader(Context context) {
		this.context = context;
	}
	
	public Member getMemberObject(String jsonString) {
		try {
			JSONObject jObject = new JSONObject(jsonString);
			
			String posterFbId = jObject.getString(KEY_POSTER_FB_ID);
	    	String targetFbId = jObject.getString(KEY_TARGET_FB_ID);
	    	String imageUrl = jObject.getString(KEY_IMAGE_URL);
	    	String title = jObject.getString(KEY_TITLE);
	    	String caption = jObject.getString(KEY_CAPTION);
	    	int picId = jObject.getInt(KEY_PIC_ID);
	    	int views = jObject.getInt(KEY_VIEWS);
	    	int upVote = jObject.getInt(KEY_UP_VOTE);
	    	int downVote = jObject.getInt(KEY_DOWN_VOTE);
		
	    	Member member = new Member(posterFbId, targetFbId, imageUrl, title, caption, picId, 
	    							   views, upVote, downVote);
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
            
            final String targetFbId = jObject.getString(KEY_TARGET_FB_ID);
            
            for (int i = 0; i < jArray.length(); i++) {
    			final String posterFbId = jArray.getJSONObject(i).getString(KEY_POSTER_FB_ID);
    	    	final String imageUrl = jArray.getJSONObject(i).getString(KEY_IMAGE_URL);
    	    	final String title = jArray.getJSONObject(i).getString(KEY_TITLE);
    	    	final String caption = jArray.getJSONObject(i).getString(KEY_CAPTION);
    	    	final int picId = jArray.getJSONObject(i).getInt(KEY_PIC_ID);
    	    	final int views = jArray.getJSONObject(i).getInt(KEY_VIEWS);
    	    	final int upVote = jArray.getJSONObject(i).getInt(KEY_UP_VOTE);
    	    	final int downVote = jArray.getJSONObject(i).getInt(KEY_DOWN_VOTE);
    	    	
    	    	String graphPath = "/" + posterFbId + "/";
    			new Request(Session.getActiveSession(), graphPath, null, HttpMethod.GET, new Request.Callback() {
    				public void onCompleted(Response response) {
    					String firstName = "";
    					try {
    						JSONObject jObject = new JSONObject(response.getGraphObject().getInnerJSONObject().toString());
    						firstName = jObject.getString("first_name");
    					} 
    					catch (JSONException e) { e.printStackTrace(); }
    					
    					Member member = new Member(posterFbId, firstName, targetFbId, imageUrl, title, 
    											   caption, picId, views, upVote, downVote);
    					teamMembersArray.add(member);
    				}
    			}).executeAndWait();
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
    			final String posterFbId = jArray.getJSONObject(i).getString(KEY_POSTER_FB_ID);
    	    	final String comment = jArray.getJSONObject(i).getString(KEY_COMMENT);
    	    	
    	    	String graphPath = "/" + posterFbId + "/";
    			new Request(Session.getActiveSession(), graphPath, null, HttpMethod.GET, new Request.Callback() {
    				public void onCompleted(Response response) {
    					String posterFirstName = "";
    					try {
    						JSONObject jObject = new JSONObject(response.getGraphObject().getInnerJSONObject().toString());
    						posterFirstName = jObject.getString("first_name");
    					} 
    					catch (JSONException e) { e.printStackTrace(); }
    					
    					Comment c = new Comment(posterFbId, posterFirstName, comment);
    	    	    	commentsArray.add(c);
    				}
    			}).executeAndWait();
            }
        }
		catch (Exception e) { e.printStackTrace(); }
		return commentsArray;
	}

}
