package com.walintukai.derpteam;

import android.content.Context;
import android.util.Log;

import java.io.*;
import java.util.List;

import com.facebook.model.GraphUser;
import com.google.gson.stream.JsonWriter;

public class JSONWriter {

	private static final String KEY_FB_USER_ID = "fbUserId";
	private static final String KEY_FB_USER_NAME = "fbUserName";
	private static final String KEY_FB_FRIENDS = "fbFriends";
	private static final String KEY_FB_ID = "fbId";
	private static final String KEY_FB_NAME = "fbName";
	
	private Context context;
	private FileOutputStream fos;
	private JsonWriter writer;
	private Preferences prefs;
	
	public JSONWriter(Context context) {
		this.context = context;
		this.prefs = new Preferences(context);
	}
	
	public String updateFriendsList(List<GraphUser> fbFriends) {	
		String filename = "fbId_" + prefs.getFbUserId() + "_friends" + ".json";
		
		try {
			fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			
			writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
			writer.beginObject();
			writer.name(KEY_FB_USER_ID).value(prefs.getFbUserId());
			writer.name(KEY_FB_USER_NAME).value(prefs.getFbUserName());
			writer.name(KEY_FB_FRIENDS);
			writer.beginArray();
			
			for (int i = 0; i < fbFriends.size(); i++) {
				GraphUser friend = fbFriends.get(i);
				
				writer.beginObject();
				writer.name(KEY_FB_ID).value(Long.parseLong(friend.getId()));
				writer.name(KEY_FB_NAME).value(friend.getName());
				writer.endObject();
				Log.v("NAME", friend.getName());
			}
			
			writer.endArray();
			writer.endObject();
			writer.close();
			fos.close();
			
			Log.i("FRIENDS JSON UPDATED", filename);
		} 
		catch (IOException e) { e.printStackTrace(); }
		
		return filename;
	}

	
//	public void writeToInternal(String filename, String data) throws IOException {
//		try {
//			fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
//			fos.write(data.getBytes());
//			fos.close();
//			Log.i("FILE SAVED", filename);
//		} 
//		catch (IOException e) { e.printStackTrace(); } 
//	}
	
//	public void writeStepImage(Step step) throws IOException {
//		try {
//			writer.beginObject();
//			writer.name(KEY_STEP_ID).value(step.getId());
//			writer.name(KEY_STEP_TYPE).value(TYPE_IMAGE);
//			writer.name(KEY_VALUE).value(step.getImageFilename());
//			if (!step.getExtraNote().isEmpty()) { writer.name(KEY_EXTRA_NOTE).value(step.getExtraNote()); }
//			if (!step.getExtraImageFilename().isEmpty()) { writer.name(KEY_EXTRA_IMAGE).value(step.getExtraImageFilename()); }
//			writer.name(KEY_TIME_STARTED).value(step.getTimeStarted());
//			writer.name(KEY_TIME_FINISHED).value(step.getTimeFinished());
//			writer.endObject();
//		} catch (IOException e) { e.printStackTrace(); }
//	}
	
	public void logPost(String filename) {
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
		
		Log.v("POST TO SERVER", fileContent.toString());
	}
	
}
