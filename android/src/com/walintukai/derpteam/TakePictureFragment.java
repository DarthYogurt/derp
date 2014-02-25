package com.walintukai.derpteam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

public class TakePictureFragment extends Fragment {

	private static final int REQUEST_PICTURE = 1;
	
	private Preferences prefs;
	private ImageView takenPicture;
	private String filename;
	private String oldFilename;
	private File file;
	private boolean hasPicture;
	
	static TakePictureFragment newInstance(String imgFilename, String caption) {
		TakePictureFragment fragment = new TakePictureFragment();
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.activity_take_picture, container, false);
		
		prefs = new Preferences(getActivity());
		filename = "";
		oldFilename = "";
		
		takenPicture = (ImageView) view.findViewById(R.id.taken_picture);
		takenPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!filename.isEmpty()) { oldFilename = filename; }
				new NewPictureThread().start();	
			}
		});
		
		// Handles image being sent from other application
		Intent intent = getActivity().getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		
		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if (type.startsWith("image/")) { handleSentImage(intent); }
		}
		
		if (filename.isEmpty()) { new NewPictureThread().start(); }
		
		Button btnAssignTeam = (Button) view.findViewById(R.id.btn_assign_team);
		btnAssignTeam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (hasPicture) {
					EditText editText = (EditText) view.findViewById(R.id.caption);
					String caption = editText.getText().toString();
					
					FragmentManager fm = getFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					ft.hide(TakePictureFragment.this);
					PickFriendFragment fragment = PickFriendFragment.newInstance(filename, caption);
					ft.add(R.id.fragment_container, fragment);
					ft.addToBackStack(null);
					ft.commit();
				}
				else {
					NoPictureDialogFrament dialog = new NoPictureDialogFrament();
					dialog.show(getFragmentManager(), "noPicture");
				}
			}
		});
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	private void handleSentImage(Intent intent) {
		Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (imageUri != null) {
			String realPath = getRealPathFromUri(imageUri);
			File extFile = new File(realPath);
			filename = getImageFilename();
			file = new File(getActivity().getExternalFilesDir(null), filename);
			copyFile(extFile, file);
			Log.i("PICTURE COPIED TO INTERNAL", filename);
		
			compressAndRotateImage(file);
			showPicture(file);
			hasPicture = true;
		}
		else { Log.e("RECEIVED IMAGE", "NULL"); }
	}
	
	public String getRealPathFromUri (Uri contentUri) {
		Cursor cursor = null;
		try { 
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = getActivity().getContentResolver().query(contentUri,  proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} 
		finally {
			if (cursor != null) { cursor.close(); }
		}
	}
	
	public static void copyFile(File src, File dst) {
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
	
	private void showPicture(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			Bitmap imgFile = BitmapFactory.decodeStream(fis);
			fis.close();
			
			takenPicture.setImageBitmap(imgFile);
			takenPicture.invalidate();
		} 
    	catch (FileNotFoundException e) { e.printStackTrace(); } 
    	catch (IOException e) { e.printStackTrace(); }
	}
	
	private String getImageFilename() {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		String timeStamp = sdf.format(new Date());
		return timeStamp + "-" + prefs.getFbUserId() + ".jpg";
	}
	
	private class NewPictureThread extends Thread {
		public void run() {
			try { 
				getActivity().runOnUiThread(new Runnable() {
					public void run() { 
						Toast toast = Toast.makeText(getActivity(), R.string.starting_camera, Toast.LENGTH_SHORT);
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
		file = new File(getActivity().getExternalFilesDir(null), filename);
		Uri outputFileUri = Uri.fromFile(file);
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		
		if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
			startActivityForResult(intent, REQUEST_PICTURE);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == REQUEST_PICTURE && resultCode == Activity.RESULT_OK) {
			hasPicture = true;
			if (!oldFilename.isEmpty()) { GlobalMethods.deleteFileFromExternal(getActivity(), oldFilename); }
			
			Log.i("PICTURE SAVED", filename);
			
			file = new File(getActivity().getExternalFilesDir(null), filename);
			compressAndRotateImage(file);
			showPicture(file);
		}
	}
		
	private void compressAndRotateImage(File file) {
		try {
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
			try { exif = new ExifInterface(getActivity().getExternalFilesDir(null) + "/" + filename); } 
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

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getActivity().getMenuInflater().inflate(R.menu.take_picture, menu);
//		
//		MenuItem shareItem = menu.findItem(R.id.action_share);
//		
//		// Get the provider and hold onto it to set/change the share intent
//		ShareActionProvider shareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
//		
//		// Attach an intent to this ShareActionProvider.  You can update this at any time,
//	    // like when the user selects a new piece of data they might like to share.
//		shareActionProvider.setShareIntent(createShareIntent());
//		
//		return true;
//	}
	
	private Intent createShareIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		intent.setType("image/*");
		File file = new File(getActivity().getExternalFilesDir(null), filename);
		Uri uri = Uri.fromFile(file);
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		
		return intent;
	}
	
	private class GoBackDialogFrament extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.dialog_go_back)
	        	.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
	        			if (!oldFilename.isEmpty()) { GlobalMethods.deleteFileFromExternal(getActivity(), oldFilename); }
	        			if (!filename.isEmpty()) { GlobalMethods.deleteFileFromExternal(getActivity(), filename); }
	        		}
	        	})
	        	.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
	        			dismiss();
	        		}
	        	});
	        
	        // Create the AlertDialog object and return it
	        return builder.create();
		}
	}
	
	private class NoPictureDialogFrament extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.dialog_no_picture)
	        	.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
	        			dismiss();
	        		}
	        	});
	        
	        // Create the AlertDialog object and return it
	        return builder.create();
		}
	}
	
}
