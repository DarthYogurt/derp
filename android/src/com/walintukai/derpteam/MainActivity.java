package com.walintukai.derpteam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.leanplum.activities.LeanplumActivity;

public class MainActivity extends LeanplumActivity {
	
	private static final int REQUEST_CROP_SHARED_IMAGE = 300;
	
	private Preferences prefs;
	private String imgFilename;
	private UiLifecycleHelper uiHelper;
	private PopupWindow pwReportBug;
	private View vReportBug;
	private EditText etReportBug;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
        	onSessionStateChange(session, state, exception);
        }
    };
    
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) { Log.i("FB LOGIN", "SUCCESS"); } 
        else if (state.isClosed()) { Log.i("FB LOGOUT", "SUCCESS"); }
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setTitle("");
		
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		
		prefs = new Preferences(this);
		pwReportBug = new PopupWindow(this);
		vReportBug = getLayoutInflater().inflate(R.layout.popwin_report_bug, null);
		etReportBug = (EditText) vReportBug.findViewById(R.id.report_bug);
		Button btnSendReport = (Button) vReportBug.findViewById(R.id.btn_send_report);
		
		btnSendReport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String report = etReportBug.getText().toString();
				if (GlobalMethods.isNetworkAvailable(MainActivity.this)) { new SendBugReportTask(report).execute(); }
				else { Toast.makeText(MainActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show(); }
			}
		});
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		MainFragment mainFragment = MainFragment.newInstance();
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
			case R.id.action_report_bug:
				pwReportBug.setTouchable(true);
				pwReportBug.setFocusable(true);
				pwReportBug.setOutsideTouchable(true);
				pwReportBug.setTouchInterceptor(new OnTouchListener() {
			        public boolean onTouch(View v, MotionEvent event) {
			            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
			            	pwReportBug.dismiss();
			                return true;
			            }
			            return false;
			        }
			    });
				pwReportBug.setWidth(550);
				pwReportBug.setHeight(450);
				pwReportBug.setContentView(vReportBug);
				pwReportBug.setBackgroundDrawable(new BitmapDrawable());
				pwReportBug.setAnimationStyle(R.style.AddCommentAnimation);
				pwReportBug.showAtLocation(this.findViewById(R.id.fragment_container), Gravity.CENTER, 0, -140);
				pwReportBug.setOnDismissListener(new PopupWindow.OnDismissListener() {
					@Override
					public void onDismiss() { 
						getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
					}
				});
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		        imm.showSoftInput(etReportBug, InputMethodManager.SHOW_IMPLICIT);
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
		
//		uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
//			@Override
//			public void onError(PendingCall pendingCall, Exception error, Bundle data) {
//				Log.e("POST TO WALL", "ERROR");
//			}
//			
//			@Override
//			public void onComplete(PendingCall pendingCall, Bundle data) {
//				Log.v("POST TO WALL", "SUCCESS");
//			}
//		});
		
		if (requestCode == REQUEST_CROP_SHARED_IMAGE && resultCode == Activity.RESULT_OK) {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			TakePictureFragment fragment = TakePictureFragment.newInstance(imgFilename);
			ft.replace(R.id.fragment_container, fragment);
			ft.addToBackStack(null);
			ft.commit();
		}
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
	
	private class SendBugReportTask extends AsyncTask<Void, Void, Void> {
		private String bugMessage;
		
		private SendBugReportTask(String bugMessage) {
			this.bugMessage = bugMessage;
		}
		
	    protected Void doInBackground(Void... params) {
	    	JSONWriter writer = new JSONWriter(MainActivity.this);
			writer.createJsonForBugReport(bugMessage);
			
			HttpPostRequest post = new HttpPostRequest(MainActivity.this);
			post.createPost(HttpPostRequest.BUG_REPORT_URL);
			post.addJSON(JSONWriter.FILENAME_BUG_REPORT);
			post.sendPost();
			
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	pwReportBug.dismiss();
	    	etReportBug.setText("");
	    	Toast.makeText(MainActivity.this, R.string.bug_report_sent, Toast.LENGTH_SHORT).show();
	        return;
	    }
	}

}
