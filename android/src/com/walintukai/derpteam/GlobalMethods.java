package com.walintukai.derpteam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Set;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;

public class GlobalMethods {
	
	public static final String FILENAME_VOTED_PICTURES = "voted_pictures";
	
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
	
	public static void writeVotedPicturesSet(Context context, Set<Integer> list) {
		try {
			FileOutputStream fos = context.openFileOutput(FILENAME_VOTED_PICTURES, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(list);
			oos.close();
			
			String s = "";
			for (Integer i : list) {
				s = s + i.toString() + ", ";
			}
			Log.v("VOTED PICTURE IDS", s);
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	@SuppressWarnings("unchecked")
	public static Set<Integer> readVotedPicturesSet(Context context) {
		try {
			FileInputStream fis = context.openFileInput(FILENAME_VOTED_PICTURES);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Set<Integer> list = (Set<Integer>)ois.readObject();
			ois.close();
			return list;
		}
		catch (Exception e) { e.printStackTrace(); }
		return null;
	}
	
}
