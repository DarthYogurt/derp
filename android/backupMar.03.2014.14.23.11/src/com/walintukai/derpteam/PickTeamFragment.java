package com.walintukai.derpteam;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PickTeamFragment extends Fragment {

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
		
		final List<Friend> fbFriends = GlobalMethods.readFriendsArray(getActivity());
		
		ListView listView = (ListView) view.findViewById(R.id.fb_friend_listview);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view , int position, long id) {
				Friend friend = fbFriends.get(position);
				
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				
				ViewTeamFragment fragment = ViewTeamFragment.newInstance(friend.getFbId());
				ft.replace(R.id.fragment_container, fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		FriendsListAdapter adapter = new FriendsListAdapter(getActivity(), R.layout.listview_row_friend, fbFriends);
		listView.setAdapter(adapter);
		
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
	
}
