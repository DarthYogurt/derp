package com.walintukai.derpteam;

import android.content.Context;
import android.util.Log;

import java.io.*;
import java.util.List;

import com.facebook.model.GraphUser;
import com.google.gson.stream.JsonWriter;

public class JSONWriter {

	public static final String FILENAME_FRIENDS_LIST = "friends.json";
	public static final String FILENAME_ASSIGN_TEAM = "assign_team.json";
	public static final String FILENAME_PIC_VOTE = "vote.json";
	public static final String FILENAME_COMMENT = "comment.json";
	private static final String KEY_FB_USER_ID = "fbUserId";
	private static final String KEY_FB_USER_NAME = "fbUserName";
	private static final String KEY_FB_FRIENDS = "fbFriends";
	private static final String KEY_FB_ID = "fbId";
	private static final String KEY_FB_NAME = "fbName";
	private static final String KEY_USER_ID = "userId";
	private static final String KEY_IMAGE = "image";
	private static final String KEY_TITLE = "title";
	private static final String KEY_CAPTION = "caption";
	private static final String KEY_TARGET_FB_ID = "targetFbId";
	private static final String KEY_TARGET_USER_ID = "targetUserId";
	private static final String KEY_POSTER_FB_ID = "posterFbId";
	private static final String KEY_PIC_ID = "picId";
	private static final String KEY_UP_VOTE = "upVote";
	private static final String KEY_DOWN_VOTE = "downVote";
	private static final String KEY_COMMENT = "comment";
	
	private Context context;
	private FileOutputStream fos;
	private JsonWriter writer;
	private Preferences prefs;
	
	public JSONWriter(Context context) {
		this.context = context;
		this.prefs = new Preferences(context);
	}
	
	public void updateFriendsList(List<GraphUser> fbFriends) {	
		try {
			fos = context.openFileOutput(FILENAME_FRIENDS_LIST, Context.MODE_PRIVATE);
			
			writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
			writer.beginObject();
			writer.name(KEY_FB_USER_ID).value(prefs.getFbUserId());
			writer.name(KEY_FB_USER_NAME).value(prefs.getFbName());
			writer.name(KEY_FB_FRIENDS);
			writer.beginArray();
			
			for (int i = 0; i < fbFriends.size(); i++) {
				GraphUser friend = fbFriends.get(i);
				
				writer.beginObject();
				writer.name(KEY_FB_ID).value(friend.getId());
				writer.name(KEY_FB_NAME).value(friend.getName());
				writer.endObject();
			}
			
			writer.endArray();
			writer.endObject();
			writer.close();
			fos.close();
			
			Log.i("FRIENDS JSON UPDATED", FILENAME_FRIENDS_LIST);
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public void createJsonForMember(String imgFilename, String title, String caption, 
								   String targetFbId, String targetUserId) {
		try {
			fos = context.openFileOutput(FILENAME_ASSIGN_TEAM, Context.MODE_PRIVATE);
			
			writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
			writer.beginObject();
			writer.name(KEY_FB_USER_ID).value(prefs.getFbUserId());
			writer.name(KEY_USER_ID).value(prefs.getUserId());
			writer.name(KEY_IMAGE).value(imgFilename);
			writer.name(KEY_TITLE).value(title);
			writer.name(KEY_CAPTION).value(caption);
			writer.name(KEY_TARGET_FB_ID).value(targetFbId);
			writer.name(KEY_TARGET_USER_ID).value(targetUserId);
			writer.endObject();
			writer.close();
			fos.close();
			
			Log.i("JSON FOR MEMBER CREATED", FILENAME_ASSIGN_TEAM);
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public void createJsonForUpVote(int picId) {
		try {
			fos = context.openFileOutput(FILENAME_PIC_VOTE, Context.MODE_PRIVATE);
			
			writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
			writer.beginObject();
			writer.name(KEY_PIC_ID).value(picId);
			writer.name(KEY_UP_VOTE).value(true);
			writer.name(KEY_DOWN_VOTE).value(false);
			writer.endObject();
			writer.close();
			fos.close();
			
			Log.i("JSON FOR UP VOTE CREATED", FILENAME_PIC_VOTE);
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public void createJsonForDownVote(int picId) {
		try {
			fos = context.openFileOutput(FILENAME_PIC_VOTE, Context.MODE_PRIVATE);
			
			writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
			writer.beginObject();
			writer.name(KEY_PIC_ID).value(picId);
			writer.name(KEY_UP_VOTE).value(false);
			writer.name(KEY_DOWN_VOTE).value(true);
			writer.endObject();
			writer.close();
			fos.close();
			
			Log.i("JSON FOR DOWN VOTE CREATED", FILENAME_PIC_VOTE);
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public void createJsonForComment(int picId, String comment) {
		try {
			fos = context.openFileOutput(FILENAME_COMMENT, Context.MODE_PRIVATE);
			
			writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
			writer.beginObject();
			writer.name(KEY_PIC_ID).value(picId);
			writer.name(KEY_POSTER_FB_ID).value(prefs.getFbUserId());
			writer.name(KEY_COMMENT).value(comment);
			writer.endObject();
			writer.close();
			fos.close();
			
			Log.i("JSON FOR COMMENT CREATED", FILENAME_COMMENT);
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public void logJson(String filename) {
		FileInputStream fis = null;
		try { fis = context.openFileInput(filename); }
		catch (FileNotFoundException e) { e.printStackTrace(); }
		StringBuffer fileContent = new StringBuffer("");

		byte[] buffer = new byte[1024];

		try {
			while (fis.read(buffer) != -1) {
			    fileContent.append(new String(buffer));
			}
		} 
		catch (IOException e) { e.printStackTrace(); }
		
		Log.v(filename, fileContent.toString());
	}
	
}
