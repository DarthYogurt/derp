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

public class LoginActivity extends Activity {
	
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
        	Log.i("FACEBOOK", "LOGGED IN");
        	
        	Request.newMeRequest(session, new Request.GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {
						prefs.setFbUserId(user.getId());
						prefs.setFbUserName(user.getName());
					}
				}
			}).executeAsync();
        	
        	requestFacebookFriends(Session.getActiveSession());
//        	new UpdateJsonTask().execute();
        	goToMainActivity();
        }
        else if (session.isClosed()) {
        	Log.i("FACEBOOK", "LOGGED OUT");
        	prefs.setFbUserId("");
			prefs.setFbUserName("");
        }
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
//	    new UpdateJsonTask().execute();
	    goToMainActivity();
	}
	
	private void requestFacebookFriends(Session session) {
		Request friendsRequest = createRequest(session);
		friendsRequest.setCallback(new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				fbFriends = getResults(response);
				
				JSONWriter writer = new JSONWriter(LoginActivity.this);
				writer.updateFriendsList(fbFriends);
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
	
	private class UpdateJsonTask extends AsyncTask<Void, Void, Void> {
	    protected Void doInBackground(Void... params) {
	    	runOnUiThread(new Runnable() {
				public void run() { 
					requestFacebookFriends(Session.getActiveSession());
				}
			});
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	if (fbFriends == null) {
	    		Log.v("FRIENDS LIST", "NULL");
	    	}
	    	else {
	    		Log.v("FB FRIENDS SIZE", Integer.toString(fbFriends.size()));
	    		JSONWriter writer = new JSONWriter(LoginActivity.this);
	    	}
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
	

	
//	private static final int SPLASH = 0;
//	private static final int SELECTION = 1;
//	private static final int SETTINGS = 2;
//	private static final int FRAGMENT_COUNT = SETTINGS +1;
//	
//	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
//	private MenuItem settings;
//	private boolean isResumed = false;
//	private UiLifecycleHelper uiHelper;
//	private Session.StatusCallback callback = new Session.StatusCallback() {
//		@Override
//		public void call(Session session, SessionState state, Exception exception) {
//			onSessionStateChange(session, state, exception);
//	    }
//	};
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		uiHelper = new UiLifecycleHelper(this, callback);
//		uiHelper.onCreate(savedInstanceState);
//		
//		setContentView(R.layout.activity_main);
//		
//		FragmentManager fm = getSupportFragmentManager();
//		fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
//		fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
//		fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);
//		
//		FragmentTransaction transaction = fm.beginTransaction();
//		for(int i = 0; i < fragments.length; i++) {
//		    transaction.hide(fragments[i]);
//		}
//		transaction.commit();
//	}
//	
//	private void showFragment(int fragmentIndex, boolean addToBackStack) {
//		FragmentManager fm = getSupportFragmentManager();
//		FragmentTransaction transaction = fm.beginTransaction();
//		for (int i = 0; i < fragments.length; i++) {
//		    if (i == fragmentIndex) { transaction.show(fragments[i]); } 
//		    else { transaction.hide(fragments[i]); }
//		}
//		if (addToBackStack) { transaction.addToBackStack(null); }
//		transaction.commit();
//	}
//	
//	@Override
//	public void onResume() {
//	    super.onResume();
//	    uiHelper.onResume();
//	    isResumed = true;
//	}
//
//	@Override
//	public void onPause() {
//	    super.onPause();
//	    uiHelper.onPause();
//	    isResumed = false;
//	}
//	
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//	    super.onActivityResult(requestCode, resultCode, data);
//	    uiHelper.onActivityResult(requestCode, resultCode, data);
//	}
//	
//	@Override
//	public void onDestroy() {
//	    super.onDestroy();
//	    uiHelper.onDestroy();
//	}
//	
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//	    super.onSaveInstanceState(outState);
//	    uiHelper.onSaveInstanceState(outState);
//	}
//	
//	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
//	    // Only make changes if the activity is visible
//	    if (isResumed) {
//	        FragmentManager manager = getSupportFragmentManager();
//	        // Get the number of entries in the back stack
//	        int backStackSize = manager.getBackStackEntryCount();
//	        // Clear the back stack
//	        for (int i = 0; i < backStackSize; i++) {
//	            manager.popBackStack();
//	        }
//	        if (state.isOpened()) {
//	            // If the session state is open: Show the authenticated fragment
//	            showFragment(SELECTION, false);
//	        } else if (state.isClosed()) {
//	            // If the session state is closed: Show the login fragment
//	            showFragment(SPLASH, false);
//	        }
//	    }
//	}
//	
//	@Override
//	protected void onResumeFragments() {
//	    super.onResumeFragments();
//	    Session session = Session.getActiveSession();
//	
//	    if (session != null && session.isOpened()) {
//	        // if the session is already open, try to show the selection fragment
//	        showFragment(SELECTION, false);
//	    } else {
//	        // otherwise present the splash screen and ask the person to login.
//	        showFragment(SPLASH, false);
//	    }
//	}
//	
//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//	    // only add the menu when the selection fragment is showing
//	    if (fragments[SELECTION].isVisible()) {
//	        if (menu.size() == 0) {
//	            settings = menu.add(R.string.settings);
//	        }
//	        return true;
//	    } else {
//	        menu.clear();
//	        settings = null;
//	    }
//	    return false;
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//	    if (item.equals(settings)) {
//	        showFragment(SETTINGS, true);
//	        return true;
//	    }
//	    return false;
//	}

}
