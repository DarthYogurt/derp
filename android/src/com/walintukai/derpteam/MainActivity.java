package com.walintukai.derpteam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.leanplum.activities.LeanplumActivity;

public class MainActivity extends LeanplumActivity {
	
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
		ImageView btnYourTeam = (ImageView) findViewById(R.id.btn_your_team);
		ImageView btnTakePicture = (ImageView) findViewById(R.id.btn_take_pic);
		ImageView btnHome = (ImageView) findViewById(R.id.btn_home);
		ImageView btnGallery = (ImageView) findViewById(R.id.btn_gallery);
		ImageView btnFriendsTeam = (ImageView) findViewById(R.id.btn_friends_team);
		pwReportBug = new PopupWindow(this);
		vReportBug = getLayoutInflater().inflate(R.layout.popwin_report_bug, null);
		etReportBug = (EditText) vReportBug.findViewById(R.id.report_bug);
		Button btnSendReport = (Button) vReportBug.findViewById(R.id.btn_send_report);
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		MainFragment mainFragment = MainFragment.newInstance();
		ft.add(R.id.fragment_container, mainFragment);
		ft.commit();
		
		btnSendReport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String report = etReportBug.getText().toString();
				if (GlobalMethods.isNetworkAvailable(MainActivity.this)) { new SendBugReportTask(report).execute(); }
				else { Toast.makeText(MainActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show(); }
			}
		});
		
		btnYourTeam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ViewTeamFragment fragment = ViewTeamFragment.newInstance(prefs.getFbUserId());
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		btnTakePicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				NewPictureFragment fragment = NewPictureFragment.newInstance("");
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		btnHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				MainFragment fragment = MainFragment.newInstance();
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		btnGallery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				GalleryFragment fragment = GalleryFragment.newInstance();
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		btnFriendsTeam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				PickTeamFragment fragment = PickTeamFragment.newInstance();
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		// Checks to see if alarm manager for notifications is active, if not, start new one
		Intent alarmIntent = new Intent(this, GetNotificationAlarmReceiver.class);
		boolean alarmActive = (PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);
		if (alarmActive) { Log.v("ALARM MANAGER", "ACTIVE"); }
		else { 
			Log.v("ALARM MANAGER", "STARTING"); 
			// Periodically checks for notifications
			AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			Intent intent2 = new Intent(this, GetNotificationAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent2, 0);
			int checkTime = (1000 * 60) * (60 + new Random().nextInt(30));
			alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.uptimeMillis(), checkTime, pendingIntent);
		}
		
		// If clicked from notification, load your team page
		Bundle notificationExtra = getIntent().getExtras();
		if (notificationExtra != null) {
			boolean startYourTeamFragment = false;
			boolean startTakePictureFragment = false;
			boolean startViewMemberFragment = false;
			
			startYourTeamFragment = notificationExtra.getBoolean("picture");
			if (startYourTeamFragment) {
				FragmentManager fm2 = getFragmentManager();
				FragmentTransaction ft2 = fm2.beginTransaction();
				ViewTeamFragment fragment = ViewTeamFragment.newInstance(prefs.getFbUserId());
				ft2.replace(R.id.fragment_container, fragment);
				ft2.addToBackStack(null);
				ft2.commit();
			}
			
			startTakePictureFragment = notificationExtra.getBoolean("reminder");
			if (startTakePictureFragment) {
				FragmentManager fm2 = getFragmentManager();
				FragmentTransaction ft2 = fm2.beginTransaction();
				NewPictureFragment fragment = NewPictureFragment.newInstance("");
				ft2.replace(R.id.fragment_container, fragment);
				ft2.addToBackStack(null);
				ft2.commit();
			}
			
			startViewMemberFragment = notificationExtra.getBoolean("comment");
			if (startViewMemberFragment) {
				FragmentManager fm2 = getFragmentManager();
				FragmentTransaction ft2 = fm2.beginTransaction();
				ViewMemberFragment fragment = ViewMemberFragment.newInstance(notificationExtra.getInt("picId"));
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
			if (type.startsWith("image/")) { 
				Uri sharedImageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
				copyGalleryImage(sharedImageUri);
			}
			if (type.equalsIgnoreCase("text/plain")) {
				String imgUrl = intent.getStringExtra(Intent.EXTRA_TEXT).toLowerCase();
				if (imgUrl.contains(".jpg") || imgUrl.contains(".gif") || imgUrl.contains(".png")) {
					new CopyImageFromUrlTask(imgUrl).execute();
				}
				else { Toast.makeText(this, "Invalid image URL", Toast.LENGTH_SHORT).show(); }
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private class ExitDialog extends DialogFragment {
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Dialog dialog = new Dialog(getActivity());
			dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			dialog.setContentView(R.layout.dialog_exit);
			dialog.getWindow().setLayout(480, 300);
			dialog.setCanceledOnTouchOutside(false);
			
			ImageView btnYes = (ImageView) dialog.findViewById(R.id.btn_yes);
			btnYes.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					finish();
				}
			});
			
			ImageView btnNo = (ImageView) dialog.findViewById(R.id.btn_no);
			btnNo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					dismiss();
				}
			});
			
			return dialog;
		}
	}
	
	@SuppressWarnings("deprecation")
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
	
	private void copyGalleryImage(Uri uri) {
		String realPath = ImageHandler.getRealPathFromUri(this, uri);
		File galleryFile = new File(realPath);
		
		imgFilename = ImageHandler.getImageFilename(this);
		File file = new File(getExternalFilesDir(null), imgFilename);
		GlobalMethods.copyFile(galleryFile, file);
		Log.i("IMAGE COPIED TO INTERNAL", imgFilename);
		
		ImageHandler.compressAndRotateImage(this, imgFilename);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		NewPictureFragment fragment = NewPictureFragment.newInstance(imgFilename);
		ft.replace(R.id.fragment_container, fragment);
		ft.addToBackStack(null);
		ft.commit();
		
//		startCrop(file);
	}
	
	private class CopyImageFromUrlTask extends AsyncTask<Void, Void, Void> {
		String imgUrl;
		String filepath;
		
		private CopyImageFromUrlTask(String imgUrl) {
			this.imgUrl = imgUrl;
		}
		
	    protected Void doInBackground(Void... params) {
	    	imgFilename = ImageHandler.getImageFilename(MainActivity.this);
	    	filepath = ImageHandler.copyImageFromUrl(MainActivity.this, imgUrl, imgFilename);
	    	
	        return null;
	    }

	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	startCrop(new File(filepath));
	        return;
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
	    startActivityForResult(intent, NewPictureFragment.REQUEST_CROP_EXISTING);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode) {
		case NewPictureFragment.REQUEST_CROP_EXISTING:
			if (resultCode == Activity.RESULT_OK) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				NewPictureFragment fragment = NewPictureFragment.newInstance(imgFilename);
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
			break;
			
		case NewPictureFragment.REQUEST_SELECT_FROM_GALLERY:
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImageUri = data.getData();
				copyGalleryImage(selectedImageUri);
			}
			break;
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
