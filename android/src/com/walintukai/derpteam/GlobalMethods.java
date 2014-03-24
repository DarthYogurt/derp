package com.walintukai.derpteam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Set;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;

public class GlobalMethods {
	
	public static final String FILENAME_VOTED_PICTURES = "voted_pictures";
	public static final String FILENAME_FRIENDS_LIST = "friends_list";
	public static final String FILENAME_ACTIVE_FRIENDS_LIST = "active_friends_list";
	
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
			dialog.setCanceledOnTouchOutside(false);
			dialog.setContentView(R.layout.dialog_loading);
			dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		}
		catch (BadTokenException e) { e.printStackTrace(); }
		return dialog;
	}
	
	public static ProgressDialog createLoadingDialogForGallery(Context context) {
		ProgressDialog dialog = new ProgressDialog(context);
		try {
			dialog.show();
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setContentView(R.layout.dialog_gallery_load);
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.gravity = Gravity.BOTTOM;
			dialog.getWindow().setAttributes(lp);
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
	
	public static void writeFriendsArray(Context context, List<Friend> friends) {
		try {
			FileOutputStream fos = context.openFileOutput(FILENAME_FRIENDS_LIST, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(friends);
			oos.close();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	@SuppressWarnings("unchecked")
	public static List<Friend> readFriendsArray(Context context) {
		try {
			FileInputStream fis = context.openFileInput(FILENAME_FRIENDS_LIST);
			ObjectInputStream ois = new ObjectInputStream(fis);
			List<Friend> friends = (List<Friend>)ois.readObject();
			ois.close();
			return friends;
		}
		catch (Exception e) { e.printStackTrace(); }
		return null;
	}
	
	public static void writeActiveFriendsArray(Context context, List<Friend> activeFriends) {
		try {
			FileOutputStream fos = context.openFileOutput(FILENAME_ACTIVE_FRIENDS_LIST, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(activeFriends);
			oos.close();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	@SuppressWarnings("unchecked")
	public static List<Friend> readActiveFriendsArray(Context context) {
		try {
			FileInputStream fis = context.openFileInput(FILENAME_ACTIVE_FRIENDS_LIST);
			ObjectInputStream ois = new ObjectInputStream(fis);
			List<Friend> activeFriends = (List<Friend>)ois.readObject();
			ois.close();
			return activeFriends;
		}
		catch (Exception e) { e.printStackTrace(); }
		return null;
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
	
	public static String getFirstName(String fullName) {
		return fullName.substring(0, fullName.indexOf(" "));
	}
	
}
