package com.walintukai.derpteam;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	    	Intent intent = new Intent(SplashActivity.this, MainActivity.class);
	    	startActivity(intent);
			finish();
	        return;
	    }
	}
	
}
