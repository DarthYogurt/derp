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
	private static final String KEY_TARGET_USER_ID = "targetUserId";
	private static final String KEY_TARGET_FB_ID = "targetFbId";
	private static final String KEY_IMAGE_URL = "imageUrl";
	private static final String KEY_TITLE = "title";
	private static final String KEY_CAPTION = "caption";
	private static final String KEY_PIC_ID = "picId";
	private static final String KEY_VIEWS = "views";
	private static final String KEY_TOTAL_PAGES = "totalPages";

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
		
	    	Member member = new Member(posterFbId, targetFbId, imageUrl, title, caption, picId, views);
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
		List<Member> teamMembersArray = new ArrayList<Member>();
		
		try {
            JSONObject jObject = new JSONObject(jsonString);
            JSONArray jArray = jObject.getJSONArray("teamGallery");
            
            String targetFbId = jObject.getString(KEY_TARGET_FB_ID);
            
            for (int i = 0; i < jArray.length(); i++) {
    			String posterFbId = jArray.getJSONObject(i).getString(KEY_POSTER_FB_ID);
    	    	String imageUrl = jArray.getJSONObject(i).getString(KEY_IMAGE_URL);
    	    	String title = jArray.getJSONObject(i).getString(KEY_TITLE);
    	    	String caption = jArray.getJSONObject(i).getString(KEY_CAPTION);
    	    	int picId = jArray.getJSONObject(i).getInt(KEY_PIC_ID);
    	    	int views = jArray.getJSONObject(i).getInt(KEY_VIEWS);
                
    	    	Member member = new Member(posterFbId, targetFbId, imageUrl, title, caption, picId, views);
    	    	teamMembersArray.add(member);
            }
        } 
		catch (Exception e) { e.printStackTrace(); }
		return teamMembersArray;
	}

}
