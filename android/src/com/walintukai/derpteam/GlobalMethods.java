package com.walintukai.derpteam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;

public class GlobalMethods {
	
	public static final String FILENAME_VOTED_PICTURES_ARRAY = "voted_pictures_array";
	
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] networkInfo = conManager.getAllNetworkInfo();
		boolean wifiAvailable = false;
		boolean mobileAvailable = false;
		
		for (NetworkInfo netInfo : networkInfo) {
			if (netInfo.getTypeName().equalsIgnoreCase("WIFI")) {
				if (netInfo.isConnected()) { wifiAvailable = true; }
			}
			if (netInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
				if (netInfo.isConnected()) { mobileAvailable = true; }
			}
		}
		return wifiAvailable || mobileAvailable;
	}
	
	public static void deleteFileFromInternal(Context context, String filename) {
		File file = new File(context.getFilesDir(), filename);
		boolean deleted = file.delete();
		if (deleted) { Log.i("FILE DELETED", filename); }
	}
	
	public static void deleteFileFromExternal(Context context, String filename) {
		File file = new File(context.getExternalFilesDir(null), filename);
		boolean deleted = file.delete();
		if (deleted) { Log.i("FILE DELETED", filename); }
	}
	
	public static ProgressDialog createLoadingDialog(Context context) {
		ProgressDialog dialog = new ProgressDialog(context);
		try {
			dialog.show();
			dialog.setCancelable(false);
			dialog.setContentView(R.layout.loading_dialog);
			dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		}
		catch (BadTokenException e) { e.printStackTrace(); }
		return dialog;
	}
	
	public static void writeVotedPicturesArray(Context context, List<Integer> list) {
		try {
			FileOutputStream fos = context.openFileOutput(FILENAME_VOTED_PICTURES_ARRAY, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(list);
			oos.close();
			
			String s = "";
			for (int i = 0; i < list.size(); i++) { s = s + list.get(i).toString() + ", "; }
			Log.v("VOTED PICTURE IDS", s);
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	@SuppressWarnings("unchecked")
	public static List<Integer> readVotedPicturesArray(Context context) {
		try {
			FileInputStream fis = context.openFileInput(FILENAME_VOTED_PICTURES_ARRAY);
			ObjectInputStream ois = new ObjectInputStream(fis);
			List<Integer> list = (ArrayList<Integer>)ois.readObject();
			ois.close();
			return list;
		}
		catch (Exception e) { e.printStackTrace(); }
		return null;
	}
	
}
