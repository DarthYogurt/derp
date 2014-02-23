package com.walintukai.derpteam;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private static final int HTTP_RESPONSE_SUCCESS = 200;
	
	private Preferences prefs;
	public List<GraphUser> fbFriends;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		getActionBar().setTitle("");
		
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		
		prefs = new Preferences(this);
		
		getKeyHash();
	}
	
	private void goToMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	private void getKeyHash() {
		try {
	        PackageInfo info = getPackageManager().getPackageInfo("com.walintukai.derpteam", 
	        		PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
	    } 
		catch (NameNotFoundException e) { } 
		catch (NoSuchAlgorithmException e) { }
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (session.isOpened()) {
        	Request.newMeRequest(session, new Request.GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {
						prefs.setFbUserId(user.getId());
						prefs.setFbUserName(user.getName());
						Log.i("FB LOGIN", user.getName());
					}
				}
			}).executeAsync();
        	
        	requestFacebookFriends(Session.getActiveSession());
        	goToMainActivity();
        }
        else if (session.isClosed()) {
        	Log.i("FB LOGOUT", "");
        	prefs.setFbUserId("");
			prefs.setFbUserName("");
        }
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	private void requestFacebookFriends(Session session) {
		Request friendsRequest = createRequest(session);
		friendsRequest.setCallback(new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				fbFriends = getResults(response);
				
				JSONWriter writer = new JSONWriter(LoginActivity.this);
				writer.updateFriendsList(fbFriends);
				writer.logJson("friends.json");
				
				new PostToServerTask().execute();
			}
		});
		friendsRequest.executeAsync();
	}
	
	private Request createRequest(Session session) {
		Request request = Request.newGraphPathRequest(session, "me/friends", null);

		Set<String> fields = new HashSet<String>();
		String[] requiredFields = new String[] {"id", "name"};
		fields.addAll(Arrays.asList(requiredFields));

		Bundle parameters = request.getParameters();
		parameters.putString("fields", TextUtils.join(",", fields));
		request.setParameters(parameters);

		return request;
    }
	
	private List<GraphUser> getResults(Response response) {
		GraphMultiResult multiResult = response.getGraphObjectAs(GraphMultiResult.class);
		GraphObjectList<GraphObject> data = multiResult.getData();
		return data.castToListOf(GraphUser.class);
	}
	
	private class PostToServerTask extends AsyncTask<Void, Void, Void> {
		
	    protected Void doInBackground(Void... params) {
	    	HttpPostRequest post = new HttpPostRequest(LoginActivity.this);
			post.createPost();
			post.addJSON("friends.json");
			String idAsString = post.sendPost();
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	        return;
	    }
	}

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	protected void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
}
