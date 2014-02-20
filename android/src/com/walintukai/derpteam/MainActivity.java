package com.walintukai.derpteam;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.facebook.widget.FriendPickerFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends FragmentActivity {
	
	private static final int FRAGMENT_MAIN = 1;
	private static final int FRAGMENT_TAKE_PICTURE = 2;
	private static final int FRAGMENT_FRIEND_PICKER = 3;
	private static final int FRAGMENT_GALLERY = 4;

	private FragmentManager fm;
	private FragmentTransaction ft;
	private FriendPickerFragment fragmentFriendPicker;
	private TakePictureFragment fragmentTakePicture;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.fragment_main);
		getActionBar().setTitle("");
		
		fm = getSupportFragmentManager();

		loadMainFragment();
//		loadFriendPickerFragment();
	}
	
	private void loadMainFragment() {
		MainFragment fragmentMain = new MainFragment();
		
		ft = fm.beginTransaction();
		ft.replace(android.R.id.content, fragmentMain);
        ft.commit();
	}
	
	public void loadTakePictureFragment() {
		TakePictureFragment takePictureFragment = new TakePictureFragment();
		
		ft = fm.beginTransaction();
		ft.replace(android.R.id.content, takePictureFragment);
        ft.commit();
	}
	
	private void loadFriendPickerFragment() {
		fragmentFriendPicker = new FriendPickerFragment();
		fragmentFriendPicker.setMultiSelect(false);
		
		ft = fm.beginTransaction();
		ft.replace(android.R.id.content, fragmentFriendPicker);
        ft.commit();
	}
	
//	@Override
//	protected void onStart() {
//		super.onStart();
//		fragmentFriendPicker.loadData(false);
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
