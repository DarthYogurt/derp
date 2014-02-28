package com.walintukai.derpteam;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;

public class PickTeamFragment extends Fragment {

	private static final String KEY_IMG_FILENAME = "imgFilename";
	private static final String KEY_TITLE = "title";
	private static final String KEY_CAPTION = "caption";
	
	private List<GraphUser> fbFriends;
	private ListView listView;
	private String imgFilename;
	private String title;
	private String caption;
	private String targetName;
	private String targetFbId;
	private String targetUserId;

	static PickTeamFragment newInstance() {
		PickTeamFragment fragment = new PickTeamFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pick_team, container, false);
		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		TextView header = (TextView) view.findViewById(R.id.pick_team_header);
		header.setText(R.string.view_team);
		
		listView = (ListView) view.findViewById(R.id.fb_friend_listview);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view , int position, long id) {
				GraphUser friend = fbFriends.get(position);
				
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				
				ViewTeamFragment fragment = ViewTeamFragment.newInstance(friend.getId());
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		requestFacebookFriends(Session.getActiveSession());
		
		return view;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getActivity().getFragmentManager().popBackStack();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void requestFacebookFriends(Session session) {
		final ProgressDialog loadingDialog;
		loadingDialog = GlobalMethods.createLoadingDialog(getActivity());
		loadingDialog.show();
		
		Request friendsRequest = createRequest(session);
		friendsRequest.setCallback(new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				fbFriends = getResults(response);
				Collections.sort(fbFriends, new GraphUserComparator());
				FriendsListAdapter adapter = new FriendsListAdapter(getActivity(), R.layout.listview_row_friend, fbFriends);
				listView.setAdapter(adapter);
				loadingDialog.hide();
			}
		});
		friendsRequest.executeAsync();
	}
	
	private Request createRequest(Session session) {
		Request request = Request.newGraphPathRequest(session, "me/friends", null);

		Set<String> fields = new HashSet<String>();
		String[] requiredFields = new String[] {"id", "name"};
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
	
	private class GraphUserComparator implements Comparator<GraphUser> {
		@Override
		public int compare(GraphUser user1, GraphUser user2) {
			return user1.getName().compareTo(user2.getName());
		}
	}
	
}
