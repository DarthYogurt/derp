package com.walintukai.derpteam;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Preferences {
	
	private static final String FILENAME_PREF = "preferences";
	private static final String KEY_FB_USER_ID = "fbId";
	private static final String KEY_FB_NAME = "fbName";
	private static final String KEY_FB_FIRST_NAME = "fbFirstName";
	private static final String KEY_USER_ID = "userId";
	
	private SharedPreferences pref;
	private Editor prefEditor;
	
	public Preferences(Context context) {
		this.pref = context.getSharedPreferences(FILENAME_PREF, Context.MODE_PRIVATE);
		this.prefEditor = pref.edit();
	}
	
	public void setFbUserId(String s) {
		prefEditor.putString(KEY_FB_USER_ID, s).commit();
		Log.i("PREF: SET FB USER ID", s);
	}
	
	public String getFbUserId() {
		return pref.getString(KEY_FB_USER_ID, "");
	}
	
	public void setFbName(String s) {
		prefEditor.putString(KEY_FB_NAME, s).commit();
		Log.i("PREF: SET FB NAME", s);
	}
	
	public String getFbName() {
		return pref.getString(KEY_FB_NAME, "");
	}
	
	public void setFbFirstName(String s) {
		prefEditor.putString(KEY_FB_FIRST_NAME, s).commit();
		Log.i("PREF: SET FB FIRST NAME", s);
	}
	
	public String getFbFirstName() {
		return pref.getString(KEY_FB_FIRST_NAME, "");
	}
	
	public void setUserId(String s) {
		prefEditor.putString(KEY_USER_ID, s).commit();
		Log.i("PREF: SET USER ID", s);
	}
	
	public String getUserId() {
		return pref.getString(KEY_USER_ID, "");
	}
	
}