package com.walintukai.derpteam;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpGetRequest {
	
	private static final String BASE_URL = "http://dev.darthyogurt.com:8001/";
	private static final String GET_ID_URL = "getUserId/";
	private static final String GET_PIC_URL = "getPic/";
	private static final String GET_GALLERY_URL = "gallery/";
	private static final String GET_TEAM_URL = "getTeamGallery/";
	
	public String getString(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		String string = "";
		
		try { 
			HttpResponse response = client.execute(get);
			string = EntityUtils.toString(response.getEntity());
			
			int statusCode = response.getStatusLine().getStatusCode();
			Log.i("HTTP GET STATUS CODE", Integer.toString(statusCode));
			
			return string;
		} 
		catch (ClientProtocolException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
		return "";
	}
	
	public String getUserId(String fbId) {
		String url = BASE_URL + GET_ID_URL + fbId;
		return getString(url);
	}
	
	public String getMemberJsonString(int picId) {
		String url = BASE_URL + GET_PIC_URL + Integer.toString(picId);
		String jsonString = getString(url);
		Log.v("MEMBER JSON", jsonString);
		return jsonString;
	}
	
	public String getGalleryJsonString(int picsPerPage, int pageNum) {
		String url = BASE_URL + GET_GALLERY_URL + Integer.toString(picsPerPage) + "/" + Integer.toString(pageNum) + "/";
		String jsonString = getString(url);
		Log.v("GALLERY JSON", jsonString);
		return jsonString;
	}
	
	public String getTeamJsonString(String fbId) {
		String url = BASE_URL + GET_TEAM_URL + fbId;
		String jsonString = getString(url);
		Log.v("TEAM JSON", jsonString);
		return jsonString;
	}
	
}
