package com.walintukai.derpteam;

import com.facebook.Session;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setTitle("");
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		MainFragment mainFragment = new MainFragment();
		ft.add(R.id.fragment_container, mainFragment);
		ft.commit();
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_log_out:
				Session.getActiveSession().closeAndClearTokenInformation();
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				finish();
				return true;
			case R.id.action_settings:
				return true;
			default:
				return super.onOptionsItemSelected(item);
	    }
	}

}
