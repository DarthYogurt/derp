package com.walintukai.derpteam;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.Callback;

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
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	String message = "Hello There!";
	private UiLifecycleHelper uiHelper;
	ImageView facebook;
	
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
		
		facebook = (ImageView) findViewById(R.id.fbicon);
		facebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				facebook();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onDestroy() {
		uiHelper.onDestroy();
		super.onDestroy();
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
	
	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}
	
	public void facebook() {
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
					.setLink("http://www.google.com").setDescription(message).build();
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

}
