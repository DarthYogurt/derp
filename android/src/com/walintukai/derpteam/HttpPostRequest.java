package com.walintukai.derpteam;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

public class HttpPostRequest {
	public static final String BASE_URL ="http://dev.darthyogurt.com:8001";
	public static final String LOGIN_URL = BASE_URL + "/login/";
	public static final String ACTIVE_FRIENDS_URL = BASE_URL + "/getFriends/";
	public static final String GET_PIC_URL = BASE_URL + "/getPic/";
	public static final String GET_NOTIFICATION = BASE_URL + "/getNotification/";
	public static final String UPLOAD_PIC_URL = BASE_URL + "/uploadPic/";
	public static final String VOTE_URL = BASE_URL + "/vote/";
	public static final String COMMENT_URL = BASE_URL + "/addComment/";
	private static final String ERROR_URL = BASE_URL + "/uploadError/";
	private static final String ERROR_FILENAME = "error.txt";
	private static final int HTTP_RESPONSE_SUCCESS = 200;
	
	private Context context;
	private HttpClient client;
	private HttpPost post;
	private MultipartEntityBuilder multipartEntity;
	private String responseBody;
	
	public HttpPostRequest(Context context) {
		this.context = context;
	}
	
	@SuppressWarnings("deprecation")
	public void createPost(String url) {
		client = new DefaultHttpClient();
		post = new HttpPost(url);
		post.setHeader("enctype", "multipart/form-data");

		multipartEntity = MultipartEntityBuilder.create();
		multipartEntity.setCharset(Charset.forName("UTF-8"));
		multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	}
	
	public void sendPost() {
		post.setEntity(multipartEntity.build());
		
		try {
			HttpResponse response = client.execute(post);
			
			int responseCode = response.getStatusLine().getStatusCode();
			Log.v("POST RESPONSE CODE", Integer.toString(responseCode));
			
			responseBody = EntityUtils.toString(response.getEntity());
			Log.v("POST RESPONSE BODY", responseBody);

			if (responseCode == HTTP_RESPONSE_SUCCESS) { 
				Log.i("POST TO SERVER", "SUCCESS"); 
			}
	    	else { 
	    		Log.e("POST TO SERVER", "ERROR"); 
	    		sendErrorPost();
	    	}
		} 
		catch (ClientProtocolException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public String sendPostReturnJson() {
		post.setEntity(multipartEntity.build());
		
		try {
			HttpResponse response = client.execute(post);
			
			int responseCode = response.getStatusLine().getStatusCode();
			Log.v("POST RESPONSE CODE", Integer.toString(responseCode));
			
			responseBody = EntityUtils.toString(response.getEntity());
			Log.v("POST RESPONSE JSON", responseBody);

			if (responseCode == HTTP_RESPONSE_SUCCESS) { 
				Log.i("POST TO SERVER", "SUCCESS"); 
			}
	    	else { 
	    		Log.e("POST TO SERVER", "ERROR"); 
	    		sendErrorPost();
	    	}
			return responseBody;
		} 
		catch (ClientProtocolException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
		return "";
	}
	
	public void addJSON(String filename) {
		File json = new File(context.getFilesDir() + File.separator + filename);
		multipartEntity.addPart("data", new FileBody(json));
	}
	
	public void addPicture(String filename) {
		File file = new File(context.getExternalFilesDir(null) + File.separator + filename);
		multipartEntity.addPart("image", new FileBody(file));
	}
	
	@SuppressWarnings("deprecation")
	private void sendErrorPost() {
		File errorFile = new File(context.getFilesDir() + File.separator + ERROR_FILENAME);
		
		// Writes error from regular post to text file
		try {
			FileWriter fw = new FileWriter(errorFile);
			fw.write(responseBody);
			fw.close();
		} catch (IOException e) { e.printStackTrace(); }
		
		HttpClient errorClient = new DefaultHttpClient();
		HttpPost errorPost = new HttpPost(ERROR_URL);
		errorPost.setHeader("enctype", "multipart/form-data");

		MultipartEntityBuilder errorEntity = MultipartEntityBuilder.create();
		errorEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		errorEntity.addPart("error", new FileBody(errorFile));
		errorPost.setEntity(errorEntity.build());
		
		try { 
			HttpResponse errorResponse = errorClient.execute(errorPost);
			
			int errorResponseCode = errorResponse.getStatusLine().getStatusCode();
			Log.e("ERROR RESPONSE CODE", Integer.toString(errorResponseCode));
			
			String errorResponseBody = EntityUtils.toString(errorResponse.getEntity());
			Log.e("ERROR RESPONSE BODY", errorResponseBody);
		} 
		catch (ClientProtocolException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
	}

}
