package com.walintukai.derpteam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class TakePictureFragment extends Fragment {

	private static final int REQUEST_PICTURE = 1;
	private static final String KEY_FILENAME = "filename";
	
	private ImageView takenPicture;
	private String filename;
	private String oldFilename;
	private File file;
	private boolean hasPicture;
	
	static TakePictureFragment newInstance(String filename) {
		TakePictureFragment fragment = new TakePictureFragment();
		Bundle args = new Bundle();
		args.putString(KEY_FILENAME, filename);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_take_picture, container, false);
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bundle args = getArguments();
		filename = args.getString(KEY_FILENAME);
		oldFilename = "";
		
		takenPicture = (ImageView) view.findViewById(R.id.taken_picture);
		takenPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!filename.isEmpty()) { oldFilename = filename; }
				new NewPictureThread().start();	
			}
		});
		
		if (!filename.isEmpty()) { 
			hasPicture = true;
			File file = new File(getActivity().getExternalFilesDir(null), filename);
			showPicture(file);
		}
		
		if (filename.isEmpty()) { new NewPictureThread().start(); }
		
		Button btnAssignTeam = (Button) view.findViewById(R.id.btn_assign_team);
		btnAssignTeam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (hasPicture) {
					EditText etTitle = (EditText) view.findViewById(R.id.enter_title);
					String title = etTitle.getText().toString();
					
					EditText etCaption = (EditText) view.findViewById(R.id.enter_caption);
					String caption = etCaption.getText().toString();
					
					if (title.isEmpty() || caption.isEmpty()) {
						Toast.makeText(getActivity(), R.string.not_complete, Toast.LENGTH_SHORT).show();
					}
					else {
						FragmentManager fm = getFragmentManager();
						FragmentTransaction ft = fm.beginTransaction();
						ft.hide(TakePictureFragment.this);
						AssignTeamFragment fragment = AssignTeamFragment.newInstance(filename, title, caption);
						ft.add(R.id.fragment_container, fragment);
						ft.addToBackStack(null);
						ft.commit();
					}
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (hasPicture) {
				GoBackDialogFrament dialog = new GoBackDialogFrament();
				dialog.show(getFragmentManager(), "goBack");
			}
			else {
				getActivity().getFragmentManager().popBackStack();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if( keyCode == KeyEvent.KEYCODE_BACK ) {
//	    	if (!oldFilename.isEmpty()) { GlobalMethods.deleteFileFromExternal(getActivity(), oldFilename); }
//			if (!filename.isEmpty()) { GlobalMethods.deleteFileFromExternal(getActivity(), filename); }
//	    	return true;
//	    }
//	    return false;
//	}
	
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
		filename = ImageHandler.getImageFilename(getActivity());
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

			ImageHandler.compressAndRotateImage(getActivity(), filename);
			showPicture(file);
		}
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
	        			getActivity().getFragmentManager().popBackStack();
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
