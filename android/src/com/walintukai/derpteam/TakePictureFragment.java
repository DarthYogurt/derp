package com.walintukai.derpteam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class TakePictureFragment extends Fragment {

	public static final int REQUEST_PICTURE = 100;
	public static final int REQUEST_CROP_NEW = 200;
	public static final int REQUEST_CROP_EXISTING = 300;
	public static final int REQUEST_SELECT_FROM_GALLERY = 400;
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
		setHasOptionsMenu(false);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		
		Bundle args = getArguments();
		filename = args.getString(KEY_FILENAME);
		oldFilename = "";
		
		takenPicture = (ImageView) view.findViewById(R.id.taken_picture);
		ImageButton btnAssignTeam = (ImageButton) view.findViewById(R.id.btn_assign_team);
		
		takenPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!filename.isEmpty()) { oldFilename = filename; }
				PictureDialog dialog = new PictureDialog();
				dialog.show(getFragmentManager(), "picture");
			}
		});
		
		if (!filename.isEmpty()) { 
			hasPicture = true;
			File file = new File(getActivity().getExternalFilesDir(null), filename);
			showPicture(file);
		}
		else {
			PictureDialog dialog = new PictureDialog();
			dialog.show(getFragmentManager(), "picture");
		}
		
		btnAssignTeam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (hasPicture) {
					EditText etTitle = (EditText) view.findViewById(R.id.enter_title);
					String title = etTitle.getText().toString();
					
					EditText etCaption = (EditText) view.findViewById(R.id.enter_caption);
					String caption = etCaption.getText().toString();
					
					if (title.isEmpty()) {
						Toast.makeText(getActivity(), R.string.title_empty, Toast.LENGTH_SHORT).show();
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
					NoPictureDialog dialog = new NoPictureDialog();
					dialog.show(getFragmentManager(), "noPicture");
				}
			}
		});
		
		return view;
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
		
		switch(requestCode) {
		case REQUEST_PICTURE:
			if (resultCode == Activity.RESULT_OK) {
				hasPicture = true;
				if (!oldFilename.isEmpty()) { GlobalMethods.deleteFileFromExternal(getActivity(), oldFilename); }
				
				Log.i("PICTURE SAVED", filename);
				
				Intent intent = new Intent("com.android.camera.action.CROP");
			    intent.setDataAndType(Uri.fromFile(file), "image/*");
			    intent.putExtra("outputX", 400);
			    intent.putExtra("outputY", 400);
			    intent.putExtra("aspectX", 1);
			    intent.putExtra("aspectY", 1);
			    intent.putExtra("scale", true);
			    intent.putExtra("noFaceDetection", true);
			    intent.putExtra("output", Uri.fromFile(file));
			    startActivityForResult(intent, REQUEST_CROP_NEW);
			}
			break;
			
		case REQUEST_CROP_NEW:
			if (resultCode == Activity.RESULT_OK) {
				showPicture(file);
			}
			break;
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
	
	private class PictureDialog extends DialogFragment {
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Dialog dialog = new Dialog(getActivity());
			dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			dialog.setContentView(R.layout.dialog_picture);
			dialog.getWindow().setLayout(500, 350);
			
			ImageView btnSelectGallery = (ImageView) dialog.findViewById(R.id.btn_select_gallery);
			btnSelectGallery.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        			photoPickerIntent.setType("image/*");
        			getActivity().startActivityForResult(photoPickerIntent, REQUEST_SELECT_FROM_GALLERY);
        			dismiss();
				}
			});
			
			ImageView btnNewPicture = (ImageView) dialog.findViewById(R.id.btn_new_picture);
			btnNewPicture.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					new NewPictureThread().start();
					dismiss();
				}
			});
			
			return dialog;
		}
	}
	
	private class NoPictureDialog extends DialogFragment {
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Dialog dialog = new Dialog(getActivity());
			dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			dialog.setContentView(R.layout.dialog_no_picture);
			dialog.getWindow().setLayout(500, 350);
			
			ImageView btnOk = (ImageView) dialog.findViewById(R.id.btn_ok);
			btnOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
        			dismiss();
				}
			});
			
			return dialog;
		}
	}
	
}
