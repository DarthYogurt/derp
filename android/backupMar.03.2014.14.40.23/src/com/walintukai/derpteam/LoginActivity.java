package com.walintukai.derpteam;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.leanplum.Leanplum;
import com.leanplum.activities.LeanplumActivity;

public class LoginActivity extends LeanplumActivity {
	
	private Preferences prefs;
	private List<Friend> fbFriends;
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        // We've inserted your API keys here for you
        if (BuildConfig.DEBUG) {
            Leanplum.setAppIdForDevelopmentMode("5QoBzfoODCMG6X3ndukJ47KPGNszkyFvX0cKZiylL3k", "FkYfO9Gv6tFY1GkzsEHMNDOPtOnYeSmtpt4zKxBGdFU");
        } else {
            Leanplum.setAppIdForProductionMode("5QoBzfoODCMG6X3ndukJ47KPGNszkyFvX0cKZiylL3k", "kKyZ5kuTs6qLhHU80khwYGjWvN0dXN5xaZIQZHny8io");
        }

        // This will only run once per session, even if the activity is restarted.
        Leanplum.start(this);


		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		getActionBar().setTitle("");
		
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		
		prefs = new Preferences(this);
		
		getKeyHash();
		deleteOldImages();
		initSeenPicturesFile();
		
		if (Session.getActiveSession().isOpened()) {
			requestFacebookFriends(Session.getActiveSession());
			goToMainActivity();
		}
	}
	
	
	
	private void goToMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void deleteOldImages() {
		String path = getExternalFilesDir(null).toString();
		File file = new File(path);
		File files[] = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().contains(".jpg")) {
				Log.i("FILE DELETED", files[i].getName());
				files[i].delete();
			}
		}
	}
	
	private void initSeenPicturesFile() {
		File file = new File(getFilesDir(), GlobalMethods.FILENAME_VOTED_PICTURES);
		if (!file.exists()) {
			Set<Integer> init = new HashSet<Integer>();
			GlobalMethods.writeVotedPicturesSet(this, init);
		}
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
						Log.i("FB LOGIN", user.getName());
						prefs.setFbUserId(user.getId());
						prefs.setFbName(user.getName());
						prefs.setFbFirstName(user.getFirstName());
						new SetIdThread(user.getId()).start();
					}
				}
			}).executeAsync();
        	
        	requestFacebookFriends(Session.getActiveSession());
        	goToMainActivity();
        }
        else if (session.isClosed()) {
        	Log.i("FB LOGOUT", "SUCCESS");
        	prefs.setFbUserId("");
			prefs.setFbName("");
			prefs.setFbFirstName("");
			prefs.setUserId("");
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
				List<GraphUser> graphUsers = getResults(response);
				
				fbFriends = new ArrayList<Friend>();
				for (GraphUser user : graphUsers) {
					String fbId = user.getId();
					String fbName = user.getName();
					String fbFirstName = user.getFirstName();
					Friend friend = new Friend(fbId, fbName, fbFirstName);
					fbFriends.add(friend);
				}
				Collections.sort(fbFriends, new FriendComparator());
				GlobalMethods.writeFriendsArray(LoginActivity.this, fbFriends);
				
				JSONWriter writer = new JSONWriter(LoginActivity.this);
				writer.updateFriendsList(fbFriends);
				writer.logJson(JSONWriter.FILENAME_FRIENDS_LIST);
				new UpdateFriendsThread().start();
				
				new GetActiveFriendsTask().execute();
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
	
	private class FriendComparator implements Comparator<Friend> {
		@Override
		public int compare(Friend user1, Friend user2) {
			return user1.getFbName().compareTo(user2.getFbName());
		}
	}
	
	private class UpdateFriendsThread extends Thread {
		public void run() {
			if (GlobalMethods.isNetworkAvailable(LoginActivity.this)) {
				HttpPostRequest post = new HttpPostRequest(LoginActivity.this);
				post.createPost(HttpPostRequest.LOGIN_URL);
				post.addJSON(JSONWriter.FILENAME_FRIENDS_LIST);
				post.sendPost();
			}
			else {
				runOnUiThread(new Runnable() {
					public void run() { 
						Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}
	
	private class GetActiveFriendsTask extends AsyncTask<Void, Void, Void> {
		private String[] activeFriends;
		
	    protected Void doInBackground(Void... params) {
	    	JSONWriter writer = new JSONWriter(LoginActivity.this);
	    	writer.createJsonForActiveFriends();
	    	
			HttpPostRequest post = new HttpPostRequest(LoginActivity.this);
			post.createPost(HttpPostRequest.ACTIVE_FRIENDS_URL);
			post.addJSON(JSONWriter.FILENAME_ACTIVE_FRIENDS);
			String jsonString = post.sendPostReturnJson();
			
			JSONReader reader = new JSONReader(LoginActivity.this);
			activeFriends = reader.getActiveFriendsArray(jsonString);
			
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	List<Friend> activeFriendsArray = new ArrayList<Friend>();
	    	
	    	// Add each matching friend to active friends list
	    	for (int i = 0; i < activeFriends.length; i++) {
	    		String activeFriend = activeFriends[i];
	    		for (Friend friend : fbFriends) {
	    			if (friend.getFbId().equals(activeFriend)) { activeFriendsArray.add(friend); }
	    		}
	    	}
	    	Collections.sort(activeFriendsArray, new FriendComparator());
	    	
	    	// Remove all active friends from full friends list
	    	for (Friend friend : activeFriendsArray) { 
	    		fbFriends.remove(friend);
	    		Log.v("MATCH", friend.getFbName()); 
    		}
	    	GlobalMethods.writeActiveFriendsArray(LoginActivity.this, activeFriendsArray);
	    	
	        return;
	    }
	}
	
	private class SetIdThread extends Thread {
		String fbId;
		
		private SetIdThread(String fbId) {
			this.fbId = fbId;
		}
		
		public void run() {
			HttpGetRequest get = new HttpGetRequest();
			prefs.setUserId(get.getUserId(fbId));
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
