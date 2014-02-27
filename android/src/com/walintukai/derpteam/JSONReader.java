package com.walintukai.derpteam;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
	
	public Picture getPictureObject(String jsonString) {
		try {
			JSONObject jObject = new JSONObject(jsonString);
			
			String posterUserId = jObject.getString(KEY_POSTER_USER_ID);
			String posterFbId = jObject.getString(KEY_POSTER_FB_ID);
	    	String targetUserId = jObject.getString(KEY_TARGET_USER_ID);
	    	String targetFbId = jObject.getString(KEY_TARGET_FB_ID);
	    	String imageUrl = jObject.getString(KEY_IMAGE_URL);
	    	String title = jObject.getString(KEY_TITLE);
	    	String caption = jObject.getString(KEY_CAPTION);
	    	int picId = jObject.getInt(KEY_PIC_ID);
	    	int views = jObject.getInt(KEY_VIEWS);
		
	    	Picture picture = new Picture(posterUserId, posterFbId, targetUserId, targetFbId, 
	    			imageUrl, title, caption, picId, views);
	    	
	    	return picture;
		} 
		catch (JSONException e) { e.printStackTrace(); }
		return null;
	}
	
	public List<Picture> getPicturesArray(String jsonString) {
		List<Picture> picturesArray = new ArrayList<Picture>();
		
		try {
            JSONObject jObject = new JSONObject(jsonString);
            JSONArray jArray = jObject.getJSONArray("gallery");
            
            for (int i = 0; i < jArray.length(); i++) {
            	String imageUrl = jArray.getJSONObject(i).getString(KEY_IMAGE_URL);
            	int picId = jArray.getJSONObject(i).getInt(KEY_PIC_ID);
                
                Picture picture = new Picture(imageUrl, picId);
                picturesArray.add(picture);
            }
        } 
		catch (Exception e) { e.printStackTrace(); }
		return picturesArray;
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
	
//	public String readFromInternal(String filename) throws IOException {
//		BufferedReader br = null;
//		String jsonString = "";
//		
//		try {
//			FileInputStream fis = context.openFileInput(filename);
//			InputStreamReader isr = new InputStreamReader(fis);
//			br = new BufferedReader(isr);
//			jsonString = br.readLine();
//			
//			Log.i("JSON STRING", jsonString);
//		} 
//		catch (FileNotFoundException e) { e.printStackTrace(); } 
//		finally {
//			try { if (br != null) { br.close(); } } 
//			catch (IOException e) { e.printStackTrace(); }
//		}
//		return jsonString;
//	}

}
