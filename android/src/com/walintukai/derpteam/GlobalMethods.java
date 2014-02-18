package com.walintukai.derpteam;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class GlobalMethods {
	
	public static boolean isNetworkAvailable(Context context) {
		boolean wifiAvailable = false;
		boolean mobileAvailable = false;
		ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] networkInfo = conManager.getAllNetworkInfo();
		
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
}
