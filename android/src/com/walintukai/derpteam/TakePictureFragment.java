package com.walintukai.derpteam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TakePictureFragment extends Fragment {
	
	private static final int REQUEST_PICTURE = 1;
	
	private String filename;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_take_picture, null);
		new NewPictureThread().start();
		
		return view;
	}
	
	public String getImageFilename() {
		filename = "photo" + getTimeStampForFilename() + ".jpg";
		return filename;
	}
	
	private static String getTimeStampForFilename() {
		SimpleDateFormat sdf = new SimpleDateFormat("_MMddyy_HHmmss_");
		String now = sdf.format(new Date());
		return now;
	}
	
	public class NewPictureThread extends Thread {
		public void run() {
			try { Thread.sleep(700); } 
	    	catch (InterruptedException e) { e.printStackTrace(); } 
			startCameraActivity();
		}
	}
	
	private void startCameraActivity() {
		File file = new File(getActivity().getExternalFilesDir(null), getImageFilename());
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
			Log.i("PICTURE SAVED", filename);
		}
		
//		switch (requestCode) {
//		case REQUEST_PICTURE:
//			if (resultCode == Activity.RESULT_OK) {	
//				Log.i("IMAGE FILE WRITTEN", step.getImageFilename());
//				ImageHandler.compressAndRotateImage(getActivity(), step.getImageFilename());
//				
//		    	finishStep();
//		    	showResult();
//		    	checkIfAllFinished();
//		    	if (step.getIsAllFinished()) { ((StepActivity)getActivity()).goToNextStep(); }
//		    }
//			break;
//			
//		case REQUEST_PICTURE_EXTRA:
//			if (resultCode == Activity.RESULT_OK) {
//				Log.i("IMAGE FILE WRITTEN", step.getExtraImageFilename());
//				ImageHandler.compressAndRotateImage(getActivity(), step.getExtraImageFilename());
//				showExtras();
//				
//		    	if (step.getReqPicture()) { step.setIsReqPictureFinished(true); }
//		    	checkIfAllFinished();
//		    	if (step.getIsAllFinished()) { ((StepActivity)getActivity()).goToNextStep(); }
//		    }
//			break;
//		
//		default:
//			break;
//		}
	}
	
	public static void compressAndRotateImage(Context context, String filename) {
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

}
