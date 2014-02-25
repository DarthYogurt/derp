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
	
	public String getString(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		String string = "";
		
		try { 
			HttpResponse response = client.execute(get);
			string = EntityUtils.toString(response.getEntity());
			
			int statusCode = response.getStatusLine().getStatusCode();
			Log.i("HTTP GET STATUS CODE", Integer.toString(statusCode));
		} 
		catch (ClientProtocolException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
		return string;
	}
	
	public String getUserId(String fbId) {
		String url = BASE_URL + GET_ID_URL + fbId;
		return getString(url);
	}
	
	public String getImageJsonString(int id) {
		String url = BASE_URL + GET_PIC_URL + Integer.toString(id);
		return getString(url);
	}
	
}
