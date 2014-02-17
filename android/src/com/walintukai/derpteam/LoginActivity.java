package com.walintukai.derpteam;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.Callback;
import com.facebook.widget.LoginButton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private Facebook facebook;
	private GraphUser user;
	private UiLifecycleHelper uiHelper;
	private Preferences prefs;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Log.v("FACEBOOK", "LOGGED IN");
		}
		else if (state.isClosed()) {
			Log.v("FACEBOOK", "LOGGED OUT");
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		
		// Generates keyhash code
		try {
			PackageInfo info = getPackageManager().getPackageInfo("com.walintukai.derpteam", 
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				System.out.println("KeyHash: "+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} 
		catch (NameNotFoundException e) { } 
		catch (NoSuchAlgorithmException e) { } 
		
//		final TextView username = (TextView) findViewById(R.id.username);
		
		prefs = new Preferences(this);
		
		LoginButton loginButton = (LoginButton) findViewById(R.id.fb_login_button);
		loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
			
			@Override
			public void onUserInfoFetched(GraphUser user) {
				LoginActivity.this.user = user;
				Session session = Session.getActiveSession();
				boolean validSession = session != null && session.isOpened();
				
				if (validSession && user != null) {
					// Sets Facebook access token and expiration in shared preferences
					prefs.setAccessToken(session.getAccessToken());
					prefs.setTokenExpiration(session.getExpirationDate().getTime());
					
					Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
						@Override
						public void onCompleted(GraphUser user, Response response) {
//							username.setText("Welcome " + user.getName() + "!");
						}
					});
					request.executeAsync();
				}
				else {
//					username.setText("Not Logged In");
				}
			}
		});
		
		ImageButton myTeamButton = (ImageButton) findViewById(R.id.btn_my_team);
		myTeamButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		ImageButton createMemberButton = (ImageButton) findViewById(R.id.btn_create_member);
		createMemberButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
//		ImageView shareButton = (ImageView) findViewById(R.id.fb_icon);
//		shareButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				shareToFacebook();
//			}
//		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data, new Callback() {
			@Override
			public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
				Log.e("ACTIVITY", String.format("Error: %s", error.toString()));
			}
			
			@Override
			public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
				Log.i("ACTIVITY", "SUCCESS");
			}
		});
	}
	
	public void shareToFacebook() {
		if (!checkNetwork()) {
			Toast.makeText(getApplicationContext(), "No active internet connection", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!checkFbInstalled()) {
			Toast.makeText(getApplicationContext(), "Facebook app not installed", Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_SHORT).show();
		if (FacebookDialog.canPresentShareDialog(this, FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
			FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this).setName("Derp Team")
					.setLink("http://www.google.com").setDescription("Hello!").build();
			uiHelper.trackPendingDialogCall(shareDialog.present());	
		}
		else {
			Log.e("FB DIALOG", "FAILED");
		}
	}
	
	private boolean checkNetwork() {
		boolean wifiAvailable = false;
		boolean mobileAvailable = false;
		ConnectivityManager conManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] networkInfo = conManager.getAllNetworkInfo();
		
		for (NetworkInfo netInfo : networkInfo) {
			if (netInfo.getTypeName().equalsIgnoreCase("WIFI")) {
				if (netInfo.isConnected()) { wifiAvailable = true; }
			}
			if (netInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
				if (netInfo.isConnected()) { mobileAvailable = true; }
			}
		}
		return wifiAvailable || mobileAvailable;
	}
	
	public boolean checkFbInstalled() {
		PackageManager pm = getPackageManager();
		boolean flag = false;
		
		try {
			pm.getPackageInfo("com.facebook.katana", PackageManager.GET_ACTIVITIES);
			flag = true;
		}
		catch (PackageManager.NameNotFoundException e){
			flag = false;
		}
		return flag;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

}
