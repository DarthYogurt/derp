package com.walintukai.derpteam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

public class TakePictureActivity extends Activity {
	
	private static final int REQUEST_PICTURE = 1;
	
	private Preferences prefs;
	private String filename;
	private String oldFilename;
	private ImageView takenPicture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_picture);
		getActionBar().setTitle("");
		
		prefs = new Preferences(this);
		filename = "";
		oldFilename = "";
		
		takenPicture = (ImageView) findViewById(R.id.taken_picture);
		takenPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!filename.isEmpty()) { oldFilename = filename; }
				new NewPictureThread().start();	
			}
		});
		
		if (filename.isEmpty()) {
			new NewPictureThread().start();
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (!oldFilename.isEmpty()) { GlobalMethods.deleteFileFromExternal(this, oldFilename); }
		if (!filename.isEmpty()) { GlobalMethods.deleteFileFromExternal(this, filename); }
	}
	
	private void showPicture() {
		try {
    		File file = new File(getExternalFilesDir(null), filename);
			FileInputStream fis = new FileInputStream(file);
			Bitmap imgFromFile = BitmapFactory.decodeStream(fis);
			fis.close();
			takenPicture.setImageBitmap(imgFromFile);
			takenPicture.invalidate();
		} 
    	catch (FileNotFoundException e) { e.printStackTrace(); } 
    	catch (IOException e) { e.printStackTrace(); }
	}
	
	private String getImageFilename() {
		filename = "fid_" + prefs.getFbUserId() + "_photo_" + getTimeStampForFilename() + ".jpg";
		return filename;
	}
	
	private static String getTimeStampForFilename() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_HHmmss");
		String now = sdf.format(new Date());
		return now;
	}
	
	private class NewPictureThread extends Thread {
		public void run() {
			try { 
				runOnUiThread(new Runnable() {
					public void run() { 
						Toast toast = Toast.makeText(getApplicationContext(), R.string.starting_camera, 
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
				});
				Thread.sleep(600);
			} 
	    	catch (InterruptedException e) { e.printStackTrace(); } 
			startCameraActivity();
		}
	}
	
	private void startCameraActivity() {
		filename = getImageFilename();
		
		File file = new File(getExternalFilesDir(null), filename);
		Uri outputFileUri = Uri.fromFile(file);
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent, REQUEST_PICTURE);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == REQUEST_PICTURE && resultCode == Activity.RESULT_OK) {
			if (!oldFilename.isEmpty()) { GlobalMethods.deleteFileFromExternal(this, oldFilename); }
			
			Log.i("PICTURE SAVED", filename);
			compressAndRotateImage(this, filename);
			showPicture();
		}
	}
		
	private static void compressAndRotateImage(Context context, String filename) {
		try {
			File file = new File(context.getExternalFilesDir(null), filename);
			
		    // Decode image size
		    BitmapFactory.Options o = new BitmapFactory.Options();
		    o.inJustDecodeBounds = true;
		    BitmapFactory.decodeStream(new FileInputStream(file), null, o);

		    // The new size we want to scale to
		    final int REQUIRED_SIZE = 300;

		    // Find the correct scale value. It should be the power of 2.
		    int scale = 1;
		    while (o.outWidth/scale/2 >= REQUIRED_SIZE && o.outHeight/scale/2 >= REQUIRED_SIZE) {
		    	scale*=2;
		    }
		        
		    // Decode with inSampleSize
		    BitmapFactory.Options o2 = new BitmapFactory.Options();
		    o2.inSampleSize = scale;
		    Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
		    
		    // Get orientation of picture
		    ExifInterface exif = null;
			try { exif = new ExifInterface(context.getExternalFilesDir(null) + "/" + filename); } 
			catch (IOException e1) { e1.printStackTrace(); }
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
			Log.i("ORIENTATION", Integer.toString(orientation));
			
			// Rotate image to portrait based on taken orientation
			Matrix matrix = new Matrix();
            if (orientation == 6) { matrix.postRotate(90); }
            else if (orientation == 3) { matrix.postRotate(180); }
            else if (orientation == 8) { matrix.postRotate(270); }
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		    
	        // Save file
		    FileOutputStream fos = new FileOutputStream(file);
		    bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		    
		    try { fos.flush(); fos.close(); } 
		    catch (IOException e) { e.printStackTrace(); }
		    
		    bm.recycle();
		    System.gc();
		} 
		catch (FileNotFoundException e) { e.printStackTrace(); }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.take_picture, menu);
		
		MenuItem shareItem = menu.findItem(R.id.action_share);
		
		// Get the provider and hold onto it to set/change the share intent
		ShareActionProvider shareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
		
		// Attach an intent to this ShareActionProvider.  You can update this at any time,
	    // like when the user selects a new piece of data they might like to share.
		shareActionProvider.setShareIntent(createShareIntent());
		
		return true;
	}
	
	private Intent createShareIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.setType("image/*");
		
		File file = new File(getExternalFilesDir(null), filename);
		Uri uri = Uri.fromFile(file);
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		return intent;
	}
	
	//	private void loadFriendPickerFragment() {
	//	fragmentFriendPicker = new FriendPickerFragment();
	//	fragmentFriendPicker.setMultiSelect(false);
	//	
	//	ft = fm.beginTransaction();
	//	ft.replace(android.R.id.content, fragmentFriendPicker);
	//    ft.commit();
	//}
	
	//@Override
	//protected void onStart() {
	//	super.onStart();
	//	fragmentFriendPicker.loadData(false);
	//}

}
