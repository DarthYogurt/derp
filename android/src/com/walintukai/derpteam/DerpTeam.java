package com.walintukai.derpteam;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;
import android.util.Log;

public class DerpTeam extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
		ImageLoader.getInstance().init(config);
		
		Log.i("IMAGELOADER CONFIG", "LOADED");
	}

}
