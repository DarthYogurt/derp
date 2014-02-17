package com.walintukai.derpteam;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preferences {
	
	private static final String FILENAME_PREF = "preferences";
	private static final String KEY_ACCESS_TOKEN = "accessToken";
	private static final String KEY_TOKEN_EXPIRATION = "accessExpires";
	
	private SharedPreferences pref;
	private Editor prefEditor;
	
	public Preferences(Context context) {
		this.pref = context.getSharedPreferences(FILENAME_PREF, Context.MODE_PRIVATE);
		this.prefEditor = pref.edit();
	}
	
	public void setAccessToken(String s) {
		prefEditor.putString(KEY_ACCESS_TOKEN, s).commit();
	}
	
	public String getAccessToken() {
		return pref.getString(KEY_ACCESS_TOKEN, "");
	}
	
	public void setTokenExpiration(long l) {
		prefEditor.putLong(KEY_TOKEN_EXPIRATION, l).commit();
	}
	
	public long getTokenExpiration() {
		return pref.getLong(KEY_TOKEN_EXPIRATION, -1);
	}
	
}