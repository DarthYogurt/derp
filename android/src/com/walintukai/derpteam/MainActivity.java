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

public class MainActivity extends FragmentActivity {
	
	private static final int FRAGMENT_MAIN = 1;
	private static final int FRAGMENT_TAKE_PICTURE = 2;
	private static final int FRAGMENT_FRIEND_PICKER = 3;
	private static final int FRAGMENT_GALLERY = 4;

	private FriendPickerFragment fragmentFriendPicker;
	private TakePictureFragment fragmentTakePicture;
	private FragmentManager fm;
	private FragmentTransaction ft;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
		getActionBar().setTitle("");
		
		fm = getSupportFragmentManager();
		
		loadFriendPickerFragment();
		
	}
	
	private void loadFriendPickerFragment() {
		fragmentFriendPicker = new FriendPickerFragment();
		fragmentFriendPicker.setMultiSelect(false);
		
		ft = fm.beginTransaction();
		ft.replace(android.R.id.content, fragmentFriendPicker);
        ft.commit();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		fragmentFriendPicker.loadData(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void requestFacebookFriends(Session session) {
		Request friendsRequest = createRequest(session);
		friendsRequest.setCallback(new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				List<GraphUser> friends = getResults(response);
              
				for (int i = 0; i < friends.size(); i++) {
					GraphUser friend = friends.get(i);
					Log.e("FRIEND", friend.getName() + " " + friend.getId());
					
				}
			}
		});
		friendsRequest.executeAsync();
	}
	
	private Request createRequest(Session session) {
		Request request = Request.newGraphPathRequest(session, "me/friends", null);

		Set<String> fields = new HashSet<String>();
		String[] requiredFields = new String[] {"id", "name", "picture"};
		fields.addAll(Arrays.asList(requiredFields));

		Bundle parameters = request.getParameters();
		parameters.putString("fields", TextUtils.join(",", fields));
		request.setParameters(parameters);

		return request;
    }
	
	private List<GraphUser> getResults(Response response) {
		GraphMultiResult multiResult = response.getGraphObjectAs(GraphMultiResult.class);
		GraphObjectList<GraphObject> data = multiResult.getData();
		return data.castToListOf(GraphUser.class);
	}

}
