package com.walintukai.derpteam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.Session;
import com.leanplum.activities.LeanplumActivity;

public class MainActivity extends LeanplumActivity {
	
	private static final int REQUEST_CROP_SHARED_IMAGE = 300;
	
	private Preferences prefs;
	private String imgFilename;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setTitle("");
		
		prefs = new Preferences(this);
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		MainFragment mainFragment = new MainFragment();
		ft.add(R.id.fragment_container, mainFragment);
		ft.commit();
		
		// If clicked from notification, load your team page
		Bundle notificationExtra = getIntent().getExtras();
		if (notificationExtra != null) {
			boolean startYourTeamFragment = false;
			startYourTeamFragment = notificationExtra.getBoolean("viewYourTeam");
			if (startYourTeamFragment) {
				FragmentManager fm2 = getFragmentManager();
				FragmentTransaction ft2 = fm2.beginTransaction();
				ViewTeamFragment fragment = ViewTeamFragment.newInstance(prefs.getFbUserId());
				ft2.replace(R.id.fragment_container, fragment);
				ft2.addToBackStack(null);
				ft2.commit();
			}
		}
		
		// Handles image being sent from other application
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		
		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if (type.startsWith("image/")) { handleSentImage(intent); }
		}

		// Periodically checks for notifications
		AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent intent2 = new Intent(this, GetNotificationAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent2, 0);
		alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.uptimeMillis(), 1000 * 10, pendingIntent);
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
	
	private void handleSentImage(Intent intent) {
		Uri sharedImgUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (sharedImgUri != null) {
			String realPath = getRealPathFromUri(sharedImgUri);
			File galleryFile = new File(realPath);
			
			imgFilename = ImageHandler.getImageFilename(this);
			File file = new File(getExternalFilesDir(null), imgFilename);
			copyFile(galleryFile, file);
			Log.i("IMAGE COPIED TO INTERNAL", imgFilename);
			
			startCrop(file);
		}
		else { Log.e("RECEIVED IMAGE", "NULL"); }
	}
	
	public String getRealPathFromUri (Uri contentUri) {
		Cursor cursor = null;
		try { 
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = getContentResolver().query(contentUri,  proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} 
		finally {
			if (cursor != null) { cursor.close(); }
		}
	}
	
	private static void copyFile(File src, File dst) {
	    FileChannel inChannel = null;
		try { inChannel = new FileInputStream(src).getChannel(); } 
		catch (FileNotFoundException e) { e.printStackTrace(); }
		
	    FileChannel outChannel = null;
		try { outChannel = new FileOutputStream(dst).getChannel(); } 
		catch (FileNotFoundException e) { e.printStackTrace(); }
	    
    	try { inChannel.transferTo(0, inChannel.size(), outChannel); } 
    	catch (IOException e) { e.printStackTrace(); }
    	finally {    		
    		if (inChannel != null) {
    			try { inChannel.close(); } 
    			catch (IOException e) { e.printStackTrace(); }
    		}
				
    		if (outChannel != null) {
	        	try { outChannel.close(); } 
	        	catch (IOException e) { e.printStackTrace(); }
	        }	
	    }
	}
	
	private void startCrop(File file) {
		Intent intent = new Intent("com.android.camera.action.CROP");
	    intent.setDataAndType(Uri.fromFile(file), "image/*");
	    intent.putExtra("outputX", 400);
	    intent.putExtra("outputY", 400);
	    intent.putExtra("aspectX", 1);
	    intent.putExtra("aspectY", 1);
	    intent.putExtra("scale", true);
	    intent.putExtra("noFaceDetection", true);
	    intent.putExtra("output", Uri.fromFile(file));
	    startActivityForResult(intent, REQUEST_CROP_SHARED_IMAGE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == REQUEST_CROP_SHARED_IMAGE && resultCode == Activity.RESULT_OK) {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			
			TakePictureFragment fragment = TakePictureFragment.newInstance(imgFilename);
			ft.replace(R.id.fragment_container, fragment);
			ft.addToBackStack(null);
			ft.commit();
		}
	}

}
