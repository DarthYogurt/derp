package com.walintukai.derpteam;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PickTeamFragment extends Fragment {

	static PickTeamFragment newInstance() {
		PickTeamFragment fragment = new PickTeamFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pick_team, container, false);
		
		List<Friend> fbFriends = GlobalMethods.readFriendsArray(getActivity());
		List<Friend> activeFriends = GlobalMethods.readActiveFriendsArray(getActivity());
		
		ListView listView = (ListView) view.findViewById(R.id.fb_friend_listview);
		
		FriendsListAdapter activeFriendsAdapter = new FriendsListAdapter(getActivity(), 
				R.layout.listview_row_friend, activeFriends);
		FriendsListAdapter fbFriendsAdapter = new FriendsListAdapter(getActivity(), 
				R.layout.listview_row_friend, fbFriends);
		
		SeparatedListAdapter adapter = new SeparatedListAdapter(getActivity());
		adapter.addSection("Active Friends", activeFriendsAdapter);
		adapter.addSection("All Friends", fbFriendsAdapter);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view , int position, long id) {
				Friend friend = (Friend) parent.getAdapter().getItem(position);
				
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ViewTeamFragment fragment = ViewTeamFragment.newInstance(friend.getFbId());
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		return view;
	}
	
}
