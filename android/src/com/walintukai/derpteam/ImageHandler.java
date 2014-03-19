package com.walintukai.derpteam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class ImageHandler {
	
	public static void compressAndRotateImage(Context context, String filename) {
		try {
			File file = new File(context.getExternalFilesDir(null), filename);
			
		    // Decode image size
		    BitmapFactory.Options o = new BitmapFactory.Options();
		    o.inJustDecodeBounds = true;
		    BitmapFactory.decodeStream(new FileInputStream(file), null, o);
		    
		    // The new size we want to scale to
		    final int REQUIRED_SIZE = 720;

		    // Find the correct scale value. It should be the power of 2.
		    int scale = 4;
		    while (o.outHeight/scale/2 >= REQUIRED_SIZE) { scale*=2; }
//		    while (o.outWidth/scale/2 >= REQUIRED_SIZE && o.outHeight/scale/2 >= REQUIRED_SIZE) { scale*=2; }
		        
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
		    
	        // Save and compress file
		    FileOutputStream fos = new FileOutputStream(file);
		    bm.compress(Bitmap.CompressFormat.JPEG, 90, fos);
		    try { fos.flush(); fos.close(); } 
		    catch (IOException e) { e.printStackTrace(); }
		    
		    bm.recycle();
		    System.gc();
		} 
		catch (FileNotFoundException e) { e.printStackTrace(); }
	}
	
	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
	 
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      
		}       
		final float totalPixels = width * height;
		final float totalReqPixelsCap = reqWidth * reqHeight * 2;
		while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
			inSampleSize++;
		}
	 
	    return inSampleSize;
	}
	
	public static String getImageFilename(Context context) {
		Preferences prefs = new Preferences(context);
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		String timeStamp = sdf.format(new Date());
		return timeStamp + "-" + prefs.getFbUserId() + ".jpg";
	}
	
	public static String getRealPathFromUri (Context context, Uri contentUri) {
		Cursor cursor = null;
		try { 
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} 
		finally {
			if (cursor != null) { cursor.close(); }
		}
	}
	
	public static int copyImageFromUrl(Context context, String urlAsString, String imgFilename) {
		int responseCode = 0;
		try {
			URL url = new URL(urlAsString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.connect();
			responseCode = urlConnection.getResponseCode();
			
			if (responseCode == HttpURLConnection.HTTP_OK) {
				File file = new File(context.getExternalFilesDir(null), imgFilename);
				if (file.createNewFile()) { file.createNewFile(); }
				FileOutputStream fos = new FileOutputStream(file);
				InputStream is = urlConnection.getInputStream();
				int totalSize = urlConnection.getContentLength();
				int downloadedSize = 0;
				byte[] buffer = new byte[1024];
				int bufferLength = 0;
				while ((bufferLength = is.read(buffer)) > 0) {
					fos.write(buffer, 0, bufferLength);
					downloadedSize += bufferLength;
				}
				fos.close();
				if (downloadedSize == totalSize) { Log.v("FILE COPIED FROM URL", imgFilename); }
			}
		} 
		catch (MalformedURLException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
		return responseCode;
	}
	
}
