package com.walintukai.derpteam;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Preferences {
	
	private static final String FILENAME_PREF = "preferences";
	private static final String KEY_FB_USER_ID = "fbUserId";
	private static final String KEY_FB_USER_NAME = "fbUserName";
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
	
	public void setFbUserName(String s) {
		prefEditor.putString(KEY_FB_USER_NAME, s).commit();
		Log.i("PREF: SET FB USER NAME", s);
	}
	
	public String getFbUserName() {
		return pref.getString(KEY_FB_USER_NAME, "");
	}
	
	public void setUserId(int i) {
		prefEditor.putInt(KEY_USER_ID, i).commit();
		Log.i("PREF: SET FB USER ID", Integer.toString(i));
	}
	
	public int getUserId() {
		return pref.getInt(KEY_USER_ID, -1);
	}
	
}