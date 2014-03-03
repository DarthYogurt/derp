package com.walintukai.derpteam;

import com.flurry.android.FlurryAgent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.content.Intent;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Removes title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
						
		// Removes notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		
		setContentView(R.layout.activity_splash);
		
		new ShowLogo().execute();
	}

	private class ShowLogo extends AsyncTask<Void, Void, Void> {

	    protected Void doInBackground(Void... params) {
	    	try { Thread.sleep(2000); } 
			catch (Exception e) { e.printStackTrace(); }
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
	    	startActivity(intent);
			finish();
	        return;
	    }
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, "8Q5JHWCYR8BY35Z7FVMW");
	}
	
	@Override
	protected void onStop() {
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
	
}
